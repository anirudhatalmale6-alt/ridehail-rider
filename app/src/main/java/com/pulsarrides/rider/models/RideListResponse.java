package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RideListResponse {

    @SerializedName("rides")
    private List<Ride> rides;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    public List<Ride> getRides() { return rides; }
    public String getMessage() { return message; }
    public String getError() { return error; }
}
