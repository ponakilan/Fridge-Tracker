package com.example.fridgeapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FridgeItemAdapter extends ListAdapter<FridgeItem, FridgeItemAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final FridgeViewModel viewModel;

    public FridgeItemAdapter(Context context, FridgeViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
    }

    private static final DiffUtil.ItemCallback<FridgeItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<FridgeItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FridgeItem oldItem, @NonNull FridgeItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull FridgeItem oldItem, @NonNull FridgeItem newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getExpiryDateMillis() == newItem.getExpiryDateMillis() &&
                    oldItem.getQuantity() == newItem.getQuantity();
        }
    };

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_fridge, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        FridgeItem item = getItem(position);
        holder.nameView.setText(item.getName());
        holder.quantityView.setText(" (" + item.getQuantity() + ")");

        String expiryText = "Expires: " + dateFormat.format(item.getExpiryDateCalendar().getTime());
        holder.expiryView.setText(expiryText);

        long daysLeft = item.getDaysLeft();
        String daysLeftText = daysLeft + (daysLeft == 1 || daysLeft == -1 ? " day" : " days");

        if (daysLeft < 0) {
            holder.daysLeftView.setText("Expired");
            holder.daysLeftView.setTextColor(Color.RED);
        } else if (daysLeft == 0) {
            holder.daysLeftView.setText("Expires Today");
            holder.daysLeftView.setTextColor(Color.parseColor("#FFA500"));
        } else if (daysLeft <= 3) {
            holder.daysLeftView.setText(daysLeftText);
            holder.daysLeftView.setTextColor(Color.parseColor("#FFC107"));
        } else {
            holder.daysLeftView.setText(daysLeftText);
            holder.daysLeftView.setTextColor(Color.parseColor("#4CAF50"));
        }

        holder.deleteButton.setOnClickListener(v -> {
            viewModel.delete(item);
        });
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView quantityView;
        TextView expiryView;
        TextView daysLeftView;
        ImageButton deleteButton;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.textViewItemName);
            quantityView = itemView.findViewById(R.id.textViewQuantity);
            expiryView = itemView.findViewById(R.id.textViewExpiryDate);
            daysLeftView = itemView.findViewById(R.id.textViewDaysLeft);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
