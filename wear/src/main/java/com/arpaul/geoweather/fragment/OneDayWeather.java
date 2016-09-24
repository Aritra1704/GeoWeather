package com.arpaul.geoweather.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arpaul.geoweather.R;
import com.arpaul.geoweather.common.WearableConstants;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.dataObjects.WeatherDescriptionDO;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by ARPaul on 24-09-2016.
 */

public class OneDayWeather extends Fragment {

    private TextView tvDayTitle, tvDayDate, tvDayTempMax, tvDayTempMin, tvDayWeather;
    private ImageView ivDayWeather;
    private WeatherDataDO objWeatherDO;
    private DecimalFormat degreeFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("WEATHER_DETAIL")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            objWeatherDO = (WeatherDataDO) getArguments().getSerializable("WEATHER_DETAIL");

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View  rootView = inflater.inflate(R.layout.activity_weather_watch, null);

        initialiseControls(rootView);

        bindControls();

        return rootView;
    }

    private void bindControls(){
        degreeFormat = new DecimalFormat("##");
        degreeFormat.setRoundingMode(RoundingMode.CEILING);
        degreeFormat.setMinimumFractionDigits(0);
        degreeFormat.setMaximumFractionDigits(0);


        tvDayTitle.setText(CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), WearableConstants.DATE_PATTERN_WEEKNAME_FORMAT));
        tvDayDate.setText(CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), WearableConstants.DATE_PATTERN_WEATHER_DETAIL));
        tvDayTempMax.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX)) + (char) 0x00B0);
        tvDayTempMin.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN)) + (char) 0x00B0);

        if(objWeatherDO.arrWeatheDescp != null && objWeatherDO.arrWeatheDescp.size() > 0){
            String weather = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);
            tvDayWeather.setText(weather);

            String icon = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
            ivDayWeather.setImageResource(WearableConstants.getArtResourceForWeatherCondition(StringUtils.getInt(icon)));
        }

    }

    private void initialiseControls(View rootView){
        tvDayTitle      = (TextView) rootView.findViewById(R.id.tvDayTitle);
        tvDayDate       = (TextView) rootView.findViewById(R.id.tvDayDate);
        tvDayTempMax    = (TextView) rootView.findViewById(R.id.tvDayTempMax);
        tvDayTempMin    = (TextView) rootView.findViewById(R.id.tvDayTempMin);
        tvDayWeather    = (TextView) rootView.findViewById(R.id.tvDayWeather);

        ivDayWeather    = (ImageView) rootView.findViewById(R.id.ivDayWeather);
    }
}
