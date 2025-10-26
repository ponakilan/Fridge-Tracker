package com.example.fridgeapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "fridge_items")
public class FridgeItem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private long expiryDateMillis; // Store as milliseconds
    private int remindDaysBefore;
    private int quantity;

    public FridgeItem(String name, long expiryDateMillis, int remindDaysBefore, int quantity) {
        this.name = name;
        this.expiryDateMillis = expiryDateMillis;
        this.remindDaysBefore = remindDaysBefore;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpiryDateMillis() {
        return expiryDateMillis;
    }

    public void setExpiryDateMillis(long expiryDateMillis) {
        this.expiryDateMillis = expiryDateMillis;
    }

    public int getRemindDaysBefore() {
        return remindDaysBefore;
    }

    public void setRemindDaysBefore(int remindDaysBefore) {
        this.remindDaysBefore = remindDaysBefore;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Helper to get expiry as Calendar
    public Calendar getExpiryDateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(expiryDateMillis);
        return cal;
    }

    // Helper to calculate days left
    public long getDaysLeft() {
        long diff = expiryDateMillis - System.currentTimeMillis();
        // Convert millis to days, rounding up
        return (long) Math.ceil(diff / (1000.0 * 60 * 60 * 24));
    }
}
