package com.coppermobile.myweather.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coppermobile.myweather.Models.FiveDaysModel;
import com.coppermobile.myweather.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sagar on 15-Jul-16.
 */
public class FiveDaysAdapter extends RecyclerView.Adapter<FiveDaysAdapter.ViewHolder> {

    Context mContext;
    List<FiveDaysModel> mFiveDaysModels;

    public FiveDaysAdapter(Context context) {
        mContext = context;
    }

    public void setAdapterList(List<FiveDaysModel> list) {
        mFiveDaysModels = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.five_days_forecast_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mDateTextView.setText(mFiveDaysModels.get(position).getDate());
        holder.mDescriptionTextView.setText(mFiveDaysModels.get(position).getDescription());
        Picasso.with(mContext).load("http://openweathermap.org/img/w/" + mFiveDaysModels.get(position).getWeatherIcon() + ".png").fit().placeholder(R.drawable.placeholder).into(holder.mWeatherIconImageView);
        holder.mTempTextView.setText(mFiveDaysModels.get(position).getTemp());
        holder.mTempMaxTextView.setText(mFiveDaysModels.get(position).getTemp_max());
        holder.mTempMinTextView.setText(mFiveDaysModels.get(position).getTemp_min());
    }

    @Override
    public int getItemCount() {
        return mFiveDaysModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTempTextView;
        TextView mTempMaxTextView;
        TextView mTempMinTextView;
        TextView mDateTextView;
        TextView mDescriptionTextView;
        ImageView mWeatherIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mTempTextView = (TextView) itemView.findViewById(R.id.five_days_text_view_temp);
            mTempMaxTextView = (TextView) itemView.findViewById(R.id.five_days_text_view_temp_max);
            mTempMinTextView = (TextView) itemView.findViewById(R.id.five_days_text_view_temp_min);
            mDateTextView = (TextView) itemView.findViewById(R.id.five_days_text_view_day_and_date);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.five_days_text_view_description);
            mWeatherIconImageView = (ImageView) itemView.findViewById(R.id.five_days_image_view_temp);

        }
    }
}
