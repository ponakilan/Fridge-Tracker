package com.example.fridgeapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class FridgeItemRepository {

    private FridgeItemDao mFridgeItemDao;
    private LiveData<List<FridgeItem>> mAllItems;

    FridgeItemRepository(Application application) {
        FridgeDatabase db = FridgeDatabase.getDatabase(application);
        mFridgeItemDao = db.fridgeItemDao();
        mAllItems = mFridgeItemDao.getAll();
    }

    LiveData<List<FridgeItem>> getAllItems() {
        return mAllItems;
    }

    void insert(FridgeItem item) {
        FridgeDatabase.databaseWriteExecutor.execute(() -> {
            mFridgeItemDao.insert(item);
        });
    }

    void delete(FridgeItem item) {
        FridgeDatabase.databaseWriteExecutor.execute(() -> {
            mFridgeItemDao.delete(item);
        });
    }
}
