package org.example.dao;

import org.example.config.DB;
import org.example.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // Créer une notification
    public void create(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (type, message, date_envoi, lu, id_destinataire, id_evenement, lien_action) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, notification.getType());
            pstmt.setString(2, notification.getMessage());
            pstmt.setTimestamp(3, new Timestamp(notification.getDateEnvoi().getTime()));
            pstmt.setBoolean(4, notification.isLu());
            pstmt.setInt(5, notification.getIdDestinataire());
            pstmt.setObject(6, notification.getIdEvenement() > 0 ? notification.getIdEvenement() : null, Types.INTEGER);
            pstmt.setString(7, notification.getLienAction());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                notification.setId(rs.getInt(1));
            }
        }
    }

    // Trouver les notifications d'un utilisateur
    public List<Notification> findByDestinataire(int destinataireId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE id_destinataire = ? ORDER BY date_envoi DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, destinataireId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapNotification(rs));
            }
        }
        return notifications;
    }

    // Trouver les notifications non lues
    public List<Notification> findNonLues(int destinataireId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE id_destinataire = ? AND lu = false ORDER BY date_envoi DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, destinataireId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapNotification(rs));
            }
        }
        return notifications;
    }

    // Marquer comme lu
    public void marquerCommeLu(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET lu = true WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        }
    }

    // Marquer toutes les notifications d'un utilisateur comme lues
    public void marquerToutCommeLu(int destinataireId) throws SQLException {
        String sql = "UPDATE notifications SET lu = true WHERE id_destinataire = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, destinataireId);
            pstmt.executeUpdate();
        }
    }

    // Supprimer une notification
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Compter les notifications non lues
    public int countNonLues(int destinataireId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE id_destinataire = ? AND lu = false";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, destinataireId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setType(rs.getString("type"));
        notification.setMessage(rs.getString("message"));
        notification.setDateEnvoi(rs.getTimestamp("date_envoi"));
        notification.setLu(rs.getBoolean("lu"));
        notification.setIdDestinataire(rs.getInt("id_destinataire"));

        int idEvenement = rs.getInt("id_evenement");
        if (!rs.wasNull()) {
            notification.setIdEvenement(idEvenement);
        }

        notification.setLienAction(rs.getString("lien_action"));

        return notification;
    }
}