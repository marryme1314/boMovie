package com.xgd.demo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable {
    @SerializedName(value = "movieId", alternate = {"id"})
    private int movieId;

    @SerializedName(value = "name", alternate = {"nm"})
    private String name;

    @SerializedName(value = "poster", alternate = {"img"})
    private String poster;

    private String score;

    @SerializedName("sc")
    private double sc;

    private String rt;
    private String star;
    private String dra;
    private String cat;
    private String dir;
    private int dur;
    private String src;
    private String pubDesc;
    private String scm;
    private String ver;
    private String filmAlias;
    private String enm;
    private String oriLang;
    private long wish;
    private long watched;
    private int snum;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterUrl() {
        return poster;
    }

    public void setPosterUrl(String poster) {
        this.poster = poster;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public double getRating() {
        if (score != null && !score.isEmpty()) {
            try {
                return Double.parseDouble(score);
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        if (sc > 0) {
            return sc;
        }
        return 0.0;
    }

    public void setRating(double rating) {
        this.score = String.valueOf(rating);
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getReleaseDate() {
        return rt != null ? rt : "";
    }

    public void setReleaseDate(String rt) {
        this.rt = rt;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getRatingText() {
        double rating = getRating();
        if (rating > 0) {
            return String.format("评分 %.1f", rating);
        }
        return "暂无评分";
    }

    public String getDra() {
        return dra;
    }

    public String getCat() {
        return cat;
    }

    public String getDir() {
        return dir;
    }

    public int getDur() {
        return dur;
    }

    public String getDurationText() {
        if (dur > 0) {
            return dur + " 分钟";
        }
        return "未知";
    }

    public String getSrc() {
        return src;
    }

    public String getPubDesc() {
        return pubDesc;
    }

    public String getScm() {
        return scm;
    }

    public String getVer() {
        return ver;
    }

    public String getFilmAlias() {
        return filmAlias;
    }

    public String getEnm() {
        return enm;
    }

    public String getOriLang() {
        return oriLang;
    }

    public long getWish() {
        return wish;
    }

    public long getWatched() {
        return watched;
    }

    public int getSnum() {
        return snum;
    }

    public String getWishText() {
        return formatCount(wish) + " 人想看";
    }

    public String getWatchedText() {
        return formatCount(watched) + " 人看过";
    }

    public String getCommentText() {
        if (snum > 0) {
            return formatCount(snum) + " 条评论";
        }
        return "";
    }

    public void mergeFrom(Movie other) {
        if (other == null) {
            return;
        }
        if (other.movieId > 0) {
            movieId = other.movieId;
        }
        if (other.name != null) {
            name = other.name;
        }
        if (other.poster != null) {
            poster = other.poster;
        }
        if (other.score != null) {
            score = other.score;
        }
        if (other.sc > 0) {
            sc = other.sc;
        }
        if (other.rt != null) {
            rt = other.rt;
        }
        if (other.star != null) {
            star = other.star;
        }
        if (other.dra != null) {
            dra = other.dra;
        }
        if (other.cat != null) {
            cat = other.cat;
        }
        if (other.dir != null) {
            dir = other.dir;
        }
        if (other.dur > 0) {
            dur = other.dur;
        }
        if (other.src != null) {
            src = other.src;
        }
        if (other.pubDesc != null) {
            pubDesc = other.pubDesc;
        }
        if (other.scm != null) {
            scm = other.scm;
        }
        if (other.ver != null) {
            ver = other.ver;
        }
        if (other.filmAlias != null) {
            filmAlias = other.filmAlias;
        }
        if (other.enm != null) {
            enm = other.enm;
        }
        if (other.oriLang != null) {
            oriLang = other.oriLang;
        }
        if (other.wish > 0) {
            wish = other.wish;
        }
        if (other.watched > 0) {
            watched = other.watched;
        }
        if (other.snum > 0) {
            snum = other.snum;
        }
    }

    private static String formatCount(long count) {
        if (count >= 100000000) {
            return String.format("%.1f亿", count / 100000000.0);
        }
        if (count >= 10000) {
            return String.format("%.1f万", count / 10000.0);
        }
        return String.valueOf(count);
    }
}
