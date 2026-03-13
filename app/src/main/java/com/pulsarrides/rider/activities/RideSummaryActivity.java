package com.pulsarrides.rider.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.pulsarrides.rider.R;
import com.pulsarrides.rider.utils.Constants;

import java.util.Locale;

public class RideSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_summary);

        double fare = getIntent().getDoubleExtra(Constants.EXTRA_FARE, 24);
        double distance = getIntent().getDoubleExtra(Constants.EXTRA_DISTANCE, 0);
        double duration = getIntent().getDoubleExtra(Constants.EXTRA_DURATION, 0);
        String pickupAddress = getIntent().getStringExtra(Constants.EXTRA_PICKUP_ADDRESS);
        String dropoffAddress = getIntent().getStringExtra(Constants.EXTRA_DROPOFF_ADDRESS);

        TextView tvFare = findViewById(R.id.tvFare);
        TextView tvDistance = findViewById(R.id.tvDistance);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvPickup = findViewById(R.id.tvPickupAddress);
        TextView tvDropoff = findViewById(R.id.tvDropoffAddress);
        MaterialButton btnDone = findViewById(R.id.btnDone);

        tvFare.setText(String.format(Locale.getDefault(), "N$%.0f", fare));
        tvDistance.setText(String.format(Locale.getDefault(), "%.1f km", distance));
        tvDuration.setText(String.format(Locale.getDefault(), "%.0f min", duration));

        if (tvPickup != null && pickupAddress != null) {
            tvPickup.setText(pickupAddress);
        }
        if (tvDropoff != null && dropoffAddress != null) {
            tvDropoff.setText(dropoffAddress);
        }

        btnDone.setOnClickListener(v -> finish());
    }
}
