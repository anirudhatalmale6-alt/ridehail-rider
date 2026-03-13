package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;

public class RideRequest {

    @SerializedName("pickup_lat")
    private double pickupLat;

    @SerializedName("pickup_lng")
    private double pickupLng;

    @SerializedName("pickup_address")
    private String pickupAddress;

    @SerializedName("dropoff_lat")
    private double dropoffLat;

    @SerializedName("dropoff_lng")
    private double dropoffLng;

    @SerializedName("dropoff_address")
    private String dropoffAddress;

    public RideRequest(double pickupLat, double pickupLng, String pickupAddress,
                       double dropoffLat, double dropoffLng, String dropoffAddress) {
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.pickupAddress = pickupAddress;
        this.dropoffLat = dropoffLat;
        this.dropoffLng = dropoffLng;
        this.dropoffAddress = dropoffAddress;
    }

    // Getters
    public double getPickupLat() { return pickupLat; }
    public double getPickupLng() { return pickupLng; }
    public String getPickupAddress() { return pickupAddress; }
    public double getDropoffLat() { return dropoffLat; }
    public double getDropoffLng() { return dropoffLng; }
    public String getDropoffAddress() { return dropoffAddress; }
}
