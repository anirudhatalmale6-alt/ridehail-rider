package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;

public class RideResponse {

    @SerializedName("ride")
    private Ride ride;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    public Ride getRide() { return ride; }
    public String getMessage() { return message; }
    public String getError() { return error; }
}
