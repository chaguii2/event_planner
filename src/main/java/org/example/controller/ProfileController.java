package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.security.PasswordUtil;
import org.example.service.UserService;
import org.example.session.Session;
import org.example.util.AlertUtil;

public class ProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label roleLabel;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private UserService userService = new UserService();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = Session.getCurrentUser();
        loadUserData();
    }

    private void loadUserData() {
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());
        roleLabel.setText(currentUser.getRole().toString());
    }

    @FXML
    private void handleSave() {
        try {
            // Validation
            if (nameField.getText().trim().isEmpty()) {
                AlertUtil.showWarning("Attention", "Le nom est obligatoire");
                return;
            }

            if (phoneField.getText().trim().isEmpty()) {
                AlertUtil.showWarning("Attention", "Le téléphone est obligatoire");
                return;
            }

            // Mise à jour des informations
            currentUser.setName(nameField.getText().trim());
            currentUser.setEmail(emailField.getText().trim());
            currentUser.setPhone(phoneField.getText().trim());

            // Changement de mot de passe
            String newPassword = newPasswordField.getText();
            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPasswordField.getText())) {
                    AlertUtil.showWarning("Attention", "Les mots de passe ne correspondent pas");
                    return;
                }
                if (newPassword.length() < 6) {
                    AlertUtil.showWarning("Attention", "Le mot de passe doit contenir au moins 6 caractères");
                    return;
                }
                currentUser.setPassword(PasswordUtil.hash(newPassword));
            }

            userService.updateProfile(currentUser);
            AlertUtil.showInfo("Succès", "Profil mis à jour avec succès!");

            // Fermer la fenêtre
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de mettre à jour le profil: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}