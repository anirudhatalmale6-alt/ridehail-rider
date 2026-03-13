package com.pulsarrides.rider.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pulsarrides.rider.R;
import com.pulsarrides.rider.models.Ride;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {

    private final List<Ride> rides;

    public RideHistoryAdapter(List<Ride> rides) {
        this.rides = rides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.bind(ride);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvStatus;
        private final TextView tvPickup;
        private final TextView tvDropoff;
        private final TextView tvFare;
        private final TextView tvDistance;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPickup = itemView.findViewById(R.id.tvPickup);
            tvDropoff = itemView.findViewById(R.id.tvDropoff);
            tvFare = itemView.findViewById(R.id.tvFare);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }

        void bind(Ride ride) {
            // Date
            String dateStr = ride.getRequestedAt();
            if (dateStr != null && !dateStr.isEmpty()) {
                tvDate.setText(formatDate(dateStr));
            } else {
                tvDate.setText("");
            }

            // Status
            String status = ride.getStatus();
            if (status != null) {
                tvStatus.setText(status.toUpperCase(Locale.getDefault()));
                int color;
                switch (status) {
                    case "completed":
                        color = ContextCompat.getColor(itemView.getContext(), R.color.status_success);
                        break;
                    case "cancelled":
                        color = ContextCompat.getColor(itemView.getContext(), R.color.status_error);
                        break;
                    default:
                        color = ContextCompat.getColor(itemView.getContext(), R.color.primary_cyan);
                        break;
                }
                tvStatus.setTextColor(color);
            }

            // Addresses
            tvPickup.setText(ride.getPickupAddress() != null ? ride.getPickupAddress() : "Pickup");
            tvDropoff.setText(ride.getDropoffAddress() != null ? ride.getDropoffAddress() : "Destination");

            // Fare
            tvFare.setText(String.format(Locale.getDefault(), "N$%.0f", ride.getFare()));

            // Distance
            if (ride.getDistanceKm() > 0) {
                tvDistance.setText(String.format(Locale.getDefault(), "%.1f km", ride.getDistanceKm()));
            } else {
                tvDistance.setText("");
            }
        }

        private String formatDate(String dateStr) {
            try {
                // Try ISO format
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = isoFormat.parse(dateStr);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                return date != null ? displayFormat.format(date) : dateStr;
            } catch (ParseException e) {
                return dateStr;
            }
        }
    }
}
