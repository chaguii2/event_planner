package org.example.model;

import java.util.Date;

public class Notification {
    private int id;
    private String type; // "INSCRIPTION", "RAPPEL", "ANNULATION", "MODIFICATION", "CONFIRMATION"
    private String message;
    private Date dateEnvoi;
    private boolean lu;
    private int idDestinataire;
    private int idEvenement; // Optionnel: lié à un événement spécifique
    private String lienAction; // Optionnel: lien pour action rapide

    public Notification() {}

    public Notification(String type, String message, int idDestinataire) {
        this.type = type;
        this.message = message;
        this.idDestinataire = idDestinataire;
        this.dateEnvoi = new Date();
        this.lu = false;
    }

    public Notification(String type, String message, int idDestinataire, int idEvenement) {
        this(type, message, idDestinataire);
        this.idEvenement = idEvenement;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(Date dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public int getIdDestinataire() { return idDestinataire; }
    public void setIdDestinataire(int idDestinataire) { this.idDestinataire = idDestinataire; }

    public int getIdEvenement() { return idEvenement; }
    public void setIdEvenement(int idEvenement) { this.idEvenement = idEvenement; }

    public String getLienAction() { return lienAction; }
    public void setLienAction(String lienAction) { this.lienAction = lienAction; }

    // Méthodes métier
    public void marquerCommeLu() {
        this.lu = true;
    }

    public boolean estLu() {
        return lu;
    }

    public boolean estRecente() {
        long diff = new Date().getTime() - dateEnvoi.getTime();
        long diffHours = diff / (60 * 60 * 1000);
        return diffHours < 24;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + message + " (" + dateEnvoi + ")";
    }
}