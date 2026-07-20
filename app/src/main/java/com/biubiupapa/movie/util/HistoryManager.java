package com.biubiupapa.movie.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.biubiupapa.movie.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {

    private static final String PREF_NAME = "movie_history";
    private static final String KEY_HISTORY = "history_list";
    private static final int MAX_HISTORY = 50;

    private static HistoryManager instance;
    private final SharedPreferences sp;
    private final Gson gson;

    private HistoryManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized HistoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryManager(context);
        }
        return instance;
    }

    public void add(Movie movie) {
        List<Movie> history = getHistory();
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getMovieId() == movie.getMovieId()) {
                history.remove(i);
                break;
            }
        }
        history.add(0, movie);
        if (history.size() > MAX_HISTORY) {
            history = history.subList(0, MAX_HISTORY);
        }
        save(history);
    }

    public List<Movie> getHistory() {
        String json = sp.getString(KEY_HISTORY, "");
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

    public void clear() {
        sp.edit().remove(KEY_HISTORY).apply();
    }

    public void remove(int movieId) {
        List<Movie> history = getHistory();
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getMovieId() == movieId) {
                history.remove(i);
                save(history);
                return;
            }
        }
    }

    public int getCount() {
        return getHistory().size();
    }

    private void save(List<Movie> history) {
        String json = gson.toJson(history);
        sp.edit().putString(KEY_HISTORY, json).apply();
    }
}