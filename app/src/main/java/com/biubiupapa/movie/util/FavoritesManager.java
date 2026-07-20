package com.biubiupapa.movie.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.biubiupapa.movie.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {

    private static final String PREF_NAME = "movie_favorites";
    private static final String KEY_FAVORITES = "favorites_list";

    private static FavoritesManager instance;
    private final SharedPreferences sp;
    private final Gson gson;

    private FavoritesManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized FavoritesManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritesManager(context);
        }
        return instance;
    }

    public void toggle(Movie movie) {
        List<Movie> favorites = getFavorites();
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getMovieId() == movie.getMovieId()) {
                favorites.remove(i);
                save(favorites);
                return;
            }
        }
        favorites.add(movie);
        save(favorites);
    }

    public boolean isFavorite(int movieId) {
        for (Movie m : getFavorites()) {
            if (m.getMovieId() == movieId) {
                return true;
            }
        }
        return false;
    }

    public void remove(int movieId) {
        List<Movie> favorites = getFavorites();
        favorites.removeIf(m -> m.getMovieId() == movieId);
        save(favorites);
    }

    public List<Movie> getFavorites() {
        String json = sp.getString(KEY_FAVORITES, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<Movie>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int getCount() {
        return getFavorites().size();
    }

    public void clear() {
        sp.edit().remove(KEY_FAVORITES).apply();
    }

    private void save(List<Movie> favorites) {
        sp.edit().putString(KEY_FAVORITES, gson.toJson(favorites)).apply();
    }
}
