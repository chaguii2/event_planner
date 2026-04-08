package org.example.service;

import org.example.dao.EventDAO;
import org.example.dao.ParticipationDAO;
import org.example.model.Event;
import org.example.model.User;
import org.example.session.Session;

import java.sql.SQLException;
import java.util.List;

public class EventService {
    private EventDAO eventDAO = new EventDAO();
    private ParticipationDAO participationDAO = new ParticipationDAO();
    private NotificationService notificationService = new NotificationService();

    // Méthodes pour Organisateur
    public void createEvent(Event event) throws SQLException {
        if (!Session.isOrganizer()) {
            throw new RuntimeException("Seuls les organisateurs peuvent créer des événements");
        }
        eventDAO.create(event);
    }

    public void updateEvent(Event event) throws SQLException {
        if (!Session.isOrganizer()) {
            throw new RuntimeException("Seuls les organisateurs peuvent modifier des événements");
        }

        // Vérifier que l'organisateur est bien le propriétaire
        Event existingEvent = eventDAO.findById(event.getId());
        if (existingEvent.getIdOrganisateur() != Session.getCurrentUser().getId()) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres événements");
        }

        eventDAO.update(event);
    }

    public void deleteEvent(int eventId) throws SQLException {
        if (!Session.isOrganizer()) {
            throw new RuntimeException("Seuls les organisateurs peuvent supprimer des événements");
        }

        Event event = eventDAO.findById(eventId);
        if (event.getIdOrganisateur() != Session.getCurrentUser().getId()) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres événements");
        }

        eventDAO.delete(eventId);
    }

    public List<Event> getMyEvents() throws SQLException {
        if (!Session.isOrganizer()) {
            throw new RuntimeException("Accès réservé aux organisateurs");
        }
        return eventDAO.findByOrganisateur(Session.getCurrentUser().getId());
    }

    // Méthodes pour Participant
    public List<Event> getAvailableEvents() throws SQLException {
        return eventDAO.findAvailableEvents();
    }

    public List<Event> getMyParticipations() throws SQLException {
        if (!Session.isParticipant()) {
            throw new RuntimeException("Accès réservé aux participants");
        }
        return eventDAO.findByParticipant(Session.getCurrentUser().getId());
    }

    public boolean participer(int eventId) throws SQLException {
        if (!Session.isParticipant()) {
            throw new RuntimeException("Seuls les participants peuvent s'inscrire");
        }
        return participationDAO.inscrire(Session.getCurrentUser().getId(), eventId);
    }

    public boolean annulerParticipation(int eventId) throws SQLException {
        if (!Session.isParticipant()) {
            throw new RuntimeException("Seuls les participants peuvent annuler leur inscription");
        }
        return participationDAO.desinscrire(Session.getCurrentUser().getId(), eventId);
    }

    public boolean estInscrit(int eventId) throws SQLException {
        return participationDAO.estInscrit(Session.getCurrentUser().getId(), eventId);
    }

    // Méthodes pour Admin
    public List<Event> getAllEvents() throws SQLException {
        if (!Session.isAdmin()) {
            throw new RuntimeException("Accès réservé aux administrateurs");
        }
        return eventDAO.findAll();
    }

    public List<Event> getEventsByOrganisateur(int organisateurId) throws SQLException {
        if (!Session.isAdmin()) {
            throw new RuntimeException("Accès réservé aux administrateurs");
        }
        return eventDAO.findByOrganisateur(organisateurId);
    }

    public List<User> getParticipantsByEvent(int eventId) throws SQLException {
        if (!Session.isAdmin()) {
            throw new RuntimeException("Accès réservé aux administrateurs");
        }
        return participationDAO.getParticipantsByEvent(eventId);
    }
    public boolean confirmerParticipation(int participantId, int eventId, String commentaire) throws SQLException {
        if (!Session.isOrganizer()) {
            throw new RuntimeException("Seuls les organisateurs peuvent confirmer des participations");
        }

        // Vérifier que l'organisateur est propriétaire de l'événement
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new RuntimeException("Événement non trouvé");
        }
        if (event.getIdOrganisateur() != Session.getCurrentUser().getId()) {
            throw new RuntimeException("Vous ne pouvez confirmer que les participations de vos propres événements");
        }

        // Vérifier que le participant est bien inscrit
        if (!participationDAO.estInscrit(participantId, eventId)) {
            throw new RuntimeException("Ce participant n'est pas inscrit à cet événement");
        }

        boolean success = participationDAO.confirmerParticipation(participantId, eventId, commentaire);

        if (success) {
            // Envoyer une notification de confirmation au participant
            notificationService.envoyerNotification(
                    participantId,
                    "CONFIRMATION",
                    "✅ Votre participation à l'événement \"" + event.getTitre() + "\" a été confirmée.\n" +
                            (commentaire != null && !commentaire.isEmpty() ? "Message de l'organisateur : " + commentaire : ""),
                    eventId
            );
        }

        return success;
    }

    /**
     * Vérifier si une participation est confirmée
     */
    public boolean estParticipationConfirmee(int participantId, int eventId) throws SQLException {
        return participationDAO.estConfirmee(participantId, eventId);
    }

    /**
     * Obtenir les participations en attente de confirmation pour tous les événements de l'organisateur connecté
     */
    public List<org.example.service.ParticipationEnAttente> getParticipationsEnAttente() throws SQLException {
        if (!Session.isOrganizer()) {
            throw new RuntimeException("Accès réservé aux organisateurs");
        }

        List<org.example.service.ParticipationEnAttente> enAttente = new java.util.ArrayList<>();
        List<Event> myEvents = eventDAO.findByOrganisateur(Session.getCurrentUser().getId());

        for (Event event : myEvents) {
            List<User> participants = participationDAO.getParticipationsEnAttente(event.getId());
            for (User participant : participants) {
                org.example.service.ParticipationEnAttente p = new org.example.service.ParticipationEnAttente();
                p.setEvent(event);
                p.setParticipant(participant);
                p.setDateInscription(participationDAO.getDateInscription(participant.getId(), event.getId()));
                enAttente.add(p);
            }
        }

        return enAttente;
    }

    /**
     * Obtenir le nombre de participations en attente pour l'organisateur connecté
     */
    public int getNombreParticipationsEnAttente() throws SQLException {
        if (!Session.isOrganizer()) {
            return 0;
        }
        return getParticipationsEnAttente().size();
    }
}
