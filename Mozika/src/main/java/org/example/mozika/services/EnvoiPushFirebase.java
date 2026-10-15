package org.example.mozika.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import org.example.mozika.models.User;
import org.example.mozika.models.UserDevice;
import org.example.mozika.repositories.UserDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Envoi reel des notifications push, par Firebase Cloud Messaging.
 *
 * Classe sans annotation : c'est ConfigurationPush qui l'instancie, et
 * seulement si la cle de compte de service est lisible. Un @Service aurait
 * fait construire ce bean dans tous les cas, et le constructeur aurait leve
 * une IOException sur un poste sans cle — faisant echouer le demarrage du
 * contexte Spring entier, pas seulement le push.
 */
public class EnvoiPushFirebase implements EnvoiPush {

    private static final Logger journal = LoggerFactory.getLogger(EnvoiPushFirebase.class);

    private final UserDeviceRepository appareilRepository;
    private final FirebaseMessaging messagerie;

    public EnvoiPushFirebase(UserDeviceRepository appareilRepository,
                             @Value("${app.firebase.fichier-cle}") String cheminCle)
            throws IOException {
        this.appareilRepository = appareilRepository;
        this.messagerie = initialiser(cheminCle);
    }

    /**
     * Initialise Firebase une seule fois pour toute l'application.
     *
     * FirebaseApp est un singleton global a la machine virtuelle : appeler
     * initializeApp deux fois leve une exception. Le controle sur getApps()
     * rend la classe sure au rechargement a chaud, ou le contexte Spring est
     * recree sans que la JVM redemarre.
     */
    private FirebaseMessaging initialiser(String cheminCle) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseMessaging.getInstance();
        }

        try (FileInputStream cle = new FileInputStream(cheminCle)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(cle))
                    .build();
            FirebaseApp.initializeApp(options);
        }
        journal.info("Firebase initialise, envoi push actif");
        return FirebaseMessaging.getInstance();
    }

    /**
     * Vrai si la cle existe et est lisible.
     *
     * Methode statique, appelee depuis la configuration avant que le bean soit
     * construit : sans ce controle, un fichier manquant ferait echouer le
     * demarrage du contexte Spring entier.
     */
    public static boolean cleDisponible(String cheminCle) {
        if (cheminCle == null || cheminCle.isBlank()) {
            return false;
        }
        return Files.isReadable(Path.of(cheminCle));
    }

    @Override
    @Transactional
    public void envoyer(User destinataire, String titre, String corps, String type, Long idCible) {
        List<UserDevice> appareils = appareilRepository.findByUseridUsers(destinataire);
        if (appareils.isEmpty()) {
            return;
        }

        // Les donnees accompagnent la notification pour que l'application sache
        // ou aller quand on appuie dessus. Tout est en chaines : FCM n'accepte
        // que ca dans le champ data.
        Map<String, String> donnees = new HashMap<>();
        donnees.put("type", type);
        if (idCible != null) {
            donnees.put("idCible", String.valueOf(idCible));
        }

        for (UserDevice appareil : appareils) {
            envoyerA(appareil, titre, corps, donnees);
        }
    }

    /**
     * Envoie a un appareil, et le supprime si son jeton n'est plus valide.
     *
     * Les jetons FCM expirent : application desinstallee, donnees effacees,
     * trop longtemps sans ouverture. Sans cette purge la table grossirait
     * indefiniment, et chaque envoi perdrait du temps sur des appareils morts.
     *
     * UNREGISTERED signifie que le jeton a ete revoque. INVALID_ARGUMENT
     * signifie qu'il est malforme — dans les deux cas il ne redeviendra jamais
     * valide, donc on le retire. Les autres erreurs, comme une panne reseau,
     * sont seulement tracees : le jeton est peut-etre encore bon.
     */
    private void envoyerA(UserDevice appareil, String titre, String corps,
                          Map<String, String> donnees) {
        Message message = Message.builder()
                .setToken(appareil.getToken())
                .setNotification(Notification.builder()
                        .setTitle(titre)
                        .setBody(corps)
                        .build())
                .putAllData(donnees)
                .build();

        try {
            messagerie.send(message);
        } catch (FirebaseMessagingException e) {
            MessagingErrorCode code = e.getMessagingErrorCode();
            if (code == MessagingErrorCode.UNREGISTERED
                    || code == MessagingErrorCode.INVALID_ARGUMENT) {
                journal.info("Jeton revoque, appareil retire : plateforme={}, code={}",
                        appareil.getPlatform(), code);
                appareilRepository.delete(appareil);
            } else {
                // On ne propage pas : la notification est deja en base, et
                // l'utilisateur la verra en ouvrant l'application. Faire
                // echouer l'acceptation d'une invitation parce que Firebase est
                // indisponible serait hors de proportion.
                journal.warn("Echec de l'envoi push : code={}, message={}", code, e.getMessage());
            }
        }
    }

    @Override
    public boolean estActif() {
        return true;
    }
}
