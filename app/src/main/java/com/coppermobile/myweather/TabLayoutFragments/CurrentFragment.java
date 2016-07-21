package com.coppermobile.myweather.TabLayoutFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coppermobile.myweather.MainActivity;
import com.coppermobile.myweather.POJOs.Response;
import com.coppermobile.myweather.R;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {

    private View v;

    public CurrentFragment() {
        // Required empty public constructor
    }


    public static CurrentFragment newInstance(){
        return new CurrentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        v = getView();

    }

    public View getCurrentFragmentView(){
        return v;
    }

    /**
     *
     */
    public void setDataInCurrentFragment(retrofit2.Response<Response> response) {
        setData(v, response);
    }

    private void setData(View v, retrofit2.Response<Response> response) {

        if (response == null) {
            return;
        }

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

        //        if (v == null)
        //            return;


        //Description, Date & Day, Time
        ((TextView) v.findViewById(R.id.text_view_description)).setText(builder.toString());
        ((TextView) v.findViewById(R.id.text_view_day_and_date)).setText(dayAndDate);
        ((ShimmerTextView) v.findViewById(R.id.text_view_last_updated)).setText("(Last updated: " + timeLastUpdated + ")");

        //Image
        Picasso.with(getActivity()).load("http://openweathermap.org/img/w/" + response.body().getWeather().get(0).getIcon() + ".png").fit().into((ImageView) v.findViewById(R.id.image_view_temp));

        //Temp, MaxTemp, MinTemp
        ((TextView) v.findViewById(R.id.text_view_temp_current)).setText(response.body().getMain().getTemp().toString() + (char) 0x00B0);
        ((TextView) v.findViewById(R.id.text_view_temp_max)).setText(response.body().getMain().getTempMax().toString() + (char) 0x00B0);
        ((TextView) v.findViewById(R.id.text_view_temp_min)).setText(response.body().getMain().getTempMin().toString() + (char) 0x00B0);

        //Sunrise, Sunset, Humidity

        Date sunriseData = new Date(response.body().getSys().getSunrise() * 1000L);
        String sunrise = sunriseSunsetTimeFormatterIST.format(sunriseData);
        Date sunsetData = new Date(response.body().getSys().getSunset() * 1000L);
        String sunset = sunriseSunsetTimeFormatterIST.format(sunsetData);

        ((TextView) v.findViewById(R.id.text_view_sunrise)).setText(sunrise);
        ((TextView) v.findViewById(R.id.text_view_sunset)).setText(sunset);
        ((TextView) v.findViewById(R.id.text_view_humidity)).setText(response.body().getMain().getHumidity().toString() + "%");
    }

    public interface OnCurrentResponseFetch {
        void getCurrentResponse();
    }


}
