package org.example.dao;

import org.example.config.DB;
import org.example.dao.interfaces.IUserDAO;
import org.example.model.*;
import org.example.security.PasswordUtil;

import java.sql.*;
import java.util.*;

public class UserDAO implements IUserDAO {

    @Override
    public void create(User u) throws Exception {
        String sql = "INSERT INTO users(name, email, password, phone, role) VALUES(?,?,?,?,?)";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, PasswordUtil.hash(u.getPassword()));
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getRole().name());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                u.setId(rs.getInt(1));
            }

            insertRoleSpecificTable(u, cn);
        }
    }

    @Override
    public User findById(int id) throws Exception {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }
        }
        return null;
    }

    @Override
    public User findByEmail(String email) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection cn = DB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<User> findByRole(Role role) throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY id";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, role.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public void update(User u) throws Exception {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, role = ? WHERE id = ?";

        try (Connection cn = DB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getRole().name());
            ps.setInt(5, u.getId());

            ps.executeUpdate();

            updateRoleSpecificTable(u, cn);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection cn = DB.getConnection()) {
            User user = findById(id);
            if (user != null) {
                deleteFromRoleSpecificTable(user.getRole(), id, cn);
            }

            try (PreparedStatement ps = cn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    private void insertRoleSpecificTable(User u, Connection cn) throws SQLException {
        String table = getRoleTableName(u.getRole());

        try (PreparedStatement ps = cn.prepareStatement("INSERT INTO " + table + "(id) VALUES(?)")) {
            ps.setInt(1, u.getId());
            ps.executeUpdate();
        }
    }

    private void updateRoleSpecificTable(User u, Connection cn) throws SQLException, Exception {
        User existingUser = findById(u.getId());
        if (existingUser != null && existingUser.getRole() != u.getRole()) {
            deleteFromRoleSpecificTable(existingUser.getRole(), u.getId(), cn);
            insertRoleSpecificTable(u, cn);
        }
    }

    private void deleteFromRoleSpecificTable(Role role, int userId, Connection cn) throws SQLException {
        String table = getRoleTableName(role);

        try (PreparedStatement ps = cn.prepareStatement("DELETE FROM " + table + " WHERE id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    private String getRoleTableName(Role role) {
        // Traditional switch statement
        switch (role) {
            case ADMIN:
                return "administrateur";
            case ORGANIZER:
                return "organisateur";
            case PARTICIPANT:
                return "participant";
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User base = new User(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("phone"),
                Role.valueOf(rs.getString("role"))
        );
        base.setId(rs.getInt("id"));

        // Traditional switch statement
        switch (base.getRole()) {
            case ADMIN:
                return new Admin(base);
            case ORGANIZER:
                return new Organizer(base);
            case PARTICIPANT:
                return new Participant(base);
            default:
                return base;
        }
    }
}