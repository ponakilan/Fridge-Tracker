package com.example.fridgeapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AddItemActivity extends AppCompatActivity {

    private TextInputEditText editTextItemName;
    private DatePicker datePickerExpiry;
    private TextInputEditText editTextRemindDays;
    private TextInputEditText editTextQuantity;
    private Button buttonSave;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        editTextItemName = findViewById(R.id.editTextItemName);
        datePickerExpiry = findViewById(R.id.datePickerExpiry);
        editTextRemindDays = findViewById(R.id.editTextRemindDays);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        buttonSave = findViewById(R.id.buttonSave);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        buttonSave.setOnClickListener(view -> saveItem());
    }

    private void saveItem() {
        String itemName = editTextItemName.getText().toString().trim();
        String remindDaysStr = editTextRemindDays.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        if (itemName.isEmpty()) {
            editTextItemName.setError("Item name is required");
            editTextItemName.requestFocus();
            return;
        }

        if (remindDaysStr.isEmpty()) {
            editTextRemindDays.setError("Reminder days required");
            editTextRemindDays.requestFocus();
            return;
        }

        if (quantityStr.isEmpty()) {
            editTextQuantity.setError("Quantity is required");
            editTextQuantity.requestFocus();
            return;
        }

        int remindDays = Integer.parseInt(remindDaysStr);
        int quantity = Integer.parseInt(quantityStr);

        Calendar expiryCalendar = Calendar.getInstance();
        expiryCalendar.set(
                datePickerExpiry.getYear(),
                datePickerExpiry.getMonth(),
                datePickerExpiry.getDayOfMonth(),
                8, 0, 0
        );

        long expiryTimeMillis = expiryCalendar.getTimeInMillis();

        if (expiryTimeMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Expiry date must be in the future", Toast.LENGTH_SHORT).show();
            return;
        }

        FridgeItem newItem = new FridgeItem(itemName, expiryTimeMillis, remindDays, quantity);
        checkAlarmPermissionAndSchedule(newItem);
    }

    private void checkAlarmPermissionAndSchedule(FridgeItem item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {

                scheduleNotification(item);
            } else {

                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("To set expiry reminders, this app needs permission to schedule exact alarms.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        } else {
            scheduleNotification(item);
        }
    }

    private void scheduleNotification(FridgeItem item) {

        long reminderTimeMillis = item.getExpiryDateMillis() - TimeUnit.DAYS.toMillis(item.getRemindDaysBefore());

        if (reminderTimeMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Reminder time is in the past, no alarm set.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ExpiryBroadcastReceiver.class);
            intent.putExtra(ExpiryBroadcastReceiver.EXTRA_ITEM_NAME, item.getName());
            String daysText = item.getRemindDaysBefore() == 0 ? "expiring today"
                    : "expiring in " + item.getRemindDaysBefore() + " day(s)";
            intent.putExtra(ExpiryBroadcastReceiver.EXTRA_DAYS_TEXT, daysText);

            int notificationId = item.getName().hashCode() + (int) item.getExpiryDateMillis();
            intent.putExtra(ExpiryBroadcastReceiver.EXTRA_NOTIFICATION_ID, notificationId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            try {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                );
            } catch (SecurityException e) {
                Toast.makeText(this, "Could not schedule alarm. Permission missing.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newItem", item);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
