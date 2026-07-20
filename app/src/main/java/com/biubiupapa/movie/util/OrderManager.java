package com.biubiupapa.movie.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderManager {

    private static final String PREF_NAME = "movie_orders";
    private static final String KEY_ORDERS = "orders_list";

    private static OrderManager instance;
    private final SharedPreferences sp;
    private final Gson gson;

    private OrderManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized OrderManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrderManager(context);
        }
        return instance;
    }

    public void addOrder(MovieOrder order) {
        List<MovieOrder> orders = getOrders();
        orders.add(0, order);
        save(orders);
    }

    public List<MovieOrder> getOrders() {
        String json = sp.getString(KEY_ORDERS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<MovieOrder>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int getCount() {
        return getOrders().size();
    }

    public void clear() {
        sp.edit().remove(KEY_ORDERS).apply();
    }

    private void save(List<MovieOrder> orders) {
        sp.edit().putString(KEY_ORDERS, gson.toJson(orders)).apply();
    }

    public static class MovieOrder {
        private long orderId;
        private String movieName;
        private String cinema;
        private String showTime;
        private String seats;
        private int count;
        private double totalPrice;
        private String status; // 已支付 / 待支付 / 已取消
        private String poster;
        private long createTime;

        public MovieOrder() {
            this.orderId = System.currentTimeMillis();
            this.createTime = System.currentTimeMillis();
            this.status = "已支付";
        }

        public long getOrderId() { return orderId; }
        public void setOrderId(long orderId) { this.orderId = orderId; }

        public String getMovieName() { return movieName; }
        public void setMovieName(String movieName) { this.movieName = movieName; }

        public String getCinema() { return cinema; }
        public void setCinema(String cinema) { this.cinema = cinema; }

        public String getShowTime() { return showTime; }
        public void setShowTime(String showTime) { this.showTime = showTime; }

        public String getSeats() { return seats; }
        public void setSeats(String seats) { this.seats = seats; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getPoster() { return poster; }
        public void setPoster(String poster) { this.poster = poster; }

        public long getCreateTime() { return createTime; }

        public String getOrderIdText() {
            return String.valueOf(orderId);
        }

        public String getTimeText() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            return sdf.format(new Date(createTime));
        }

        public String getPriceText() {
            return String.format("¥%.2f", totalPrice);
        }
    }
}
