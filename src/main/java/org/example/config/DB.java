package org.example.config;

import java.sql.*;

public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/event_planner";
    private static final String USER = "root";
    private static final String PASS = ""; // Change this to your MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}