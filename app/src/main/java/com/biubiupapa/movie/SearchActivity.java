package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.biubiupapa.movie.adapter.SearchResultAdapter;
import com.biubiupapa.movie.model.Movie;
import com.biubiupapa.movie.model.SearchSuggestItem;
import com.biubiupapa.movie.model.SearchMovieResponse;
import com.biubiupapa.movie.util.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 电影搜索页面
 * biubiupapa
 */
public class SearchActivity extends AppCompatActivity {

    private static final int CITY_ID = 1;
    private static final long SEARCH_DELAY_MS = 400L;

    private EditText etSearch;
    private RecyclerView rvSuggestList;
    private RecyclerView rvSearchResults;
    private ProgressBar progressSearch;
    private TextView tvEmpty;
    private TextView tvSearchBtn;

    private SuggestAdapter suggestAdapter;
    private SearchResultAdapter resultAdapter;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etSearch = findViewById(R.id.etSearch);
        tvSearchBtn = findViewById(R.id.tvSearch);
        rvSuggestList = findViewById(R.id.rvSuggestList);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        progressSearch = findViewById(R.id.progressSearch);
        tvEmpty = findViewById(R.id.tvEmpty);

        OnMovieClickListener openDetail = movie -> {
            Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        };

        suggestAdapter = new SuggestAdapter(item -> openDetail.onMovieClick(item.toMovie()));
        rvSuggestList.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestList.setAdapter(suggestAdapter);

        resultAdapter = new SearchResultAdapter(this, openDetail);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(resultAdapter);

        // 输入时自动搜索建议
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    rvSuggestList.setVisibility(View.GONE);
                    rvSearchResults.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.GONE);
                    return;
                }
                // 延迟搜索建议
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> loadSuggest(keyword);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }
        });

        // 点击搜索按钮执行完整搜索
        tvSearchBtn.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                rvSuggestList.setVisibility(View.GONE);
                performSearch(keyword, 0);
            }
        });
    }

    private void loadSuggest(String keyword) {
        RetrofitClient.getApiService().searchSuggest(keyword, CITY_ID)
                .enqueue(new Callback<List<SearchSuggestItem>>() {
                    @Override
                    public void onResponse(Call<List<SearchSuggestItem>> call,
                                           Response<List<SearchSuggestItem>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            suggestAdapter.setItems(response.body());
                            rvSuggestList.setVisibility(View.VISIBLE);
                            rvSearchResults.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            rvSuggestList.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SearchSuggestItem>> call, Throwable t) {
                        // 静默失败
                    }
                });
    }

    private void performSearch(String keyword, int offset) {
        rvSearchResults.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        progressSearch.setVisibility(View.VISIBLE);

        String url = "search/movies?keyword=" + keyword + "&ci=" + CITY_ID + "&offset=" + offset;
        RetrofitClient.getApiService().searchMovies(url)
                .enqueue(new Callback<SearchMovieResponse>() {
                    @Override
                    public void onResponse(Call<SearchMovieResponse> call,
                                           Response<SearchMovieResponse> response) {
                        progressSearch.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            SearchMovieResponse.SearchMovies movies = response.body().getMovies();
                            if (movies != null && movies.getList() != null
                                    && !movies.getList().isEmpty()) {
                                resultAdapter.setMovies(movies.getList());
                                rvSearchResults.setVisibility(View.VISIBLE);
                                saveSearchHistory(keyword);
                            } else {
                                tvEmpty.setText("未找到相关电影");
                                tvEmpty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvEmpty.setText("搜索结果为空");
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchMovieResponse> call, Throwable t) {
                        progressSearch.setVisibility(View.GONE);
                        tvEmpty.setText("搜索失败，请检查网络");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void saveSearchHistory(String keyword) {
        android.content.SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        String history = sp.getString("history", "");
        if (!history.contains(keyword)) {
            history = keyword + "," + history;
            if (history.length() > 500) {
                history = history.substring(0, 500);
            }
            sp.edit().putString("history", history).apply();
        }
    }

    // 搜索建议适配器
    static class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {

        private final List<SearchSuggestItem> items = new ArrayList<>();
        private final OnSuggestClickListener listener;

        SuggestAdapter(OnSuggestClickListener listener) {
            this.listener = listener;
        }

        void setItems(List<SearchSuggestItem> list) {
            items.clear();
            if (list != null) items.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_suggest, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SearchSuggestItem item = items.get(position);
            holder.tvName.setText(item.getName() != null ? item.getName() : "");
            holder.itemView.setOnClickListener(v -> listener.onSuggestClick(item));

            // 拼接信息行：分类 + 上映日期
            StringBuilder info = new StringBuilder();
            if (item.getCategory() != null && !item.getCategory().isEmpty()) {
                info.append(item.getCategory());
            }
            if (item.getRelease() != null && !item.getRelease().isEmpty()) {
                if (info.length() > 0) info.append("  ");
                info.append(item.getRelease());
            }
            holder.tvInfo.setText(info.toString());

            // 评分
            if (item.getScore() != null && !item.getScore().isEmpty()) {
                holder.tvScore.setText(item.getScore());
                holder.tvScore.setVisibility(View.VISIBLE);
            } else {
                holder.tvScore.setVisibility(View.GONE);
            }

            Glide.with(holder.itemView.getContext())
                    .load(item.getPoster())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.ivPoster);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final android.widget.ImageView ivPoster;
            final TextView tvName;
            final TextView tvInfo;
            final TextView tvScore;

            ViewHolder(View itemView) {
                super(itemView);
                ivPoster = itemView.findViewById(R.id.ivPoster);
                tvName = itemView.findViewById(R.id.tvName);
                tvInfo = itemView.findViewById(R.id.tvInfo);
                tvScore = itemView.findViewById(R.id.tvScore);
            }
        }
    }

    interface OnSuggestClickListener {
        void onSuggestClick(SearchSuggestItem item);
    }
}
