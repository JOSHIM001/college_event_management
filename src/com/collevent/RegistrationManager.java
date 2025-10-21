package com.collevent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrationManager {

    // Constant for the maximum number of events a student can register for
    private static final int MAX_REGISTRATIONS = 3;

    /**
     * Attempts to register a student for an event, enforcing the 3-event limit.
     * @return true if registration was successful, false if limit was reached or an error occurred.
     */
    public static boolean registerForEvent(int studentId, int eventId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // 1. Check if the student has reached the limit (MAX_REGISTRATIONS)
            String countQuery = "SELECT COUNT(*) FROM Registration WHERE studentId = ?";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            countStmt.setInt(1, studentId);
            ResultSet rs = countStmt.executeQuery();

            if (rs.next() && rs.getInt(1) >= MAX_REGISTRATIONS) {
                return false; // Limit reached, cannot register
            }

            // 2. Perform the registration
            String insertQuery = "INSERT INTO Registration (studentId, eventId, registrationDate) VALUES (?, ?, CURDATE())";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, studentId);
            insertStmt.setInt(2, eventId);
            insertStmt.executeUpdate();

            return true; // Registration successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Removes a student's registration from a specific event.
     */
    public static void unregisterEvent(int studentId, int eventId) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            String query = "DELETE FROM Registration WHERE studentId = ? AND eventId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves all registration records for a specific student.
     */
    public static List<Registration> getRegistrationsByStudentId(int studentId) {
        List<Registration> registrations = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try {
            String query = "SELECT * FROM Registration WHERE studentId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Registration reg = new Registration();
                reg.setRegistrationId(rs.getInt("registrationId"));
                reg.setStudentId(rs.getInt("studentId"));
                reg.setEventId(rs.getInt("eventId"));
                reg.setRegistrationDate(rs.getDate("registrationDate"));
                registrations.add(reg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return registrations;
    }

    /**
     * Helper method to check if a student is already registered for an event.
     */
    public static boolean isStudentRegistered(int studentId, int eventId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) FROM Registration WHERE studentId = ? AND eventId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            stmt.setInt(2, eventId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves Student objects registered for a given Event.
     * This method joins Registration, Student, and User tables.
     */
    public static List<Student> getStudentsByEventId(int eventId) {
        List<Student> students = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT s.studentId, s.studentName, u.username, u.email " +
                    "FROM Student s " +
                    "JOIN Registration r ON s.studentId = r.studentId " +
                    "JOIN User u ON s.userId = u.userId " +
                    "WHERE r.eventId = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("studentId"));
                student.setStudentName(rs.getString("studentName"));
                student.setUsername(rs.getString("username"));
                student.setEmail(rs.getString("email"));
                // Note: userType and password are not retrieved in this query for security/simplicity
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return students;
    }
}
