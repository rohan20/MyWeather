package com.coppermobile.myweather.POJOs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("sunrise")
    @Expose
    private Long sunrise;
    @SerializedName("sunset")
    @Expose
    private Long sunset;

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return The sunrise
     */
    public Long getSunrise() {
        return sunrise;
    }

    /**
     * @return The sunset
     */
    public Long getSunset() {
        return sunset;
    }

}