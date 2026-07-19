package com.biubiupapa.movie.model;

import com.google.gson.annotations.SerializedName;

/**
 * 城市模型
 * biubiupapa
 */
public class City {

    @SerializedName("id")
    private int id;

    @SerializedName("nm")
    private String name;

    @SerializedName("py")
    private String pinyin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
