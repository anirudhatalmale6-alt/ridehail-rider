package com.pulsarrides.rider.network;

import android.util.Log;

import com.pulsarrides.rider.utils.Constants;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static final String TAG = "SocketManager";
    private static SocketManager instance;
    private Socket socket;

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void connect(String token) {
        try {
            if (socket != null && socket.connected()) {
                Log.d(TAG, "Socket already connected");
                return;
            }

            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = Integer.MAX_VALUE;
            options.reconnectionDelay = 1000;
            options.reconnectionDelayMax = 5000;
            options.timeout = 20000;
            Map<String, String> authMap = new java.util.HashMap<>();
            authMap.put("token", token);
            options.auth = authMap;

            URI uri = URI.create(Constants.SOCKET_URL + Constants.SOCKET_NAMESPACE);
            socket = IO.socket(uri, options);

            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.d(TAG, "Socket connected");
            });

            socket.on(Socket.EVENT_DISCONNECT, args -> {
                Log.d(TAG, "Socket disconnected");
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                if (args.length > 0) {
                    Log.e(TAG, "Socket connection error: " + args[0].toString());
                }
            });

            socket.connect();
            Log.d(TAG, "Socket connecting to " + Constants.SOCKET_URL + Constants.SOCKET_NAMESPACE);

        } catch (Exception e) {
            Log.e(TAG, "Socket connection failed", e);
        }
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.off();
            socket = null;
            Log.d(TAG, "Socket disconnected and cleaned up");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    public void emit(String event, Object... args) {
        if (socket != null && socket.connected()) {
            socket.emit(event, args);
        } else {
            Log.w(TAG, "Cannot emit '" + event + "': socket not connected");
        }
    }
}
