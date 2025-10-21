package com.collevent;

public class Student extends User {
    private int studentId;
    private String studentName;

    public Student() {}

    public Student(String username, String password, String email) {
        super(username, password, email, "Student");
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
