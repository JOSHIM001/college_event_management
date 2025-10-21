package com.collevent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    public static User authenticateUser(String username, String password) {
        User user = null;
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try {
            String query = "SELECT * FROM User WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("userType");
                int userId = rs.getInt("userId");

                // Use equalsIgnoreCase for robust userType checking against DB entries
                if (userType.equalsIgnoreCase("Student")) {
                    user = loadStudentDetails(conn, userId, rs);
                } else if (userType.equalsIgnoreCase("Administrator")) {
                    user = loadAdminDetails(conn, userId, rs);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return user;
    }

    private static Student loadStudentDetails(Connection conn, int userId, ResultSet userRS) throws SQLException {
        Student student = new Student();
        student.setUserId(userId);
        student.setUsername(userRS.getString("username"));
        student.setPassword(userRS.getString("password"));
        student.setEmail(userRS.getString("email"));
        student.setUserType(userRS.getString("userType"));

        String query = "SELECT * FROM Student WHERE userId = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            student.setStudentId(rs.getInt("studentId"));
            student.setStudentName(rs.getString("studentName"));
        }
        rs.close();
        stmt.close();
        return student;
    }

    private static Administrator loadAdminDetails(Connection conn, int userId, ResultSet userRS) throws SQLException {
        Administrator admin = new Administrator();
        admin.setUserId(userId);
        admin.setUsername(userRS.getString("username"));
        admin.setPassword(userRS.getString("password"));
        admin.setEmail(userRS.getString("email"));
        admin.setUserType(userRS.getString("userType"));

        String query = "SELECT * FROM Administrator WHERE userId = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            admin.setAdminId(rs.getInt("adminId"));
            admin.setAdminName(rs.getString("adminName"));
        }
        rs.close();
        stmt.close();
        return admin;
    }

    public static int registerUser(User user, String name) {
        int generatedId = -1;
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return -1;

        try {
            conn.setAutoCommit(false); // Start transaction

            String userQuery = "INSERT INTO User (username, password, email, userType) VALUES (?, ?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, user.getUsername());
            userStmt.setString(2, user.getPassword());
            userStmt.setString(3, user.getEmail());
            userStmt.setString(4, user.getUserType()); // Stores 'Student' or 'Administrator'
            userStmt.executeUpdate();

            ResultSet userKeys = userStmt.getGeneratedKeys();
            int userId = -1;
            if (userKeys.next()) {
                userId = userKeys.getInt(1);
            } else {
                conn.rollback();
                return -1;
            }
            userKeys.close();
            userStmt.close();

            if (user instanceof Student) {
                String studentQuery = "INSERT INTO Student (studentName, userId) VALUES (?, ?)";
                PreparedStatement studentStmt = conn.prepareStatement(studentQuery, Statement.RETURN_GENERATED_KEYS);
                studentStmt.setString(1, name);
                studentStmt.setInt(2, userId);
                studentStmt.executeUpdate();

                ResultSet studentKeys = studentStmt.getGeneratedKeys();
                if (studentKeys.next()) {
                    generatedId = studentKeys.getInt(1);
                }
                studentKeys.close();
                studentStmt.close();

            } else if (user instanceof Administrator) {
                String adminQuery = "INSERT INTO Administrator (adminName, userId) VALUES (?, ?)";
                PreparedStatement adminStmt = conn.prepareStatement(adminQuery, Statement.RETURN_GENERATED_KEYS);
                adminStmt.setString(1, name);
                adminStmt.setInt(2, userId);
                adminStmt.executeUpdate();

                ResultSet adminKeys = adminStmt.getGeneratedKeys();
                if (adminKeys.next()) {
                    generatedId = adminKeys.getInt(1);
                }
                adminKeys.close();
                adminStmt.close();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            generatedId = -1;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return generatedId;
    }


    public static boolean deleteStudentAccount(int studentId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        boolean success = false;
        PreparedStatement stmt = null;

        try {
            conn.setAutoCommit(false); // Start transaction

            // 1. Get userId associated with the studentId
            String getUserIdQuery = "SELECT userId FROM Student WHERE studentId = ?";
            stmt = conn.prepareStatement(getUserIdQuery);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt("userId");
            }
            rs.close();
            stmt.close();

            if (userId == -1) {
                conn.rollback(); // No student found
                return false;
            }


            String deleteRegQuery = "DELETE FROM Registration WHERE studentId = ?";
            stmt = conn.prepareStatement(deleteRegQuery);
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
            stmt.close();


            String deleteStudentQuery = "DELETE FROM Student WHERE studentId = ?";
            stmt = conn.prepareStatement(deleteStudentQuery);
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
            stmt.close();


            String deleteUserQuery = "DELETE FROM User WHERE userId = ?";
            stmt = conn.prepareStatement(deleteUserQuery);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            stmt.close();

            conn.commit();
            success = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return success;
    }
}
