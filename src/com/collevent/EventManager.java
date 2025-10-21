package com.collevent;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventManager {



    public static void createEvent(Event event) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;
        try {
            String query = "INSERT INTO Event (title, description, eventDate, location, adminId) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, new Date(event.getEventDate().getTime()));
            stmt.setString(4, event.getLocation());
            stmt.setInt(5, event.getAdminId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public static void updateEvent(Event event) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;
        try {
            String query = "UPDATE Event SET title = ?, description = ?, eventDate = ?, location = ? WHERE eventId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, new Date(event.getEventDate().getTime()));
            stmt.setString(4, event.getLocation());
            stmt.setInt(5, event.getEventId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public static void deleteEvent(int eventId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;
        try {
            String query = "DELETE FROM Event WHERE eventId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    // --- Retrieval ---

    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return events;
        try {
            String query = "SELECT * FROM Event ORDER BY eventDate ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return events;
    }

    public static Event getEventById(int eventId) {
        Event event = null;
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;
        try {
            String query = "SELECT * FROM Event WHERE eventId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                event = mapResultSetToEvent(rs);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return event;
    }

    private static Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("eventId"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setEventDate(rs.getDate("eventDate"));
        event.setLocation(rs.getString("location"));
        event.setAdminId(rs.getInt("adminId"));
        return event;
    }
}
