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
 * 横幅展示recycleView的适配器
 * biubiupapa
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final Context context;
    private final List<Movie> movies = new ArrayList<>();
    private OnMovieClickListener listener;

    public BannerAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }

    public void setMovies(List<Movie> movieList) {
        movies.clear();
        if (movieList != null) {
            int limit = Math.min(movieList.size(), 8);
            movies.addAll(movieList.subList(0, limit));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.tvBannerTitle.setText(movie.getTitle() != null ? movie.getTitle() : "未知电影");
        holder.tvBannerRating.setText(movie.getRatingText());
        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivBannerPoster);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivBannerPoster;
        final TextView tvBannerTitle;
        final TextView tvBannerRating;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerPoster = itemView.findViewById(R.id.ivBannerPoster);
            tvBannerTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvBannerRating = itemView.findViewById(R.id.tvBannerRating);
        }
    }
}
