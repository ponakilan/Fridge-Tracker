package com.example.fridgeapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FridgeViewModel fridgeViewModel;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notifications permission denied. Reminders will not work.", Toast.LENGTH_LONG).show();
                }
            });
    private RecyclerView recyclerView;
    private FridgeItemAdapter adapter;
    private TextView emptyView;
    private final ActivityResultLauncher<Intent> addItemLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    FridgeItem newItem = (FridgeItem) result.getData().getSerializableExtra("newItem");
                    if (newItem != null) {
                        fridgeViewModel.insert(newItem);
                    }
                }
            });
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        MaterialToolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        new NotificationHelper(this);

        askNotificationPermission();

        recyclerView = findViewById(R.id.recyclerViewItems);
        emptyView = findViewById(R.id.textViewEmpty);
        fab = findViewById(R.id.fabAddItem);

        fridgeViewModel = new ViewModelProvider(this).get(FridgeViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FridgeItemAdapter(this, fridgeViewModel);
        recyclerView.setAdapter(adapter);

        fridgeViewModel.getAllItems().observe(this, items -> {
            adapter.submitList(items);
            checkEmptyView();
        });

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            addItemLauncher.launch(intent);
        });

        checkEmptyView();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void checkEmptyView() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
