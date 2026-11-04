package org.example.mozika.services;

import org.example.mozika.dto.paroles.CandidatParoles;
import org.example.mozika.dto.paroles.ParolesTrouvees;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recherche de paroles sur tononkira.serasera.org.
 *
 * Service separe de ServiceMesChansons, et c'est voulu : il depend d'un site
 * exterieur qui tombe. Melange a la creation de chanson, une indisponibilite
 * du site empecherait un artiste de declarer son oeuvre. Ici, elle ne prive
 * que de la recherche ; la saisie a la main reste toujours possible.
 *
 * Ce que ce service NE fait PAS : enregistrer. Il propose des candidats, puis
 * un texte pour apercu. C'est l'appel a /mes-chansons qui decide de ce qui
 * finit en base, apres relecture par l'artiste.
 */
@Service
public class ServiceParoles {

    private static final Logger journal = LoggerFactory.getLogger(ServiceParoles.class);

    private static final String SITE = "https://tononkira.serasera.org";

    /**
     * Le seul hote autorise.
     *
     * Sans ce controle, /paroles?url=... ferait telecharger par le serveur
     * n'importe quelle adresse fournie par un client — y compris des adresses
     * internes inaccessibles depuis l'exterieur. C'est une faille classique,
     * et elle se ferme par une liste blanche, pas par un filtre de motifs.
     */
    private static final String HOTE_AUTORISE = "tononkira.serasera.org";

    /** Le site a deja expire plusieurs fois : mieux vaut echouer vite. */
    private static final int DELAI_MS = 8000;

    private static final String AGENT =
            "Kaloy/1.0 (application musicale; contact: rabezandrinasoary@gmail.com)";

    private static final int MAX_CANDIDATS = 15;

