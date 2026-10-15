package org.example.mozika.config;

import org.example.mozika.repositories.UserDeviceRepository;
import org.example.mozika.services.EnvoiPush;
import org.example.mozika.services.EnvoiPushFirebase;
import org.example.mozika.services.EnvoiPushInactif;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Choisit l'implementation d'envoi push selon la presence de la cle Firebase.
 *
 * La decision tient en un seul endroit, et elle est lisible. Les annotations
 * conditionnelles de Spring auraient pu s'en charger, mais aucune ne sait
 * verifier qu'un fichier est lisible : @ConditionalOnProperty teste la
 * presence d'une propriete, pas celle du fichier qu'elle designe. Un chemin
 * configure vers un fichier absent aurait donc produit le mauvais bean.
 *
 * Un seul bean EnvoiPush existe a la fois, et le journal dit lequel au
 * demarrage — ce qui evite de se demander pourquoi rien n'arrive sur le
 * telephone.
 */
@Configuration
public class ConfigurationPush {

    private static final Logger journal = LoggerFactory.getLogger(ConfigurationPush.class);

    @Bean
    public EnvoiPush envoiPush(UserDeviceRepository appareilRepository,
                               @Value("${app.firebase.fichier-cle:}") String cheminCle)
            throws IOException {

        if (EnvoiPushFirebase.cleDisponible(cheminCle)) {
            journal.info("Cle Firebase trouvee : {}", cheminCle);
            return new EnvoiPushFirebase(appareilRepository, cheminCle);
        }

        journal.warn("Cle Firebase absente ou illisible ({}) : les notifications push "
                + "ne seront pas envoyees. Les notifications dans l'application "
                + "fonctionnent normalement.", cheminCle.isBlank() ? "non configuree" : cheminCle);
        return new EnvoiPushInactif(appareilRepository);
    }
}
