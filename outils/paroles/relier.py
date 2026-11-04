"""Relie les chansons de Kaloy aux pages de tononkira.serasera.org.

Produit un fichier de correspondances a RELIRE A LA MAIN avant tout import.
Rien n'est ecrit en base par ce programme, et aucune parole n'est recuperee
ici : cette etape ne fait qu'etablir des liens.

Pourquoi partir de l'artiste et non du titre
--------------------------------------------
La recherche par titre du site est globale. « Malagasy », « Veloma » ou
« Tantara » sont des mots courants : ils remontent une vingtaine de chansons
de chorales et d'artistes sans rapport. Relier sur cette base collerait les
paroles d'un inconnu sur une chanson de Rossy, et rien a l'ecran ne le
signalerait.

La page d'un artiste, elle, ne liste que ses chansons. On y compare donc des
titres entre eux, dans un ensemble de dix, ce qui est fiable.

Usage
-----
    python -I relier.py chansons.tsv correspondances.csv

Le fichier d'entree a trois colonnes separees par des tabulations :
    identifiant, titre, nom de scene
Voir le README pour la commande psql qui le produit.
"""
import csv
import difflib
import re
import sys
import time
import unicodedata
import urllib.error
import urllib.request

SITE = "https://tononkira.serasera.org"

# Une adresse de contact dans l'en-tete : un robot anonyme se fait bloquer, et
# c'est normal. Remplace-la par la tienne.
ENTETE = {
    "User-Agent": "Kaloy/1.0 (projet etudiant; contact: rabezandrinasoary@gmail.com)"
}

# Une requete par seconde et demie. Le site est petit et benevole : rien ne
# justifie de le marteler pour une trentaine de pages.
DELAI = 1.5

# En dessous, la ressemblance ne veut plus rien dire et il vaut mieux
# presenter la ligne comme douteuse que risquer une fausse correspondance.
SEUIL = 0.72

MOTIF_CHANSON = re.compile(
    r'https://tononkira\.serasera\.org/hira/([^/"]+)/([^/"]+)"'
)


def normaliser(texte):
    """« Lalan-kizorana » -> « lalan-kizorana », accents et ponctuation retires."""
    sans_accent = unicodedata.normalize("NFKD", texte).encode("ascii", "ignore").decode()
    return re.sub(r"[^a-z0-9]+", "-", sans_accent.lower()).strip("-")


def lire(url):
    requete = urllib.request.Request(url, headers=ENTETE)
    with urllib.request.urlopen(requete, timeout=25) as reponse:
        return reponse.read().decode("utf-8", "replace")


def chansons_de_l_artiste(slug_artiste):
    """Les identifiants de chansons listes sur la page d'un artiste."""
    try:
        page = lire(f"{SITE}/mpihira/{slug_artiste}")
    except urllib.error.HTTPError as erreur:
        return None if erreur.code == 404 else []
    except Exception:
        return []

    # On ne garde que les chansons de CET artiste : sa page cite aussi des
    # reprises et des suggestions qui appartiennent a d'autres.
    return sorted({
        titre for artiste, titre in MOTIF_CHANSON.findall(page)
        if artiste == slug_artiste
    })


def meilleure_correspondance(titre, candidats):
    """Le candidat le plus proche, et son score entre 0 et 1."""
    cible = normaliser(titre)
    meilleur, score = None, 0.0
    for candidat in candidats:
        proximite = difflib.SequenceMatcher(None, cible, candidat).ratio()
        if proximite > score:
            meilleur, score = candidat, proximite
    return meilleur, score


def main():
    if len(sys.argv) != 3:
        raise SystemExit(__doc__)

    entree, sortie = sys.argv[1], sys.argv[2]

    chansons = []
    with open(entree, encoding="utf-8") as fichier:
        for ligne in fichier:
            morceaux = ligne.rstrip("\n").split("\t")
            if len(morceaux) >= 3 and morceaux[0].strip().isdigit():
                chansons.append((int(morceaux[0]), morceaux[1].strip(), morceaux[2].strip()))

    print(f"{len(chansons)} chanson(s) a relier")

    # Une seule visite par artiste, meme s'il a dix chansons.
    catalogues = {}
    lignes = []

    for identifiant, titre, artiste in chansons:
        slug_artiste = normaliser(artiste)

        if slug_artiste not in catalogues:
            catalogues[slug_artiste] = chansons_de_l_artiste(slug_artiste)
            time.sleep(DELAI)

        catalogue = catalogues[slug_artiste]

        if catalogue is None:
            statut, trouve, score, url = "artiste absent du site", "", 0.0, ""
        elif not catalogue:
            statut, trouve, score, url = "page artiste illisible", "", 0.0, ""
        else:
            trouve, score = meilleure_correspondance(titre, catalogue)
            url = f"{SITE}/hira/{slug_artiste}/{trouve}" if trouve else ""
            statut = "a verifier" if score >= SEUIL else "doute"

        lignes.append({
            "id": identifiant,
            "titre_kaloy": titre,
            "artiste_kaloy": artiste,
            "score": f"{score:.2f}",
            "titre_site": trouve,
            "url": url,
            "statut": statut,
            # La colonne que TU remplis : « oui » pour importer cette ligne.
            "valide": "",
        })
        print(f"  {titre[:30]:32} {score:.2f}  {statut}")

    with open(sortie, "w", encoding="utf-8", newline="") as fichier:
        auteur = csv.DictWriter(fichier, fieldnames=list(lignes[0].keys()), delimiter=";")
        auteur.writeheader()
        auteur.writerows(lignes)

    srs = sum(1 for l in lignes if l["statut"] == "a verifier")
    print()
    print(f"ecrit : {sortie}")
    print(f"{srs} ligne(s) au-dessus du seuil, {len(lignes) - srs} a examiner de pres")
    print()
    print("RIEN n'est importe tant que tu n'as pas mis « oui » dans la colonne valide.")


if __name__ == "__main__":
    main()
