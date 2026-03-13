package com.pulsarrides.rider.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.pulsarrides.rider.R;
import com.pulsarrides.rider.utils.PrefsManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::navigate, SPLASH_DELAY);
    }

    private void navigate() {
        PrefsManager prefs = new PrefsManager(this);
        Intent intent;
        if (prefs.isLoggedIn()) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
