package org.example.service;

import org.example.model.Event;
import org.example.model.User;
import java.util.Date;

public class ParticipationEnAttente {
    private Event event;
    private User participant;
    private Date dateInscription;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }
}