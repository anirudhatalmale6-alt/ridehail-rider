package com.pulsarrides.rider.api;

import com.pulsarrides.rider.models.AuthResponse;
import com.pulsarrides.rider.models.GenericResponse;
import com.pulsarrides.rider.models.ProfileResponse;
import com.pulsarrides.rider.models.RideListResponse;
import com.pulsarrides.rider.models.RideRequest;
import com.pulsarrides.rider.models.RideResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PulsarApi {

    // ---- Auth ----

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body Map<String, String> body);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    @PUT("api/auth/fcm-token")
    Call<GenericResponse> updateFcmToken(@Body Map<String, String> body);

    @GET("api/auth/profile")
    Call<ProfileResponse> getProfile();

    // ---- Rides ----

    @POST("api/rides/request")
    Call<RideResponse> requestRide(@Body RideRequest request);

    @GET("api/rides/active")
    Call<RideResponse> getActiveRide();

    @GET("api/rides/{id}")
    Call<RideResponse> getRide(@Path("id") String rideId);

    @POST("api/rides/{id}/cancel")
    Call<RideResponse> cancelRide(@Path("id") String rideId);

    @GET("api/rides/history/list")
    Call<RideListResponse> getRideHistory();
}
