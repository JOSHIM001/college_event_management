package com.collevent;

public class Administrator extends User {
    private int adminId;
    private String adminName;

    public Administrator() {}

    public Administrator(String username, String password, String email) {
        // Use capitalized type for consistency with database
        super(username, password, email, "Administrator");
    }

    // Getters and Setters
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
}
