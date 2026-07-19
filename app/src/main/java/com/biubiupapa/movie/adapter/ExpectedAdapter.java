package com.biubiupapa.movie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biubiupapa.movie.R;
import com.biubiupapa.movie.OnMovieClickListener;
import com.biubiupapa.movie.model.MostExpectedResponse;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 最受期待电影适配器
 * biubiupapa
 */
public class ExpectedAdapter extends RecyclerView.Adapter<ExpectedAdapter.ExpectedViewHolder> {

    private final Context context;
    private final List<MostExpectedResponse.ExpectedMovie> list = new ArrayList<>();
    private final OnMovieClickListener listener;

    public ExpectedAdapter(Context context, OnMovieClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setMovies(List<MostExpectedResponse.ExpectedMovie> movies) {
        list.clear();
        if (movies != null) {
            list.addAll(movies);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expected, parent, false);
        return new ExpectedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpectedViewHolder holder, int position) {
        MostExpectedResponse.ExpectedMovie item = list.get(position);
        holder.tvTitle.setText(item.getName() != null ? item.getName() : "未知电影");
        holder.tvWish.setText(formatCount(item.getWish()) + "人想看");

        if (item.getComingTitle() != null && !item.getComingTitle().isEmpty()) {
            holder.tvRelease.setText(item.getComingTitle());
            holder.tvRelease.setVisibility(View.VISIBLE);
        } else {
            holder.tvRelease.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(item.getImg())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> listener.onMovieClick(item.toMovie()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ExpectedViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPoster;
        final TextView tvTitle;
        final TextView tvWish;
        final TextView tvRelease;

        ExpectedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvWish = itemView.findViewById(R.id.tvMovieRating);
            tvRelease = itemView.findViewById(R.id.tvRelease);
        }
    }

    private static String formatCount(long count) {
        if (count >= 100000000) {
            return String.format("%.1f亿", count / 100000000.0);
        }
        if (count >= 10000) {
            return String.format("%.1f万", count / 10000.0);
        }
        return String.valueOf(count);
    }
}
