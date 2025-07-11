package com.example.leave_management.Controller;

public class LoginResponse {
    private String token;
    private String role;
    private String username;
    private Long roleId;

    public LoginResponse(String token, String role, String username, Long roleId) {
        this.token = token;
        this.role = role;
        this.username = username;
        this.roleId = roleId;
    }

    public String getToken() {
        return token;
    }
    public String getRole() {
        return role;
    }
    public String getUsername() {
        return username;
    }
    public Long getRoleId() {
        return roleId;
    }
} 