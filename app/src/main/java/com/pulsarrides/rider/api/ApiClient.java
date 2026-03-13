package com.pulsarrides.rider.api;

import com.pulsarrides.rider.utils.PrefsManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit API client
 */
public class ApiClient {

    private static final String BASE_URL = "https://pulsar-rides.com/";
    private static PulsarApi apiInstance;
    private static Retrofit retrofitInstance;

    private ApiClient() { }

    public static synchronized PulsarApi getApi(PrefsManager prefsManager) {
        if (apiInstance == null) {
            apiInstance = getRetrofit(prefsManager).create(PulsarApi.class);
        }
        return apiInstance;
    }

    private static Retrofit getRetrofit(PrefsManager prefsManager) {
        if (retrofitInstance == null) {
            // Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(prefsManager))
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Retrofit
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }

    /**
     * Reset API client (call on logout to clear token interceptor)
     */
    public static synchronized void reset() {
        apiInstance = null;
        retrofitInstance = null;
    }
}
