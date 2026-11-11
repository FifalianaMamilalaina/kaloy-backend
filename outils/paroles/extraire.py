"""Telecharge une page de tononkira.serasera.org et en extrait le texte.

Produit un fichier de relecture. N'ecrit RIEN en base : l'import est une
etape separee, lancee a la main apres verification.

Usage
-----
    python -I extraire.py <url> [--id <identifiant en base>]
    python -I extraire.py --depuis correspondances.csv

Pourquoi l'adresse est fournie et non devinee
---------------------------------------------
On a d'abord essaye de fabriquer l'adresse a partir du nom de l'artiste et du
titre en base. Ca ne marche pas : « Eny Ampifitia » de FJKM Isotry Fitiavana
se trouve a /hira/ampifitia/eny, ou « Ampifitia » est l'artiste et « Eny » le
titre. Les deux catalogues ne nomment pas les memes choses de la meme facon.

L'adresse est donc une donnee d'entree, pas un calcul. On la prend du site
lui-meme, ou on la colle a la main.

Les mots pieges
---------------
Le site se defend de la copie : il insere des mots au hasard dans les
paroles, puis un JavaScript les masque apres chargement. Dans un navigateur
le texte parait propre ; une extraction qui se contente d'enlever les
balises les garde, melanges aux vers. La liste des mots et l'identifiant du
bloc a nettoyer sont declares dans la page, et les deux changent a chaque
page : on les lit plutot que de les deviner.

La colonne « url » du fichier de relecture garde l'adresse d'origine de
chaque texte, pour que la source reste tracable apres import.
"""
import argparse
import csv
import html
import os
import re
import time
import urllib.request

SITE = "https://tononkira.serasera.org"

ENTETE = {
    "User-Agent": "Kaloy/1.0 (projet etudiant; contact: rabezandrinasoary@gmail.com)"
}

DELAI = 1.5
CACHE = "cache"

MOTIF_BR = re.compile(r"<br\s*/?>", re.I)

# Le site injecte des mots au hasard dans les paroles puis les masque par
# JavaScript apres chargement. Au navigateur le texte parait propre ;
# qui enleve les balises garde les pieges. La liste est declaree dans la
# page meme, juste avant l'identifiant du bloc a nettoyer.
MOTIF_PIEGES = re.compile(r"trapWords\s*=\s*\[(.*?)\]", re.S)
MOTIF_BLOC = re.compile(r'getElementById\(\s*"([^"]+)"\s*\)')


def chemin_cache(url):
    nom = re.sub(r"[^a-z0-9]+", "_", url.replace(SITE, "").lower()).strip("_")
    return os.path.join(CACHE, f"{nom}.html")


def recuperer(url):
    """Lit la page, du cache si elle y est deja.

    Le cache n'est pas une optimisation : c'est ce qui evite de redemander
    cinquante fois la meme page au site pendant qu'on met au point l'analyse.
    """
    os.makedirs(CACHE, exist_ok=True)
    fichier = chemin_cache(url)

    if os.path.exists(fichier):
        with open(fichier, encoding="utf-8") as f:
            return f.read(), True

    requete = urllib.request.Request(url, headers=ENTETE)
    with urllib.request.urlopen(requete, timeout=40) as reponse:
        page = reponse.read().decode("utf-8", "replace")

    with open(fichier, "w", encoding="utf-8") as f:
        f.write(page)
    time.sleep(DELAI)
    return page, False


def en_texte(fragment):
    """Convertit un fragment HTML en texte : seuls les <br> font des vers.

    Les sauts de ligne du HTML lui-meme ne comptent pas. Un navigateur replie
    tout blanc -- espace, tabulation, saut de ligne -- en une seule espace, et
    n'affiche un retour que sur un <br>. On fait pareil.

    La premiere version prenait les deux pour des vers. La silhouette mesuree
    sur une chanson de 17 vers etait « X.X.X.X.X... » : dix-sept vers, seize
    lignes vides, et plus aucune strophe -- les vraies coupures, deux <br> de
    suite, ne se distinguaient plus des simples retours a la ligne.

    Au passage, cela ramene aussi a une espace les doubles espaces laisses par
    le retrait d'un mot piege, ce que le navigateur faisait deja.
    """
    # Le repli vient AVANT la traduction des <br>, et c'est tout l'enjeu :
    # la page ecrit « vers<br />\nvers suivant ». Traduire d'abord donnerait
    # deux sauts la ou le navigateur n'en montre qu'un, donc une ligne vide
    # entre chaque vers -- et les vraies coupures de strophe, deux <br> de
    # suite, deviendraient indistinguables des simples retours a la ligne.
    texte = re.sub(r"\s+", " ", fragment)
    texte = MOTIF_BR.sub("\n", texte)
    texte = re.sub(r"<[^>]+>", "", texte)
    texte = html.unescape(texte)

    lignes = [re.sub(r"\s+", " ", ligne).strip() for ligne in texte.split("\n")]

    # Jamais plus d'une ligne vide d'affilee.
    propre, vide_avant = [], False
    for ligne in lignes:
        if ligne:
            propre.append(ligne)
            vide_avant = False
        elif not vide_avant:
            propre.append("")
            vide_avant = True
    return "\n".join(propre).strip()


