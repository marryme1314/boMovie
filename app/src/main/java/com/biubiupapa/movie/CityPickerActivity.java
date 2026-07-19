package com.biubiupapa.movie;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.biubiupapa.movie.model.City;
import com.biubiupapa.movie.model.CityResponse;
import com.biubiupapa.movie.util.RetrofitClient;

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
    private static final int REQUEST_LOCATION = 1002;

    private RecyclerView rvHotCities;
    private RecyclerView rvCityList;
    private EditText etSearch;
    private LinearLayout llLocationCity;
    private TextView tvLocationCity;
    private ProgressBar progressLocation;

    private CityTagAdapter hotAdapter;
    private CityListAdapter allAdapter;
    private List<City> allCities = new ArrayList<>();
    private City locatedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);

        ImageView ivBack = findViewById(R.id.ivBack);
        rvHotCities = findViewById(R.id.rvHotCities);
        rvCityList = findViewById(R.id.rvCityList);
        etSearch = findViewById(R.id.etSearch);
        llLocationCity = findViewById(R.id.llLocationCity);
        tvLocationCity = findViewById(R.id.tvLocationCity);
        progressLocation = findViewById(R.id.progressLocation);

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
        requestLocation();
    }

    /**
     * 请求位置权限并开始定位
     * biubiupapa
     */
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, REQUEST_LOCATION);
            return;
        }
        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocation();
            } else {
                tvLocationCity.setText("定位失败（权限被拒绝）");
                progressLocation.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 开始获取位置
     * biubiupapa
     */
    private void startLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            tvLocationCity.setText("定位不可用");
            progressLocation.setVisibility(View.GONE);
            return;
        }

        // 优先网络定位，再 GPS
        Location location = null;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location == null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location != null) {
            onLocationObtained(location);
        } else {
            // 没有缓存位置，请求一次更新
            try {
                String provider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;
                locationManager.requestSingleUpdate(provider, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location loc) {
                        onLocationObtained(loc);
                        try {
                            locationManager.removeUpdates(this);
                        } catch (SecurityException ignored) {
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        tvLocationCity.setText("定位服务未开启");
                        progressLocation.setVisibility(View.GONE);
                    }
                }, getMainLooper());
            } catch (SecurityException e) {
                tvLocationCity.setText("定位失败");
                progressLocation.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 拿到经纬度后，通过 Geocoder 转城市名，再匹配城市列表
     * biubiupapa
     */
    private void onLocationObtained(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.CHINA);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getCity();
                // 有些返回 "北京市" 需要去掉 "市" 后缀
                if (cityName != null && cityName.endsWith("市")) {
                    cityName = cityName.substring(0, cityName.length() - 1);
                }
                // 有些返回的是区级，取.adminArea 省市级
                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getAdminArea();
                    if (cityName != null && cityName.endsWith("市")) {
                        cityName = cityName.substring(0, cityName.length() - 1);
                    }
                }

                if (cityName != null) {
                    matchAndShowLocatedCity(cityName);
                    return;
                }
            }
        } catch (IOException e) {
            // Geocoder 失败
        }

        tvLocationCity.setText("定位失败");
        progressLocation.setVisibility(View.GONE);
    }

    /**
     * 从城市列表中匹配定位到的城市名
     * biubiupapa
     */
    private void matchAndShowLocatedCity(String cityName) {
        // 如果城市列表已加载，直接匹配
        if (!allCities.isEmpty()) {
            City matched = matchCity(cityName);
            if (matched != null) {
                locatedCity = matched;
                tvLocationCity.setText(matched.getName());
                progressLocation.setVisibility(View.GONE);
                llLocationCity.setOnClickListener(v -> onCitySelected(locatedCity));
                return;
            }
        }

        // 城市列表未加载完，先把名字显示出来，等加载完再匹配
        tvLocationCity.setText(cityName);
        progressLocation.setVisibility(View.GONE);
    }

    private City matchCity(String cityName) {
        for (City city : allCities) {
            if (cityName.equals(city.getName())) {
                return city;
            }
        }
        // 模糊匹配：定位到的名字包含在城市名中，或城市名包含定位名
        for (City city : allCities) {
            if (cityName.contains(city.getName()) || city.getName().contains(cityName)) {
                return city;
            }
        }
        return null;
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

                    // 如果定位已经拿到名字但还没匹配到城市对象，现在再匹配一次
                    String currentText = tvLocationCity.getText().toString();
                    if (locatedCity == null
                            && !currentText.equals("正在定位...")
                            && !currentText.startsWith("定位失败")
                            && !currentText.startsWith("定位不可用")
                            && !currentText.startsWith("定位服务")
                            && !allCities.isEmpty()) {
                        City matched = matchCity(currentText);
                        if (matched != null) {
                            locatedCity = matched;
                            tvLocationCity.setText(matched.getName());
                            llLocationCity.setOnClickListener(v -> onCitySelected(locatedCity));
                        }
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