package com.biubiupapa.movie.model;

/**
 * 响应
 * biubiupapa
 */
public class MovieIntroResponse {
    private DataWrapper data;

    public Movie getMovie() {
        return data != null ? data.movie : null;
    }

    private static class DataWrapper {
        private Movie movie;
    }
}
