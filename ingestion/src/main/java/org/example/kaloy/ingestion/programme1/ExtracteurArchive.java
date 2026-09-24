package org.example.kaloy.ingestion.programme1;

import org.example.kaloy.ingestion.config.ProprietesIngestion;
import org.example.kaloy.ingestion.modele.ArchiveExtraite;
import org.example.kaloy.ingestion.modele.FichierExtrait;
import org.example.kaloy.ingestion.modele.TypeFichier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extraction d'une archive et routage des fichiers par type.
 */
@Component
public class ExtracteurArchive {

    private static final Logger log = LoggerFactory.getLogger(ExtracteurArchive.class);

    /** Garde-fou contre une archive « zip bomb » : 500 Mo decompresses. */
    private static final long TAILLE_MAX_DECOMPRESSEE = 500L * 1024L * 1024L;

    private final ProprietesIngestion proprietes;

    public ExtracteurArchive(ProprietesIngestion proprietes) {
        this.proprietes = proprietes;
    }

    public ArchiveExtraite extraire(Path archive, String nomArtiste) throws IOException {
        Path racineTravail = Paths.get(proprietes.getTravail()).toAbsolutePath().normalize();
        Path destination = racineTravail.resolve(
                UUID.randomUUID().toString().replace("-", ""));
        Files.createDirectories(destination);

        List<FichierExtrait> fichiers = new ArrayList<>();
        long totalDecompresse = 0;

        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entree;
            while ((entree = zip.getNextEntry()) != null) {
                if (entree.isDirectory()) {
                    zip.closeEntry();
                    continue;
                }

                Path cible = resoudreEnSecurite(destination, entree.getName());
                if (cible == null) {
                    // Zip Slip : une entree tentant de sortir du dossier
                    // d'extraction est ignoree, et l'archive signalee.
                    log.warn("Entree ignoree (chemin hors du dossier d'extraction) : {}",
                            entree.getName());
                    zip.closeEntry();
                    continue;
                }

                Files.createDirectories(cible.getParent());
                long ecrits = copier(zip, cible);
                totalDecompresse += ecrits;
                if (totalDecompresse > TAILLE_MAX_DECOMPRESSEE) {
                    throw new IOException("Archive trop volumineuse une fois decompressee");
                }

                String nomOriginal = Paths.get(entree.getName()).getFileName().toString();
                TypeFichier type = typeDe(nomOriginal);
                if (type == TypeFichier.INCONNU) {
                    log.debug("Fichier ignore, type non gere : {}", nomOriginal);
                } else {
                    fichiers.add(new FichierExtrait(cible.toString(), nomOriginal, type));
                }
                zip.closeEntry();
            }
        }

        return new ArchiveExtraite(
                archive.getFileName().toString(),
                archive.toString(),
                nomArtiste,
                destination.toString(),
                fichiers
        );
    }

    private long copier(InputStream source, Path cible) throws IOException {
        Files.copy(source, cible, StandardCopyOption.REPLACE_EXISTING);
        return Files.size(cible);
    }

    /**
     * Protection Zip Slip.
     *
     * Une archive malveillante peut contenir une entree nommee
     * « ../../../etc/passwd » : resolue naivement, elle ecrit hors du dossier
     * d'extraction. On normalise donc le chemin obtenu et on verifie qu'il
     * commence toujours par le dossier de destination.
     *
     * @return le chemin sur, ou null si l'entree tente de sortir
     */
    static Path resoudreEnSecurite(Path destination, String nomEntree) {
        Path racine = destination.toAbsolutePath().normalize();
        Path resolu = racine.resolve(nomEntree).normalize();
        return resolu.startsWith(racine) ? resolu : null;
    }

    /** Routage par extension, comme prevu au cahier des charges. */
    static TypeFichier typeDe(String nomFichier) {
        String nom = nomFichier.toLowerCase(Locale.ROOT);
        if (nom.endsWith(".mp3")) return TypeFichier.AUDIO;
        if (nom.endsWith(".mp4")) return TypeFichier.VIDEO;
        if (nom.endsWith(".jpg") || nom.endsWith(".jpeg") || nom.endsWith(".png")) return TypeFichier.IMAGE;
        if (nom.endsWith(".pdf")) return TypeFichier.PAROLES;
        if (nom.endsWith(".xlsx") || nom.endsWith(".xls")) return TypeFichier.TABLEUR;
        return TypeFichier.INCONNU;
    }
}
