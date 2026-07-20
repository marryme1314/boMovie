package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class SeatSelectActivity extends AppCompatActivity {

    private TextView tvSeatMovie, tvSeatCinema, tvSeatTime, tvSelectedSeats, tvSeatTotal;
    private Button btnConfirmSeat;

    private String movieName, cinema, showTime;
    private String selectedSeats = "";
    private int seatCount = 0;
    private static final double PRICE_PER_SEAT = 35.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_select);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        movieName = intent.getStringExtra("movie");
        cinema = intent.getStringExtra("cinema");
        showTime = intent.getStringExtra("time");

        tvSeatMovie = findViewById(R.id.tvSeatMovie);
        tvSeatCinema = findViewById(R.id.tvSeatCinema);
        tvSeatTime = findViewById(R.id.tvSeatTime);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvSeatTotal = findViewById(R.id.tvSeatTotal);
        btnConfirmSeat = findViewById(R.id.btnConfirmSeat);

        tvSeatMovie.setText(movieName);
        tvSeatCinema.setText(cinema);
        tvSeatTime.setText(showTime);

        // Setup seat click listeners for all 40 seats
        setupSeatListeners();

        btnConfirmSeat.setOnClickListener(v -> confirmSeat());
    }

    private void setupSeatListeners() {
        String[] rows = {"A", "B", "C", "D", "E"};
        for (int i = 1; i <= 40; i++) {
            int seatId = getResources().getIdentifier("seat_" + i, "id", getPackageName());
            if (seatId != 0) {
                View seat = findViewById(seatId);
                if (seat != null) {
                    final int seatIndex = i;
                    seat.setOnClickListener(v -> {
                        toggleSeat(v, rows[(seatIndex - 1) / 8], ((seatIndex - 1) % 8) + 1);
                    });
                }
            }
        }
    }

    private void toggleSeat(View seat, String row, int col) {
        boolean isSelected = seat.getTag() != null && (boolean) seat.getTag();
        if (isSelected) {
            seat.setBackgroundResource(R.drawable.seat_available);
            seat.setTag(false);
            seatCount--;
            String seatLabel = row + col;
            if (selectedSeats.contains(seatLabel)) {
                selectedSeats = selectedSeats.replace(seatLabel + " ", "").replace(seatLabel, "");
            }
        } else {
            // Check if seat is occupied (light gray color)
            if (seat.getBackground() != null) {
                try {
                    android.graphics.drawable.GradientDrawable bg = (android.graphics.drawable.GradientDrawable) seat.getBackground();
                    // Simple check: just try selecting
                } catch (Exception e) {
                    // Ignore
                }
            }
            seat.setBackgroundResource(R.drawable.seat_selected);
            seat.setTag(true);
            seatCount++;
            String seatLabel = row + col;
            if (!selectedSeats.contains(seatLabel)) {
                selectedSeats += seatLabel + " ";
            }
        }
        updateSeatDisplay();
    }

    private void updateSeatDisplay() {
        if (seatCount > 0) {
            tvSelectedSeats.setText("已选: " + selectedSeats.trim());
            tvSeatTotal.setText(String.format("¥%.0f", seatCount * PRICE_PER_SEAT));
        } else {
            tvSelectedSeats.setText("暂未选座");
            tvSeatTotal.setText("¥0");
        }
    }

    private void confirmSeat() {
        if (seatCount == 0) {
            Toast.makeText(this, "请选择至少一个座位", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("movie", movieName);
        intent.putExtra("cinema", cinema);
        intent.putExtra("time", showTime);
        intent.putExtra("seats", selectedSeats.trim());
        intent.putExtra("count", seatCount);
        intent.putExtra("price", seatCount * PRICE_PER_SEAT);
        startActivity(intent);
    }
}
