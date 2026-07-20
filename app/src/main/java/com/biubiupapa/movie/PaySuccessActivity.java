package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class PaySuccessActivity extends AppCompatActivity {

    private TextView tvSuccessAmount, tvSuccessMovie, tvSuccessCinema, tvSuccessTime, tvSuccessSeats;
    private Button btnViewOrder, btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        tvSuccessAmount = findViewById(R.id.tvSuccessAmount);
        tvSuccessMovie = findViewById(R.id.tvSuccessMovie);
        tvSuccessCinema = findViewById(R.id.tvSuccessCinema);
        tvSuccessTime = findViewById(R.id.tvSuccessTime);
        tvSuccessSeats = findViewById(R.id.tvSuccessSeats);
        btnViewOrder = findViewById(R.id.btnViewOrder);
        btnBackHome = findViewById(R.id.btnBackHome);

        Intent intent = getIntent();
        tvSuccessAmount.setText(intent.getStringExtra("amount"));
        tvSuccessMovie.setText("影片: " + intent.getStringExtra("movie"));
        tvSuccessCinema.setText("影院: " + intent.getStringExtra("cinema"));
        tvSuccessTime.setText("场次: " + intent.getStringExtra("time"));
        tvSuccessSeats.setText("座位: " + intent.getStringExtra("seats"));

        btnViewOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderListActivity.class));
            finish();
        });

        btnBackHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
        });
    }
}
