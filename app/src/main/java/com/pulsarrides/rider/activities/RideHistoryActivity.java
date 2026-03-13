package com.pulsarrides.rider.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.pulsarrides.rider.R;
import com.pulsarrides.rider.adapters.RideHistoryAdapter;
import com.pulsarrides.rider.models.Ride;
import com.pulsarrides.rider.models.RideListResponse;
import com.pulsarrides.rider.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private RideHistoryAdapter adapter;
    private final List<Ride> rideList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyState = findViewById(R.id.emptyState);
        progressBar = findViewById(R.id.progressBar);

        adapter = new RideHistoryAdapter(rideList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.primary_cyan, null));
        swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.background_card, null));
        swipeRefresh.setOnRefreshListener(this::loadHistory);

        progressBar.setVisibility(View.VISIBLE);
        loadHistory();
    }

    private void loadHistory() {
        ApiClient.getInstance(this).getService().getRideHistory().enqueue(new Callback<RideListResponse>() {
            @Override
            public void onResponse(@NonNull Call<RideListResponse> call, @NonNull Response<RideListResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().getRides() != null) {
                    rideList.clear();
                    rideList.addAll(response.body().getRides());
                    adapter.notifyDataSetChanged();

                    if (rideList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RideListResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(RideHistoryActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                emptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}
