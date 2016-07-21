package com.coppermobile.myweather.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTAdapter {

    private WeatherAPI weatherAPI;

    public RESTAdapter(String base_url) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherAPI = retrofit.create(WeatherAPI.class);

    }

    public WeatherAPI getWeatherAPI() {
        return weatherAPI;
    }
}
