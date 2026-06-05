package com.biubiupapa.movie;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivDetailPoster;
    private TextView tvDetailTitle;
    private TextView tvDetailEnName;
    private TextView tvDetailTagline;
    private TextView tvDetailRating;
    private TextView tvDetailDuration;
    private TextView tvDetailRegion;
    private TextView tvDetailStats;
    private TextView tvDetailCategory;
    private TextView tvDetailDirector;
    private TextView tvDetailStars;
    private TextView tvDetailAlias;
    private TextView tvDetailRelease;
    private TextView tvDetailVersion;
    private TextView tvDetailSynopsis;
    private TextView tvDetailError;
    private ProgressBar progressDetail;

    private Movie movie;
/**
 * 电影详情页展示功能，包括评分等等
 * biubiupapa
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        bindViews();
        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie == null) {
            showError("未收到电影数据");
            return;
        }

        bindMovie(movie);
        loadMovieIntro(movie.getMovieId());
    }

    private void bindViews() {
        ivDetailPoster = findViewById(R.id.ivDetailPoster);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailEnName = findViewById(R.id.tvDetailEnName);
        tvDetailTagline = findViewById(R.id.tvDetailTagline);
        tvDetailRating = findViewById(R.id.tvDetailRating);
        tvDetailDuration = findViewById(R.id.tvDetailDuration);
        tvDetailRegion = findViewById(R.id.tvDetailRegion);
        tvDetailStats = findViewById(R.id.tvDetailStats);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailDirector = findViewById(R.id.tvDetailDirector);
        tvDetailStars = findViewById(R.id.tvDetailStars);
        tvDetailAlias = findViewById(R.id.tvDetailAlias);
        tvDetailRelease = findViewById(R.id.tvDetailRelease);
        tvDetailVersion = findViewById(R.id.tvDetailVersion);
        tvDetailSynopsis = findViewById(R.id.tvDetailSynopsis);
        tvDetailError = findViewById(R.id.tvDetailError);
        progressDetail = findViewById(R.id.progressDetail);
    }

    private void loadMovieIntro(int movieId) {
        if (movieId <= 0) {
            progressDetail.setVisibility(View.GONE);
            if (TextUtils.isEmpty(movie.getDra())) {
                tvDetailSynopsis.setText("暂无剧情简介");
            }
            return;
        }

        RetrofitClient.getApiService().getMovieIntro(movieId).enqueue(new Callback<MovieIntroResponse>() {
            @Override
            public void onResponse(Call<MovieIntroResponse> call, Response<MovieIntroResponse> response) {
                progressDetail.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Movie detail = response.body().getMovie();
                    if (detail != null) {
                        movie.mergeFrom(detail);
                        bindMovie(movie);
                        return;
                    }
                }
                onIntroLoadFailed();
            }

            @Override
            public void onFailure(Call<MovieIntroResponse> call, Throwable t) {
                progressDetail.setVisibility(View.GONE);
                onIntroLoadFailed();
            }
        });
    }

    private void onIntroLoadFailed() {
        if (TextUtils.isEmpty(movie.getDra())) {
            tvDetailSynopsis.setText("简介加载失败，请检查网络后重试");
        }
    }

    private void bindMovie(Movie movie) {
        tvDetailTitle.setText(movie.getTitle() != null ? movie.getTitle() : "未知电影");

        setOptionalText(tvDetailEnName, movie.getEnm());
        setOptionalText(tvDetailTagline, movie.getScm());

        double rating = movie.getRating();
        if (rating > 0) {
            tvDetailRating.setText(String.format("%.1f", rating));
        } else {
            tvDetailRating.setText("--");
        }

        tvDetailDuration.setText(movie.getDurationText());
        tvDetailRegion.setText(
                !TextUtils.isEmpty(movie.getSrc()) ? movie.getSrc() : "地区未知");

        StringBuilder stats = new StringBuilder();
        if (movie.getWish() > 0) {
            stats.append(movie.getWishText());
        }
        if (movie.getWatched() > 0) {
            if (stats.length() > 0) {
                stats.append("  ·  ");
            }
            stats.append(movie.getWatchedText());
        }
        String commentText = movie.getCommentText();
        if (!commentText.isEmpty()) {
            if (stats.length() > 0) {
                stats.append("  ·  ");
            }
            stats.append(commentText);
        }
        setOptionalText(tvDetailStats, stats.toString());

        setInfoLine(tvDetailCategory, "类型", movie.getCat());
        setInfoLine(tvDetailDirector, "导演", movie.getDir());
        setInfoLine(tvDetailStars, "主演", movie.getStar());
        setInfoLine(tvDetailAlias, "又名", movie.getFilmAlias());

        String release = !TextUtils.isEmpty(movie.getPubDesc())
                ? movie.getPubDesc()
                : movie.getReleaseDate();
        if (!TextUtils.isEmpty(release)) {
            tvDetailRelease.setText("上映：" + release);
            tvDetailRelease.setVisibility(View.VISIBLE);
        } else {
            tvDetailRelease.setText("上映：待定");
        }

        String versionLine = movie.getVer();
        if (!TextUtils.isEmpty(movie.getOriLang())) {
            versionLine = (versionLine != null ? versionLine + "  ·  " : "") + movie.getOriLang();
        }
        setInfoLine(tvDetailVersion, "制式", versionLine);

        if (!TextUtils.isEmpty(movie.getDra())) {
            tvDetailSynopsis.setText(movie.getDra().trim());
        } else {
            tvDetailSynopsis.setText("暂无剧情简介");
        }

        Glide.with(this)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivDetailPoster);
    }

    private void setInfoLine(TextView view, String label, String value) {
        if (!TextUtils.isEmpty(value)) {
            view.setText(label + "：" + value);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void setOptionalText(TextView view, String text) {
        if (!TextUtils.isEmpty(text)) {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        progressDetail.setVisibility(View.GONE);
        tvDetailError.setText(message);
        tvDetailError.setVisibility(View.VISIBLE);
        tvDetailSynopsis.setText(message);
    }
}
