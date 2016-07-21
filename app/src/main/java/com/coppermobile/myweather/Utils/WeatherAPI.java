package com.coppermobile.myweather.Utils;

import com.coppermobile.myweather.POJOs.Response;
import com.coppermobile.myweather.POJOsFiveDays.ResponseFiveDays;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather")
    Call<Response> getResponseCall(@Query("q") String city, @Query("units") String units, @Query("appid") String app_id);

    @GET("forecast")
    Call<ResponseFiveDays> getResponseFiveDaysCall(@Query("q") String city, @Query("units") String units, @Query("appid") String app_id);

//    Temperature Units Default: Kelvin; Metric: Celsius; Imperial: Fahrenheit.

}
