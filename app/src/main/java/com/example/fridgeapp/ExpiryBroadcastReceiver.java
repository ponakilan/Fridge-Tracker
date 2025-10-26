package com.example.fridgeapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ExpiryBroadcastReceiver extends BroadcastReceiver {

    public static final String EXTRA_ITEM_NAME = "EXTRA_ITEM_NAME";
    public static final String EXTRA_DAYS_TEXT = "EXTRA_DAYS_TEXT";
    public static final String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        String itemName = intent.getStringExtra(EXTRA_ITEM_NAME);
        String daysText = intent.getStringExtra(EXTRA_DAYS_TEXT);
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

        if (itemName == null) {
            itemName = "Your item";
        }
        if (daysText == null) {
            daysText = "expiring soon";
        }

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification(itemName, daysText);
        builder.setContentIntent(pendingIntent);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }
}
