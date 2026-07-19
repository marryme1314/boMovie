package com.biubiupapa.movie.model;

import com.google.gson.annotations.SerializedName;

/**
 * 搜索建议项
 * biubiupapa
 */
public class SearchSuggestItem {

    private long id;
    private String poster;
    private String name;
    private String score;
    private String ename;

    @SerializedName("catogary")
    private String category;

    private String release;

    public long getId() {
        return id;
    }

    public String getPoster() {
        return poster;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getEname() {
        return ename;
    }

    public String getCategory() {
        return category;
    }

    public String getRelease() {
        return release;
    }

    /**
     * 转换为Movie对象用于跳转详情页
     */
    public Movie toMovie() {
        Movie movie = new Movie();
        movie.setMovieId((int) id);
        movie.setName(name);
        movie.setPosterUrl(poster);
        if (score != null && !score.isEmpty()) {
            try {
                movie.setRating(Double.parseDouble(score));
            } catch (NumberFormatException ignored) {
            }
        }
        return movie;
    }
}