    /**
     * Petit cache des pages deja lues.
     *
     * Un artiste qui compare trois candidats puis revient au premier ne doit
     * pas redemander trois fois la meme page. Borne a 50 entrees : au-dela on
     * oublie la plus ancienne, et on ne garde pas la memoire du serveur
     * occupee par des pages que personne ne reverra.
     */
    private final Map<String, Document> cache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Document> plusAncienne) {
            return size() > 50;
        }
    };

    /** Candidats pour un titre, sans leur texte. */
    public List<CandidatParoles> rechercher(String titre, String artisteSouhaite) {
        String url = SITE + "/tononkira?lohateny="
                + URLEncoder.encode(titre, StandardCharsets.UTF_8);

        Document page = lire(url, false);

        // Les liens de resultat sont ABSOLUS dans cette page. Un selecteur sur
        // les chemins relatifs ne remonterait rien — erreur commise lors de la
        // mise au point, qui a fait croire a un catalogue vide.
        Elements liens = page.select("a[href*=/hira/]");

        List<CandidatParoles> candidats = new ArrayList<>();
        List<String> dejaVus = new ArrayList<>();

        for (Element lien : liens) {
            String adresse = lien.absUrl("href");
            String[] morceaux = cheminChanson(adresse);
            if (morceaux == null || dejaVus.contains(adresse)) {
                continue;
            }
            dejaVus.add(adresse);

            String artisteTrouve = lisible(morceaux[0]);
            String titreTrouve = lisible(morceaux[1]);

            candidats.add(new CandidatParoles(
                    titreTrouve, artisteTrouve, adresse,
                    pertinence(titre, titreTrouve, artisteSouhaite, artisteTrouve)));
        }

        candidats.sort(Comparator.comparingDouble(CandidatParoles::getScore).reversed());
        return candidats.size() > MAX_CANDIDATS
                ? candidats.subList(0, MAX_CANDIDATS)
                : candidats;
    }

    /** Le texte d'un candidat, pour apercu. */
    public ParolesTrouvees texte(String url) {
        verifierAdresse(url);
        Document page = lire(url, true);

        String texte = extraireTexte(page);
        if (texte.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aucune parole trouvee sur cette page");
        }

        String[] morceaux = cheminChanson(url);
        String artiste = morceaux != null ? lisible(morceaux[0]) : "";
        String titre = morceaux != null ? lisible(morceaux[1]) : page.title();

        int lignes = (int) texte.lines().filter(l -> !l.isBlank()).count();
        return new ParolesTrouvees(titre, artiste, url, texte, lignes);
    }

    /**
     * Le bloc de paroles : l'element qui porte le plus de <br> en ENFANTS DIRECTS.
     *
     * Deux fausses pistes avant celle-ci, qui expliquent le detour.
     *
     * D'abord un decoupage de la page avant les sections audio, tablatures et
     * commentaires : resultat vide, parce qu'un lien vers les commentaires
     * figure A L'INTERIEUR du bloc de paroles et que le repere se declenchait
     * donc au milieu du texte.
     *
     * Ensuite une recherche du <p> le plus dense : vide aussi. Le prototype en
     * Python croyait avoir trouve un paragraphe de 9360 caracteres, mais une
     * expression reguliere ne verifie pas la validite du HTML : elle courait
     * d'un <p> non ferme jusqu'a un </p> lointain, avalant navigation,
     * formulaire et boutons. Jsoup, qui respecte la norme, ferme le paragraphe
     * des qu'il rencontre un <div> — un <p> ne peut pas en contenir. Les vrais
     * paragraphes n'avaient alors presque plus de sauts.
     *
     * Le texte vit en fait dans un <div> a identifiant genere, donc impossible
     * a viser par un selecteur. On ne compte que les <br> ENFANTS DIRECTS :
     * autrement chaque conteneur parent heriterait du total de son contenu et
     * c'est le <body> qui gagnerait.
     */
    private String extraireTexte(Document page) {
        Element zone = page.selectFirst("#main");

        Element meilleur = null;
        int maxSauts = 0;
        for (Element candidat : (zone != null ? zone : page).getAllElements()) {
            int sauts = 0;
            for (Node enfant : candidat.childNodes()) {
                if (enfant instanceof Element balise && "br".equals(balise.tagName())) {
                    sauts++;
                }
            }
            if (sauts > maxSauts) {
                maxSauts = sauts;
                meilleur = candidat;
            }
        }

        // Un bloc de navigation ou de mention legale n'a pas de sauts.
        if (meilleur == null || maxSauts < 4) {
            return "";
        }
        return enTexte(meilleur);
    }

    /**
     * HTML vers texte, les <br> devenant de vrais sauts de ligne.
     *
     * Jsoup.text() ecraserait tout sur une seule ligne : il normalise les
     * blancs. Et Jsoup.clean() ne traduit pas <br> non plus : verifie, il rend
     * « aa<br>bb » en « aabb ». On insere donc un marqueur avant chaque <br>
     * avant de nettoyer.
     */
    private String enTexte(Element element) {
        Element copie = element.clone();

        // Les retours a la ligne du code source ne sont pas des sauts voulus.
        // La page en place un apres chaque <br> pour sa propre lisibilite ;
        // additionnes a nos marqueurs, ils inseraient une ligne vide entre
        // TOUTES les lignes, et les vraies separations de couplets — des <br>
        // doubles — devenaient alors indiscernables. Seul <br> fait un saut.
        for (Element noeud : copie.getAllElements()) {
            for (Node enfant : noeud.childNodes()) {
                if (enfant instanceof TextNode texte) {
                    texte.text(texte.getWholeText().replaceAll("\\s+", " "));
                }
            }
        }

        copie.select("br").before("\\n");

        // Element.html() suit les reglages de SON document, et ces reglages
        // embellissent par defaut : Jsoup met alors chaque <br> sur sa propre
        // ligne, ce qui ajoutait un saut au notre et inserait une ligne vide
        // entre TOUTES les lignes. Les vraies separations de couplets — des
        // <br> doubles — en devenaient indiscernables. D'ou ce document a
        // nous, embellissement coupe.
        Document conteneur = Document.createShell("");
        conteneur.outputSettings().prettyPrint(false);
        conteneur.body().appendChild(copie);

        String html = conteneur.body().html().replaceAll("\\\\n", "\n");
        String brut = Jsoup.clean(html, "", Safelist.none(),
                new Document.OutputSettings().prettyPrint(false));
        brut = Parser.unescapeEntities(brut, false);

        // Chaque ligne est nettoyee de l'indentation de la page, sans supprimer
        // les lignes vides : elles separent les couplets.
        StringBuilder propre = new StringBuilder();
        boolean videAvant = false;
        for (String ligne : brut.split("\n", -1)) {
            String nette = ligne.strip();
            if (!nette.isEmpty()) {
                propre.append(nette).append('\n');
                videAvant = false;
            } else if (!videAvant) {
                propre.append('\n');
                videAvant = true;
            }
        }
        return propre.toString().strip();
    }

    /** Lit la page, du cache si elle y est deja. */
    private Document lire(String url, boolean avecCache) {
        if (avecCache) {
            synchronized (cache) {
                Document connue = cache.get(url);
                if (connue != null) {
                    return connue;
                }
            }
        }

        try {
            Document page = Jsoup.connect(url)
                    .userAgent(AGENT)
                    .timeout(DELAI_MS)
                    .followRedirects(true)
                    .get();
            if (avecCache) {
                synchronized (cache) {
                    cache.put(url, page);
                }
            }
            return page;
        } catch (IOException erreur) {
            journal.warn("Site de paroles injoignable : {} ({})", url, erreur.getMessage());
            // 503 et non 500 : rien n'est casse chez nous, c'est la source qui
            // ne repond pas. L'ecran peut alors proposer la saisie a la main.
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Le site de paroles ne repond pas, saisissez le texte a la main");
        }
    }

    private void verifierAdresse(String url) {
        URI adresse;
        try {
            adresse = URI.create(url);
        } catch (IllegalArgumentException erreur) {
            throw new IllegalArgumentException("Adresse invalide");
        }
        if (!HOTE_AUTORISE.equalsIgnoreCase(adresse.getHost())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Seules les adresses de " + HOTE_AUTORISE + " sont acceptees");
        }
    }

    /** « .../hira/artiste/titre » donne {artiste, titre}, sinon null. */
    private String[] cheminChanson(String url) {
        int position = url.indexOf("/hira/");
        if (position < 0) {
            return null;
        }
        String[] parties = url.substring(position + "/hira/".length()).split("/");
        // Exactement deux segments : /hira/artiste/titre. Les sous-pages
        // « /ankafizo » et « /hevitra » en ont trois et ne sont pas des paroles.
        return parties.length == 2 && !parties[0].isBlank() && !parties[1].isBlank()
                ? new String[]{parties[0], parties[1]}
                : null;
    }

    /** « medley-hira-fisaorana » devient « Medley hira fisaorana ». */
    private String lisible(String identifiant) {
        String avecEspaces = identifiant.replace('-', ' ').strip();
        return avecEspaces.isEmpty()
                ? avecEspaces
                : Character.toUpperCase(avecEspaces.charAt(0)) + avecEspaces.substring(1);
    }

    /**
     * Ressemblance combinee du titre et de l'artiste.
     *
     * Le titre pese le double : c'est sur lui qu'on a cherche. L'artiste
     * departage, parce qu'un mot courant comme « Veloma » remonte une
     * vingtaine de chansons sans rapport.
     */
    private double pertinence(String titreCherche, String titreTrouve,
                              String artisteCherche, String artisteTrouve) {
        double surTitre = proximite(titreCherche, titreTrouve);
        if (artisteCherche == null || artisteCherche.isBlank()) {
            return surTitre;
        }
        return (2 * surTitre + proximite(artisteCherche, artisteTrouve)) / 3;
    }

    /** Proximite de deux chaines, accents et casse ignores, entre 0 et 1. */
    private double proximite(String a, String b) {
        String x = sansAccent(a);
        String y = sansAccent(b);
        if (x.isEmpty() || y.isEmpty()) {
            return 0;
        }
        if (x.equals(y)) {
            return 1;
        }
        if (x.contains(y) || y.contains(x)) {
            return 0.85;
        }

        int distance = levenshtein(x, y);
        return Math.max(0, 1.0 - (double) distance / Math.max(x.length(), y.length()));
    }

    private static String sansAccent(String valeur) {
        if (valeur == null) {
            return "";
        }
        String decompose = Normalizer.normalize(valeur, Normalizer.Form.NFKD);
        return decompose.replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", " ")
                .strip();
    }

    private static int levenshtein(String a, String b) {
        int[] precedente = new int[b.length() + 1];
        int[] courante = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            precedente[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            courante[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cout = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                courante[j] = Math.min(Math.min(courante[j - 1] + 1, precedente[j] + 1),
                        precedente[j - 1] + cout);
            }
            int[] echange = precedente;
            precedente = courante;
            courante = echange;
        }
        return precedente[b.length()];
    }
}
