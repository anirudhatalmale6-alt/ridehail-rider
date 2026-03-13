package com.pulsarrides.rider.api;

import androidx.annotation.NonNull;

import com.pulsarrides.rider.utils.PrefsManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp interceptor that adds JWT Bearer token to all requests
 */
public class AuthInterceptor implements Interceptor {

    private final PrefsManager prefsManager;

    public AuthInterceptor(PrefsManager prefsManager) {
        this.prefsManager = prefsManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        String token = prefsManager.getToken();
        if (token != null && !token.isEmpty()) {
            Request authorized = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .build();
            return chain.proceed(authorized);
        }

        return chain.proceed(original);
    }
}
