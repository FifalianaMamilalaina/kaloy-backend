package org.example.kaloy.ingestion.programme2;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Extraction du texte d'un PDF de paroles.
 *
 * Le texte est stocke tel quel dans songs.lyrics : conserver le PDF sans le
 * lire n'apporterait rien a l'application, qui affiche des paroles et non un
 * document.
 */
@Component
public class LecteurParolesPdf {

    private static final Logger log = LoggerFactory.getLogger(LecteurParolesPdf.class);

    public String lire(Path fichier) {
        try (PDDocument document = Loader.loadPDF(fichier.toFile())) {
            String texte = new PDFTextStripper().getText(document);
            return (texte == null || texte.isBlank()) ? null : texte.trim();
        } catch (Exception e) {
            log.warn("Paroles illisibles dans {} : {}", fichier.getFileName(), e.getMessage());
            return null;
        }
    }
}
