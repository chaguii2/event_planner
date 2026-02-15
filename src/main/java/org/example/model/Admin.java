package org.example.model;

public class Admin extends User {
    public Admin(User u) {
        super(u.name, u.email, u.password, u.phone, Role.ADMIN);
        this.id = u.id;
    }
}