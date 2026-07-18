package com.biubiupapa.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器和Movie是一对
 * biubiupapa
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final Context context;
    private final List<Movie> movieList = new ArrayList<>();
    private final OnMovieClickListener listener;
    private final boolean showReleaseDate;

    public MovieAdapter(Context context, OnMovieClickListener listener) {
        this(context, listener, false);
    }

    public MovieAdapter(Context context, OnMovieClickListener listener, boolean showReleaseDate) {
        this.context = context;
        this.listener = listener;
        this.showReleaseDate = showReleaseDate;
    }

    public void setMovies(List<Movie> movies) {
        movieList.clear();
        if (movies != null) {
            movieList.addAll(movies);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvMovieTitle.setText(movie.getTitle() != null ? movie.getTitle() : "未知电影");
        if (showReleaseDate && movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
            holder.tvMovieRating.setText(movie.getReleaseDate() + " 上映");
            holder.tvMovieRating.setTextColor(0xFF3C9FE6);
        } else {
            holder.tvMovieRating.setText(movie.getRatingText());
            holder.tvMovieRating.setTextColor(0xFFFF6B6B);
        }
        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivPoster);
        holder.itemView.setOnClickListener(v -> listener.onMovieClick(movie));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPoster;
        final TextView tvMovieTitle;
        final TextView tvMovieRating;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = itemView.findViewById(R.id.tvMovieRating);
        }
    }
}
