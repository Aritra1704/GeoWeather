package com.arpaul.geoweather.webServices;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;

import com.arpaul.geoweather.dataAccess.SSCPConstants;
import com.arpaul.geoweather.dataObjects.LocationDO;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.dataObjects.WeatherDescriptionDO;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.LinkedHashMap;

/**
 * Created by Aritra on 26-09-2016.
 */

public class SendWearableDataService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

//    private WeatherDataDO objWeatherDataDO;
    private GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = "SendWearableDataService";
    private static final String WEATHER_PATH = "/weather";

    //Shiva surya
    //https://github.com/shivasurya/go-ubiquitous


    public SendWearableDataService(){
//        super("SendWearableDataService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mGoogleApiClient = new GoogleApiClient.Builder(SendWearableDataService.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
////        if(intent.hasExtra("TodayWeather")){
////            objWeatherDataDO = (WeatherDataDO) intent.getExtras().get("TodayWeather");
//
//            mGoogleApiClient = new GoogleApiClient.Builder(SendWearableDataService.this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(Wearable.API)
//                    .build();
//
//            mGoogleApiClient.connect();
////        }
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogUtils.infoLog(LOG_TAG, "onConnected: ");

        /**
         * Add Cursor calls.
         */
        WeatherDataDO objWeatherDO = null;
        WeatherDescriptionDO objWeatherDescriptionDO;
        LinkedHashMap<String, WeatherDataDO> hashWeatherDO = new LinkedHashMap<>();

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
                        SSCPConstants.AS_WEATHER_TABLE + SSCPConstants.TABLE_DOT + WeatherDataDO.DATE + " = Date('now')");

        LogUtils.infoLog("QUERY_FARM_LIST", queryBuilder.getTables());

        Cursor cursor = getContentResolver().query(SSCPConstants.CONTENT_URI_RELATIONSHIP_JOIN,
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

        if (cursor != null && cursor.moveToFirst()) {
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

            PutDataMapRequest dataMap = PutDataMapRequest.create(WEATHER_PATH);
            dataMap.getDataMap().putLong(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS + "", (long) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS));
            dataMap.getDataMap().putDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX + "", (double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX));
            dataMap.getDataMap().putDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN + "", (double) objWeatherDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN));

            String icon = (String) objWeatherDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
            dataMap.getDataMap().putString(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON + "", icon);

            PutDataRequest request = dataMap.asPutDataRequest();
            DataApi.DataItemResult dataItemResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.infoLog(LOG_TAG, "onConnectionFailed: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        LogUtils.infoLog(LOG_TAG, "onConnectionSuspended: ");
    }
}
