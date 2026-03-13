package com.pulsarrides.rider.utils;

public final class Constants {

    private Constants() {}

    // API
    public static final String BASE_URL = "https://pulsar-rides.com/api/";
    public static final String SOCKET_URL = "https://pulsar-rides.com";
    public static final String SOCKET_NAMESPACE = "/rider";

    // SharedPreferences
    public static final String PREFS_NAME = "pulsar_rider_prefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_USER_ROLE = "user_role";

    // Notification
    public static final String NOTIFICATION_CHANNEL_ID = "pulsar_rides_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Pulsar Rides";
    public static final int NOTIFICATION_ID_RIDE_UPDATE = 1001;
    public static final int NOTIFICATION_ID_DRIVER_ARRIVED = 1002;

    // Default Location (Windhoek, Namibia)
    public static final double DEFAULT_LAT = -22.5609;
    public static final double DEFAULT_LNG = 17.0658;
    public static final float DEFAULT_ZOOM = 15f;

    // Intent Extras
    public static final String EXTRA_RIDE_ID = "ride_id";
    public static final String EXTRA_FARE = "fare";
    public static final String EXTRA_DISTANCE = "distance";
    public static final String EXTRA_DURATION = "duration";
    public static final String EXTRA_PICKUP_ADDRESS = "pickup_address";
    public static final String EXTRA_DROPOFF_ADDRESS = "dropoff_address";

    // Ride Statuses
    public static final String STATUS_REQUESTED = "requested";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_ARRIVING = "arriving";
    public static final String STATUS_ARRIVED = "arrived";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";
}
