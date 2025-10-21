package com.collevent;

import java.util.Date;

public class Registration {
    private int registrationId;
    private int studentId;
    private int eventId;
    private Date registrationDate;

    public Registration() {}

    public Registration(int studentId, int eventId) {
        this.studentId = studentId;
        this.eventId = eventId;
        this.registrationDate = new Date();
    }


    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
}
