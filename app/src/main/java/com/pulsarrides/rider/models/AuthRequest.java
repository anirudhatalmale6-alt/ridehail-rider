package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {

    // Login request
    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    // Registration only
    @SerializedName("name")
    private String name;

    @SerializedName("role")
    private String role;

    /**
     * Create login request
     */
    public static AuthRequest login(String phone, String password) {
        AuthRequest req = new AuthRequest();
        req.phone = phone;
        req.password = password;
        return req;
    }

    /**
     * Create registration request
     */
    public static AuthRequest register(String name, String phone, String password) {
        AuthRequest req = new AuthRequest();
        req.name = name;
        req.phone = phone;
        req.password = password;
        req.role = "rider";
        return req;
    }

    // Getters
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getRole() { return role; }
}
