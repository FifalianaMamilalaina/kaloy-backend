package org.example.kaloy.ingestion.notification;

import org.example.kaloy.ingestion.config.ProprietesIngestion;
import org.example.kaloy.ingestion.modele.ResultatPublication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Retour en cas d'echec d'import, comme prevu au cahier des charges :
 * un message listant les fichiers concernes.
 *
 * L'envoi est **desactive par defaut** (kaloy.ingestion.notifications.actives).
 * La configuration SMTP pointe sur un vrai compte : on ne veut pas envoyer de
 * courriels reels a chaque test. Desactive, le motif est journalise, ce qui
 * suffit a verifier que le message aurait ete envoye.
 */
@Component
public class NotificateurEchec {

    private static final Logger log = LoggerFactory.getLogger(NotificateurEchec.class);

    private final ProprietesIngestion proprietes;
    private final JavaMailSender expediteur;

    public NotificateurEchec(ProprietesIngestion proprietes, JavaMailSender expediteur) {
        this.proprietes = proprietes;
        this.expediteur = expediteur;
    }

    public void signaler(ResultatPublication resultat) {
        String corps = redigerMessage(resultat);

        if (!proprietes.getNotifications().isActives()) {
            log.info("Notification desactivee — message qui aurait ete envoye :\n{}", corps);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(proprietes.getNotifications().getDestinataire());
            message.setSubject("Kaloy — echec d'import : " + resultat.nomArchive());
            message.setText(corps);
            expediteur.send(message);
            log.info("Notification d'echec envoyee pour {}", resultat.nomArchive());
        } catch (Exception e) {
            // Un envoi qui echoue ne doit pas interrompre le pipeline.
            log.error("Notification impossible pour {} : {}", resultat.nomArchive(), e.getMessage());
        }
    }

    private String redigerMessage(ResultatPublication resultat) {
        StringBuilder texte = new StringBuilder();
        texte.append("L'import de l'archive « ").append(resultat.nomArchive())
                .append(" » n'a pas abouti.\n\n");
        texte.append("Artiste indique : ").append(resultat.nomArtiste()).append('\n');
        texte.append("Chansons publiees : ").append(resultat.nombrePubliees()).append("\n\n");
        texte.append("Motifs :\n");
        resultat.erreurs().forEach(erreur -> texte.append("  - ").append(erreur).append('\n'));
        texte.append("\nL'archive est restee dans le dossier de depot. ")
                .append("Corrigez les points ci-dessus puis redeposez-la.\n");
        return texte.toString();
    }
}
