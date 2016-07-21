package com.coppermobile.myweather.Models;

/**
 * Created by sagar on 15-Jul-16.
 */
public class FiveDaysModel {

    private String weatherIcon;
    private String date;
    private String description;
    private String temp_max;
    private String temp_min;
    private String temp;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FiveDaysModel() {

    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public String getTemp_min() {
        return temp_min;
    }
}
