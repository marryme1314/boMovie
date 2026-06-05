package com.xgd.demo;
import java.util.List;

public class MovieResponse {
    private String title;
    private List<Movie> movieList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }


    public List<Movie> getData() {
        return movieList;
    }
}
