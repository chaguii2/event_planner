package org.example.model;

public class Participant extends User {
    public Participant(User u) {
        super(u.name, u.email, u.password, u.phone, Role.PARTICIPANT);
        this.id = u.id;
    }
}
