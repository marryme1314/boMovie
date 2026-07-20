package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biubiupapa.movie.util.CartManager;
import com.biubiupapa.movie.util.CartManager.CartItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private TextView tvEmpty;
    private TextView tvTotal;
    private Button btnCheckout;
    private CheckBox cbSelectAll;

    private CartAdapter adapter;
    private List<CartManager.CartItem> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvCart = findViewById(R.id.rvCart);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
        cbSelectAll = findViewById(R.id.cbSelectAll);

        adapter = new CartAdapter(this::onItemClick, this::onDeleteClick);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.setAllSelected(isChecked);
            updateTotal();
        });

        btnCheckout.setOnClickListener(v -> checkout());

        // Bottom navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_cart);
            bottomNav.setOnNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return id == R.id.nav_cart;
            });
        }

        loadCart();
    }

    private void loadCart() {
        List<CartManager.CartItem> cart = CartManager.getInstance(this).getCart();
        if (cart.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            adapter.setItems(cart);
        }
        updateTotal();
    }

    private void onItemClick(CartManager.CartItem item) {
        Intent intent = new Intent(this, DetailActivity.class);
        com.biubiupapa.movie.model.Movie movie = new com.biubiupapa.movie.model.Movie();
        movie.setMovieId(item.getMovieId());
        movie.setName(item.getName());
        movie.setPosterUrl(item.getPoster());
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    private void onDeleteClick(int movieId) {
        CartManager.getInstance(this).remove(movieId);
        loadCart();
        Toast.makeText(this, "已从购物车移除", Toast.LENGTH_SHORT).show();
    }

    private void updateTotal() {
        double total = 0;
        int selectedCount = 0;
        List<CartManager.CartItem> cart = adapter.getItems();

        for (CartManager.CartItem item : cart) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getCount();
                selectedCount++;
            }
        }

        tvTotal.setText(String.format("合计: ¥%.2f", total));
        btnCheckout.setEnabled(selectedCount > 0);
        btnCheckout.setAlpha(selectedCount > 0 ? 1.0f : 0.5f);
        cbSelectAll.setChecked(selectedCount == cart.size() && !cart.isEmpty());
    }

    private void checkout() {
        List<CartManager.CartItem> cart = adapter.getItems();
        List<CartManager.CartItem> selected = new ArrayList<>();
        for (CartManager.CartItem item : cart) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }

        if (selected.isEmpty()) {
            Toast.makeText(this, "请选择要结算的商品", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("count", selected.size());
        startActivity(intent);
    }

    static class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

        private final List<CartManager.CartItem> items = new ArrayList<>();
        private final OnItemClickListener listener;
        private final OnDeleteClickListener deleteListener;

        CartAdapter(OnItemClickListener listener, OnDeleteClickListener deleteListener) {
            this.listener = listener;
            this.deleteListener = deleteListener;
        }

        void setItems(List<CartManager.CartItem> list) {
            items.clear();
            if (list != null) {
                for (CartManager.CartItem item : list) {
                    item.setSelected(true);
                    items.add(item);
                }
            }
            notifyDataSetChanged();
        }

        List<CartManager.CartItem> getItems() {
            return items;
        }

        void setAllSelected(boolean selected) {
            for (CartManager.CartItem item : items) {
                item.setSelected(selected);
            }
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CartManager.CartItem item = items.get(position);
            holder.cbSelect.setChecked(item.isSelected());
            holder.tvName.setText(item.getName());
            holder.tvPrice.setText(item.getPriceText());
            holder.tvCount.setText("x" + item.getCount());
            holder.tvTotal.setText(item.getTotalPriceText());

            Glide.with(holder.itemView.getContext())
                    .load(item.getPoster())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.ivPoster);

            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setSelected(isChecked);
            });

            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
            holder.tvDelete.setOnClickListener(v -> deleteListener.onDeleteClick(item.getMovieId()));

            holder.btnMinus.setOnClickListener(v -> {
                int newCount = item.getCount() - 1;
                if (newCount <= 0) {
                    deleteListener.onDeleteClick(item.getMovieId());
                    return;
                }
                item.setCount(newCount);
                holder.tvCount.setText("x" + newCount);
                holder.tvTotal.setText(item.getTotalPriceText());
                com.biubiupapa.movie.util.CartManager.getInstance(holder.itemView.getContext()).updateCount(item.getMovieId(), newCount);
            });

            holder.btnPlus.setOnClickListener(v -> {
                int newCount = item.getCount() + 1;
                item.setCount(newCount);
                holder.tvCount.setText("x" + newCount);
                holder.tvTotal.setText(item.getTotalPriceText());
                com.biubiupapa.movie.util.CartManager.getInstance(holder.itemView.getContext()).updateCount(item.getMovieId(), newCount);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final CheckBox cbSelect;
            final ImageView ivPoster;
            final TextView tvName;
            final TextView tvPrice;
            final TextView tvCount;
            final TextView tvTotal;
            final TextView tvDelete;
            final TextView btnMinus;
            final TextView btnPlus;

            ViewHolder(View itemView) {
                super(itemView);
                cbSelect = itemView.findViewById(R.id.cbSelect);
                ivPoster = itemView.findViewById(R.id.ivPoster);
                tvName = itemView.findViewById(R.id.tvName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvCount = itemView.findViewById(R.id.tvCount);
                tvTotal = itemView.findViewById(R.id.tvTotal);
                tvDelete = itemView.findViewById(R.id.tvDelete);
                btnMinus = itemView.findViewById(R.id.btnMinus);
                btnPlus = itemView.findViewById(R.id.btnPlus);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(CartManager.CartItem item);
    }

    interface OnDeleteClickListener {
        void onDeleteClick(int movieId);
    }
}