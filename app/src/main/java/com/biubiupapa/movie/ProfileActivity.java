package com.biubiupapa.movie;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.biubiupapa.movie.util.FavoritesManager;
import com.biubiupapa.movie.util.HistoryManager;
import com.biubiupapa.movie.util.OrderManager;
import com.biubiupapa.movie.model.Movie;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvTicketCount;
    private TextView tvHistoryCount;
    private TextView tvFavCount;
    private TextView tvUserName;
    private TextView tvUserDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTicketCount = findViewById(R.id.tvTicketCount);
        tvHistoryCount = findViewById(R.id.tvHistoryCount);
        tvFavCount = findViewById(R.id.tvFavCount);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserDesc = findViewById(R.id.tvUserDesc);

        findViewById(R.id.llMyTickets).setOnClickListener(v -> {
            startActivity(new Intent(this, OrderListActivity.class));
        });
        findViewById(R.id.llWatchHistory).setOnClickListener(v -> showHistoryList());
        findViewById(R.id.llFavorites).setOnClickListener(v -> showFavorites());
        findViewById(R.id.llSettings).setOnClickListener(v -> showSettings());
        findViewById(R.id.llFeedback).setOnClickListener(v -> showFeedback());
        findViewById(R.id.llAbout).setOnClickListener(v -> showAbout());

        // Bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profile);
            bottomNav.setOnNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_cart) {
                    startActivity(new Intent(this, CartActivity.class));
                    finish();
                    return true;
                }
                return id == R.id.nav_profile;
            });
        }

        updateUserInfo();
        updateCounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo();
        updateCounts();
    }

    private void updateUserInfo() {
        android.content.SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("logged_in", false);

        if (loggedIn) {
            tvUserName.setText(sp.getString("name", "用户"));
            tvUserDesc.setText(sp.getString("phone", ""));
        } else {
            tvUserName.setText("点击登录");
            tvUserDesc.setText("登录后享受更多功能");
            tvUserName.setOnClickListener(v -> {
                startActivityForResult(new Intent(this, LoginActivity.class), 1001);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            updateUserInfo();
        }
    }

    private void updateCounts() {
        tvTicketCount.setText("" + OrderManager.getInstance(this).getCount());
        tvHistoryCount.setText("" + HistoryManager.getInstance(this).getCount());
        tvFavCount.setText("" + FavoritesManager.getInstance(this).getCount());
    }

    private void showFavorites() {
        List<Movie> favorites = FavoritesManager.getInstance(this).getFavorites();
        if (favorites.isEmpty()) {
            Toast.makeText(this, "暂无收藏", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("我的收藏");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        for (Movie movie : favorites) {
            View item = getLayoutInflater().inflate(R.layout.item_history, null);
            ((TextView) item.findViewById(R.id.tvMovieName)).setText(movie.getName());
            ((TextView) item.findViewById(R.id.tvRating)).setText(movie.getRatingText());
            item.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            });
            layout.addView(item);
        }

        builder.setView(layout);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }

    private int getMockTicketCount() {
        return 2;
    }

    private void showTicketList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("我的订单");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        List<TicketItem> tickets = getMockTickets();
        for (TicketItem ticket : tickets) {
            View item = getLayoutInflater().inflate(R.layout.item_ticket, null);
            ((TextView) item.findViewById(R.id.tvMovieName)).setText(ticket.movieName);
            ((TextView) item.findViewById(R.id.tvCinema)).setText(ticket.cinema);
            ((TextView) item.findViewById(R.id.tvTime)).setText(ticket.time);
            ((TextView) item.findViewById(R.id.tvSeat)).setText(ticket.seat);
            ((TextView) item.findViewById(R.id.tvPrice)).setText(ticket.price);
            ((TextView) item.findViewById(R.id.tvStatus)).setText(ticket.status);
            layout.addView(item);
        }

        builder.setView(layout);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }

    private void showHistoryList() {
        List<com.biubiupapa.movie.model.Movie> history = HistoryManager.getInstance(this).getHistory();
        if (history.isEmpty()) {
            Toast.makeText(this, "暂无浏览历史", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("浏览历史");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        for (com.biubiupapa.movie.model.Movie movie : history) {
            View item = getLayoutInflater().inflate(R.layout.item_history, null);
            ((TextView) item.findViewById(R.id.tvMovieName)).setText(movie.getName());
            ((TextView) item.findViewById(R.id.tvRating)).setText(movie.getRatingText());
            item.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            });
            layout.addView(item);
        }

        builder.setView(layout);
        builder.setNegativeButton("清除", (dialog, which) -> {
            HistoryManager.getInstance(this).clear();
            Toast.makeText(this, "已清除浏览历史", Toast.LENGTH_SHORT).show();
            updateCounts();
        });
        builder.setPositiveButton("关闭", null);
        builder.show();
    }

    private void showSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        String[] settings = {"清除缓存", "版本更新", "隐私政策", "用户协议", "退出登录"};
        for (String setting : settings) {
            TextView item = new TextView(this);
            item.setText(setting);
            item.setTextSize(16);
            item.setTextColor(getResources().getColor(R.color.text_primary));
            item.setPadding(0, 16, 0, 16);
            item.setOnClickListener(v -> {
                switch (setting) {
                    case "清除缓存":
                        clearCache();
                        break;
                    case "版本更新":
                        Toast.makeText(this, "已是最新版本", Toast.LENGTH_SHORT).show();
                        break;
                    case "隐私政策":
                        Toast.makeText(this, "隐私政策：我们尊重您的隐私", Toast.LENGTH_SHORT).show();
                        break;
                    case "用户协议":
                        Toast.makeText(this, "用户协议：请遵守使用条款", Toast.LENGTH_SHORT).show();
                        break;
                    case "退出登录":
                        logout();
                        break;
                }
            });
            layout.addView(item);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(getResources().getColor(R.color.divider));
            layout.addView(divider);
        }

        builder.setView(layout);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }

    private void logout() {
        android.content.SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        sp.edit().clear().apply();
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        updateUserInfo();
    }

    private void clearCache() {
        try {
            File cacheDir = getCacheDir();
            deleteDir(cacheDir);
            File externalCacheDir = getExternalCacheDir();
            if (externalCacheDir != null) {
                deleteDir(externalCacheDir);
            }
            Toast.makeText(this, "缓存清除成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "清除失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void showFeedback() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("意见反馈");

        View view = getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        builder.setView(view);

        builder.setPositiveButton("提交", (dialog, which) -> {
            TextView etFeedback = view.findViewById(R.id.etFeedback);
            String feedback = etFeedback.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "反馈提交成功，感谢您的建议", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("关于阿博电影");
        builder.setMessage("阿博电影 v2.0.0\n\n一款专注于电影购票的应用，提供最新的电影资讯、热映电影推荐和便捷的购票服务。\n\n感谢您的使用！");
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    private List<TicketItem> getMockTickets() {
        List<TicketItem> tickets = new ArrayList<>();

        TicketItem t1 = new TicketItem();
        t1.movieName = "哪吒之魔童降世";
        t1.cinema = "万达影城（CBD店）";
        t1.time = "2026-07-20 19:30";
        t1.seat = "3排8座";
        t1.price = "¥68.00";
        t1.status = "已支付";
        tickets.add(t1);

        TicketItem t2 = new TicketItem();
        t2.movieName = "流浪地球3";
        t2.cinema = "CGV影城";
        t2.time = "2026-07-22 20:00";
        t2.seat = "5排12座";
        t2.price = "¥88.00";
        t2.status = "待支付";
        tickets.add(t2);

        return tickets;
    }

    static class TicketItem {
        String movieName;
        String cinema;
        String time;
        String seat;
        String price;
        String status;
    }
}