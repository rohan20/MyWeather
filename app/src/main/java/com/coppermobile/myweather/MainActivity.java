package com.coppermobile.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coppermobile.myweather.Adapters.FiveDaysAdapter;
import com.coppermobile.myweather.Adapters.SavedCitiesAdapter;
import com.coppermobile.myweather.Adapters.ViewPagerFragmentsAdapter;
import com.coppermobile.myweather.Models.FiveDaysModel;
import com.coppermobile.myweather.Models.SavedCityModel;
import com.coppermobile.myweather.POJOs.Response;
import com.coppermobile.myweather.POJOsFiveDays.ResponseFiveDays;
import com.coppermobile.myweather.Utils.Constants;
import com.coppermobile.myweather.Utils.RESTAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;

//String displayStringHalf = place.getName().toString();
//String displayString = place.getName().toString() + ", " + response.body().getSys().getCountry();
//String searchString = place.getName().toString() + " " + place.getAddress().toString();

/**
 * @author Rohan Taneja
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private SharedPreferences mSharedPreferences;

    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private TextView mHeaderCityNameTextView;
    private ImageView mHeaderCityWeatherImageView;
    private ActionBarDrawerToggle mDrawerToggle;
    private AutocompleteFilter mOnlyCitiesFilter;
    private GoogleApiClient mGoogleApiClient;

    private List<SavedCityModel> mSavedCityModelList;
    private List<FiveDaysModel> mFiveDaysModelList;

    private SavedCitiesAdapter mNavigationDrawerAdapter;
    private FiveDaysAdapter mFiveDaysAdapter;

    private RESTAdapter mRetrofitAdapter;

    private ViewPager mViewPager;

    private SavedCityModel mCurrentSavedCity;

    private retrofit2.Response<Response> response;

    RecyclerView mFiveDaysRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViewPager();
        init();

        Intent widgetIntent = getIntent();
//        Toast.makeText(MainActivity.this, "Intent Received", Toast.LENGTH_SHORT).show();
        if (widgetIntent != null && widgetIntent.getExtras() != null) {
//            Toast.makeText(MainActivity.this, "NOT NULL", Toast.LENGTH_SHORT).show();
            callApi(widgetIntent.getStringExtra(Constants.SEND_TO_MAIN_ACTIVITY_DISPLAY_STRING_HALF), widgetIntent.getStringExtra(Constants.SEND_TO_MAIN_ACTIVITY_SEARCH_STRING));
        }


    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    private void setPlacesAutocompleteFilter() {
        mOnlyCitiesFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
    }

    private void setRecyclerView() {
        RecyclerView mSavedCitiesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_saved_cities);

        mSavedCityModelList = new ArrayList<>();
        mFiveDaysModelList = new ArrayList<>();

        mNavigationDrawerAdapter = new SavedCitiesAdapter(MainActivity.this);
        mFiveDaysAdapter = new FiveDaysAdapter(MainActivity.this);

        mNavigationDrawerAdapter.setAdapaterList(mSavedCityModelList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSavedCitiesRecyclerView.setLayoutManager(layoutManager);

        mSavedCitiesRecyclerView.setAdapter(mNavigationDrawerAdapter);


    }

    private void initializeViews() {
        mHeaderCityWeatherImageView = (ImageView) findViewById(R.id.header_image_view);
        mHeaderCityNameTextView = (TextView) findViewById(R.id.header_text_view);

        FloatingActionButton addLocationFAB = (FloatingActionButton) findViewById(R.id.add_location_fab);
        addLocationFAB.setOnClickListener(this);
    }

    private void initializeDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);
    }

    private void init() {

        mFiveDaysRecyclerView = null;

        mRetrofitAdapter = new RESTAdapter(Constants.OPEN_WEATHER_MAP_API_BASE_URL);

        mSharedPreferences = getSharedPreferences(Constants.SAVED_CITIES_SHARED_PREFS, Context.MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setTitle("My Weather App");
        buildGoogleApiClient();
        setPlacesAutocompleteFilter();
        initializeDrawer();
        initializeViews();
        setRecyclerView();

        getAllSavedCitiesFromSharedPreferences();
        updateHeader(getCurrentCity());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    private void setUpViewPager() {

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerFragmentsAdapter adapter = new ViewPagerFragmentsAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public ImageView getmHeaderCityWeatherImageView() {
        return mHeaderCityWeatherImageView;
    }

    public TextView getmHeaderCityNameTextView() {
        return mHeaderCityNameTextView;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    //For hamburger icon
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /**
     * @return DrawerLayout
     */
    public DrawerLayout getmDrawer() {
        return mDrawer;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_location_fab:

                try {

                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(mOnlyCitiesFilter)
                            .build(this);

                    startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(MainActivity.this, "GooglePlayServicesRepairableException exception", Toast.LENGTH_SHORT).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(MainActivity.this, "GooglePlayServicesNotAvailableException exception", Toast.LENGTH_SHORT).show();

                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:

                Shimmer shimmer = new Shimmer();
                shimmer.setRepeatCount(0)
                        .setDuration(1500)
                        .setStartDelay(0)
                        .setDirection(Shimmer.ANIMATION_DIRECTION_LTR);

                if (getCurrentCity() == null) {
                    Toast.makeText(MainActivity.this, "Please add a city first.", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    shimmer.start((ShimmerTextView) (mViewPager.getChildAt(0).findViewById(R.id.text_view_last_updated)));
                    callApiForUpdate(getCurrentCity());
                    updateHeader(getCurrentCity());
                    break;
                }

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param currentCity
     */
    public void updateHeader(SavedCityModel currentCity) {

        if (currentCity == null) {
            return;
        }

        mHeaderCityNameTextView.setText(currentCity.getDisplayName());
        Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + currentCity.getWeatherIcon() + ".png").fit().into(mHeaderCityWeatherImageView);
        setTitle(currentCity.getDisplayName());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Places API Error? On adding Faridabad, noida is added to recycler view.

                Place place = PlaceAutocomplete.getPlace(this, data);

                //create new saved city
                callApi(place);
//                mDrawer.closeDrawer(GravityCompat.START);
//                mNavigationDrawerAdapter.notifyDataSetChanged();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(MainActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {

                // The user canceled the operation.
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(MainActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void callApi(final String mDisplayStringHalf, final String mSearchString) {

        if (mDisplayStringHalf == null || mSearchString == null) {
//            Toast.makeText(MainActivity.this, "string(s) is null", Toast.LENGTH_SHORT).show();
            return;
        }

        mDrawer.closeDrawer(GravityCompat.START);

        Toast.makeText(MainActivity.this, "All temp units: Celcius", Toast.LENGTH_SHORT).show();

        String searchString = mSearchString;

        Call<Response> request = mRetrofitAdapter.getWeatherAPI().getResponseCall(searchString, Constants.TEMP_UNITS_CELCIUS, Constants.APP_ID);
        Call<ResponseFiveDays> requestFiveDays = mRetrofitAdapter.getWeatherAPI().getResponseFiveDaysCall(searchString, Constants.TEMP_UNITS_CELCIUS, Constants.APP_ID);

        request.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                if (!response.isSuccessful()) {
                    Log.i("error", response.message());
                    return;
                }

                setResponse(response);
                getNavigationDrawerData(mDisplayStringHalf, mSearchString, response);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });

        requestFiveDays.enqueue(new Callback<ResponseFiveDays>() {
            @Override
            public void onResponse(Call<ResponseFiveDays> call, retrofit2.Response<ResponseFiveDays> responseFiveDays) {

                if (!responseFiveDays.isSuccessful()) {
                    Log.i("error", responseFiveDays.message());
                    return;
                }

                mFiveDaysModelList.addAll(setFiveDaysFragmentData(responseFiveDays));
                initializeFiveDaysFragment(mFiveDaysModelList);
            }

            @Override
            public void onFailure(Call<ResponseFiveDays> call, Throwable t) {

            }
        });

    }

    /**
     * @param place
     */
    public void callApi(final Place place) {

        mDrawer.closeDrawer(GravityCompat.START);

        Toast.makeText(MainActivity.this, "All temp units: Celcius", Toast.LENGTH_SHORT).show();

        String searchString = place.getName().toString() + " " + place.getAddress().toString();

        Call<Response> request = mRetrofitAdapter.getWeatherAPI().getResponseCall(searchString, Constants.TEMP_UNITS_CELCIUS, Constants.APP_ID);
        Call<ResponseFiveDays> requestFiveDays = mRetrofitAdapter.getWeatherAPI().getResponseFiveDaysCall(searchString, Constants.TEMP_UNITS_CELCIUS, Constants.APP_ID);

        request.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                if (!response.isSuccessful()) {
                    Log.i("error", response.message());
                    return;
                }

                setResponse(response);
                getNavigationDrawerData(place, response);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });

        requestFiveDays.enqueue(new Callback<ResponseFiveDays>() {
            @Override
            public void onResponse(Call<ResponseFiveDays> call, retrofit2.Response<ResponseFiveDays> responseFiveDays) {

                if (!responseFiveDays.isSuccessful()) {
                    Log.i("error", responseFiveDays.message());
                    return;
                }

                mFiveDaysModelList.addAll(setFiveDaysFragmentData(responseFiveDays));
                initializeFiveDaysFragment(mFiveDaysModelList);
            }

            @Override
            public void onFailure(Call<ResponseFiveDays> call, Throwable t) {

            }
        });

    }

    private void getNavigationDrawerData(String mDisplayStringHalf, String mSearchString, retrofit2.Response<Response> response) {

//        String displayStringHalf = place.getName().toString();
        String displayStringHalf = mDisplayStringHalf;
//        String searchString = place.getName().toString() + " " + place.getAddress().toString();
        String searchString = mSearchString;

        SavedCityModel newSavedCity = new SavedCityModel();

        Log.i("array size", response.body().toString());

        newSavedCity.setWeatherIcon(response.body().getWeather().get(0).getIcon());
        String displayString = displayStringHalf + ", " + response.body().getSys().getCountry();
        setTitle(displayString);
        newSavedCity.setDisplayName(displayString);
        newSavedCity.setWeatherTemp(response.body().getMain().getTemp().toString());
        newSavedCity.setApiSearchString(searchString);
        newSavedCity.setId(response.body().getId().toString());

        setCurrentCity(newSavedCity);

        mSharedPreferences.edit().putString(Constants.CURRENT_CITY, new Gson().toJson(newSavedCity)).apply();
        updateHeader(newSavedCity);

        updateSharedPreferencesSavedCities(newSavedCity);

        mSavedCityModelList.add(0, newSavedCity);
        mNavigationDrawerAdapter.notifyDataSetChanged();
        setCurrentFragmentData(response);
    }

    private void getNavigationDrawerData(Place place, retrofit2.Response<Response> response) {

        String displayStringHalf = place.getName().toString();
        String searchString = place.getName().toString() + " " + place.getAddress().toString();

        SavedCityModel newSavedCity = new SavedCityModel();

        Log.i("array size", response.body().toString());

        newSavedCity.setWeatherIcon(response.body().getWeather().get(0).getIcon());
        String displayString = displayStringHalf + ", " + response.body().getSys().getCountry();
        setTitle(displayString);
        newSavedCity.setDisplayName(displayString);
        newSavedCity.setWeatherTemp(response.body().getMain().getTemp().toString());
        newSavedCity.setApiSearchString(searchString);
        newSavedCity.setId(response.body().getId().toString());

        setCurrentCity(newSavedCity);

        mSharedPreferences.edit().putString(Constants.CURRENT_CITY, new Gson().toJson(newSavedCity)).apply();
        updateHeader(newSavedCity);

        updateSharedPreferencesSavedCities(newSavedCity);

        mSavedCityModelList.add(0, newSavedCity);
        mNavigationDrawerAdapter.notifyDataSetChanged();
        setCurrentFragmentData(response);
    }

    /**
     * @param savedCity
     */
    public void callApiForUpdate(final SavedCityModel savedCity) {

        Toast.makeText(MainActivity.this, "All temp units: Celcius", Toast.LENGTH_SHORT).show();

        String searchString = savedCity.getApiSearchString();

        Call<Response> request = mRetrofitAdapter.getWeatherAPI().getResponseCall(searchString, Constants.TEMP_UNITS_CELCIUS, Constants.APP_ID);
        Call<ResponseFiveDays> requestFiveDays = mRetrofitAdapter.getWeatherAPI().getResponseFiveDaysCall(searchString, Constants.TEMP_UNITS_CELCIUS, Constants.APP_ID);

        request.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                if (!response.isSuccessful()) {
                    Log.i("error", response.message());
                    return;
                }

                setResponse(response);
                updateSavedCityData(savedCity, response);
                setCurrentFragmentData(response);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });

        requestFiveDays.enqueue(new Callback<ResponseFiveDays>() {
            @Override
            public void onResponse(Call<ResponseFiveDays> call, retrofit2.Response<ResponseFiveDays> responseFiveDays) {
                if (!responseFiveDays.isSuccessful()) {
                    Log.i("error", responseFiveDays.message());
                    return;
                }

                if (mFiveDaysAdapter == null) {
                    return;
                }

                mFiveDaysModelList.addAll(setFiveDaysFragmentData(responseFiveDays));
                //Setting adapterList again doesn't work here but initializing recycler view, adapter and layout manager does work. (????)
                //{Probably because I can't get instance of recyclerView anywhere else --> viewpager.getChildAt(1) becomes null)
//                mFiveDaysAdapter.setAdapterList(mFiveDaysModelList);
                initializeFiveDaysFragment(mFiveDaysModelList);
            }

            @Override
            public void onFailure(Call<ResponseFiveDays> call, Throwable t) {

            }
        });

    }

    private List<FiveDaysModel> setFiveDaysFragmentData(retrofit2.Response<ResponseFiveDays> responseFiveDays) {

        for (com.coppermobile.myweather.POJOsFiveDays.List responseList : responseFiveDays.body().getList()) {

            FiveDaysModel fiveDaysModel = new FiveDaysModel();

            //convert first letter to caps
            String[] strArray = responseList.getWeather().get(0).getDescription().split(" ");
            StringBuilder builder = new StringBuilder();
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");
            }

            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("EEE, dd MMM yyyy - hh:mm aa");
            Date date = null;
            try {
                date = originalFormat.parse(responseList.getDtTxt().toString());
            } catch (ParseException e) {
                Toast.makeText(MainActivity.this, "Invalid Date", Toast.LENGTH_SHORT).show();
            }

            String formattedDate = targetFormat.format(date);

            fiveDaysModel.setDate(formattedDate);
            fiveDaysModel.setDescription(builder.toString());
            fiveDaysModel.setTemp(responseList.getMain().getTemp().toString() + (char) 0x00B0);
            fiveDaysModel.setTemp_max(responseList.getMain().getTempMax().toString() + (char) 0x00B0);
            fiveDaysModel.setTemp_min(responseList.getMain().getTempMin().toString() + (char) 0x00B0);
            fiveDaysModel.setWeatherIcon(responseList.getWeather().get(0).getIcon());
            fiveDaysModel.setId(responseFiveDays.body().getCity().getId().toString());

            mFiveDaysModelList.add(fiveDaysModel);

        }


        return mFiveDaysModelList;

    }

    private void updateSavedCityData(SavedCityModel savedCity, retrofit2.Response<Response> response) {

        savedCity.setWeatherIcon(response.body().getWeather().get(0).getIcon());
        savedCity.setWeatherTemp(response.body().getMain().getTemp().toString());

        updateSharedPreferencesSavedCities(savedCity);

        mNavigationDrawerAdapter.notifyDataSetChanged();
    }

    private void setCurrentFragmentData(retrofit2.Response<Response> response) {

        //convert first letter to caps
        String[] strArray = response.body().getWeather().get(0).getDescription().split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        //TimeZone
        TimeZone timeZoneIST = TimeZone.getTimeZone("Asia/Kolkata");

        //getEpochTime
        Date epochTimeLastUpdate = new Date(response.body().getDt() * 1000L);

        //dateFormatter
        SimpleDateFormat dateFormatterIST = new SimpleDateFormat("EEE, dd MMM yyyy");
        dateFormatterIST.setTimeZone(timeZoneIST);

        //timeFormatter
        SimpleDateFormat timeFormatterIST = new SimpleDateFormat("hh:mm a");
        timeFormatterIST.setTimeZone(timeZoneIST);

        //sunriseSunsetTimeFormatter
        SimpleDateFormat sunriseSunsetTimeFormatterIST = new SimpleDateFormat("dd MMM, hh:mm a");
        sunriseSunsetTimeFormatterIST.setTimeZone(timeZoneIST);

        //Date
        String dayAndDate = dateFormatterIST.format(epochTimeLastUpdate);

        //Time
        String timeLastUpdated = timeFormatterIST.format(epochTimeLastUpdate);

        //Description, Date & Day, Time
        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_description)).setText(builder.toString());
        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_day_and_date)).setText(dayAndDate);
        ((ShimmerTextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_last_updated)).setText("(Last updated: " + timeLastUpdated + ")");

        //Image
        Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + response.body().getWeather().get(0).getIcon() + ".png").fit().placeholder(R.drawable.placeholder).into((ImageView) mViewPager.getChildAt(0).findViewById(R.id.image_view_temp));

        //Temp, MaxTemp, MinTemp
        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_temp_current)).setText(response.body().getMain().getTemp().toString() + (char) 0x00B0);
        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_temp_max)).setText(response.body().getMain().getTempMax().toString() + (char) 0x00B0);
        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_temp_min)).setText(response.body().getMain().getTempMin().toString() + (char) 0x00B0);

        //Sunrise, Sunset, Humidity

        Date sunriseData = new Date(response.body().getSys().getSunrise() * 1000L);
        String sunrise = sunriseSunsetTimeFormatterIST.format(sunriseData);
        Date sunsetData = new Date(response.body().getSys().getSunset() * 1000L);
        String sunset = sunriseSunsetTimeFormatterIST.format(sunsetData);

        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_sunrise)).setText(sunrise);
        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_sunset)).setText(sunset);
        ((TextView) mViewPager.getChildAt(0).findViewById(R.id.text_view_humidity)).setText(response.body().getMain().getHumidity().toString() + "%");

    }

    private void initializeFiveDaysFragment(List<FiveDaysModel> list) {

//        RecyclerView mFiveDaysRecyclerView = (RecyclerView) mViewPager.getChildAt(1).findViewById(R.id.recycler_view_five_days_fragment);
        mFiveDaysRecyclerView = (RecyclerView) mViewPager.getChildAt(1).findViewById(R.id.recycler_view_five_days_fragment);
        mFiveDaysAdapter.setAdapterList(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFiveDaysRecyclerView.setLayoutManager(layoutManager);
        mFiveDaysRecyclerView.setAdapter(mFiveDaysAdapter);
    }

    //SharedPreferences

    private void updateSharedPreferencesSavedCities(SavedCityModel savedCity) {

        SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savedCity);
        prefsEditor.putString(savedCity.getId(), json);
        prefsEditor.apply();
    }

    public void getAllSavedCitiesFromSharedPreferences() {

        mSavedCityModelList.clear();

        Gson gson = new Gson();

        if (mSharedPreferences.getAll() == null) {
            mNavigationDrawerAdapter.notifyDataSetChanged();
            return;
        }

        if (mSharedPreferences.getAll().isEmpty()) {
            mHeaderCityNameTextView.setText("My Weather App");
            Picasso.with(MainActivity.this).load(R.drawable.logo).into(mHeaderCityWeatherImageView);
            setTitle("My Weather App");
            return;
        }

        Map<String, ?> keys = mSharedPreferences.getAll();

        for (Map.Entry<String, ?> savedItemKey : keys.entrySet()) {

            String json = mSharedPreferences.getString(savedItemKey.getKey(), null);

            SavedCityModel item = gson.fromJson(json, SavedCityModel.class);

            if (item == null) {
                Log.i("SharedPrefItem", "is null");
                continue;
            }

            if (savedItemKey.getKey().equals(Constants.CURRENT_CITY)) {
                setCurrentCity(item);
                callApiForUpdate(item);
                updateHeader(item);
                continue;
            }

            mSavedCityModelList.add(item);
        }

        mNavigationDrawerAdapter.notifyDataSetChanged();

    }

    //Set and Get current city

    /**
     * @param savedCity
     */
    public void setCurrentCity(SavedCityModel savedCity) {
        mCurrentSavedCity = savedCity;
    }

    /**
     * @return
     */
    public SavedCityModel getCurrentCity() {
        return mCurrentSavedCity;
    }

    //For Fragments

    /**
     * @return
     */
    public retrofit2.Response<Response> getReponse() {
        return response;
    }

    private void setResponse(retrofit2.Response<Response> mResponse) {
        response = mResponse;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Failed to create GoogleApiClient", Toast.LENGTH_SHORT).show();
    }
}