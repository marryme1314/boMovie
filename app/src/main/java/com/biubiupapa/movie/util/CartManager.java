package com.biubiupapa.movie.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.biubiupapa.movie.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String PREF_NAME = "movie_cart";
    private static final String KEY_CART = "cart_list";

    private static CartManager instance;
    private final SharedPreferences sp;
    private final Gson gson;

    private CartManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    public void add(Movie movie) {
        List<CartItem> cart = getCart();
        for (CartItem item : cart) {
            if (item.getMovieId() == movie.getMovieId()) {
                item.setCount(item.getCount() + 1);
                save(cart);
                return;
            }
        }
        CartItem newItem = new CartItem();
        newItem.setMovieId(movie.getMovieId());
        newItem.setName(movie.getName());
        newItem.setPoster(movie.getPosterUrl());
        newItem.setPrice(35.0);
        newItem.setCount(1);
        cart.add(newItem);
        save(cart);
    }

    public void remove(int movieId) {
        List<CartItem> cart = getCart();
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getMovieId() == movieId) {
                cart.remove(i);
                save(cart);
                return;
            }
        }
    }

    public void updateCount(int movieId, int count) {
        List<CartItem> cart = getCart();
        for (CartItem item : cart) {
            if (item.getMovieId() == movieId) {
                item.setCount(count);
                save(cart);
                return;
            }
        }
    }

    public List<CartItem> getCart() {
        String json = sp.getString(KEY_CART, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<CartItem>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void clear() {
        sp.edit().remove(KEY_CART).apply();
    }

    public int getCount() {
        int total = 0;
        for (CartItem item : getCart()) {
            total += item.getCount();
        }
        return total;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : getCart()) {
            total += item.getPrice() * item.getCount();
        }
        return total;
    }

    public boolean contains(int movieId) {
        for (CartItem item : getCart()) {
            if (item.getMovieId() == movieId) {
                return true;
            }
        }
        return false;
    }

    private void save(List<CartItem> cart) {
        String json = gson.toJson(cart);
        sp.edit().putString(KEY_CART, json).apply();
    }

    public static class CartItem {
        private int movieId;
        private String name;
        private String poster;
        private double price;
        private int count;
        private boolean selected;

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getPriceText() {
            return String.format("¥%.2f", price);
        }

        public String getTotalPriceText() {
            return String.format("¥%.2f", price * count);
        }
    }
}