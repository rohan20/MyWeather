package com.coppermobile.myweather.POJOsFiveDays;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("id")
    @Expose
    public Long id;

    public Long getId() {
        return id;
    }
}