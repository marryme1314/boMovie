package com.biubiupapa.movie.util;

import java.util.List;

import com.biubiupapa.movie.model.ComingListResponse;
import com.biubiupapa.movie.model.MovieIntroResponse;
import com.biubiupapa.movie.model.CityResponse;
import com.biubiupapa.movie.model.MostExpectedResponse;
import com.biubiupapa.movie.model.SearchSuggestItem;
import com.biubiupapa.movie.model.SearchMovieResponse;
import com.biubiupapa.movie.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    @GET("cities.json")
    Call<CityResponse> getCities();

    @GET("index/mostExpected")
    Call<MostExpectedResponse> getMostExpected(
            @Query("ci") String cityId,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("search/suggest")
    Call<List<SearchSuggestItem>> searchSuggest(
            @Query("kw") String keyword,
            @Query("cityId") int cityId
    );

    @GET
    Call<SearchMovieResponse> searchMovies(@Url String url);
}
