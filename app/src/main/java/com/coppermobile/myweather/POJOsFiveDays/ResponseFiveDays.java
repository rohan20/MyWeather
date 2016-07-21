package com.coppermobile.myweather.POJOsFiveDays;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseFiveDays {

    @SerializedName("list")
    @Expose
    private java.util.List<com.coppermobile.myweather.POJOsFiveDays.List> list = new ArrayList<com.coppermobile.myweather.POJOsFiveDays.List>();


    @SerializedName("city")
    @Expose
    private City city;

    /**
     *
     * @return
     * The list
     */
    public java.util.List<com.coppermobile.myweather.POJOsFiveDays.List> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }
}