package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.App;
import org.example.model.User;
import org.example.service.AuthService;
import org.example.util.AlertUtil;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtil.showError("Error", "Please fill in all fields");
            return;
        }

        try {
            User user = authService.login(email, password);
            AlertUtil.showInfo("Success", "Welcome " + user.getName() + "!");

            Stage stage = (Stage) emailField.getScene().getWindow();
            App.loadDashboard(stage);

        } catch (Exception e) {
            AlertUtil.showError("Login Failed", e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            App.loadRegisterScreen(stage);
        } catch (Exception e) {
            AlertUtil.showError("Error", "Could not load registration screen");
        }
    }
}