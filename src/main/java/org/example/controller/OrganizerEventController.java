package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.App;
import org.example.model.Event;
import org.example.service.EventService;
import org.example.session.Session;
import org.example.util.AlertUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class OrganizerEventController implements Initializable {

    @FXML
    private Label welcomeLabel;

    // Table des événements
    @FXML
    private TableView<Event> eventsTable;
    @FXML
    private TableColumn<Event, Integer> idColumn;
    @FXML
    private TableColumn<Event, String> titreColumn;
    @FXML
    private TableColumn<Event, String> descriptionColumn;
    @FXML
    private TableColumn<Event, Date> dateDebutColumn;
    @FXML
    private TableColumn<Event, Date> dateFinColumn;
    @FXML
    private TableColumn<Event, String> lieuColumn;
    @FXML
    private TableColumn<Event, Integer> placesColumn;
    @FXML
    private TableColumn<Event, String> statutColumn;

    // Champs du formulaire
    @FXML
    private TextField titreField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private TextField lieuField;
    @FXML
    private Spinner<Integer> placesSpinner;
    @FXML
    private Label eventIdLabel;

    private EventService eventService = new EventService();
    private ObservableList<Event> eventList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        welcomeLabel.setText("Bienvenue, " + Session.getCurrentUser().getName() + " (Organisateur)");

        // Initialiser les colonnes du tableau
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        placesColumn.setCellValueFactory(new PropertyValueFactory<>("placesDispo"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configurer le spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 50);
        placesSpinner.setValueFactory(valueFactory);

        // Ajouter un listener pour la sélection dans le tableau
        eventsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showEventDetails(newSelection);
                    }
                }
        );

        // Ajouter des contraintes aux DatePickers
        setupDatePickers();

        loadEvents();
    }

    /**
     * Configure les DatePickers avec des contraintes
     */
    @FXML
    private void refreshTable() {
        eventsTable.refresh();
        // Forcer la mise à jour des colonnes
        eventsTable.getColumns().get(0).setVisible(false);
        eventsTable.getColumns().get(0).setVisible(true);
    }
    private void setupDatePickers() {
        // La date de début ne peut pas être dans le passé
        dateDebutPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Désactiver les dates passées (avant aujourd'hui)
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffcccc;"); // Rouge clair pour les dates désactivées
                }
            }
        });

        // La date de fin dépend de la date de début
        dateFinPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate debut = dateDebutPicker.getValue();

                // Désactiver les dates avant la date de début
                if (debut != null && date.isBefore(debut)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffcccc;");
                }
                // Désactiver les dates passées
                else if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffcccc;");
                }
            }
        });

        // Mettre à jour le DatePicker de fin quand la date de début change
        dateDebutPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            dateFinPicker.setValue(null); // Réinitialiser la date de fin
            dateFinPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (newDate != null && date.isBefore(newDate)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffcccc;");
                    } else if (date.isBefore(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffcccc;");
                    }
                }
            });
        });
    }

    @FXML
    private void loadEvents() {
        try {
            eventList.setAll(eventService.getMyEvents());
            eventsTable.setItems(eventList);
            eventsTable.refresh(); // ← Ajouter ceci

            // 🔍 Debug
            System.out.println("=== Mes événements ===");
            for (Event e : eventList) {
                System.out.println("ID: " + e.getId() +
                        ", Titre: " + e.getTitre() +
                        ", Statut: " + e.getStatut() +
                        ", Places dispo: " + e.getPlacesDispo());
            }

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showEventDetails(Event event) {
        titreField.setText(event.getTitre());
        descriptionField.setText(event.getDescription());

        if (event.getDateDebut() != null) {
            dateDebutPicker.setValue(event.getDateDebut().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        }
        if (event.getDateFin() != null) {
            dateFinPicker.setValue(event.getDateFin().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        }

        lieuField.setText(event.getLieu());
        placesSpinner.getValueFactory().setValue(event.getPlacesMax());
        eventIdLabel.setText(String.valueOf(event.getId()));
    }

    @FXML
    private void handleNewEvent() {
        clearFields();
        eventIdLabel.setText("");
    }
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProfileView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mon Profil");
            stage.setScene(new Scene(root, 500, 450));
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.initOwner(welcomeLabel.getScene().getWindow());
            stage.showAndWait();

            // Rafraîchir le nom après modification
            welcomeLabel.setText("Bienvenue, " + Session.getCurrentUser().getName());

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible d'ouvrir le profil: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleCreateEvent() {
        if (!validateFields()) return;

        try {
            Event event = new Event();
            event.setTitre(titreField.getText().trim());
            event.setDescription(descriptionField.getText().trim());

            LocalDate debut = dateDebutPicker.getValue();
            LocalDate fin = dateFinPicker.getValue();

            // Convertir LocalDate en Date (avec l'heure à 00:00)
            event.setDateDebut(Date.from(debut.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            event.setDateFin(Date.from(fin.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            event.setLieu(lieuField.getText().trim());
            event.setPlacesMax(placesSpinner.getValue());
            event.setPlacesDispo(placesSpinner.getValue());
            event.setIdOrganisateur(Session.getCurrentUser().getId());

            eventService.createEvent(event);
            AlertUtil.showInfo("Succès", "Événement créé avec succès!");
            loadEvents();
            clearFields();

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de créer l'événement: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateEvent() {
        if (eventIdLabel.getText().isEmpty()) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un événement à modifier");
            return;
        }

        if (!validateFields()) return;

        try {
            Event event = new Event();
            event.setId(Integer.parseInt(eventIdLabel.getText()));
            event.setTitre(titreField.getText().trim());
            event.setDescription(descriptionField.getText().trim());

            LocalDate debut = dateDebutPicker.getValue();
            LocalDate fin = dateFinPicker.getValue();

            if (debut.isBefore(LocalDate.now())) {
                AlertUtil.showWarning("Attention", "Impossible de modifier un événement avec une date de début passée");
                return;
            }

            event.setDateDebut(Date.from(debut.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            event.setDateFin(Date.from(fin.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            event.setLieu(lieuField.getText().trim());
            event.setPlacesMax(placesSpinner.getValue());
            event.setPlacesDispo(placesSpinner.getValue()); // ⚠️ Important: remettre placesDispo = placesMax
            event.setIdOrganisateur(Session.getCurrentUser().getId());

            eventService.updateEvent(event);

            // ✅ FORCER LE RAFRAÎCHISSEMENT
            loadEvents();  // Recharger la liste
            clearFields(); // Vider les champs
            eventsTable.refresh(); // Forcer le rafraîchissement du tableau

            AlertUtil.showInfo("Succès", "Événement mis à jour avec succès!");

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de mettre à jour l'événement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteEvent() {
        if (eventIdLabel.getText().isEmpty()) {
            AlertUtil.showWarning("Attention", "Veuillez sélectionner un événement à supprimer");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirmation",
                "Voulez-vous vraiment supprimer cet événement?");

        if (confirm) {
            try {
                int eventId = Integer.parseInt(eventIdLabel.getText());
                eventService.deleteEvent(eventId);
                AlertUtil.showInfo("Succès", "Événement supprimé avec succès!");
                loadEvents();
                clearFields();

            } catch (Exception e) {
                AlertUtil.showError("Erreur", "Impossible de supprimer l'événement: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        eventsTable.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        lieuField.clear();
        placesSpinner.getValueFactory().setValue(50);
        eventIdLabel.setText("");
    }

    /**
     * Validation complète des champs
     */
    private boolean validateFields() {
        // Vérifier les champs obligatoires
        if (titreField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Attention", "Le titre est obligatoire");
            titreField.requestFocus();
            return false;
        }

        if (dateDebutPicker.getValue() == null) {
            AlertUtil.showWarning("Attention", "La date de début est obligatoire");
            dateDebutPicker.requestFocus();
            return false;
        }

        if (dateFinPicker.getValue() == null) {
            AlertUtil.showWarning("Attention", "La date de fin est obligatoire");
            dateFinPicker.requestFocus();
            return false;
        }

        if (lieuField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Attention", "Le lieu est obligatoire");
            lieuField.requestFocus();
            return false;
        }

        // Contrôles de dates
        LocalDate aujourdhui = LocalDate.now();
        LocalDate debut = dateDebutPicker.getValue();
        LocalDate fin = dateFinPicker.getValue();

        // Vérifier que la date de début n'est pas dans le passé
        if (debut.isBefore(aujourdhui)) {
            AlertUtil.showWarning("Attention", "Impossible de créer un événement avec une date de début passée");
            dateDebutPicker.requestFocus();
            return false;
        }

        // Vérifier que la date de fin n'est pas dans le passé
        if (fin.isBefore(aujourdhui)) {
            AlertUtil.showWarning("Attention", "Impossible de créer un événement avec une date de fin passée");
            dateFinPicker.requestFocus();
            return false;
        }

        // Vérifier que la date de fin est après la date de début
        if (fin.isBefore(debut)) {
            AlertUtil.showWarning("Attention", "La date de fin doit être après la date de début");
            dateFinPicker.requestFocus();
            return false;
        }

        // Vérifier que la date de fin n'est pas le même jour que la date de début (optionnel)
        if (fin.isEqual(debut)) {
            boolean confirm = AlertUtil.showConfirmation("Confirmation",
                    "L'événement commence et se termine le même jour. Voulez-vous continuer?");
            if (!confirm) {
                return false;
            }
        }

        return true;
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