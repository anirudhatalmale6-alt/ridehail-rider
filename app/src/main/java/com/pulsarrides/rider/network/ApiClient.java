package com.pulsarrides.rider.network;

import android.content.Context;

import com.pulsarrides.rider.api.PulsarApi;
import com.pulsarrides.rider.utils.PrefsManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Context-aware API client wrapper used by services
 */
public class ApiClient {

    private static final String BASE_URL = "https://pulsar-rides.com/";
    private static ApiClient instance;
    private final PulsarApi service;

    private ApiClient(Context context) {
        PrefsManager prefs = new PrefsManager(context);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String token = prefs.getToken();
                    Request.Builder builder = chain.request().newBuilder()
                            .header("Content-Type", "application/json");
                    if (token != null && !token.isEmpty()) {
                        builder.header("Authorization", "Bearer " + token);
                    }
                    return chain.proceed(builder.build());
                })
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(PulsarApi.class);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context.getApplicationContext());
        }
        return instance;
    }

    public PulsarApi getService() {
        return service;
    }

    public static synchronized void reset() {
        instance = null;
    }
}
