package com.arpaul.geoweather.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.ColorUtils;
import com.arpaul.utilitieslib.StringUtils;

/**
 * A fragment representing a single ItemWeather detail screen.
 * This fragment is either contained in a {@link WeatherListActivity}
 * in two-pane mode (on tablets) or a {@link ItemWeatherDetailActivity}
 * on handsets.
 */
public class ItemWeatherDetailFragment extends Fragment {

    private TextView tvDayTitle, tvDayDate, tvDayTempMax, tvDayTempMin, tvDayHumidity, tvDayWind, tvDayPressure, tvDayWeather;
    private TextView tvPressure, tvWind, tvHumidity;
    private ImageView ivDayWeather;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private WeatherDataDO objWeatherDO;

    /**
     * The dummy content this fragment is presenting.
     */

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemWeatherDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("WEATHER_DETAIL")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            objWeatherDO = (WeatherDataDO) getArguments().getSerializable("WEATHER_DETAIL");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_itemweather_detail, container, false);

        initialiseControls(rootView);
        if (objWeatherDO != null) {
            bindControls();
        }

        return rootView;
    }

    private void bindControls(){
        tvDayTitle.setText(CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), AppConstants.DATE_PATTERN_WEEKNAME_FORMAT));
        tvDayDate.setText(CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), AppConstants.DATE_PATTERN_WEATHER_DETAIL));
        tvDayTempMax.setText(((BaseActivity)getActivity()).degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX)) + (char) 0x00B0);
        tvDayTempMin.setText(((BaseActivity)getActivity()).degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN)) + (char) 0x00B0);

        tvDayHumidity.setText((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_HUMIDITY) + "");
        tvDayWind.setText((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_WIND) + "");
        tvDayPressure.setText((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_PRESSURE) + "");

        if(objWeatherDO.arrWeatheDescp != null && objWeatherDO.arrWeatheDescp.size() > 0){
            String weather = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);
            tvDayWeather.setText(weather);

            String icon = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
            ivDayWeather.setImageResource(AppConstants.getArtResourceForWeatherCondition(StringUtils.getInt(icon)));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setTextColor();
    }

    private void setTextColor(){
        if(((BaseActivity)getActivity()).text_pattern == AppConstants.TEXT_PATTERN_DARK){
            tvDayTitle.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
            tvDayDate.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
            tvDayTempMax.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
            tvDayTempMin.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
            tvDayWeather.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
//            tvDayHumidity.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
//            tvDayWind.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
//            tvDayPressure.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
//            tvPressure.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
//            tvWind.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
//            tvHumidity.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorBlack));
        } else {
            tvDayTitle.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
            tvDayDate.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
            tvDayTempMax.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
            tvDayTempMin.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
            tvDayWeather.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
//            tvDayHumidity.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
//            tvDayWind.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
//            tvDayPressure.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
//            tvPressure.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
//            tvWind.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
//            tvHumidity.setTextColor(ColorUtils.getColor(getActivity(), R.color.colorWhite));
        }
    }

    private void initialiseControls(View rootView){
        tvDayTitle      = (TextView) rootView.findViewById(R.id.tvDayTitle);
        tvDayDate       = (TextView) rootView.findViewById(R.id.tvDayDate);
        tvDayTempMax    = (TextView) rootView.findViewById(R.id.tvDayTempMax);
        tvDayTempMin    = (TextView) rootView.findViewById(R.id.tvDayTempMin);
        tvDayHumidity   = (TextView) rootView.findViewById(R.id.tvDayHumidity);
        tvDayWind       = (TextView) rootView.findViewById(R.id.tvDayWind);
        tvDayPressure   = (TextView) rootView.findViewById(R.id.tvDayPressure);
        tvDayWeather    = (TextView) rootView.findViewById(R.id.tvDayWeather);

        tvPressure      = (TextView) rootView.findViewById(R.id.tvPressure);
        tvWind          = (TextView) rootView.findViewById(R.id.tvWind);
        tvHumidity      = (TextView) rootView.findViewById(R.id.tvHumidity);

        ivDayWeather    = (ImageView) rootView.findViewById(R.id.ivDayWeather);

        ((BaseActivity)getActivity()).applyTypeface(((BaseActivity)getActivity()).getParentView(rootView), ((BaseActivity)getActivity()).tfMyriadProRegular , Typeface.NORMAL);
    }
}
