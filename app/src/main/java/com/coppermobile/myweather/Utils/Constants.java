package com.coppermobile.myweather.Utils;

import com.coppermobile.myweather.BuildConfig;

/**
 * Created by sagar on 12-Jul-16.
 */
public class Constants {

    public static final String API_KEY = BuildConfig.YOUR_API_KEY;
    public static final String APP_ID = BuildConfig.YOUR_APP_ID;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public final static String OPEN_WEATHER_MAP_API_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public final static String SAVED_CITIES_SHARED_PREFS = "MySharedPrefs";
    public final static String TEMP_UNITS_CELCIUS = "metric";
    public final static String CURRENT_CITY = "current_city";

    public static final String LAUNCH_APP = "launch_app";
    public static String UPDATE_WEATHER_REFRESH_CLICKED = "update";

    public static final String DISPLAY_STRING_HALF_INTENT_EXTRA = "displayString";
    public static final String SEARCH_STRING_INTENT_EXTRA = "searchString";

    public static final String SEND_TO_MAIN_ACTIVITY_DISPLAY_STRING_HALF = "searchStringMainActivity";
    public static final String SEND_TO_MAIN_ACTIVITY_SEARCH_STRING = "searchStringMainActivity";

}
