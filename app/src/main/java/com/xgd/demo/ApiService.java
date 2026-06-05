package com.xgd.demo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("index/topRatedMovies")
    Call<MovieResponse> getTopRatedMovies();

    @GET("index/movieOnInfoList")
    Call<MovieResponse> getHotMovies(@Query("ci") String cityId);

    @GET("index/comingList")
    Call<ComingListResponse> getComingList(
            @Query("ci") String cityId,
            @Query("limit") int limit
    );

    @GET("movie/intro")
    Call<MovieIntroResponse> getMovieIntro(@Query("movieId") int movieId);
}
