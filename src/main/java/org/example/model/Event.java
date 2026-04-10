package org.example.model;

import java.util.Date;

public class Event {
    private int id;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String lieu;
    private int placesMax;
    private int placesDispo;
    private String statut;
    private int idOrganisateur;
    private String organisateurNom; // Pour affichage

    public Event() {}

    public Event(String titre, String description, Date dateDebut, Date dateFin,
                 String lieu, int placesMax, int idOrganisateur) {
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.placesMax = placesMax;
        this.placesDispo = placesMax;
        this.idOrganisateur = idOrganisateur;
        this.statut = "À VENIR";
    }

    // Méthodes métier
    public boolean estLimitePlaces() {
        return placesMax > 0;
    }

    public boolean estComplet() {
        return placesDispo <= 0;
    }

    public void mettreAJourPlaces() {
        if (!estComplet()) {
            this.placesDispo--;
        }
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public int getPlacesMax() { return placesMax; }
    public void setPlacesMax(int placesMax) { this.placesMax = placesMax; }

    public int getPlacesDispo() { return placesDispo; }
    public void setPlacesDispo(int placesDispo) { this.placesDispo = placesDispo; }

    public String getStatut() {
        Date now = new Date();

        if (now.after(dateFin)) {
            return "TERMINÉ";
        }
        if (now.before(dateDebut)) {
            return "À VENIR";
        }
        if (placesDispo <= 0) {
            return "COMPLET";
        }
        return "EN COURS";
    }

    public int getIdOrganisateur() { return idOrganisateur; }
    public void setIdOrganisateur(int idOrganisateur) { this.idOrganisateur = idOrganisateur; }

    public String getOrganisateurNom() { return organisateurNom; }
    public void setOrganisateurNom(String organisateurNom) { this.organisateurNom = organisateurNom; }

    @Override
    public String toString() {
        return titre + " - " + lieu + " (" + placesDispo + "/" + placesMax + " places)";
    }
}