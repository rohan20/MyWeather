package com.coppermobile.myweather.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.coppermobile.myweather.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.List;

public class WidgetConfigureActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WidgetPrefs";
    private static final String PREF_DISPLAY_HALF_KEY = "displayStringHalf_appwidget_";
    private static final String PREF_SEARCH_KEY = "searchString_appwidget_";

    private PlaceAutocompleteFragment autocompleteFragment;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_configure);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_widget_configure_activity);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Select Location");

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(setPlacesAutocompleteFilter());

        // Find the widget id from the intent.
        //1
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Toast.makeText(WidgetConfigureActivity.this, "Invalid AppWidgetID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //2
        getLocationForWidget();

    }

    private AutocompleteFilter setPlacesAutocompleteFilter() {
        return new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
    }

    private void getLocationForWidget() {

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if (place == null) {
                    Toast.makeText(WidgetConfigureActivity.this, "Error in getting location selected.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String displayStringHalf = place.getName().toString();
                String searchString = place.getName().toString() + " " + place.getAddress().toString();
                saveTitlePref(WidgetConfigureActivity.this, mAppWidgetId, displayStringHalf, searchString);

                //3
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetConfigureActivity.this);

                //4
                WeatherWidget.updateAppWidget(WidgetConfigureActivity.this, appWidgetManager, mAppWidgetId);

                //5
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }

            @Override
            public void onError(Status status) {

            }
        });

    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String displayStringHalf, String searchString) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_DISPLAY_HALF_KEY + appWidgetId, displayStringHalf);
        prefs.putString(PREF_SEARCH_KEY + appWidgetId, searchString);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static List<String> loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        String displayStringHalf = prefs.getString(PREF_DISPLAY_HALF_KEY + appWidgetId, null);
        String searchStringHalf = prefs.getString(PREF_SEARCH_KEY + appWidgetId, null);

        List<String> returnValues = new ArrayList<>();

        returnValues.add(displayStringHalf);
        returnValues.add(searchStringHalf);

        return returnValues;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_SEARCH_KEY + appWidgetId);
        prefs.remove(PREF_DISPLAY_HALF_KEY + appWidgetId);
        prefs.apply();
    }


}
