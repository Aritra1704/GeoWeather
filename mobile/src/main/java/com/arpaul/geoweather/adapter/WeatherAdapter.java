package com.arpaul.geoweather.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arpaul.geoweather.R;
import com.arpaul.geoweather.activity.BaseActivity;
import com.arpaul.geoweather.activity.ItemWeatherDetailActivity;
import com.arpaul.geoweather.activity.WeatherListActivity;
import com.arpaul.geoweather.common.AppConstants;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.dataObjects.WeatherDescriptionDO;
import com.arpaul.geoweather.fragments.ItemWeatherDetailFragment;
import com.arpaul.geoweather.wearableService.SendWearableDataService;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.StringUtils;

import java.util.ArrayList;

/**
 * Created by ARPaul on 04-04-2016.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherDataDO> arrWeatherDetails;

    public WeatherAdapter(Context context, ArrayList<WeatherDataDO> arrCallDetails) {
        this.context=context;
        this.arrWeatherDetails = arrCallDetails;
    }

    public void refresh(ArrayList<WeatherDataDO> arrCallDetails) {
        this.arrWeatherDetails = arrCallDetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_weather_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final WeatherDataDO objWeatherDO = arrWeatherDetails.get(position);
        String date = CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), AppConstants.DAY_PATTERN_WEATHER_LIST)+" ";
        String weather = "";
        if(objWeatherDO.arrWeatheDescp != null && objWeatherDO.arrWeatheDescp.size() > 0)
            weather = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);
        holder.tvWeatherCondition.setText(weather);

        holder.tvMaxTemp.setText(((BaseActivity)context).degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX)) + (char) 0x00B0);
        holder.tvMinTemp.setText(((BaseActivity)context).degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN)) + (char) 0x00B0);
        holder.tvDay.setText(date);

        String icon = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
        holder.ivWeather.setImageResource(AppConstants.getArtResourceForWeatherCondition(StringUtils.getInt(icon)));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Delete later
                Intent intentService = new Intent(context, SendWearableDataService.class);
                intentService.putExtra("TodayWeather", objWeatherDO);
                ((Activity)context).startService(intentService);

                if (((WeatherListActivity)context).mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putSerializable("WEATHER_DETAIL",objWeatherDO);
                    ItemWeatherDetailFragment fragment = new ItemWeatherDetailFragment();
                    fragment.setArguments(arguments);
                    ((WeatherListActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.itemweather_detail_container, fragment).commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemWeatherDetailActivity.class);
                    intent.putExtra("WEATHER_DETAIL",objWeatherDO);

                    context.startActivity(intent);
                }
            }
        });

        ((BaseActivity)context).applyTypeface(((BaseActivity)context).getParentView(holder.mView), ((BaseActivity) context).tfMyriadProRegular , Typeface.NORMAL);
    }

    @Override
    public int getItemCount() {
        if(arrWeatherDetails != null)
            return arrWeatherDetails.size();

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivWeather;
        public final TextView tvDay;
        public final TextView tvMaxTemp;
        public final TextView tvWeatherCondition;
        public final TextView tvMinTemp;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivWeather = (ImageView) view.findViewById(R.id.ivWeather);
            tvWeatherCondition = (TextView) view.findViewById(R.id.tvWeatherCondition);
            tvDay = (TextView) view.findViewById(R.id.tvDay);
            tvMaxTemp = (TextView) view.findViewById(R.id.tvMaxTemp);
            tvMinTemp = (TextView) view.findViewById(R.id.tvMinTemp);
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
