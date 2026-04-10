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
import org.example.model.Role;
import org.example.model.User;
import org.example.service.UserService;
import org.example.session.Session;
import org.example.util.AlertUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, Role> roleColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private ComboBox<Role> filterComboBox;

    private UserService userService = new UserService();
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        welcomeLabel.setText("Welcome, " + Session.getCurrentUser().getName() + " (Admin)");

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Initialize combo boxes
        roleComboBox.getItems().setAll(Role.values());
        filterComboBox.getItems().setAll(Role.values());
        filterComboBox.getItems().add(0, null);
        filterComboBox.setPromptText("All Users");

        loadUsers();

        // Add selection listener
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showUserDetails(newSelection);
                    }
                }
        );
    }

    private void loadUsers() {
        try {
            userList.setAll(userService.findAll());
            userTable.setItems(userList);
        } catch (Exception e) {
            AlertUtil.showError("Error", "Could not load users: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilter() {
        Role selectedRole = filterComboBox.getValue();
        try {
            if (selectedRole == null) {
                userList.setAll(userService.findAll());
            } else {
                userList.setAll(userService.getByRole(selectedRole));
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Could not filter users: " + e.getMessage());
        }
    }

    private void showUserDetails(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        roleComboBox.setValue(user.getRole());
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
    private void handleUpdate() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Warning", "Please select a user to update");
            return;
        }

        try {
            selectedUser.setName(nameField.getText().trim());
            selectedUser.setEmail(emailField.getText().trim());
            selectedUser.setPhone(phoneField.getText().trim());
            selectedUser.setRole(roleComboBox.getValue());

            userService.update(selectedUser);
            loadUsers();
            AlertUtil.showInfo("Success", "User updated successfully");

        } catch (Exception e) {
            AlertUtil.showError("Error", "Could not update user: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtil.showWarning("Warning", "Please select a user to delete");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirm Delete",
                "Are you sure you want to delete " + selectedUser.getName() + "?");

        if (confirm) {
            try {
                userService.delete(selectedUser.getId());
                loadUsers();
                clearFields();
                AlertUtil.showInfo("Success", "User deleted successfully");

            } catch (Exception e) {
                AlertUtil.showError("Error", "Could not delete user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        userTable.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        roleComboBox.setValue(null);
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