package com.coppermobile.myweather.Models;

import java.util.List;

/**
 * Created by sagar on 12-Jul-16.
 */
public class SavedCityModel {

    private String displayName;
    private String weatherIcon;
    private String weatherTemp;
    private String apiSearchString;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiSearchString() {
        return apiSearchString;
    }

    public void setApiSearchString(String apiSearchString) {
        this.apiSearchString = apiSearchString;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getWeatherTemp() {
        return weatherTemp;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public void setWeatherTemp(String weatherTemp) {
        this.weatherTemp = weatherTemp;
    }
}
