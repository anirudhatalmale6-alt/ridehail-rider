package com.pulsarrides.rider.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pulsarrides.rider.R;
import com.pulsarrides.rider.activities.HomeActivity;
import com.pulsarrides.rider.network.ApiClient;
import com.pulsarrides.rider.utils.Constants;
import com.pulsarrides.rider.utils.PrefsManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PulsarFCMService extends FirebaseMessagingService {

    private static final String TAG = "PulsarFCMService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        String title = "Pulsar Rides";
        String body = "";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle() != null
                    ? remoteMessage.getNotification().getTitle() : title;
            body = remoteMessage.getNotification().getBody() != null
                    ? remoteMessage.getNotification().getBody() : "";
        }

        // Check data payload
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            if (data.containsKey("title")) title = data.get("title");
            if (data.containsKey("body")) body = data.get("body");
        }

        showNotification(title, body);
    }

    private void showNotification(String title, String body) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.primary_cyan, null));

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void sendTokenToServer(String fcmToken) {
        PrefsManager prefs = new PrefsManager(this);
        if (!prefs.isLoggedIn()) return;

        Map<String, String> body = new HashMap<>();
        body.put("fcm_token", fcmToken);

        ApiClient.getInstance(this).getService().updateFcmToken(body).enqueue(new Callback<com.pulsarrides.rider.models.GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<com.pulsarrides.rider.models.GenericResponse> call,
                                   @NonNull Response<com.pulsarrides.rider.models.GenericResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "FCM token sent to server");
                } else {
                    Log.e(TAG, "Failed to send FCM token: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.pulsarrides.rider.models.GenericResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error sending FCM token", t);
            }
        });
    }
}
