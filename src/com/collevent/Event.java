package com.collevent;

import java.util.Date;

public class Event {
    private int eventId;
    private String title;
    private String description;
    private Date eventDate;
    private String location;
    private int adminId;

    public Event() {}

    public Event(String title, String description, Date eventDate, String location, int adminId) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.adminId = adminId;
    }

    // Getters and Setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
}
