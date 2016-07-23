package com.coppermobile.myweather.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.coppermobile.myweather.MainActivity;
import com.coppermobile.myweather.POJOs.Response;
import com.coppermobile.myweather.POJOs.Weather;
import com.coppermobile.myweather.R;
import com.coppermobile.myweather.Utils.Constants;
import com.coppermobile.myweather.Utils.RESTAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetConfigureActivity WidgetConfigureActivity}
 */

public class WeatherWidget extends AppWidgetProvider {

    static RESTAdapter mRetrofitAdapter;

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

//        Toast.makeText(context, "static", Toast.LENGTH_SHORT).show();

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather);

        Intent updateWidget = new Intent(context, WeatherWidget.class);
        updateWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        updateWidget.setAction(Constants.UPDATE_WEATHER_REFRESH_CLICKED);

        Intent launchApp = new Intent(context, WeatherWidget.class);
        launchApp.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        launchApp.putExtra(Constants.DISPLAY_STRING_HALF_INTENT_EXTRA, WidgetConfigureActivity.loadTitlePref(context, appWidgetId).get(0));
        launchApp.putExtra(Constants.SEARCH_STRING_INTENT_EXTRA, WidgetConfigureActivity.loadTitlePref(context, appWidgetId).get(1));
        launchApp.setAction(Constants.LAUNCH_APP);

        PendingIntent launchAppPendingIntent = PendingIntent.getBroadcast(context, 0, launchApp, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_layout, launchAppPendingIntent);

        PendingIntent updateWidgetPendingIntent = PendingIntent.getBroadcast(context, 0, updateWidget, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.refresh_image_view, updateWidgetPendingIntent);

        final String mDisplayStringHalf = WidgetConfigureActivity.loadTitlePref(context, appWidgetId).get(0);
        String mSearchString = WidgetConfigureActivity.loadTitlePref(context, appWidgetId).get(1);

        final SimpleDateFormat timeFormatterIST = new SimpleDateFormat("HH:MM");
        timeFormatterIST.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        mRetrofitAdapter = new RESTAdapter(Constants.OPEN_WEATHER_MAP_API_BASE_URL);

        Call<Response> request = mRetrofitAdapter.getWeatherAPI().getResponseCall(mSearchString, Constants.TEMP_UNITS_CELCIUS, Constants.APP_ID);

        request.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                if (!response.isSuccessful()) {
//                    Toast.makeText(context, "Unable to fetch data right now, please try later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.body() == null) {
//                    Toast.makeText(context, "response.body() NULL", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body().getDt() == null) {
//                    Toast.makeText(context, "response.body().getDt() NULL", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(context, "Fetching weather data...", Toast.LENGTH_LONG).show();
                Date epochTimeLastUpdate = new Date(response.body().getDt() * 1000L);
                String timeLastUpdated = timeFormatterIST.format(epochTimeLastUpdate);

                Bitmap b = null;
                try {
                    b = new BitmapAsyncTask().execute("http://openweathermap.org/img/w/" + response.body().getWeather().get(0).getIcon() + ".png").get();
                } catch (InterruptedException | ExecutionException e) {
                    Toast.makeText(context, "Unable to fetch image.", Toast.LENGTH_SHORT).show();
                }

                if (b == null) {
                    Toast.makeText(context, "Unable to fetch image.", Toast.LENGTH_SHORT).show();
                } else {
                    remoteViews.setImageViewBitmap(R.id.current_weather_icon, b);
                }

                //convert first letter to caps
                String[] strArray = response.body().getWeather().get(0).getDescription().split(" ");
                StringBuilder builder = new StringBuilder();
                for (String s : strArray) {
                    String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    builder.append(cap).append(" ");
                }

                String displayString = mDisplayStringHalf + ", " + response.body().getSys().getCountry();
                remoteViews.setTextViewText(R.id.location_text_view, displayString);
                remoteViews.setTextViewText(R.id.desc_text_view, builder.toString());
                remoteViews.setTextViewText(R.id.current_temp_text_view, response.body().getMain().getTemp().toString() + (char) 0x00B0 + "C");
                remoteViews.setTextViewText(R.id.max_min_temp_text_view, response.body().getMain().getTempMin().toString() + (char) 0x00B0 + " / " + response.body().getMain().getTempMax().toString() + (char) 0x00B0);
                remoteViews.setTextViewText(R.id.time_last_updated_text_view, "(" + timeLastUpdated + ")");

                if (appWidgetManager != null) {
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                }

                Toast.makeText(context, "Weather data updated.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(context, "Failed to fetch data. Please try later.", Toast.LENGTH_SHORT).show();
            }
        });

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        Toast.makeText(context, "onUpdate()", Toast.LENGTH_SHORT).show();

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals(Constants.UPDATE_WEATHER_REFRESH_CLICKED)) {
//            Toast.makeText(context, "Refresh Clicked", Toast.LENGTH_SHORT).show();
            updateAppWidget(context, AppWidgetManager.getInstance(context), intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID));
        }

        if (intent.getAction().equals(Constants.LAUNCH_APP)) {
//            Toast.makeText(context, "App launched from widget", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Constants.SEND_TO_MAIN_ACTIVITY_DISPLAY_STRING_HALF, intent.getStringExtra(Constants.DISPLAY_STRING_HALF_INTENT_EXTRA));
            i.putExtra(Constants.SEND_TO_MAIN_ACTIVITY_SEARCH_STRING, intent.getStringExtra(Constants.SEARCH_STRING_INTENT_EXTRA));
            context.startActivity(i);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
//        Toast.makeText(context, "Widget enabled...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

