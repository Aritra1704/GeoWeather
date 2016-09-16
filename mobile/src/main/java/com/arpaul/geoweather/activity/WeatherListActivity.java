package com.arpaul.geoweather.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.arpaul.customalertlibrary.popups.statingDialog.CustomPopupType;
import com.arpaul.geoweather.R;
import com.arpaul.geoweather.adapter.WeatherAdapter;
import com.arpaul.geoweather.common.AppConstants;
import com.arpaul.geoweather.common.ApplicationInstance;
import com.arpaul.geoweather.dataAccess.SSCPConstants;
import com.arpaul.geoweather.dataObjects.LocationDO;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.dataObjects.WeatherDescriptionDO;
import com.arpaul.geoweather.dataObjects.WeatherTodayDO;
import com.arpaul.geoweather.fragments.ForecastFragment;
import com.arpaul.geoweather.webServices.WeatherLoader;
import com.arpaul.gpslibrary.fetchLocation.GPSCallback;
import com.arpaul.gpslibrary.fetchLocation.GPSErrorCode;
import com.arpaul.gpslibrary.fetchLocation.GPSUtills;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.ColorUtils;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.NetworkUtility;
import com.arpaul.utilitieslib.PermissionUtils;
import com.arpaul.utilitieslib.StringUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemWeatherDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class WeatherListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks, GPSCallback  {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public boolean mTwoPane;

    private View llWeatherListActivity;

    private TextView tvLocation, tvWeatherCondition, tvCurrentTemp, tvDay, tvMaxTemp, tvMinTemp, tvToday;
    private TextView tvTimeMorn, tvTempMorn, tvTimeDay, tvTempDay, tvTimeEve, tvTempEve, tvTimeNight, tvTempNight;
    private RecyclerView rvWeather;
    private ImageView ivDayWeather;
    private LinearLayout llTodayWeather;
    private WeatherAdapter adapterWeather;

    private GPSUtills gpsUtills;
    private boolean ispermissionGranted = false;
    private boolean isGpsEnabled;
    private LatLng currentLatLng = null;

    @Override
    public void initialize(Bundle savedInstanceState) {
        llWeatherListActivity = baseInflater.inflate(com.arpaul.geoweather.R.layout.activity_itemweather_list,null);
        llBody.addView(llWeatherListActivity, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }*/

        if (llBody.findViewById(R.id.itemweather_detail_container) != null) {
            mTwoPane = true;
        }

        initializeControls();

        bindControls();
    }

    private void bindControls(){
        gpsUtills = GPSUtills.getInstance(this);
        gpsUtills.setLogEnable(true);
        gpsUtills.setPackegeName(getPackageName());
        gpsUtills.setListner(this);

        if(new PermissionUtils().checkPermission(this,new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION}) != 0){

            new PermissionUtils().verifyLocation(this,new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION});
        }
        else{
            createGPSUtils();
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id){
            case ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_API:
                return new WeatherLoader(this, bundle);
            case ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_DB:
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

                queryBuilder.setTables(
                        SSCPConstants.LOCATION_TABLE_NAME + SSCPConstants.AS_LOCATION_TABLE +
                                SSCPConstants.TABLE_INNER_JOIN +
                                SSCPConstants.WEATHER_TABLE_NAME + SSCPConstants.AS_WEATHER_TABLE +
                                SSCPConstants.TABLE_ON +
                                SSCPConstants.AS_LOCATION_TABLE + SSCPConstants.TABLE_DOT + LocationDO.LOCATION_ID + SSCPConstants.TABLE_EQUAL +
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.LOCATION_ID +
                                SSCPConstants.TABLE_LEFT_OUTER_JOIN +
                                SSCPConstants.WEATHER_DESCRIP_TABLE_NAME + SSCPConstants.AS_WEATHER_DESC_TABLE +
                                SSCPConstants.TABLE_ON +
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.WEATHER_ID + SSCPConstants.TABLE_EQUAL +
                                SSCPConstants.AS_WEATHER_DESC_TABLE + SSCPConstants.TABLE_DOT + WeatherDescriptionDO.WEATHER_ID +
                                SSCPConstants.TABLE_WHERE +
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.DATE + " >= Date('now')");

                LogUtils.infoLog("QUERY_FARM_LIST", queryBuilder.getTables());

                return new CursorLoader(this,
                        SSCPConstants.CONTENT_URI_RELATIONSHIP_JOIN,
                        new String[]{
                                SSCPConstants.AS_LOCATION_TABLE + SSCPConstants.TABLE_DOT + LocationDO.CITY_NAME,

                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.WEATHER_ID,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.DATE,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.DATE_MILLIS,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.TEMP_DAY,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.TEMP_MINIMUM,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.TEMP_MAXIMUM,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.TEMP_NIGHT,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.TEMP_EVE,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.TEMP_MORN,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.PRESSURE,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.SEA_LEVEL,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.GRN_LEVEL,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.HUMIDITY,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.WIND,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.DEG,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.CLOUDS,
                                SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.RAIN,

                                SSCPConstants.AS_WEATHER_DESC_TABLE + SSCPConstants.TABLE_DOT + WeatherDescriptionDO.WEATHER_ICON_ID,
                                SSCPConstants.AS_WEATHER_DESC_TABLE + SSCPConstants.TABLE_DOT + WeatherDescriptionDO.MAIN},
                        queryBuilder.getTables(),
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()){
            case ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_API:
                if(data instanceof ArrayList){
                    /*ArrayList<WeatherDataDO> arrWeather = (ArrayList<WeatherDataDO>) data;
                    adapterWeather.refresh(arrWeather);*/

                    if(getSupportLoaderManager().getLoader(ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_DB) != null)
                        getSupportLoaderManager().restartLoader(ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_DB, null, this).forceLoad();
                    else
                        getSupportLoaderManager().initLoader(ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_DB, null, this).forceLoad();
                }
                break;
            case ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_DB:
                if(data instanceof Cursor){
                    Cursor cursor = (Cursor) data;
                    WeatherDataDO objWeatherDO = null;
                    if (cursor != null && cursor.moveToFirst()) {
                        ArrayList<WeatherDataDO> arrWeather = new ArrayList<>();
                        LinkedHashMap<String, WeatherDataDO> hashWeatherDO = new LinkedHashMap<>();
                        WeatherDescriptionDO objWeatherDescriptionDO = null;
                        do {
                            objWeatherDO = new WeatherDataDO();
                            objWeatherDescriptionDO = new WeatherDescriptionDO();

                            String location = SSCPConstants.AS_LOCATION_TABLE + SSCPConstants.TABLE_DOT;
                            objWeatherDO.saveData(cursor.getString(cursor.getColumnIndex(location + LocationDO.CITY_NAME)), WeatherDataDO.WEATHERDATA.TYPE_LOCATION_NAME);

                            String weather = SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT;
                            objWeatherDO.saveData(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.WEATHER_ID)), WeatherDataDO.WEATHERDATA.TYPE_WEATHER_ID);
                            objWeatherDO.saveData(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.DATE)), WeatherDataDO.WEATHERDATA.TYPE_DATE);
                            objWeatherDO.saveData(StringUtils.getLong(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.DATE_MILLIS))), WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.TEMP_DAY))), WeatherDataDO.WEATHERDATA.TYPE_TEMP);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.TEMP_MINIMUM))), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.TEMP_MAXIMUM))), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.TEMP_NIGHT))), WeatherDataDO.WEATHERDATA.TYPE_TEMP_NIGHT);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.TEMP_EVE))), WeatherDataDO.WEATHERDATA.TYPE_TEMP_EVE);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.TEMP_MORN))), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MORN);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.PRESSURE))), WeatherDataDO.WEATHERDATA.TYPE_PRESSURE);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.SEA_LEVEL))), WeatherDataDO.WEATHERDATA.TYPE_SEA_LEVEL);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.GRN_LEVEL))), WeatherDataDO.WEATHERDATA.TYPE_GRN_LEVEL);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.HUMIDITY))), WeatherDataDO.WEATHERDATA.TYPE_HUMIDITY);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.WIND))), WeatherDataDO.WEATHERDATA.TYPE_WIND);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.DEG))), WeatherDataDO.WEATHERDATA.TYPE_DEG);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.CLOUDS))), WeatherDataDO.WEATHERDATA.TYPE_CLOUDS);
                            objWeatherDO.saveData(StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(weather + WeatherDataDO.RAIN))), WeatherDataDO.WEATHERDATA.TYPE_RAIN);

                            String weather_descp = SSCPConstants.AS_WEATHER_DESC_TABLE + SSCPConstants.TABLE_DOT;
                            objWeatherDescriptionDO.saveData(cursor.getString(cursor.getColumnIndex(weather_descp + WeatherDescriptionDO.WEATHER_ICON_ID)), WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
                            objWeatherDescriptionDO.saveData(cursor.getString(cursor.getColumnIndex(weather_descp + WeatherDescriptionDO.MAIN)), WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);


                            WeatherDataDO objWeatherDataDO = hashWeatherDO.get((String) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_WEATHER_ID));
                            if (objWeatherDataDO != null && objWeatherDataDO.arrWeatheDescp.size() > 0){
                                objWeatherDataDO.arrWeatheDescp.add(objWeatherDescriptionDO);
                            } else {
                                objWeatherDO.arrWeatheDescp.add(objWeatherDescriptionDO);
                                hashWeatherDO.put((String) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_WEATHER_ID), objWeatherDO);
                            }
                        } while (cursor.moveToNext());

                        if(hashWeatherDO != null && hashWeatherDO.size() > 0){
                            for(String key: hashWeatherDO.keySet()){
                                arrWeather.add(hashWeatherDO.get(key));
                            }
                        }
