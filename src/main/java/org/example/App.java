package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.session.Session;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Session.clear();
        loadLoginScreen(primaryStage);
    }

    public static void loadLoginScreen(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(App.class.getResource("/fxml/LoginView.fxml"));
        stage.setTitle("Event Planner - Connexion");
        stage.setScene(new Scene(root, 400, 300));
        stage.setResizable(false);
        stage.show();
    }

    public static void loadRegisterScreen(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(App.class.getResource("/fxml/RegisterView.fxml"));
        stage.setTitle("Event Planner - Inscription");
        stage.setScene(new Scene(root, 450, 400));
        stage.show();
    }

    public static void loadDashboard(Stage stage) throws Exception {
        String fxmlFile;
        String title;

        switch (Session.getRole()) {
            case ADMIN:
                fxmlFile = "/fxml/AdminEventView.fxml";  // Utilise AdminEventController
                title = "Event Planner - Admin Dashboard";
                break;
            case ORGANIZER:
                fxmlFile = "/fxml/OrganizerEventView.fxml";  // Utilise OrganizerEventController
                title = "Event Planner - Organisateur";
                break;
            default:
                fxmlFile = "/fxml/ParticipantEventView.fxml";  // Utilise ParticipantEventController
                title = "Event Planner - Participant";
                break;
        }

        Parent root = FXMLLoader.load(App.class.getResource(fxmlFile));
        stage.setTitle(title);
        stage.setScene(new Scene(root, 900, 700));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}