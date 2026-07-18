package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 城市选择页面
 * biubiupapa
 */
public class CityPickerActivity extends AppCompatActivity {

    public static final String RESULT_CITY_ID = "city_id";
    public static final String RESULT_CITY_NAME = "city_name";

    private RecyclerView rvHotCities;
    private RecyclerView rvCityList;
    private EditText etSearch;

    private CityTagAdapter hotAdapter;
    private CityListAdapter allAdapter;
    private List<City> allCities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);

        ImageView ivBack = findViewById(R.id.ivBack);
        rvHotCities = findViewById(R.id.rvHotCities);
        rvCityList = findViewById(R.id.rvCityList);
        etSearch = findViewById(R.id.etSearch);

        ivBack.setOnClickListener(v -> finish());

        hotAdapter = new CityTagAdapter(this::onCitySelected);
        rvHotCities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHotCities.setAdapter(hotAdapter);

        allAdapter = new CityListAdapter(this::onCitySelected);
        rvCityList.setLayoutManager(new LinearLayoutManager(this));
        rvCityList.setAdapter(allAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterCities(s.toString().trim().toLowerCase());
            }
        });

        loadCities();
    }

    private void loadCities() {
        RetrofitClient.getApiService().getCities().enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CityResponse body = response.body();
                    allCities.clear();
                    if (body.getCities() != null) {
                        allCities.addAll(body.getCities());
                    }
                    allAdapter.setCities(allCities);

                    // 热门城市：从hot名称列表中匹配
                    List<String> hotNames = body.getHot();
                    if (hotNames != null) {
                        List<City> hotCities = new ArrayList<>();
                        for (String name : hotNames) {
                            for (City city : allCities) {
                                if (name.equals(city.getName())) {
                                    hotCities.add(city);
                                    break;
                                }
                            }
                        }
                        hotAdapter.setCities(hotCities);
                    }
                }
            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                Toast.makeText(CityPickerActivity.this, "城市数据加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCities(String query) {
        if (query.isEmpty()) {
            allAdapter.setCities(allCities);
            return;
        }
        List<City> filtered = new ArrayList<>();
        for (City city : allCities) {
            if (city.getName().toLowerCase().contains(query)
                    || city.getPinyin().toLowerCase().contains(query)) {
                filtered.add(city);
            }
        }
        allAdapter.setCities(filtered);
    }

    private void onCitySelected(City city) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CITY_ID, city.getId());
        intent.putExtra(RESULT_CITY_NAME, city.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    // 热门城市横向标签适配器
    static class CityTagAdapter extends RecyclerView.Adapter<CityTagAdapter.ViewHolder> {

        private final List<City> cities = new ArrayList<>();
        private final OnCityClickListener listener;

        CityTagAdapter(OnCityClickListener listener) {
            this.listener = listener;
        }

        void setCities(List<City> list) {
            cities.clear();
            if (list != null) cities.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_city, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            City city = cities.get(position);
            holder.tvName.setText(city.getName());
            holder.tvName.setOnClickListener(v -> listener.onCityClick(city));
        }

        @Override
        public int getItemCount() {
            return cities.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvCityName);
            }
        }
    }

    // 全部城市列表适配器
    static class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {

        private final List<City> cities = new ArrayList<>();
        private final OnCityClickListener listener;

        CityListAdapter(OnCityClickListener listener) {
            this.listener = listener;
        }

        void setCities(List<City> list) {
            cities.clear();
            if (list != null) cities.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_city_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            City city = cities.get(position);
            holder.tvName.setText(city.getName());
            holder.itemView.setOnClickListener(v -> listener.onCityClick(city));
        }

        @Override
        public int getItemCount() {
            return cities.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvCityName);
            }
        }
    }

    interface OnCityClickListener {
        void onCityClick(City city);
    }
}