//                        arrWeather.add(objWeatherDO);
                        if(arrWeather != null && arrWeather.size() > 0){
                            setData(arrWeather);

                            ArrayList<WeatherDataDO> arrWeatherDO = (ArrayList<WeatherDataDO>) arrWeather.clone();
                            arrWeatherDO.remove(0);
                            adapterWeather.refresh(arrWeatherDO);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //adapterWeather.refresh(new ArrayList<WeatherDataDO>());
    }

    @Override
    public void onResume() {
        super.onResume();

        setTextColor();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            ispermissionGranted = true;
            gpsUtills.connectGoogleApiClient();
            createGPSUtils();

            getCurrentLocation();
        }
    }

    @Override
    public void gotGpsValidationResponse(Object response, GPSErrorCode code)
    {
        if(code == GPSErrorCode.EC_GPS_PROVIDER_NOT_ENABLED) {
            isGpsEnabled = false;
            showCustomDialog(getString(R.string.gpssettings),getString(R.string.gps_not_enabled),getString(R.string.settings),getString(R.string.cancel),getString(R.string.settings), CustomPopupType.DIALOG_ALERT,false);
        }
        else if(code == GPSErrorCode.EC_GPS_PROVIDER_ENABLED) {
            isGpsEnabled = true;
            gpsUtills.getCurrentLatLng();
        }
        else if(code == GPSErrorCode.EC_UNABLE_TO_FIND_LOCATION) {
            currentLatLng = (LatLng) response;

            showCustomDialog(getString(R.string.alert),getString(R.string.unable_to_fetch_your_current_location),getString(R.string.ok),null,getString(R.string.unable_to_fetch_your_current_location), CustomPopupType.DIALOG_ALERT,false);
        }
        else if(code == GPSErrorCode.EC_LOCATION_FOUND) {
            currentLatLng = (LatLng) response;
            LogUtils.debugLog("GPSTrack", "Currrent latLng :"+currentLatLng.latitude+" \n"+currentLatLng.longitude);

            LocationDO objLocationDO = new LocationDO();
            objLocationDO.saveData(currentLatLng.latitude, LocationDO.LOCATIONDATA.TYPE_COORD_LAT);
            objLocationDO.saveData(currentLatLng.longitude, LocationDO.LOCATIONDATA.TYPE_COORD_LON);

            Bundle bundle = new Bundle();
            bundle.putSerializable(WeatherLoader.BUNDLE_WEATHERLOADER, objLocationDO);

            if(NetworkUtility.isConnectionAvailable(this))
                getSupportLoaderManager().initLoader(ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_API, bundle, this).forceLoad();
            else
                getSupportLoaderManager().initLoader(ApplicationInstance.LOADER_FETCH_DAILY_FORECAST_DB, null, this).forceLoad();

            gpsUtills.stopLocationUpdates();
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_LOCATION_IS_VALID) {
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_lOCATION_IS_INVAILD) {
        }
        else if(code == GPSErrorCode.EC_DEVICE_CONFIGURED_PROPERLY) {
        }
    }

    private void getCurrentLocation(){
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                gpsUtills.getCurrentLatLng();
            }
        }, 2 * 1000);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(gpsUtills != null && ispermissionGranted){
            gpsUtills.connectGoogleApiClient();

            getCurrentLocation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(gpsUtills != null){
            gpsUtills.stopLocationUpdates();
            gpsUtills.disConnectGoogleApiClient();
        }
    }

    private void createGPSUtils(){
        gpsUtills.isGpsProviderEnabled();
    }

    private void setData(ArrayList<WeatherDataDO> arrWeather){
        if(arrWeather != null && arrWeather.size() > 0){
            for (WeatherDataDO objWeatherDO : arrWeather){
                String date = (String) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE);
                if(date.trim().equalsIgnoreCase(CalendarUtils.getDateinPattern(CalendarUtils.DATE_FORMAT1))) {
                    String weather = "";
                    if(objWeatherDO.arrWeatheDescp != null && objWeatherDO.arrWeatheDescp.size() > 0){
                        weather = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);
                        tvWeatherCondition.setText(weather);

                        String icon = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
                        ivDayWeather.setImageResource(AppConstants.getArtResourceForWeatherCondition(StringUtils.getInt(icon)));
                    }

                    tvLocation.setText((String) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_LOCATION_NAME));

                    tvCurrentTemp.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP)) + (char) 0x00B0);

                    String dateToday = CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), AppConstants.DATE_PATTERN_WEEKNAME_FORMAT)+" ";
                    tvDay.setText(dateToday);
                    tvMaxTemp.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX)) + (char) 0x00B0);
                    tvMinTemp.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN)) + (char) 0x00B0);

                    tvTimeMorn.setText("Morning");
                    tvTempMorn.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MORN)) + (char) 0x00B0);

                    tvTimeDay.setText("Day");
                    tvTempDay.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP)) + (char) 0x00B0);

                    tvTimeEve.setText("Evening");
                    tvTempEve.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_EVE)) + (char) 0x00B0);

                    tvTimeNight.setText("Night");
                    tvTempNight.setText(degreeFormat.format((double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_NIGHT)) + (char) 0x00B0);
                }
            }
        }
    }

    private void setTextColor(){
        if(text_pattern == AppConstants.TEXT_PATTERN_DARK){
            tvWeatherCondition.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvLocation.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvCurrentTemp.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvDay.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvMaxTemp.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvMinTemp.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTimeMorn.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTempMorn.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTimeDay.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTempDay.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTimeEve.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTempEve.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTimeNight.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvTempNight.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
            tvToday.setTextColor(ColorUtils.getColor(this, R.color.colorBlack));
        } else {
            tvWeatherCondition.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvLocation.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvCurrentTemp.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvDay.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvMaxTemp.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvMinTemp.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTimeMorn.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTempMorn.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTimeDay.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTempDay.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTimeEve.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTempEve.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTimeNight.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvTempNight.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
            tvToday.setTextColor(ColorUtils.getColor(this, R.color.colorWhite));
        }
    }

    private void setTodayView(ArrayList<WeatherTodayDO> arrWeatherToday){
        llTodayWeather.removeAllViews();
        for (WeatherTodayDO objWeatherTodayDO : arrWeatherToday){

            View view = LayoutInflater.from(this).inflate(R.layout.adapter_day_timings, null);
            TextView tvDayTime = (TextView) view.findViewById(R.id.tvDayTime);
            TextView tvDayTemp = (TextView) view.findViewById(R.id.tvDayTemp);

            tvDayTime.setText(objWeatherTodayDO.dayTime);
            tvDayTemp.setText(degreeFormat.format(objWeatherTodayDO.temperature) + (char) 0x00B0);
            llTodayWeather.addView(view);
        }
    }

    private void initializeControls(){

        tvLocation = (TextView) llWeatherListActivity.findViewById(R.id.tvLocation);
        tvWeatherCondition = (TextView) llWeatherListActivity.findViewById(R.id.tvWeatherCondition);

        tvCurrentTemp = (TextView) llWeatherListActivity.findViewById(R.id.tvCurrentTemp);
        tvDay = (TextView) llWeatherListActivity.findViewById(R.id.tvDay);
        tvMaxTemp = (TextView) llWeatherListActivity.findViewById(R.id.tvMaxTemp);
        tvMinTemp = (TextView) llWeatherListActivity.findViewById(R.id.tvMinTemp);

        ivDayWeather = (ImageView) llWeatherListActivity.findViewById(R.id.ivDayWeather);

        llTodayWeather = (LinearLayout) llWeatherListActivity.findViewById(R.id.llTodayWeather);


        tvToday = (TextView) llWeatherListActivity.findViewById(R.id.tvToday);
        tvTimeMorn = (TextView) llWeatherListActivity.findViewById(R.id.tvTimeMorn);
        tvTempMorn = (TextView) llWeatherListActivity.findViewById(R.id.tvTempMorn);
        tvTimeDay = (TextView) llWeatherListActivity.findViewById(R.id.tvTimeDay);
        tvTempDay = (TextView) llWeatherListActivity.findViewById(R.id.tvTempDay);
        tvTimeEve = (TextView) llWeatherListActivity.findViewById(R.id.tvTimeEve);
        tvTempEve = (TextView) llWeatherListActivity.findViewById(R.id.tvTempEve);
        tvTimeNight = (TextView) llWeatherListActivity.findViewById(R.id.tvTimeNight);
        tvTempNight = (TextView) llWeatherListActivity.findViewById(R.id.tvTempNight);


        rvWeather = (RecyclerView) llWeatherListActivity.findViewById(R.id.rvWeather);
        adapterWeather = new WeatherAdapter(this, new ArrayList<WeatherDataDO>());
        rvWeather.setAdapter(adapterWeather);

        applyTypeface(getParentView(llWeatherListActivity), tfMyriadProRegular , Typeface.NORMAL);
    }
}
