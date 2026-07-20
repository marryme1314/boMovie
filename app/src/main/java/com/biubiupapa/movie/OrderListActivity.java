package com.biubiupapa.movie;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biubiupapa.movie.util.OrderManager;
import com.biubiupapa.movie.util.OrderManager.MovieOrder;

import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private TextView tvEmpty;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvOrders = findViewById(R.id.rvOrders);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new OrderAdapter();
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        List<MovieOrder> orders = OrderManager.getInstance(this).getOrders();
        if (orders.isEmpty()) {
            rvOrders.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvOrders.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            adapter.setOrders(orders);
        }
    }

    static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        private List<MovieOrder> orders;

        void setOrders(List<MovieOrder> orders) {
            this.orders = orders;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MovieOrder order = orders.get(position);
            holder.tvOrderId.setText("订单号: " + order.getOrderIdText());
            holder.tvOrderStatus.setText(order.getStatus());
            holder.tvMovieName.setText(order.getMovieName());
            holder.tvCinema.setText("影院: " + order.getCinema());
            holder.tvShowTime.setText("场次: " + order.getShowTime());
            holder.tvSeats.setText("座位: " + order.getSeats());
            holder.tvCount.setText(String.valueOf(order.getCount()));
            holder.tvTotalPrice.setText(order.getPriceText());

            // Set status color
            if ("已支付".equals(order.getStatus())) {
                holder.tvOrderStatus.setBackgroundResource(R.drawable.badge_primary);
            } else if ("待支付".equals(order.getStatus())) {
                holder.tvOrderStatus.setBackgroundResource(R.drawable.badge_primary);
            } else {
                holder.tvOrderStatus.setBackgroundResource(R.drawable.badge_primary);
            }

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), 
                    "订单号: " + order.getOrderIdText(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return orders != null ? orders.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderId, tvOrderStatus, tvMovieName, tvCinema, tvShowTime, tvSeats, tvCount, tvTotalPrice;

            ViewHolder(View itemView) {
                super(itemView);
                tvOrderId = itemView.findViewById(R.id.tvOrderId);
                tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
                tvMovieName = itemView.findViewById(R.id.tvMovieName);
                tvCinema = itemView.findViewById(R.id.tvCinema);
                tvShowTime = itemView.findViewById(R.id.tvShowTime);
                tvSeats = itemView.findViewById(R.id.tvSeats);
                tvCount = itemView.findViewById(R.id.tvCount);
                tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            }
        }
    }
}
