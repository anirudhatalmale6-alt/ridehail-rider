package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;

public class Ride {

    @SerializedName("id")
    private int id;

    @SerializedName("rider_id")
    private int riderId;

    @SerializedName("driver_id")
    private int driverId;

    @SerializedName("status")
    private String status;

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

    @SerializedName("fare")
    private double fare;

    @SerializedName("distance_km")
    private double distanceKm;

    @SerializedName("duration_min")
    private double durationMin;

    @SerializedName("driver_name")
    private String driverName;

    @SerializedName("driver_phone")
    private String driverPhone;

    @SerializedName("vehicle_make")
    private String vehicleMake;

    @SerializedName("vehicle_model")
    private String vehicleModel;

    @SerializedName("vehicle_color")
    private String vehicleColor;

    @SerializedName("license_plate")
    private String licensePlate;

    @SerializedName("requested_at")
    private String requestedAt;

    @SerializedName("completed_at")
    private String completedAt;

    public Ride() {}

    // Getters
    public int getId() { return id; }
    public int getRiderId() { return riderId; }
    public int getDriverId() { return driverId; }
    public String getStatus() { return status; }
    public double getPickupLat() { return pickupLat; }
    public double getPickupLng() { return pickupLng; }
    public String getPickupAddress() { return pickupAddress; }
    public double getDropoffLat() { return dropoffLat; }
    public double getDropoffLng() { return dropoffLng; }
    public String getDropoffAddress() { return dropoffAddress; }
    public double getFare() { return fare; }
    public double getDistanceKm() { return distanceKm; }
    public double getDurationMin() { return durationMin; }
    public String getDriverName() { return driverName; }
    public String getDriverPhone() { return driverPhone; }
    public String getVehicleMake() { return vehicleMake; }
    public String getVehicleModel() { return vehicleModel; }
    public String getVehicleColor() { return vehicleColor; }
    public String getLicensePlate() { return licensePlate; }
    public String getRequestedAt() { return requestedAt; }
    public String getCompletedAt() { return completedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setRiderId(int riderId) { this.riderId = riderId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public void setStatus(String status) { this.status = status; }
    public void setPickupLat(double pickupLat) { this.pickupLat = pickupLat; }
    public void setPickupLng(double pickupLng) { this.pickupLng = pickupLng; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public void setDropoffLat(double dropoffLat) { this.dropoffLat = dropoffLat; }
    public void setDropoffLng(double dropoffLng) { this.dropoffLng = dropoffLng; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }
    public void setFare(double fare) { this.fare = fare; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public void setDurationMin(double durationMin) { this.durationMin = durationMin; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setRequestedAt(String requestedAt) { this.requestedAt = requestedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}
