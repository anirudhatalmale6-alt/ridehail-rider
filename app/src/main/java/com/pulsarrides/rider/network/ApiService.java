package com.pulsarrides.rider.network;

import com.pulsarrides.rider.models.AuthResponse;
import com.pulsarrides.rider.models.GenericResponse;
import com.pulsarrides.rider.models.Ride;
import com.pulsarrides.rider.models.RideHistoryResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Auth
    @POST("auth/register")
    Call<AuthResponse> register(@Body Map<String, String> body);

    @POST("auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    @PUT("auth/fcm-token")
    Call<GenericResponse> updateFcmToken(@Body Map<String, String> body);

    @GET("auth/profile")
    Call<AuthResponse> getProfile();

    // Rider
    @POST("rider/request")
    Call<Ride> requestRide(@Body Map<String, Object> body);

    @GET("rider/active")
    Call<Ride> getActiveRide();

    @POST("rider/{id}/cancel")
    Call<Ride> cancelRide(@Path("id") int rideId, @Body Map<String, String> body);

    @GET("rider/history/list")
    Call<RideHistoryResponse> getRideHistory(@Query("page") int page);
}
