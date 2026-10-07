package org.example.mozika.services;

import org.example.mozika.dto.organisation.NouveauLieuRequest;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.Venue;
import org.example.mozika.repositories.VenueRepository;
import org.springframework.stereotype.Service;

/**
 * Resolution du lieu d'un concert : une salle connue, ou une nouvelle creee au
 * passage.
 *
 * Extrait en service a part parce que deux chemins y mènent — le creneau d'un
 * evenement et le concert declare seul — et que la regle doit etre la meme des
 * deux cotes.
 */
@Service
public class ServiceLieux {

    private final VenueRepository lieuRepository;

    public ServiceLieux(VenueRepository lieuRepository) {
        this.lieuRepository = lieuRepository;
    }

    /**
     * Exige l'un ou l'autre, et pas les deux : arbitrer entre deux intentions
     * contradictoires dans la meme requete n'aurait pas de bonne reponse.
     *
     * @param obligatoire false pour autoriser l'absence de lieu
     */
    public Venue resoudre(Long idLieu, NouveauLieuRequest nouveauLieu, boolean obligatoire) {
        boolean lieuExistant = idLieu != null;
        boolean lieuACreer = nouveauLieu != null
                && nouveauLieu.getNom() != null
                && !nouveauLieu.getNom().isBlank();

        if (lieuExistant && lieuACreer) {
            throw new IllegalArgumentException(
                    "Choisissez un lieu existant ou creez-en un, pas les deux");
        }
        if (!lieuExistant && !lieuACreer) {
            if (obligatoire) {
                throw new IllegalArgumentException("Le lieu du concert est obligatoire");
            }
            return null;
        }

        if (lieuExistant) {
            return lieuRepository.findById(idLieu)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Lieu introuvable avec l'id : " + idLieu));
        }

        Venue lieu = new Venue();
        lieu.setName(nouveauLieu.getNom().trim());
        lieu.setLocation(nouveauLieu.getLocalisation() == null
                ? null : nouveauLieu.getLocalisation().trim());
        return lieuRepository.save(lieu);
    }
}
