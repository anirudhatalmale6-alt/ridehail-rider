package com.pulsarrides.rider.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.pulsarrides.rider.R;
import com.pulsarrides.rider.api.SocketManager;
import com.pulsarrides.rider.models.Ride;
import com.pulsarrides.rider.models.RideRequest;
import com.pulsarrides.rider.models.RideResponse;
import com.pulsarrides.rider.network.ApiClient;
import com.pulsarrides.rider.utils.Constants;
import com.pulsarrides.rider.utils.PrefsManager;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, SocketManager.SocketEventListener {

    private static final String TAG = "HomeActivity";
    private static final int LOCATION_PERMISSION_CODE = 1001;
    private static final int PLACES_AUTOCOMPLETE_CODE = 2001;
    private static final int NOTIFICATION_PERMISSION_CODE = 1002;

    // Map
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng currentLocation;
    private Marker driverMarker;
    private Marker pickupMarker;
    private Marker destinationMarker;
    private Polyline routePolyline;

    // Destination
    private LatLng destinationLatLng;
    private String destinationAddress;
    private String pickupAddress = "Your location";

    // State
    private String currentRideId;
    private String currentDriverPhone;
    private RideState rideState = RideState.IDLE;

    // Views
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View searchBar;
    private View bottomSheetDestination;
    private View bottomSheetRideRequest;
    private View bottomSheetFindingDriver;
    private View bottomSheetDriverInfo;

    private BottomSheetBehavior<View> behaviorDestination;
    private BottomSheetBehavior<View> behaviorRideRequest;
    private BottomSheetBehavior<View> behaviorFinding;
    private BottomSheetBehavior<View> behaviorDriverInfo;

    // Prefs & API
    private PrefsManager prefs;
    private SocketManager socketManager;

    private enum RideState {
        IDLE, DESTINATION_SELECTED, SEARCHING, DRIVER_ASSIGNED, DRIVER_ARRIVED, IN_PROGRESS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = new PrefsManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        initMap();
        initDrawer();
        initSocket();
        requestNotificationPermission();
        checkForActiveRide();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        searchBar = findViewById(R.id.searchBar);

        bottomSheetDestination = findViewById(R.id.bottomSheetDestination);
        bottomSheetRideRequest = findViewById(R.id.bottomSheetRideRequest);
        bottomSheetFindingDriver = findViewById(R.id.bottomSheetFindingDriver);
        bottomSheetDriverInfo = findViewById(R.id.bottomSheetDriverInfo);

        behaviorDestination = BottomSheetBehavior.from(bottomSheetDestination);
        behaviorRideRequest = BottomSheetBehavior.from(bottomSheetRideRequest);
        behaviorFinding = BottomSheetBehavior.from(bottomSheetFindingDriver);
        behaviorDriverInfo = BottomSheetBehavior.from(bottomSheetDriverInfo);

        // Menu button
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        // Search bar -> Places Autocomplete
        searchBar.setOnClickListener(v -> launchPlacesAutocomplete());

        // My Location FAB
        findViewById(R.id.fabMyLocation).setOnClickListener(v -> moveToCurrentLocation());

        // Request Ride button (inside ride request bottom sheet)
        MaterialButton btnRequestRide = findViewById(R.id.btnRequestRide);
        if (btnRequestRide != null) {
            btnRequestRide.setOnClickListener(v -> requestRide());
        }

        // Cancel Search button
        MaterialButton btnCancelSearch = findViewById(R.id.btnCancelSearch);
        if (btnCancelSearch != null) {
            btnCancelSearch.setOnClickListener(v -> cancelRide());
        }

        // Cancel Ride button (in driver info)
        MaterialButton btnCancelRide = findViewById(R.id.btnCancelRide);
        if (btnCancelRide != null) {
            btnCancelRide.setOnClickListener(v -> showCancelDialog());
        }

        // Call Driver button
        ImageButton btnCallDriver = findViewById(R.id.btnCallDriver);
        if (btnCallDriver != null) {
            btnCallDriver.setOnClickListener(v -> callDriver());
        }

        // Hide all bottom sheets initially
        hideAllBottomSheets();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initDrawer() {
        // Set drawer header user info
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvDrawerName);
        TextView tvPhone = headerView.findViewById(R.id.tvDrawerPhone);
        tvName.setText(prefs.getUserName());
        tvPhone.setText(prefs.getUserPhone());

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already home
            } else if (id == R.id.nav_rides) {
                startActivity(new Intent(this, RideHistoryActivity.class));
            } else if (id == R.id.nav_logout) {
                showLogoutDialog();
            }
            return true;
        });
    }

    private void initSocket() {
        socketManager = SocketManager.getInstance();
        socketManager.setEventListener(this);
        socketManager.connect(prefs);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Apply dark style
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
        } catch (Exception e) {
            Log.e(TAG, "Failed to apply map style", e);
        }

        // Default to Windhoek
        LatLng windhoek = new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(windhoek, Constants.DEFAULT_ZOOM));

        // Disable default map UI that conflicts with our custom UI
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        // Request location permission
        requestLocationPermission();
    }

    // ======================== LOCATION ========================

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            enableMyLocation();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (googleMap == null) return;
        googleMap.setMyLocationEnabled(true);
        startLocationUpdates();
        getLastKnownLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, Constants.DEFAULT_ZOOM));
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(3000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    currentLocation = new LatLng(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()
                    );
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    private void moveToCurrentLocation() {
        if (currentLocation != null && googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, Constants.DEFAULT_ZOOM));
        } else {
            Toast.makeText(this, R.string.location_permission_needed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    // ======================== PLACES AUTOCOMPLETE ========================

    private void launchPlacesAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
        );

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountries(Arrays.asList("NA")) // Namibia
                .build(this);
        startActivityForResult(intent, PLACES_AUTOCOMPLETE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACES_AUTOCOMPLETE_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                destinationLatLng = place.getLatLng();
                destinationAddress = place.getAddress() != null ? place.getAddress() : place.getName();

                showDestinationOnMap();
                showDestinationPanel();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Log.e(TAG, "Places autocomplete error");
            }
        }
    }

    // ======================== MAP DRAWING ========================

    private void showDestinationOnMap() {
        if (googleMap == null || destinationLatLng == null) return;

        clearMapMarkers();

        // Pickup marker (current location)
        LatLng pickup = currentLocation != null ? currentLocation : new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG);
        pickupMarker = googleMap.addMarker(new MarkerOptions()
                .position(pickup)
                .title("Pickup")
                .icon(getMarkerIcon(R.drawable.ic_pickup_dot)));

        // Destination marker
        destinationMarker = googleMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title(destinationAddress)
                .icon(getMarkerIcon(R.drawable.ic_destination_dot)));

        // Draw route line
        routePolyline = googleMap.addPolyline(new PolylineOptions()
                .add(pickup, destinationLatLng)
                .width(6f)
                .color(ContextCompat.getColor(this, R.color.primary_cyan))
                .geodesic(true));

        // Fit bounds
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(pickup);
        boundsBuilder.include(destinationLatLng);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150));
    }

    private void clearMapMarkers() {
        if (pickupMarker != null) { pickupMarker.remove(); pickupMarker = null; }
        if (destinationMarker != null) { destinationMarker.remove(); destinationMarker = null; }
        if (driverMarker != null) { driverMarker.remove(); driverMarker = null; }
        if (routePolyline != null) { routePolyline.remove(); routePolyline = null; }
    }

    private BitmapDescriptor getMarkerIcon(int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableRes);
        if (drawable == null) return BitmapDescriptorFactory.defaultMarker();
        int w = drawable.getIntrinsicWidth() * 2;
        int h = drawable.getIntrinsicHeight() * 2;
        drawable.setBounds(0, 0, w, h);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void updateDriverMarker(double lat, double lng) {
        runOnUiThread(() -> {
            if (googleMap == null) return;
            LatLng pos = new LatLng(lat, lng);
            if (driverMarker == null) {
                driverMarker = googleMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title("Driver")
                        .icon(getMarkerIcon(R.drawable.ic_car_marker))
                        .flat(true));
            } else {
                driverMarker.setPosition(pos);
            }
        });
    }

    // ======================== BOTTOM SHEET MANAGEMENT ========================

    private void hideAllBottomSheets() {
        behaviorDestination.setState(BottomSheetBehavior.STATE_HIDDEN);
        behaviorRideRequest.setState(BottomSheetBehavior.STATE_HIDDEN);
        behaviorFinding.setState(BottomSheetBehavior.STATE_HIDDEN);
        behaviorDriverInfo.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetDestination.setVisibility(View.GONE);
        bottomSheetRideRequest.setVisibility(View.GONE);
        bottomSheetFindingDriver.setVisibility(View.GONE);
        bottomSheetDriverInfo.setVisibility(View.GONE);
    }

    private void showBottomSheet(View sheet, BottomSheetBehavior<View> behavior) {
        hideAllBottomSheets();
        sheet.setVisibility(View.VISIBLE);
        behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void showDestinationPanel() {
        rideState = RideState.DESTINATION_SELECTED;

        // Update destination address text
        TextView tvDest = findViewById(R.id.tvDestinationAddress);
        if (tvDest != null) tvDest.setText(destinationAddress);

        TextView tvPickup = findViewById(R.id.tvPickupAddress);
        if (tvPickup != null) tvPickup.setText(pickupAddress);

        // Show destination bottom sheet then ride request
        showBottomSheet(bottomSheetDestination, behaviorDestination);

        // Also show ride request button
        bottomSheetRideRequest.setVisibility(View.VISIBLE);
        behaviorRideRequest.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        behaviorRideRequest.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void showSearchingPanel() {
        rideState = RideState.SEARCHING;
        showBottomSheet(bottomSheetFindingDriver, behaviorFinding);
    }

    private void showDriverPanel(String name, String vehicle, String plate, String phone) {
        rideState = RideState.DRIVER_ASSIGNED;
        currentDriverPhone = phone;

        TextView tvName = findViewById(R.id.tvDriverName);
        TextView tvVehicle = findViewById(R.id.tvVehicleInfo);
        TextView tvPlate = findViewById(R.id.tvLicensePlate);
        TextView tvStatus = findViewById(R.id.tvDriverStatus);

        if (tvName != null) tvName.setText(name);
        if (tvVehicle != null) tvVehicle.setText(vehicle);
        if (tvPlate != null) tvPlate.setText(plate);
        if (tvStatus != null) tvStatus.setText(R.string.driver_assigned);

        showBottomSheet(bottomSheetDriverInfo, behaviorDriverInfo);
    }

    // ======================== RIDE ACTIONS ========================

    private void requestRide() {
        if (destinationLatLng == null) return;

        LatLng pickup = currentLocation != null ? currentLocation : new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LNG);

        showSearchingPanel();

        RideRequest request = new RideRequest(
                pickup.latitude, pickup.longitude, pickupAddress,
                destinationLatLng.latitude, destinationLatLng.longitude, destinationAddress
        );

        ApiClient.getInstance(this).getService().requestRide(request).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(@NonNull Call<RideResponse> call, @NonNull Response<RideResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getRide() != null) {
                    currentRideId = String.valueOf(response.body().getRide().getId());
                    Log.d(TAG, "Ride requested: " + currentRideId);
                } else {
                    Toast.makeText(HomeActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                    resetToIdle();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RideResponse> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                resetToIdle();
            }
        });
    }

    private void cancelRide() {
        if (currentRideId != null) {
            ApiClient.getInstance(this).getService().cancelRide(currentRideId).enqueue(new Callback<RideResponse>() {
                @Override
                public void onResponse(@NonNull Call<RideResponse> call, @NonNull Response<RideResponse> response) {
                    Log.d(TAG, "Ride cancelled");
                }

                @Override
                public void onFailure(@NonNull Call<RideResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Cancel failed", t);
                }
            });
        }
        resetToIdle();
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setTitle(R.string.cancel_ride_title)
                .setMessage(R.string.cancel_ride_message)
                .setPositiveButton(R.string.yes_cancel, (d, w) -> cancelRide())
                .setNegativeButton(R.string.no_keep, null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes_logout, (d, w) -> logout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void logout() {
        socketManager.disconnect();
        prefs.clear();
        ApiClient.reset();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void resetToIdle() {
        rideState = RideState.IDLE;
        currentRideId = null;
        currentDriverPhone = null;
        destinationLatLng = null;
        destinationAddress = null;
        clearMapMarkers();
        hideAllBottomSheets();
    }

    private void callDriver() {
        if (currentDriverPhone != null && !currentDriverPhone.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + currentDriverPhone));
            startActivity(intent);
        }
    }

    private void checkForActiveRide() {
        ApiClient.getInstance(this).getService().getActiveRide().enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(@NonNull Call<RideResponse> call, @NonNull Response<RideResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getRide() != null) {
                    Ride ride = response.body().getRide();
                    currentRideId = String.valueOf(ride.getId());
                    String status = ride.getStatus();
                    if (status != null) {
                        destinationLatLng = new LatLng(ride.getDropoffLat(), ride.getDropoffLng());
                        destinationAddress = ride.getDropoffAddress();
                        pickupAddress = ride.getPickupAddress();

                        switch (status) {
                            case Constants.STATUS_REQUESTED:
                                showSearchingPanel();
                                showDestinationOnMap();
                                break;
                            case Constants.STATUS_ACCEPTED:
                            case Constants.STATUS_ARRIVING:
                                showDestinationOnMap();
                                String vehicleStr = (ride.getVehicleMake() != null ? ride.getVehicleMake() : "") + " "
                                        + (ride.getVehicleModel() != null ? ride.getVehicleModel() : "") + " - "
                                        + (ride.getVehicleColor() != null ? ride.getVehicleColor() : "");
                                showDriverPanel(
                                        ride.getDriverName() != null ? ride.getDriverName() : "Driver",
                                        vehicleStr.trim(),
                                        ride.getLicensePlate() != null ? ride.getLicensePlate() : "",
                                        ride.getDriverPhone()
                                );
                                break;
                            case Constants.STATUS_ARRIVED:
                                showDestinationOnMap();
                                rideState = RideState.DRIVER_ARRIVED;
                                break;
                            case Constants.STATUS_IN_PROGRESS:
                                showDestinationOnMap();
                                rideState = RideState.IN_PROGRESS;
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RideResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to check active ride", t);
            }
        });
    }

    // ======================== SOCKET EVENTS ========================

    @Override
    public void onDriverAssigned(JSONObject data) {
        runOnUiThread(() -> {
            try {
                currentRideId = data.optString("rideId", currentRideId);
                JSONObject driver = data.optJSONObject("driver");
                if (driver != null) {
                    String name = driver.optString("name", "Driver");
                    String phone = driver.optString("phone", "");
                    String make = driver.optString("vehicle_make", "");
                    String model = driver.optString("vehicle_model", "");
                    String color = driver.optString("vehicle_color", "");
                    String plate = driver.optString("license_plate", "");
                    double lat = driver.optDouble("lat", 0);
                    double lng = driver.optDouble("lng", 0);

                    String vehicleStr = (make + " " + model + " - " + color).trim();
                    showDriverPanel(name, vehicleStr, plate, phone);

                    if (lat != 0 && lng != 0) {
                        updateDriverMarker(lat, lng);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing driver_assigned", e);
            }
        });
    }

    @Override
    public void onDriverLocation(JSONObject data) {
        try {
            double lat = data.getDouble("lat");
            double lng = data.getDouble("lng");
            updateDriverMarker(lat, lng);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing driver_location", e);
        }
    }

    @Override
    public void onDriverArrived(JSONObject data) {
        runOnUiThread(() -> {
            rideState = RideState.DRIVER_ARRIVED;
            TextView tvStatus = findViewById(R.id.tvDriverStatus);
            if (tvStatus != null) tvStatus.setText(R.string.driver_arrived);
            Toast.makeText(this, R.string.driver_arrived, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onTripStarted(JSONObject data) {
        runOnUiThread(() -> {
            rideState = RideState.IN_PROGRESS;
            TextView tvStatus = findViewById(R.id.tvDriverStatus);
            if (tvStatus != null) tvStatus.setText(R.string.trip_in_progress);

            // Hide cancel button during trip
            MaterialButton btnCancel = findViewById(R.id.btnCancelRide);
            if (btnCancel != null) btnCancel.setVisibility(View.GONE);
        });
    }

    @Override
    public void onRideCompleted(JSONObject data) {
        runOnUiThread(() -> {
            double fare = data.optDouble("fare", 24);
            double distance = data.optDouble("distance_km", 0);
            double duration = data.optDouble("duration_minutes", 0);
            String rideId = data.optString("rideId", "");

            Intent intent = new Intent(this, RideSummaryActivity.class);
            intent.putExtra(Constants.EXTRA_RIDE_ID, rideId);
            intent.putExtra(Constants.EXTRA_FARE, fare);
            intent.putExtra(Constants.EXTRA_DISTANCE, distance);
            intent.putExtra(Constants.EXTRA_DURATION, duration);
            if (pickupAddress != null) intent.putExtra(Constants.EXTRA_PICKUP_ADDRESS, pickupAddress);
            if (destinationAddress != null) intent.putExtra(Constants.EXTRA_DROPOFF_ADDRESS, destinationAddress);
            startActivity(intent);

            resetToIdle();
        });
    }

    @Override
    public void onRideCancelled(JSONObject data) {
        runOnUiThread(() -> {
            String cancelledBy = data.optString("cancelled_by", "");
            String reason = data.optString("reason", "");
            String msg = "driver".equals(cancelledBy) ? "Driver cancelled the ride" : "Ride cancelled";
            if (!reason.isEmpty()) msg += ": " + reason;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            resetToIdle();
        });
    }

    @Override
    public void onNoDrivers(JSONObject data) {
        runOnUiThread(() -> {
            Toast.makeText(this, R.string.error_no_drivers, Toast.LENGTH_LONG).show();
            showDestinationPanel();
        });
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "Socket connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "Socket disconnected");
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Socket error: " + error);
    }

    // ======================== LIFECYCLE ========================

    @Override
    protected void onResume() {
        super.onResume();
        if (socketManager != null && !socketManager.isConnected()) {
            socketManager.connect(prefs);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        // Don't disconnect socket on destroy - keep for background notifications
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else if (rideState == RideState.DESTINATION_SELECTED) {
            resetToIdle();
        } else {
            super.onBackPressed();
        }
    }
}
