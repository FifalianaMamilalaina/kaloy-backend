package org.example.kaloy.ingestion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration du pipeline, lue depuis application.yml sous kaloy.ingestion.
 */
@ConfigurationProperties(prefix = "kaloy.ingestion")
public class ProprietesIngestion {

    /** Dossier surveille, ou les artistes deposent leurs archives. */
    private String depot;

    /** Dossier de travail ou les archives sont extraites. */
    private String travail;

    private long intervalleScanMs = 5000;

    private Api api = new Api();
    private Notifications notifications = new Notifications();

    public static class Api {
        private String baseUrl;
        private String email;
        private String motDePasse;

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    }

    public static class Notifications {
        private boolean actives;
        private String destinataire;

        public boolean isActives() { return actives; }
        public void setActives(boolean actives) { this.actives = actives; }
        public String getDestinataire() { return destinataire; }
        public void setDestinataire(String destinataire) { this.destinataire = destinataire; }
    }

    public String getDepot() { return depot; }
    public void setDepot(String depot) { this.depot = depot; }
    public String getTravail() { return travail; }
    public void setTravail(String travail) { this.travail = travail; }
    public long getIntervalleScanMs() { return intervalleScanMs; }
    public void setIntervalleScanMs(long intervalleScanMs) { this.intervalleScanMs = intervalleScanMs; }
    public Api getApi() { return api; }
    public void setApi(Api api) { this.api = api; }
    public Notifications getNotifications() { return notifications; }
    public void setNotifications(Notifications notifications) { this.notifications = notifications; }
}
