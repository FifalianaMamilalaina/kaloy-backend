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
MOTIF_P = re.compile(r"<p[^>]*>(.*?)</p>", re.S | re.I)


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
    """Convertit un fragment HTML en texte, les <br> devenant des sauts."""
    texte = MOTIF_BR.sub("\n", fragment)
    texte = re.sub(r"<[^>]+>", "", texte)
    texte = html.unescape(texte)

    # Les pages sont indentees : on nettoie chaque ligne sans supprimer les
    # lignes vides, qui separent les couplets.
    lignes = [ligne.strip() for ligne in texte.split("\n")]

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


def extraire_paroles(page):
    """Le texte de la chanson, ou une chaine vide si rien n'est reconnu.

    On retient le paragraphe qui concentre le PLUS de sauts de ligne dans la
    zone de contenu.

    La premiere version decoupait la page avant le lecteur audio, les
    tablatures et les commentaires, puis cherchait les paragraphes. Elle
    rendait une chaine vide : un lien vers les commentaires figure A
    L'INTERIEUR du bloc de paroles, donc le repere se declenchait au milieu du
    texte et tronquait le paragraphe avant sa fermeture. Mesurer plutot que
    decouper evite ce piege.
    """
    depart = page.find('id="main"')
    zone = page[depart:] if depart >= 0 else page

    blocs = MOTIF_P.findall(zone)
    if not blocs:
        return ""

    meilleur = max(blocs, key=lambda bloc: len(MOTIF_BR.findall(bloc)))

    # Un paragraphe de navigation ou de mention legale n'a pas de sauts : ce
    # seuil evite de ramener n'importe quoi quand la page n'a pas de paroles.
    if len(MOTIF_BR.findall(meilleur)) < 4:
        return ""

    return en_texte(meilleur)


def extraire_titre(page):
    m = re.search(r"<title>(.*?)</title>", page, re.S)
    if not m:
        return ""
    # « Eny - AMPIFITIA - Tononkira malagasy »
    return html.unescape(re.sub(r"\s+", " ", m.group(1)).strip())


def traiter(url, identifiant=""):
    page, du_cache = recuperer(url)
    paroles = extraire_paroles(page)
    lignes = [l for l in paroles.split("\n") if l.strip()]
    return {
        "id": identifiant,
        "url": url,
        "titre_site": extraire_titre(page),
        "lignes": len(lignes),
        "caracteres": len(paroles),
        "paroles": paroles,
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

    if not resultats:
        raise SystemExit("rien a ecrire")

    with open(options.sortie, "w", encoding="utf-8", newline="") as f:
        auteur = csv.DictWriter(
            f,
            fieldnames=["id", "url", "titre_site", "lignes", "caracteres", "paroles", "valide"],
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
                "paroles": r["paroles"],
                # A remplir par toi : « oui » pour autoriser l'import.
                "valide": "",
            })

    print()
    print(f"ecrit : {options.sortie}")
    print("Relis chaque ligne, mets « oui » dans la colonne valide, puis lance l'import.")


if __name__ == "__main__":
    main()
