package com.example.fridgeapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL_ID = "FridgeTrackerChannel";
    public static final String CHANNEL_NAME = "Fridge Expiry Reminders";
    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for fridge item expiry notifications");
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String itemName, String daysText) {
        String title = "Fridge Item Expiring!";
        String text = itemName + " is " + daysText + ".";

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID).setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.ic_launcher_foreground).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);
    }
}
