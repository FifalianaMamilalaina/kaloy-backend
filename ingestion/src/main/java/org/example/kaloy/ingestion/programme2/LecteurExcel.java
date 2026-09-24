package org.example.kaloy.ingestion.programme2;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.kaloy.ingestion.util.Textes;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Lecture du tableur de metadonnees fourni par l'artiste.
 *
 * La premiere ligne sert d'en-tete : les colonnes sont reperees par leur
 * libelle, pas par leur position. Un artiste qui intervertit deux colonnes ou
 * en ajoute une ne casse donc pas l'import.
 *
 * Libelles reconnus (accents et casse ignores) :
 *   titre · album · genre · date de sortie · artiste
 */
@Component
public class LecteurExcel {

    private static final Logger log = LoggerFactory.getLogger(LecteurExcel.class);

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    public record LigneExcel(String titre, String album, String genre, String dateSortie, String artiste) {
    }

    /** @return les lignes indexees par titre normalise ; vide si illisible */
    public Map<String, LigneExcel> lire(Path fichier) {
        Map<String, LigneExcel> resultat = new LinkedHashMap<>();

        try (Workbook classeur = WorkbookFactory.create(Files.newInputStream(fichier))) {
            Sheet feuille = classeur.getSheetAt(0);
            if (feuille == null) {
                return resultat;
            }

            Row entete = feuille.getRow(feuille.getFirstRowNum());
            if (entete == null) {
                return resultat;
            }
            Map<String, Integer> colonnes = repererColonnes(entete);

            for (int i = entete.getRowNum() + 1; i <= feuille.getLastRowNum(); i++) {
                Row ligne = feuille.getRow(i);
                if (ligne == null) continue;

                String titre = texte(ligne, colonnes.get("titre"));
                if (titre == null || titre.isBlank()) {
                    continue;
                }
                LigneExcel valeur = new LigneExcel(
                        titre,
                        texte(ligne, colonnes.get("album")),
                        texte(ligne, colonnes.get("genre")),
                        texte(ligne, colonnes.get("datedesortie")),
                        texte(ligne, colonnes.get("artiste"))
                );
                resultat.put(Textes.normaliser(titre), valeur);
            }

            log.debug("{} : {} ligne(s) de metadonnees lue(s)", fichier.getFileName(), resultat.size());

        } catch (Exception e) {
            log.warn("Tableur illisible ({}) : {}", fichier.getFileName(), e.getMessage());
        }

        return resultat;
    }

    private Map<String, Integer> repererColonnes(Row entete) {
        Map<String, Integer> colonnes = new HashMap<>();
        for (int c = entete.getFirstCellNum(); c < entete.getLastCellNum(); c++) {
            Cell cellule = entete.getCell(c);
            if (cellule == null) continue;
            String libelle = Textes.normaliser(lireCellule(cellule));
            if (libelle.isBlank()) continue;

            // « date_de_sortie », « Date de sortie » et « DATEDESORTIE »
            // se normalisent tous en « datedesortie ».
            colonnes.putIfAbsent(libelle, c);
        }
        return colonnes;
    }

    private String texte(Row ligne, Integer colonne) {
        if (colonne == null) return null;
        Cell cellule = ligne.getCell(colonne);
        if (cellule == null) return null;
        String valeur = lireCellule(cellule);
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }

    private String lireCellule(Cell cellule) {
        return switch (cellule.getCellType()) {
            case STRING -> cellule.getStringCellValue();
            case BOOLEAN -> String.valueOf(cellule.getBooleanCellValue());
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cellule)) {
                    yield cellule.getLocalDateTimeCellValue().toLocalDate().format(FORMAT_DATE);
                }
                double nombre = cellule.getNumericCellValue();
                // Une annee ou une duree saisie en nombre ne doit pas
                // devenir « 2026.0 ».
                yield nombre == Math.floor(nombre)
                        ? String.format(Locale.ROOT, "%.0f", nombre)
                        : String.valueOf(nombre);
            }
            case FORMULA -> {
                try {
                    yield cellule.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cellule.getNumericCellValue());
                }
            }
            default -> null;
        };
    }
}
