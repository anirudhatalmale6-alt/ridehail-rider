package com.pulsarrides.rider.api;

import android.util.Log;

import com.pulsarrides.rider.utils.PrefsManager;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Manages Socket.IO connection to the rider namespace
 */
public class SocketManager {

    private static final String TAG = "SocketManager";
    private static final String SERVER_URL = "https://pulsar-rides.com";
    private static final String NAMESPACE = "/rider";

    private static SocketManager instance;
    private Socket socket;
    private boolean connected = false;

    // Event listeners
    private SocketEventListener eventListener;

    private SocketManager() { }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public interface SocketEventListener {
        void onDriverAssigned(JSONObject data);
        void onDriverLocation(JSONObject data);
        void onDriverArrived(JSONObject data);
        void onTripStarted(JSONObject data);
        void onRideCompleted(JSONObject data);
        void onRideCancelled(JSONObject data);
        void onNoDrivers(JSONObject data);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    public void setEventListener(SocketEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Connect to Socket.IO with JWT authentication
     */
    public void connect(PrefsManager prefsManager) {
        String token = prefsManager.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "No JWT token available for socket connection");
            return;
        }

        if (socket != null && socket.connected()) {
            Log.d(TAG, "Socket already connected");
            return;
        }

        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = 10;
            options.reconnectionDelay = 1000;
            options.timeout = 20000;
            options.transports = new String[]{"websocket"};

            // Pass JWT token in auth handshake
            options.auth = Collections.singletonMap("token", token);

            socket = IO.socket(SERVER_URL + NAMESPACE, options);
            setupListeners();
            socket.connect();

            Log.d(TAG, "Socket connecting to " + SERVER_URL + NAMESPACE);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI error", e);
            if (eventListener != null) {
                eventListener.onError("Socket connection error: " + e.getMessage());
            }
        }
    }

    private void setupListeners() {
        if (socket == null) return;

        socket.on(Socket.EVENT_CONNECT, args -> {
            connected = true;
            Log.d(TAG, "Socket connected");
            if (eventListener != null) eventListener.onConnected();
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            connected = false;
            Log.d(TAG, "Socket disconnected");
            if (eventListener != null) eventListener.onDisconnected();
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            String error = args.length > 0 ? args[0].toString() : "Unknown error";
            Log.e(TAG, "Socket connection error: " + error);
            if (eventListener != null) eventListener.onError(error);
        });

        // Ride events
        socket.on("ride:driver_assigned", args -> {
            Log.d(TAG, "Event: ride:driver_assigned");
            if (eventListener != null && args.length > 0) {
                eventListener.onDriverAssigned((JSONObject) args[0]);
            }
        });

        socket.on("ride:driver_location", args -> {
            if (eventListener != null && args.length > 0) {
                eventListener.onDriverLocation((JSONObject) args[0]);
            }
        });

        // Server emits "ride:arrived" for driver arrived
        socket.on("ride:arrived", args -> {
            Log.d(TAG, "Event: ride:arrived (driver arrived)");
            if (eventListener != null && args.length > 0) {
                eventListener.onDriverArrived((JSONObject) args[0]);
            }
        });

        // Also listen for the documented name
        socket.on("ride:driver_arrived", args -> {
            Log.d(TAG, "Event: ride:driver_arrived");
            if (eventListener != null && args.length > 0) {
                eventListener.onDriverArrived((JSONObject) args[0]);
            }
        });

        // Server emits "ride:in_progress" for trip started
        socket.on("ride:in_progress", args -> {
            Log.d(TAG, "Event: ride:in_progress (trip started)");
            if (eventListener != null && args.length > 0) {
                eventListener.onTripStarted((JSONObject) args[0]);
            }
        });

        // Also listen for the documented name
        socket.on("ride:trip_started", args -> {
            Log.d(TAG, "Event: ride:trip_started");
            if (eventListener != null && args.length > 0) {
                eventListener.onTripStarted((JSONObject) args[0]);
            }
        });

        socket.on("ride:completed", args -> {
            Log.d(TAG, "Event: ride:completed");
            if (eventListener != null && args.length > 0) {
                eventListener.onRideCompleted((JSONObject) args[0]);
            }
        });

        socket.on("ride:cancelled", args -> {
            Log.d(TAG, "Event: ride:cancelled");
            if (eventListener != null && args.length > 0) {
                eventListener.onRideCancelled((JSONObject) args[0]);
            }
        });

        socket.on("ride:no_drivers", args -> {
            Log.d(TAG, "Event: ride:no_drivers");
            if (eventListener != null) {
                eventListener.onNoDrivers(args.length > 0 ? (JSONObject) args[0] : new JSONObject());
            }
        });
    }

    /**
     * Disconnect socket
     */
    public void disconnect() {
        if (socket != null) {
            socket.off();
            socket.disconnect();
            socket = null;
        }
        connected = false;
        Log.d(TAG, "Socket disconnected manually");
    }

    /**
     * Check if socket is connected
     */
    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    /**
     * Emit an event
     */
    public void emit(String event, JSONObject data) {
        if (socket != null && socket.connected()) {
            socket.emit(event, data);
        } else {
            Log.w(TAG, "Cannot emit " + event + " - socket not connected");
        }
    }
}
