package com.example.fridgeapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FridgeItemDao {
    @Query("SELECT * FROM fridge_items ORDER BY expiryDateMillis ASC")
    LiveData<List<FridgeItem>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(FridgeItem item);

    @Delete
    void delete(FridgeItem item);
}