def pieges_et_bloc(page):
    """La liste des mots pieges, et l'identifiant du bloc de paroles.

    Les deux viennent de la page elle-meme, du script qui fait le masquage :

        const trapWords = ["gvxyhoq","qqmkkjes","fqivopt","tononkira.serasera.org"];
        const contentDiv = document.getElementById("6ae38de62feea");

    L'identifiant du bloc change a chaque page, et les mots aussi : rien
    ne peut etre code en dur ici. On lit ce que la page declare.

    getElementById apparait plusieurs fois dans la page, pour le bouton de
    copie du lien entre autres. On ne cherche donc qu'APRES la liste des
    pieges, ou le premier est celui du bloc de paroles.
    """
    m = MOTIF_PIEGES.search(page)
    if not m:
        return [], ""
    mots = re.findall(r'"([^"]+)"', m.group(1))
    suivant = MOTIF_BLOC.search(page, m.end())
    return mots, (suivant.group(1) if suivant else "")


def contenu_du_bloc(page, identifiant):
    """Le HTML interieur du div portant cet identifiant.

    Les <div> sont comptes plutot que cherches par le premier </div> : le
    bloc de paroles en contient parfois, et s'arreter au premier le
    tronquerait.
    """
    ouverture = re.search(r'<div[^>]*\bid="%s"[^>]*>' % re.escape(identifiant), page)
    if not ouverture:
        return ""
    return contenu_depuis(page, ouverture.end())


def contenu_depuis(page, depart):
    """Le HTML jusqu'au </div> qui equilibre celui ouvert avant [depart]."""
    niveau = 1
    for balise in re.finditer(r"<(/?)div\b[^>]*>", page[depart:]):
        niveau += -1 if balise.group(1) else 1
        if niveau == 0:
            return page[depart:depart + balise.start()]
    # Div non refermee : on rend la suite plutot que rien.
    return page[depart:]


def retirer_pieges(fragment, mots):
    """Enleve les mots pieges du HTML, comme le fait le script du site.

    Sur le HTML et non sur le texte deja nettoye : c'est la que le site
    les insere, donc c'est la qu'ils ont la forme attendue. Les limites de
    mot evitent d'amputer un vers qui contiendrait la suite de lettres.

    re.escape est necessaire pour « tononkira.serasera.org », dont les
    points seraient sinon des jokers.
    """
    retires = []
    for mot in mots:
        motif = re.compile(r"\b%s\b" % re.escape(mot))
        nombre = len(motif.findall(fragment))
        if nombre:
            fragment = motif.sub("", fragment)
            retires.append("%s x%d" % (mot, nombre))
    return fragment, retires


def div_le_plus_dense(page):
    """Le div qui concentre le plus de sauts de ligne, en secours.

    Des div et non des paragraphes : les paroles de ce site sont dans un div,
    jamais dans un <p>. L'ancienne version cherchait « le paragraphe le plus
    dense » et rendait quand meme les paroles, mais par accident : son motif
    « <p[^>]*> » attrapait les <path> des SVG, et comme « </path> » ne ferme
    pas « </p> », la capture partait d'un <path> pour courir jusqu'au premier
    vrai </p>, englobant au passage le div des paroles -- avec la navigation
    et la mention de source en prime. Mesure sur une page : 82 sauts de ligne
    dans ce bloc fantome, 0 dans les deux vrais paragraphes. Corriger le motif
    aurait donc supprime le secours sans le dire.

    A egalite on prend le plus court, c'est-a-dire le plus interieur : un div
    parent a toujours au moins autant de sauts que son enfant.
    """
    meilleur, score = "", 0
    for ouverture in re.finditer(r"<div\b[^>]*>", page):
        contenu = contenu_depuis(page, ouverture.end())
        sauts = len(MOTIF_BR.findall(contenu))
        if sauts > score or (sauts == score and sauts and len(contenu) < len(meilleur)):
            meilleur, score = contenu, sauts
    return meilleur


