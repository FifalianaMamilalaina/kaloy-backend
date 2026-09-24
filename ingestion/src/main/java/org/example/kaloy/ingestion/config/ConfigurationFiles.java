package org.example.kaloy.ingestion.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Les trois files qui reliente les quatre programmes.
 *
 * Elles sont durables : si le pipeline s'arrete entre deux etapes, les
 * messages en attente survivent au redemarrage du courtier. C'est ce qui
 * permet de relancer un programme sans reprendre l'ingestion depuis le debut.
 */
@Configuration
public class ConfigurationFiles {

    public static final String FILE_EXTRAITS = "kaloy.ingestion.extraits";
    public static final String FILE_METADONNEES = "kaloy.ingestion.metadonnees";
    public static final String FILE_PUBLIES = "kaloy.ingestion.publies";

    @Bean
    public Queue fileExtraits() {
        return QueueBuilder.durable(FILE_EXTRAITS).build();
    }

    @Bean
    public Queue fileMetadonnees() {
        return QueueBuilder.durable(FILE_METADONNEES).build();
    }

    @Bean
    public Queue filePublies() {
        return QueueBuilder.durable(FILE_PUBLIES).build();
    }

    /** Les messages circulent en JSON, lisibles depuis la console RabbitMQ. */
    @Bean
    public MessageConverter convertisseurJson(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory fabrique, MessageConverter convertisseur) {
        RabbitTemplate modele = new RabbitTemplate(fabrique);
        modele.setMessageConverter(convertisseur);
        return modele;
    }
}
