package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    @SerializedName("user")
    private User user;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    public User getUser() { return user; }
    public String getMessage() { return message; }
    public String getError() { return error; }
}
