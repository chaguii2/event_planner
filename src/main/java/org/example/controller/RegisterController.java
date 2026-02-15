package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.App;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.AuthService;
import org.example.util.AlertUtil;
import org.example.util.UserValidator;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<Role> roleComboBox;

    private AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(Role.ORGANIZER, Role.PARTICIPANT);
        roleComboBox.setValue(Role.PARTICIPANT);
    }

    @FXML
    private void handleRegister() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String phone = phoneField.getText().trim();
            Role role = roleComboBox.getValue();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                AlertUtil.showError("Error", "Please fill in all fields");
                return;
            }

            User user = new User(name, email, password, phone, role);
            UserValidator.validate(user);

            authService.register(user);

            AlertUtil.showInfo("Success", "Registration successful! Please login.");
            goToLogin();

        } catch (Exception e) {
            AlertUtil.showError("Registration Failed", e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            App.loadLoginScreen(stage);
        } catch (Exception e) {
            AlertUtil.showError("Error", "Could not load login screen");
        }
    }
}