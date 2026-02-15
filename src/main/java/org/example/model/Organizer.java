package org.example.model;

public class Organizer extends User {
    public Organizer(User u) {
        super(u.name, u.email, u.password, u.phone, Role.ORGANIZER);
        this.id = u.id;
    }
}
