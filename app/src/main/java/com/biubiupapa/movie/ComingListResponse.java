package com.biubiupapa.movie;

import java.util.List;
/**
 *获取电影列表
 * biubiupapa
 */

public class ComingListResponse {
    private List<Movie> coming;

    public List<Movie> getComing() {
        return coming;
    }

    public void setComing(List<Movie> coming) {
        this.coming = coming;
    }
}
