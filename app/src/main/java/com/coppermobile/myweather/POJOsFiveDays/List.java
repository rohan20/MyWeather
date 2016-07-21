package com.coppermobile.myweather.POJOsFiveDays;

import java.util.ArrayList;
import com.coppermobile.myweather.POJOs.Main;
import com.coppermobile.myweather.POJOs.Weather;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {

    @SerializedName("dt")
    @Expose
    private Long dt;
    @SerializedName("main")
    @Expose
    private Main main;
    @SerializedName("weather")
    @Expose
    private java.util.List<Weather> weather = new ArrayList<Weather>();
    @SerializedName("dt_txt")
    @Expose
    private String dtTxt;

    /**
     *
     * @return
     * The dt
     */
    public Long getDt() {
        return dt;
    }

    /**
     *
     * @return
     * The main
     */
    public Main getMain() {
        return main;
    }

    /**
     *
     * @return
     * The weather
     */
    public java.util.List<Weather> getWeather() {
        return weather;
    }

    /**
     *
     * @return
     * The dtTxt
     */
    public String getDtTxt() {
        return dtTxt;
    }

}
