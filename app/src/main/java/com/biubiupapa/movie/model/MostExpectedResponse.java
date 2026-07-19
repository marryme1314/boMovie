package com.biubiupapa.movie.model;

import java.util.List;

/**
 * 最受期待电影响应
 * biubiupapa
 */
public class MostExpectedResponse {

    private List<ExpectedMovie> coming;

    public List<ExpectedMovie> getComing() {
        return coming;
    }

    public void setComing(List<ExpectedMovie> coming) {
        this.coming = coming;
    }

    public static class ExpectedMovie {

        private long id;
        private String img;
        private long wish;
        private int wishst;
        private String nm;
        private String comingTitle;

        public long getId() {
            return id;
        }

        public String getImg() {
            return img;
        }

        public long getWish() {
            return wish;
        }

        public String getName() {
            return nm;
        }

        public String getComingTitle() {
            return comingTitle;
        }

        /**
         * 转换为Movie对象用于跳转详情页
         */
        public Movie toMovie() {
            Movie movie = new Movie();
            movie.setMovieId((int) id);
            movie.setName(nm);
            movie.setPosterUrl(img);
            return movie;
        }
    }
}
