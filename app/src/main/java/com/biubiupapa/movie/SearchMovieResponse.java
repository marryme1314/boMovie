package com.biubiupapa.movie;

import java.util.List;

/**
 * 电影搜索响应
 * biubiupapa
 */
public class SearchMovieResponse {

    private boolean success;
    private SearchMovies movies;

    public boolean isSuccess() {
        return success;
    }

    public SearchMovies getMovies() {
        return movies;
    }

    public static class SearchMovies {

        private List<Movie> list;
        private int total;
        private String moreDesc;

        public List<Movie> getList() {
            return list;
        }

        public int getTotal() {
            return total;
        }

        public String getMoreDesc() {
            return moreDesc;
        }
    }
}