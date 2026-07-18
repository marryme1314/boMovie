package com.biubiupapa.movie;

import java.util.List;

/**
 * 城市列表响应
 * biubiupapa
 */
public class CityResponse {

    private List<String> hot;
    private List<City> cts;

    public List<String> getHot() {
        return hot;
    }

    public void setHot(List<String> hot) {
        this.hot = hot;
    }

    public List<City> getCities() {
        return cts;
    }

    public void setCities(List<City> cts) {
        this.cts = cts;
    }
}