package com.arpaul.geoweather.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.WindowInsets;

import com.arpaul.geoweather.R;
import com.arpaul.geoweather.adapter.GridPagerAdapter;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.service.WatchService;
import com.arpaul.utilitieslib.LogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by ARPaul on 17-09-2016.
 */
public class WeatherWatchActivity extends Activity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient mGoogleApiClient;
    private GridViewPager pager;
    private DotsPageIndicator page_indicator;

    private static final String TAG = WeatherWatchActivity.class.getSimpleName();
    //https://github.com/hemal-shah/Sunshine/blob/master/Wear/src/main/java/hemal/mukesh/shah/sunshine/MyWatchFace.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initialiseControls();

        bindControls();
    }

    private void bindControls(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final Resources res = getResources();
        pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.margin_100);
                int colMargin = res.getDimensionPixelOffset(round ? R.dimen.margin_50 : R.dimen.margin_10);
                pager.setPageMargins(rowMargin, colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                pager.onApplyWindowInsets(insets);
                return insets;
            }
        });

        pager.setAdapter(new GridPagerAdapter(this, getFragmentManager()));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogUtils.infoLog(TAG, "onConnected: ");
        Wearable.DataApi.addListener(mGoogleApiClient, new DataApi.DataListener(){
            @Override
            public void onDataChanged(DataEventBuffer dataEventBuffer) {
                for(DataEvent dataEvent : dataEventBuffer){
                    if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                        DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                        String path = dataEvent.getDataItem().getUri().getPath();
                        if(path.equals("/weather")){
                            WeatherDataDO objWeatherDataDO = new WeatherDataDO();
                            objWeatherDataDO.saveData(dataMap.getLong(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS + ""), WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS);

                            objWeatherDataDO.saveData(dataMap.getDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX + ""), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX);
                            objWeatherDataDO.saveData(dataMap.getDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN + ""), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void sendStepCount(int step, long timestamp){
        PutDataMapRequest putdatamaprequest = PutDataMapRequest.create("/step-counter");

        putdatamaprequest.getDataMap().putInt("step-count", step);
        putdatamaprequest.getDataMap().putLong("timestamp", timestamp);

        PutDataRequest request = putdatamaprequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        if(!dataItemResult.getStatus().isSuccess()){

                        } else {

                        }
                    }
                });
    }

    private void initialiseControls(){
        page_indicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        pager = (GridViewPager) findViewById(R.id.pager);

    }
}
