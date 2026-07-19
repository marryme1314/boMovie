package com.biubiupapa.movie.util;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 用于请求客户端
 * biubiupapa
 */
public final class RetrofitClient {

    private static final String BASE_URL = "https://apis.netstart.cn/maoyan/";
    private static ApiService apiService;

    private RetrofitClient() {
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request request = chain.request().newBuilder()
                                .header("User-Agent",
                                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                                .header("Accept", "application/json")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
