package com.arpaul.geoweather.webServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.utilitieslib.LogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Aritra on 26-09-2016.
 */

public class SendWearableDataService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private WeatherDataDO objWeatherDataDO;
    private GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = "SendWearableDataService";
    private static final String WEATHER_PATH = "/weather";

    //Shiva surya
    //https://github.com/shivasurya/go-ubiquitous


    public SendWearableDataService(){
        super("SendWearableDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.hasExtra("TodayWeather")){
            objWeatherDataDO = (WeatherDataDO) intent.getExtras().get("TodayWeather");

            mGoogleApiClient = new GoogleApiClient.Builder(SendWearableDataService.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogUtils.infoLog(LOG_TAG, "onConnected: ");

        /**
         * Add Cursor calls.
         */

        PutDataMapRequest dataMap = PutDataMapRequest.create(WEATHER_PATH);
        dataMap.getDataMap().putLong(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS + "", (long) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS));
        dataMap.getDataMap().putDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX + "", (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX));
        dataMap.getDataMap().putDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN + "", (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN));

        PutDataRequest request = dataMap.asPutDataRequest();
        DataApi.DataItemResult dataItemResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
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
