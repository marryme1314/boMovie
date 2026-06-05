package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CITY_ID = "1";
    private static final int COMING_LIMIT = 10;
    private static final long AUTO_SCROLL_DELAY_MS = 3500L;

    private ViewPager2 vpHotBanner;
    private LinearLayout llBannerIndicators;
    private RecyclerView rvTopRated;
    private RecyclerView rvComing;
    private ProgressBar progressBar;
    private TextView tvMessage;
    private TextView tvTopRatedHeader;

    private BannerAdapter bannerAdapter;
    private MovieAdapter topRatedAdapter;
    private MovieAdapter comingAdapter;

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private final AtomicInteger pendingRequests = new AtomicInteger(3);
/**
 * 开始加载
 * biubiupapa
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpHotBanner = findViewById(R.id.vpHotBanner);
        llBannerIndicators = findViewById(R.id.llBannerIndicators);
        rvTopRated = findViewById(R.id.rvTopRated);
        rvComing = findViewById(R.id.rvComing);
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
        tvTopRatedHeader = findViewById(R.id.tvTopRatedHeader);

        OnMovieClickListener openDetail = movie -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        };

        bannerAdapter = new BannerAdapter(this);
        bannerAdapter.setOnItemClickListener(openDetail);
        vpHotBanner.setAdapter(bannerAdapter);
        vpHotBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateBannerIndicators(position);
            }
        });

        topRatedAdapter = new MovieAdapter(this, openDetail);
        comingAdapter = new MovieAdapter(this, openDetail, true);

        rvTopRated.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTopRated.setAdapter(topRatedAdapter);

        rvComing.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvComing.setAdapter(comingAdapter);

        loadAllData();
    }

    private void loadAllData() {
        showLoading(true);
        showMessage("正在加载电影数据...");
        pendingRequests.set(3);

        ApiService api = RetrofitClient.getApiService();
        api.getHotMovies(CITY_ID).enqueue(wrapCallback(this::onHotMoviesLoaded));
        api.getTopRatedMovies().enqueue(wrapCallback(this::onTopRatedLoaded));
        api.getComingList(CITY_ID, COMING_LIMIT).enqueue(wrapCallback(this::onComingLoaded));
    }

    private <T> Callback<T> wrapCallback(DataConsumer<T> consumer) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    consumer.accept(response.body());
                }
                checkAllRequestsDone();
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                checkAllRequestsDone();
            }
        };
    }

    private void onHotMoviesLoaded(MovieResponse response) {
        List<Movie> movies = response.getMovieList();
        if (movies != null && !movies.isEmpty()) {
            bannerAdapter.setMovies(movies);
            setupBannerIndicators(bannerAdapter.getItemCount());
            startAutoScroll();
            vpHotBanner.setVisibility(View.VISIBLE);
            llBannerIndicators.setVisibility(View.VISIBLE);
        }
    }

    private void onTopRatedLoaded(MovieResponse response) {
        List<Movie> movies = response.getMovieList();
        if (movies != null && !movies.isEmpty()) {
            String title = response.getTitle();
            tvTopRatedHeader.setText(title != null && !title.isEmpty() ? title : "最受好评");
            topRatedAdapter.setMovies(movies);
            rvTopRated.setVisibility(View.VISIBLE);
        }
    }

    private void onComingLoaded(ComingListResponse response) {
        List<Movie> movies = response.getComing();
        if (movies != null && !movies.isEmpty()) {
            comingAdapter.setMovies(movies);
            rvComing.setVisibility(View.VISIBLE);
        }
    }

    private void checkAllRequestsDone() {
        if (pendingRequests.decrementAndGet() <= 0) {
            showLoading(false);
            boolean hasContent = bannerAdapter.getItemCount() > 0
                    || topRatedAdapter.getItemCount() > 0
                    || comingAdapter.getItemCount() > 0;
            if (hasContent) {
                tvMessage.setVisibility(View.GONE);
            } else {
                showMessage("暂无电影数据，请稍后重试");
            }
        }
    }

    private void setupBannerIndicators(int count) {
        llBannerIndicators.removeAllViews();
        int indicatorCount = Math.min(count, 8);
        for (int i = 0; i < indicatorCount; i++) {
            View dot = new View(this);
            int size = (int) (8 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            int margin = (int) (4 * getResources().getDisplayMetrics().density);
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == 0
                    ? R.drawable.banner_indicator_dot_selected
                    : R.drawable.banner_indicator_dot);
            llBannerIndicators.addView(dot);
        }
    }

    private void updateBannerIndicators(int selected) {
        int childCount = llBannerIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            llBannerIndicators.getChildAt(i).setBackgroundResource(
                    i == selected
                            ? R.drawable.banner_indicator_dot_selected
                            : R.drawable.banner_indicator_dot);
        }
    }

    private void startAutoScroll() {
        stopAutoScroll();
        if (bannerAdapter.getItemCount() <= 1) {
            return;
        }
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int next = (vpHotBanner.getCurrentItem() + 1) % bannerAdapter.getItemCount();
                vpHotBanner.setCurrentItem(next, true);
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY_MS);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY_MS);
    }

    private void stopAutoScroll() {
        if (autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollRunnable = null;
        }
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showMessage(String message) {
        tvMessage.setText(message);
        tvMessage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bannerAdapter.getItemCount() > 1) {
            startAutoScroll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @FunctionalInterface
    private interface DataConsumer<T> {
        void accept(T data);
    }
}
