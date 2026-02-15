package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.App;
import org.example.session.Session;
import org.example.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class OrganizerController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        welcomeLabel.setText("Welcome, " + Session.getCurrentUser().getName() + " (Organizer)");
    }

    @FXML
    private void handleLogout() {
        boolean confirm = AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?");
        if (confirm) {
            try {
                Session.logout();
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                App.loadLoginScreen(stage);
            } catch (Exception e) {
                AlertUtil.showError("Error", "Could not logout");
            }
        }
    }
}
