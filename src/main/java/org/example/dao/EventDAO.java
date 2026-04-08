package org.example.dao;

import org.example.config.DB;
import org.example.model.Event;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    // Créer un événement
    public void create(Event event) throws SQLException {
        String sql = "INSERT INTO evenements (titre, description, date_debut, date_fin, lieu, places_max, places_dispo, id_organisateur) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, event.getTitre());
            pstmt.setString(2, event.getDescription());
            pstmt.setTimestamp(3, new Timestamp(event.getDateDebut().getTime()));
            pstmt.setTimestamp(4, new Timestamp(event.getDateFin().getTime()));
            pstmt.setString(5, event.getLieu());
            pstmt.setInt(6, event.getPlacesMax());
            pstmt.setInt(7, event.getPlacesDispo());
            pstmt.setInt(8, event.getIdOrganisateur());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getInt(1));
            }
        }
    }

    // Trouver un événement par ID
    public Event findById(int id) throws SQLException {
        String sql = "SELECT e.*, u.name as organisateur_nom FROM evenements e " +
                "JOIN users u ON e.id_organisateur = u.id WHERE e.id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapEvent(rs);
            }
        }
        return null;
    }

    // Trouver tous les événements
    public List<Event> findAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, u.name as organisateur_nom FROM evenements e " +
                "JOIN users u ON e.id_organisateur = u.id ORDER BY e.date_debut";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(mapEvent(rs));
            }
        }
        return events;
    }

    // Trouver les événements par organisateur
    public List<Event> findByOrganisateur(int organisateurId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, u.name as organisateur_nom FROM evenements e " +
                "JOIN users u ON e.id_organisateur = u.id WHERE e.id_organisateur = ? ORDER BY e.date_debut";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organisateurId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapEvent(rs));
            }
        }
        return events;
    }

    // Trouver les événements disponibles (pas complets, à venir)
    public List<Event> findAvailableEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, u.name as organisateur_nom FROM evenements e " +
                "JOIN users u ON e.id_organisateur = u.id " +
                "WHERE e.places_dispo > 0 AND e.date_fin > NOW() " +
                "ORDER BY e.date_debut";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(mapEvent(rs));
            }
        }
        return events;
    }

    // Trouver les événements auxquels un participant est inscrit
    public List<Event> findByParticipant(int participantId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, u.name as organisateur_nom FROM evenements e " +
                "JOIN users u ON e.id_organisateur = u.id " +
                "JOIN participations p ON e.id = p.id_evenement " +
                "WHERE p.id_participant = ? ORDER BY e.date_debut";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, participantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapEvent(rs));
            }
        }
        return events;
    }

    // Mettre à jour un événement
    public void update(Event event) throws SQLException {
        String sql = "UPDATE evenements SET titre = ?, description = ?, date_debut = ?, " +
                "date_fin = ?, lieu = ?, places_max = ?, places_dispo = ? WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getTitre());
            pstmt.setString(2, event.getDescription());
            pstmt.setTimestamp(3, new Timestamp(event.getDateDebut().getTime()));
            pstmt.setTimestamp(4, new Timestamp(event.getDateFin().getTime()));
            pstmt.setString(5, event.getLieu());
            pstmt.setInt(6, event.getPlacesMax());
            pstmt.setInt(7, event.getPlacesDispo());
            pstmt.setInt(8, event.getId());

            pstmt.executeUpdate();
        }
    }

    // Supprimer un événement
    public void delete(int id) throws SQLException {
        // D'abord supprimer les participations
        String deleteParticipations = "DELETE FROM participations WHERE id_evenement = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteParticipations)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }

        // Puis supprimer l'événement
        String sql = "DELETE FROM evenements WHERE id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Mapper ResultSet vers Event
    private Event mapEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitre(rs.getString("titre"));
        event.setDescription(rs.getString("description"));
        event.setDateDebut(rs.getTimestamp("date_debut"));
        event.setDateFin(rs.getTimestamp("date_fin"));
        event.setLieu(rs.getString("lieu"));
        event.setPlacesMax(rs.getInt("places_max"));
        event.setPlacesDispo(rs.getInt("places_dispo"));
        event.setIdOrganisateur(rs.getInt("id_organisateur"));

        try {
            event.setOrganisateurNom(rs.getString("organisateur_nom"));
        } catch (SQLException e) {
            // Pas de nom d'organisateur dans la requête
        }

        return event;
    }
}