def extraire_paroles(page):
    """Le texte de la chanson, et la liste des mots pieges retires.

    Rend un couple (paroles, pieges_retires).

    On passe par l'identifiant du bloc que la page declare, et non plus par
    « le paragraphe le plus dense ». Cette heuristique etait fragile deux fois :
    elle ne voyait pas les mots pieges, et son motif de paragraphe attrapait
    les <path> des SVG. L'identifiant, lui, est donne par le site.

    L'heuristique reste en secours, pour le jour ou le site changera son
    masquage : mieux vaut des paroles a relire que rien du tout. Mais dans ce
    cas les pieges ne sont pas retires, et la colonne « pieges » du fichier de
    relecture le dit.
    """
    mots, identifiant = pieges_et_bloc(page)

    fragment = contenu_du_bloc(page, identifiant) if identifiant else ""
    par_identifiant = bool(fragment)

    if not fragment:
        depart = page.find('id="main"')
        zone = page[depart:] if depart >= 0 else page
        fragment = div_le_plus_dense(zone)

    # Un bloc de navigation ou de mention legale n'a pas de sauts : ce seuil
    # evite de ramener n'importe quoi quand la page n'a pas de paroles.
    if len(MOTIF_BR.findall(fragment)) < 4:
        return "", []

    fragment, retires = retirer_pieges(fragment, mots)
    if not par_identifiant:
        retires.append("BLOC NON IDENTIFIE : relire de pres")
    return en_texte(fragment), retires


def extraire_titre(page):
    m = re.search(r"<title>(.*?)</title>", page, re.S)
    if not m:
        return ""
    # « Eny - AMPIFITIA - Tononkira malagasy »
    return html.unescape(re.sub(r"\s+", " ", m.group(1)).strip())


def traiter(url, identifiant=""):
    page, du_cache = recuperer(url)
    paroles, pieges = extraire_paroles(page)
    lignes = [l for l in paroles.split("\n") if l.strip()]
    return {
        "id": identifiant,
        "url": url,
        "titre_site": extraire_titre(page),
        "lignes": len(lignes),
        "caracteres": len(paroles),
        "paroles": paroles,
        # Ce qui a ete retire, pour que la relecture sache quoi verifier.
        "pieges": " ; ".join(pieges),
        # Un caractere de remplacement signale un probleme de decodage,
        # pas un piege : il ne se corrige pas en retirant un mot.
        "illisibles": paroles.count("\ufffd"),
        "source": "cache" if du_cache else "site",
    }


def main():
    analyseur = argparse.ArgumentParser(description=__doc__)
    analyseur.add_argument("url", nargs="?", help="adresse d'une page de chanson")
    analyseur.add_argument("--id", default="", help="identifiant de la chanson en base")
    analyseur.add_argument("--depuis", help="fichier CSV avec une colonne url")
    analyseur.add_argument("--sortie", default="paroles.csv")
    options = analyseur.parse_args()

    taches = []
    if options.depuis:
        with open(options.depuis, encoding="utf-8") as f:
            for ligne in csv.DictReader(f, delimiter=";"):
                if ligne.get("url"):
                    taches.append((ligne["url"], ligne.get("id", "")))
    elif options.url:
        taches.append((options.url, options.id))
    else:
        raise SystemExit(analyseur.format_help())

    resultats = []
    for url, identifiant in taches:
        try:
            r = traiter(url, identifiant)
        except Exception as erreur:
            print(f"  ECHEC  {url}  {erreur}")
            continue
        resultats.append(r)
        etat = "VIDE" if r["caracteres"] == 0 else f'{r["lignes"]} lignes, {r["caracteres"]} car.'
        print(f'  {r["titre_site"][:40]:42} {etat:26} ({r["source"]})')
        if r["pieges"]:
            print(f'         pieges retires : {r["pieges"]}')
        if r["illisibles"]:
            print(f'         ATTENTION {r["illisibles"]} caractere(s) illisible(s) : decodage a verifier')

    if not resultats:
        raise SystemExit("rien a ecrire")

    with open(options.sortie, "w", encoding="utf-8", newline="") as f:
        auteur = csv.DictWriter(
            f,
            fieldnames=["id", "url", "titre_site", "lignes", "caracteres",
                        "pieges", "illisibles", "paroles", "valide"],
            delimiter=";",
        )
        auteur.writeheader()
        for r in resultats:
            auteur.writerow({
                "id": r["id"],
                "url": r["url"],
                "titre_site": r["titre_site"],
                "lignes": r["lignes"],
                "caracteres": r["caracteres"],
                "pieges": r["pieges"],
                "illisibles": r["illisibles"],
                "paroles": r["paroles"],
                # A remplir par toi : « oui » pour autoriser l'import.
                "valide": "",
            })

    print()
    print(f"ecrit : {options.sortie}")
    print("Relis chaque ligne, mets « oui » dans la colonne valide, puis lance l'import.")


if __name__ == "__main__":
    main()
