package com.example.fridgeapp;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FridgeViewModel extends AndroidViewModel {

    private FridgeItemRepository mRepository;
    private final LiveData<List<FridgeItem>> mAllItems;

    public FridgeViewModel (Application application) {
        super(application);
        mRepository = new FridgeItemRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    LiveData<List<FridgeItem>> getAllItems() {
        return mAllItems;
    }

    public void insert(FridgeItem item) {
        mRepository.insert(item);
    }

    public void delete(FridgeItem item) {
        mRepository.delete(item);
    }
}
