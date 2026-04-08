package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.App;
import org.example.model.Event;
import org.example.service.EventService;
import org.example.session.Session;
import org.example.util.AlertUtil;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ParticipantEventController implements Initializable {

    @FXML
    private Label welcomeLabel;

    // Table des événements disponibles
    @FXML
    private TableView<Event> availableEventsTable;
    @FXML
    private TableColumn<Event, Integer> idColumn;
    @FXML
    private TableColumn<Event, String> titreColumn;
    @FXML
    private TableColumn<Event, String> descriptionColumn;
    @FXML
    private TableColumn<Event, Date> dateDebutColumn;
    @FXML
    private TableColumn<Event, String> lieuColumn;
    @FXML
    private TableColumn<Event, String> organisateurColumn;
    @FXML
    private TableColumn<Event, Integer> placesColumn;

    // Table de mes participations
    @FXML
    private TableView<Event> myEventsTable;
    @FXML
    private TableColumn<Event, Integer> myIdColumn;
    @FXML
    private TableColumn<Event, String> myTitreColumn;
    @FXML
    private TableColumn<Event, String> myDescriptionColumn;
    @FXML
    private TableColumn<Event, Date> myDateDebutColumn;
    @FXML
    private TableColumn<Event, String> myLieuColumn;
    @FXML
    private TableColumn<Event, String> myOrganisateurColumn;
    @FXML
    private TableColumn<Event, String> myStatutColumn;

    private EventService eventService = new EventService();
    private ObservableList<Event> availableEventsList = FXCollections.observableArrayList();
    private ObservableList<Event> myEventsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        welcomeLabel.setText("Bienvenue, " + Session.getCurrentUser().getName() + " (Participant)");

        // Initialiser les colonnes du tableau des événements disponibles
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        organisateurColumn.setCellValueFactory(new PropertyValueFactory<>("organisateurNom"));
        placesColumn.setCellValueFactory(new PropertyValueFactory<>("placesDispo"));

        // Initialiser les colonnes du tableau de mes participations
        myIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        myTitreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        myDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        myDateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        myLieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        myOrganisateurColumn.setCellValueFactory(new PropertyValueFactory<>("organisateurNom"));
        myStatutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        loadAvailableEvents();
        loadMyEvents();
    }

    @FXML
    private void loadAvailableEvents() {
        try {
            availableEventsList.setAll(eventService.getAvailableEvents());
            availableEventsTable.setItems(availableEventsList);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les événements: " + e.getMessage());
        }
    }

    @FXML
    private void loadMyEvents() {
        try {
            myEventsList.setAll(eventService.getMyParticipations());
            myEventsTable.setItems(myEventsList);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger vos participations: " + e.getMessage());
        }
    }

    @FXML
    private void handleParticiper() {
        Event selectedEvent = availableEventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un événement");
            return;
        }

        try {
            boolean success = eventService.participer(selectedEvent.getId());
            if (success) {
                AlertUtil.showInfo("Succès", "Vous êtes inscrit à l'événement!");
                loadAvailableEvents();
                loadMyEvents();
            } else {
                AlertUtil.showError("Erreur", "Impossible de s'inscrire. L'événement est peut-être complet.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de s'inscrire: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        Event selectedEvent = myEventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un événement");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirmation",
                "Voulez-vous vraiment annuler votre participation?");

        if (confirm) {
            try {
                boolean success = eventService.annulerParticipation(selectedEvent.getId());
                if (success) {
                    AlertUtil.showInfo("Succès", "Participation annulée");
                    loadAvailableEvents();
                    loadMyEvents();
                } else {
                    AlertUtil.showError("Erreur", "Impossible d'annuler la participation");
                }
            } catch (Exception e) {
                AlertUtil.showError("Erreur", "Impossible d'annuler: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLogout() {
        boolean confirm = AlertUtil.showConfirmation("Déconnexion", "Voulez-vous vraiment vous déconnecter?");
        if (confirm) {
            try {
                Session.logout();
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                App.loadLoginScreen(stage);
            } catch (Exception e) {
                AlertUtil.showError("Erreur", "Impossible de se déconnecter");
            }
        }
    }
}