package org.example.kaloy.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Pipeline d'ingestion des contenus artistes.
 *
 * Quatre programmes, decouples par RabbitMQ :
 *
 *   depot/*.zip
 *      -> [P1] surveillance et extraction   -> file "extraits"
 *      -> [P2] lecture des metadonnees      -> file "metadonnees"
 *      -> [P3] publication via l'API Kaloy  -> file "publies"
 *      -> [P4] nettoyage du depot
 *
 * Regle qui traverse tout le pipeline : un fichier en echec n'est jamais
 * supprime du depot. Il reste visible et corrigible, et sa trace est
 * consignee dans content_submissions avec le motif de l'echec.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class IngestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngestionApplication.class, args);
    }
}
