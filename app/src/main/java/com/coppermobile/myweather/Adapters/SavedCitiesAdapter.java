package com.coppermobile.myweather.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.coppermobile.myweather.MainActivity;
import com.coppermobile.myweather.Models.SavedCityModel;
import com.coppermobile.myweather.R;
import com.coppermobile.myweather.Utils.Constants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sagar on 12-Jul-16.
 */
public class SavedCitiesAdapter extends RecyclerView.Adapter<SavedCitiesAdapter.ViewHolder> {

    private Context mContext;
    private List<SavedCityModel> mSavedCity;

    public SavedCitiesAdapter(Context context) {
        mContext = context;
    }

    /**
     * @param savedCity
     */
    public void setAdapaterList(List<SavedCityModel> savedCity) {
        mSavedCity = savedCity;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mCityNameTextView;
        TextView mTempTextView;
        ImageView mWeatherImageView;
        RelativeLayout mRecyclerViewItemRelativeLayout;

        public ViewHolder(final View itemView) {
            super(itemView);

            mRecyclerViewItemRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.recycler_view_item_relative_layout);
            mCityNameTextView = (TextView) itemView.findViewById(R.id.recycler_view_saved_cities_item_name_text_view);
            mTempTextView = (TextView) itemView.findViewById(R.id.recycler_view_saved_cities_item_temp_text_view);
            mWeatherImageView = (ImageView) itemView.findViewById(R.id.recycler_view_saved_cities_item_image_view);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.saved_city_recycler_view_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mCityNameTextView.setText(mSavedCity.get(position).getDisplayName());
        Picasso.with(mContext).load("http://openweathermap.org/img/w/" + mSavedCity.get(position).getWeatherIcon() + ".png").fit().placeholder(R.drawable.placeholder).into(holder.mWeatherImageView);
        holder.mTempTextView.setText(mSavedCity.get(position).getWeatherTemp() + (char) 0x00B0);

        holder.mRecyclerViewItemRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) mContext).callApiForUpdate(mSavedCity.get(position));
                ((MainActivity) mContext).updateHeader(mSavedCity.get(position));
                ((MainActivity) mContext).getmDrawer().closeDrawer(GravityCompat.START);
                ((MainActivity) mContext).setCurrentCity(mSavedCity.get(position));

                //save current city
                mContext.getSharedPreferences(Constants.SAVED_CITIES_SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(Constants.CURRENT_CITY, new Gson().toJson(((MainActivity) mContext).getCurrentCity())).apply();
            }
        });

        holder.mRecyclerViewItemRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title("Delete?")
                        .positiveText("Yes")
                        .positiveColor(Color.parseColor("#673AB7"))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SharedPreferences mPrefs = mContext.getSharedPreferences(Constants.SAVED_CITIES_SHARED_PREFS, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.remove(mSavedCity.get(position).getId()).apply();
                                mSavedCity.remove(position);
                                notifyDataSetChanged();

                                if (position == 0 && mSavedCity.size() > 0) {
                                    ((MainActivity) mContext).callApiForUpdate(mSavedCity.get(0));
                                    ((MainActivity) mContext).updateHeader(mSavedCity.get(0));
                                    ((MainActivity) mContext).setCurrentCity(mSavedCity.get(0));
                                }


                                if (mSavedCity.size() == 0) {
                                    //set default header image and header text
                                    ((MainActivity) mContext).getmHeaderCityNameTextView().setText("My Weather App");
                                    Picasso.with(mContext).load(R.drawable.logo).into(((MainActivity) mContext).getmHeaderCityWeatherImageView());
                                    ((MainActivity) mContext).setTitle("My Weather App");
                                    mPrefs.edit().clear().apply();
                                }

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .negativeText("No")
                        .negativeColor(Color.parseColor("#000000"))
                        .show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        return mSavedCity.size();
    }
}
