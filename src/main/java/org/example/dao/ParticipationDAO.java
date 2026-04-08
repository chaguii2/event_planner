package org.example.dao;

import org.example.config.DB;
import org.example.model.Event;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationDAO {

    // Inscrire un participant à un événement
    public boolean inscrire(int participantId, int eventId) throws SQLException {
        Connection conn = null;
        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false);

            // Vérifier les places disponibles
            String checkSql = "SELECT places_dispo FROM evenements WHERE id = ? FOR UPDATE";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, eventId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int placesDispo = rs.getInt("places_dispo");
                    if (placesDispo <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Mettre à jour les places disponibles
            String updateSql = "UPDATE evenements SET places_dispo = places_dispo - 1 WHERE id = ? AND places_dispo > 0";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, eventId);
                int updated = updateStmt.executeUpdate();

                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Créer la participation
            String insertSql = "INSERT INTO participations (id_participant, id_evenement, date_inscription) VALUES (?, ?, NOW())";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, participantId);
                insertStmt.setInt(2, eventId);
                insertStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Désinscrire un participant
    public boolean desinscrire(int participantId, int eventId) throws SQLException {
        Connection conn = null;
        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false);

            // Supprimer la participation
            String deleteSql = "DELETE FROM participations WHERE id_participant = ? AND id_evenement = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, participantId);
                deleteStmt.setInt(2, eventId);
                int deleted = deleteStmt.executeUpdate();

                if (deleted == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Remettre une place disponible
            String updateSql = "UPDATE evenements SET places_dispo = places_dispo + 1 WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, eventId);
                updateStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Vérifier si un participant est inscrit à un événement
    public boolean estInscrit(int participantId, int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participations WHERE id_participant = ? AND id_evenement = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, participantId);
            pstmt.setInt(2, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Compter le nombre de participants pour un événement
    public int countParticipants(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participations WHERE id_evenement = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Obtenir tous les participants d'un événement
    public List<User> getParticipantsByEvent(int eventId) throws SQLException {
        List<User> participants = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                "JOIN participations p ON u.id = p.id_participant " +
                "WHERE p.id_evenement = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                participants.add(user);
            }
        }
        return participants;
    }
    // À ajouter dans ParticipationDAO

    // Confirmer une participation (organisateur)
    public boolean confirmerParticipation(int participantId, int eventId, String commentaire) throws SQLException {
        String sql = "UPDATE participations SET confirmee = true, date_confirmation = NOW(), commentaire_organisateur = ? " +
                "WHERE id_participant = ? AND id_evenement = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, commentaire);
            pstmt.setInt(2, participantId);
            pstmt.setInt(3, eventId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // Vérifier si une participation est confirmée
    public boolean estConfirmee(int participantId, int eventId) throws SQLException {
        String sql = "SELECT confirmee FROM participations WHERE id_participant = ? AND id_evenement = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, participantId);
            pstmt.setInt(2, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("confirmee");
            }
        }
        return false;
    }

    // Obtenir les participations en attente de confirmation pour un événement
    public List<User> getParticipationsEnAttente(int eventId) throws SQLException {
        List<User> participants = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                "JOIN participations p ON u.id = p.id_participant " +
                "WHERE p.id_evenement = ? AND p.confirmee = false";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                participants.add(user);
            }
        }
        return participants;
    }
    public Date getDateInscription(int participantId, int eventId) throws SQLException {
        String sql = "SELECT date_inscription FROM participations WHERE id_participant = ? AND id_evenement = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, participantId);
            pstmt.setInt(2, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getTimestamp("date_inscription");
            }
        }
        return null;
    }
}