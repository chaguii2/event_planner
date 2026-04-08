package org.example.service;

import org.example.dao.NotificationDAO;
import org.example.model.Notification;
import org.example.model.User;
import org.example.session.Session;

import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO = new NotificationDAO();

    // Envoyer une notification à un utilisateur
    public void envoyerNotification(int destinataireId, String type, String message) throws SQLException {
        Notification notification = new Notification(type, message, destinataireId);
        notificationDAO.create(notification);
    }

    public void envoyerNotification(int destinataireId, String type, String message, int idEvenement) throws SQLException {
        Notification notification = new Notification(type, message, destinataireId, idEvenement);
        notificationDAO.create(notification);
    }

    // Envoyer une notification à tous les participants d'un événement
    public void envoyerNotificationATous(List<User> participants, String type, String message, int idEvenement) throws SQLException {
        for (User participant : participants) {
            envoyerNotification(participant.getId(), type, message, idEvenement);
        }
    }

    // Obtenir mes notifications
    public List<Notification> getMesNotifications() throws SQLException {
        if (!Session.isLoggedIn()) {
            throw new RuntimeException("Vous devez être connecté");
        }
        return notificationDAO.findByDestinataire(Session.getCurrentUser().getId());
    }

    // Obtenir mes notifications non lues
    public List<Notification> getNotificationsNonLues() throws SQLException {
        if (!Session.isLoggedIn()) {
            throw new RuntimeException("Vous devez être connecté");
        }
        return notificationDAO.findNonLues(Session.getCurrentUser().getId());
    }

    // Nombre de notifications non lues
    public int getNombreNotificationsNonLues() throws SQLException {
        if (!Session.isLoggedIn()) {
            return 0;
        }
        return notificationDAO.countNonLues(Session.getCurrentUser().getId());
    }

    // Marquer comme lu
    public void marquerCommeLu(int notificationId) throws SQLException {
        notificationDAO.marquerCommeLu(notificationId);
    }

    // Marquer tout comme lu
    public void marquerToutCommeLu() throws SQLException {
        if (!Session.isLoggedIn()) {
            throw new RuntimeException("Vous devez être connecté");
        }
        notificationDAO.marquerToutCommeLu(Session.getCurrentUser().getId());
    }

    // Supprimer une notification
    public void supprimerNotification(int notificationId) throws SQLException {
        notificationDAO.delete(notificationId);
    }
}