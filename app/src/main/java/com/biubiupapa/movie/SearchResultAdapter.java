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
 * 搜索结果纵向列表适配器
 * biubiupapa
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> movieList = new ArrayList<>();
    private final OnMovieClickListener listener;

    public SearchResultAdapter(Context context, OnMovieClickListener listener) {
        this.context = context;
        this.listener = listener;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "未知电影");

        double rating = movie.getRating();
        if (rating > 0) {
            holder.tvRating.setText(String.format("评分 %.1f", rating));
            holder.tvRating.setVisibility(View.VISIBLE);
        } else {
            holder.tvRating.setVisibility(View.GONE);
        }

        // 拼接信息行
        StringBuilder info = new StringBuilder();
        if (movie.getCat() != null && !movie.getCat().isEmpty()) {
            info.append(movie.getCat());
        }
        if (movie.getDir() != null && !movie.getDir().isEmpty()) {
            if (info.length() > 0) info.append(" | ");
            info.append("导演: ").append(movie.getDir());
        }
        holder.tvInfo.setText(info.toString());

        // 上映日期
        String release = movie.getReleaseDate();
        if (release != null && !release.isEmpty()) {
            holder.tvRelease.setText("上映: " + release);
            holder.tvRelease.setVisibility(View.VISIBLE);
        } else {
            holder.tvRelease.setVisibility(View.GONE);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPoster;
        final TextView tvTitle;
        final TextView tvRating;
        final TextView tvInfo;
        final TextView tvRelease;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvRating = itemView.findViewById(R.id.tvMovieRating);
            tvInfo = itemView.findViewById(R.id.tvMovieInfo);
            tvRelease = itemView.findViewById(R.id.tvMovieRelease);
        }
    }
}