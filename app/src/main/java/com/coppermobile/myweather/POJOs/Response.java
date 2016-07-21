package com.coppermobile.myweather.POJOs;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Response {

    @SerializedName("weather")
    private List<Weather> weather = new ArrayList<>();
    @SerializedName("main")
    private Main main;
    @SerializedName("dt")
    private Long dt;
    @SerializedName("sys")
    private Sys sys;
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public Long getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
