package com.pulsarrides.rider.models;

import com.google.gson.annotations.SerializedName;

public class Driver {

    @SerializedName("id")
    private String id;

    @SerializedName("_id")
    private String mongoId;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("vehicle_make")
    private String vehicleMake;

    @SerializedName("vehicle_model")
    private String vehicleModel;

    @SerializedName("vehicle_color")
    private String vehicleColor;

    @SerializedName("license_plate")
    private String licensePlate;

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    // Getters
    public String getId() { return id != null ? id : mongoId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getVehicleMake() { return vehicleMake; }
    public String getVehicleModel() { return vehicleModel; }
    public String getVehicleColor() { return vehicleColor; }
    public String getLicensePlate() { return licensePlate; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    /**
     * Get vehicle display string e.g. "Toyota Corolla - White"
     */
    public String getVehicleDisplay() {
        StringBuilder sb = new StringBuilder();
        if (vehicleMake != null) sb.append(vehicleMake);
        if (vehicleModel != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(vehicleModel);
        }
        if (vehicleColor != null) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(vehicleColor);
        }
        return sb.toString();
    }
}
