package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.biubiupapa.movie.util.CartManager;
import com.biubiupapa.movie.util.OrderManager;
import com.biubiupapa.movie.util.OrderManager.MovieOrder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {

    private TextView tvOrderMovie, tvOrderCinema, tvOrderTime, tvOrderSeat;
    private TextView tvOrderCount, tvAmount, tvTotalAmount, tvBottomTotal;
    private Button btnPay;
    private RadioGroup rgPayment;

    private String movieName, cinema, showTime, seats;
    private int count = 1;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvOrderMovie = findViewById(R.id.tvOrderMovie);
        tvOrderCinema = findViewById(R.id.tvOrderCinema);
        tvOrderTime = findViewById(R.id.tvOrderTime);
        tvOrderSeat = findViewById(R.id.tvOrderSeat);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        tvAmount = findViewById(R.id.tvAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvBottomTotal = findViewById(R.id.tvBottomTotal);
        btnPay = findViewById(R.id.btnPay);
        rgPayment = findViewById(R.id.rgPayment);

        btnPay.setOnClickListener(v -> payOrder());
        loadOrderInfo();
    }

    private void loadOrderInfo() {
        Intent intent = getIntent();
        movieName = intent.getStringExtra("movie");
        cinema = intent.getStringExtra("cinema");
        showTime = intent.getStringExtra("time");
        seats = intent.getStringExtra("seats");
        count = intent.getIntExtra("count", 1);
        totalPrice = intent.getDoubleExtra("price", 0);

        if (movieName == null) {
            // Fallback: use cart data
            loadFromCart();
            return;
        }

        tvOrderMovie.setText("影片: " + movieName);
        tvOrderCinema.setText("影院: " + cinema);
        tvOrderTime.setText("时间: " + showTime);
        tvOrderSeat.setText("座位: " + seats);
        tvOrderCount.setText("数量: " + count + "张");
        tvAmount.setText(String.format("¥%.2f", totalPrice));
        tvTotalAmount.setText(String.format("¥%.2f", totalPrice));
        tvBottomTotal.setText(String.format("¥%.2f", totalPrice));
    }

    private void loadFromCart() {
        List<CartManager.CartItem> cart = CartManager.getInstance(this).getCart();
        StringBuilder movies = new StringBuilder();
        totalPrice = 0;
        count = 0;

        for (CartManager.CartItem item : cart) {
            if (movies.length() > 0) movies.append("、");
            movies.append(item.getName());
            totalPrice += item.getPrice() * item.getCount();
            count += item.getCount();
        }

        movieName = movies.toString();
        cinema = "万达影城（CBD店）";
        showTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date());
        seats = "3排8-10座";

        tvOrderMovie.setText("影片: " + movieName);
        tvOrderCinema.setText("影院: " + cinema);
        tvOrderTime.setText("时间: " + showTime);
        tvOrderSeat.setText("座位: " + seats);
        tvOrderCount.setText("数量: " + count + "张");
        tvAmount.setText(String.format("¥%.2f", totalPrice));
        tvTotalAmount.setText(String.format("¥%.2f", totalPrice));
        tvBottomTotal.setText(String.format("¥%.2f", totalPrice));
    }

    private void payOrder() {
        btnPay.setEnabled(false);
        btnPay.setText("支付中...");

        new android.os.Handler().postDelayed(() -> {
            // Save order to OrderManager
            MovieOrder order = new MovieOrder();
            order.setMovieName(movieName);
            order.setCinema(cinema);
            order.setShowTime(showTime);
            order.setSeats(seats);
            order.setCount(count);
            order.setTotalPrice(totalPrice);
            OrderManager.getInstance(this).addOrder(order);

            // Clear cart
            CartManager.getInstance(this).clear();

            // Go to success page
            Intent successIntent = new Intent(this, PaySuccessActivity.class);
            successIntent.putExtra("amount", String.format("¥%.2f", totalPrice));
            successIntent.putExtra("movie", movieName);
            successIntent.putExtra("cinema", cinema);
            successIntent.putExtra("time", showTime);
            successIntent.putExtra("seats", seats);
            startActivity(successIntent);
            finish();
        }, 1500);
    }
}
