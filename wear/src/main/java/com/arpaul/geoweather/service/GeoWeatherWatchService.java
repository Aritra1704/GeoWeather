package com.arpaul.geoweather.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;

import com.arpaul.geoweather.R;
import com.arpaul.geoweather.common.WearableConstants;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.dataObjects.WeatherDescriptionDO;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.ColorUtils;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by Aritra on 23-09-2016.
 */

public class GeoWeatherWatchService extends CanvasWatchFaceService {

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private static final String TAG = "GeoWeatherWatchService";

    private Paint mBackgroundPaint;
    private Bitmap mBackgroundBitmap;
    private WeatherDataDO objWeatherDataDO;

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {

        return new Engine();
    }

    private class EngineHandler extends Handler {
        private final WeakReference<GeoWeatherWatchService.Engine> mWeakReference;

        public EngineHandler(GeoWeatherWatchService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            GeoWeatherWatchService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        final Handler mUpdateTimeHandler = new EngineHandler(this);
        Context context;
        Paint mBackgroundPaint, linePaint;
        Paint mTPTimeHigh;
        Paint mTPDayHigh;
        Paint mTPMaxHigh;
        Paint mTPMinHigh;
        Paint mTextLowAmbientPaint;
        Bitmap mWeatherIcon;
        public int text_pattern = 0;
        public DecimalFormat degreeFormat;
        float xOffset, xOffsetMax, xOffsetMin, yOffsetTime, yOffsetDay, yOffsetDate, yOffsetMax, yOffsetMin, yOffsetIcon, yOffsetSkyCond;
        private static final String WEATHER_PATH = "/weather";

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(GeoWeatherWatchService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            context = getBaseContext();
            Resources resources = GeoWeatherWatchService.this.getResources();

            degreeFormat = new DecimalFormat("##");
            degreeFormat.setRoundingMode(RoundingMode.CEILING);
            degreeFormat.setMinimumFractionDigits(0);
            degreeFormat.setMaximumFractionDigits(0);

            setWatchFaceStyle(new WatchFaceStyle.Builder(GeoWeatherWatchService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            int setBgColor = setupBaseView();
            int setTextColor = ColorUtils.getColor(context, R.color.colorWhite);
            if(text_pattern == WearableConstants.TEXT_PATTERN_DARK)
                setTextColor = ColorUtils.getColor(context, R.color.colorBlack);
            else if(text_pattern == WearableConstants.TEXT_PATTERN_NOON)
                setTextColor = ColorUtils.getColor(context, R.color.colorAccent);
            else
                setTextColor = ColorUtils.getColor(context, R.color.colorWhite);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(setBgColor);
            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sp_event_icon);

            mTPTimeHigh = createTextPaint(setTextColor);
            mTPDayHigh = createTextPaint(setTextColor);
            mTPMaxHigh = createTextPaint(setTextColor);
            mTPMinHigh = createTextPaint(setTextColor);

            mTextLowAmbientPaint = createTextPaint(ColorUtils.getColor(context, R.color.colorWhite));

            linePaint = new Paint();
            linePaint.setColor(setTextColor);
            linePaint.setStrokeWidth(2);
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            LogUtils.debugLog(TAG, "Data Changed");
            for(DataEvent event : dataEventBuffer){
                if(event.getType() == DataEvent.TYPE_CHANGED){
                    DataItem item = event.getDataItem();
                    if(item.getUri().getPath().compareTo(WEATHER_PATH) == 0){
                        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                        objWeatherDataDO = new WeatherDataDO();
                        objWeatherDataDO.saveData(dataMap.getLong(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS + ""), WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS);

                        objWeatherDataDO.saveData(dataMap.getDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX + ""), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX);
                        objWeatherDataDO.saveData(dataMap.getDouble(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN + ""), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN);

                        WeatherDescriptionDO objWeatherDescriptionDO = new WeatherDescriptionDO();
                        objWeatherDescriptionDO.saveData(dataMap.getString(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON + ""), WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
                        objWeatherDescriptionDO.saveData(dataMap.getString(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN + ""), WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);

                        objWeatherDataDO.arrWeatheDescp.add(objWeatherDescriptionDO);

                    }
                }
            }
        }

        @Override
        public void onConnected(Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, Engine.this);
            trigger();
            Log.d(TAG, "connected Google Playservice API client");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "suspended");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            LogUtils.infoLog(TAG, "onConnectionFailed: ");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                mGoogleApiClient.connect();
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                invalidate();
            } else {
                unregisterReceiver();

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }

        }

        public void trigger() {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WEATHER_PATH);
            putDataMapRequest.getDataMap().putString("DATA", UUID.randomUUID().toString());
            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                Log.d(TAG, "Trigger failed for weather data");
                            } else {
                                Log.d(TAG, "Trigger success for weather data");
                            }
                        }
                    });
        }

        boolean mRegisteredTimeZoneReceiver = false;
        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            GeoWeatherWatchService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            GeoWeatherWatchService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                invalidate();
            }
        };

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = GeoWeatherWatchService.this.getResources();
            boolean isRound = insets.isRound();

            float tsTime = resources.getDimension(isRound ? R.dimen.margin_18 : R.dimen.margin_16);
            float tsDay = resources.getDimension(isRound ? R.dimen.margin_16 : R.dimen.margin_14);
            float tsMax = resources.getDimension(isRound ? R.dimen.margin_25 : R.dimen.margin_20);
            float tsMin = resources.getDimension(isRound ? R.dimen.margin_16 : R.dimen.margin_14);
            float tsLowAmbient = resources.getDimension(isRound ? R.dimen.margin_14 : R.dimen.margin_12);

            yOffsetTime = resources.getDimension(R.dimen.margin_10);
            yOffsetDay = resources.getDimension(R.dimen.margin_30);
            yOffsetDate = yOffsetDay;
            yOffsetMax = resources.getDimension(R.dimen.margin_30);
            yOffsetMin = yOffsetMax + resources.getDimension(R.dimen.margin_20);
            yOffsetSkyCond = yOffsetMin + resources.getDimension(R.dimen.margin_30);
            yOffsetIcon = resources.getDimension(R.dimen.margin_190);

            xOffset = resources.getDimension(isRound ? R.dimen.margin_30 : R.dimen.margin_25);
            xOffsetMax = xOffset + 20;
            xOffsetMin = xOffsetMax + 10;

            mTPTimeHigh.setTextSize(tsTime);
            mTPDayHigh.setTextSize(tsDay);
            mTPMaxHigh.setTextSize(tsMax);
            mTPMinHigh.setTextSize(tsMin);
            mTextLowAmbientPaint.setTextSize(tsLowAmbient);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);

            // Draw the background.
            int width = bounds.width(), height = bounds.height();
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, width, height, mBackgroundPaint);
            }

            canvas.drawLine(0, height/2, width, height/2, linePaint);

            //Remove later
            setDummyData();

            if(objWeatherDataDO != null){
                String day = CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), WearableConstants.DATE_PATTERN_WEEKNAME_FORMAT);
                String date = CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), WearableConstants.DATE_PATTERN_WEATHER_DETAIL);
                String textMax = degreeFormat.format((double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX)) + (char) 0x00B0;
                String textMin = degreeFormat.format((double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN)) + (char) 0x00B0;

                String current_time = CalendarUtils.getDateinPattern(CalendarUtils.TIME_FORMAT);

                float iconDimen = (int) getResources().getDimension(R.dimen.margin_70);
                int xDayPos = (width / 2);
                if (mAmbient) {
                    float lowTextDateLen = mTextLowAmbientPaint.measureText(day + ", " + date);
                    float lowTextTempLen = mTextLowAmbientPaint.measureText(textMax + " / " + textMin);
                    canvas.drawText(day + ", " + date, xDayPos - lowTextDateLen/2, height/2 - yOffsetDay, mTextLowAmbientPaint);

                    float lowTextCurTimeLen = mTextLowAmbientPaint.measureText(current_time);
                    canvas.drawText(current_time, xDayPos - lowTextCurTimeLen/2, height/2 - yOffsetTime, mTextLowAmbientPaint);

                    canvas.drawText(textMax + " / " + textMin, xDayPos - lowTextTempLen/2, height/2 + yOffsetMax, mTextLowAmbientPaint);
                    if(objWeatherDataDO != null && objWeatherDataDO.arrWeatheDescp != null && objWeatherDataDO.arrWeatheDescp.size() > 0){
                        String skyCondition = (String) objWeatherDataDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);
                        float highTextLen = mTPMinHigh.measureText(skyCondition);
                        canvas.drawText(skyCondition, xDayPos - highTextLen/2, height/2 + yOffsetMax + 50, mTextLowAmbientPaint);
                    }
                } else {
                    float lowTextLen = mTPDayHigh.measureText(day + ", " + date);
                    canvas.drawText(day + ", " + date, xDayPos - lowTextLen/2, height/2 - yOffsetDay, mTPDayHigh);

                    float lowTextCurTimeLen = mTPTimeHigh.measureText(current_time);
                    canvas.drawText(current_time, xDayPos - lowTextCurTimeLen/2, height/2 - yOffsetTime, mTPTimeHigh);

                    canvas.drawText(textMax, xOffsetMax, height/2 + yOffsetMax, mTPMaxHigh);
                    canvas.drawText(textMin, xOffsetMin, height/2 + yOffsetMin, mTPMinHigh);

                    if(objWeatherDataDO != null && objWeatherDataDO.arrWeatheDescp != null && objWeatherDataDO.arrWeatheDescp.size() > 0){

                        //Icon
                        String iconid = (String) objWeatherDataDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
                        Drawable b = getResources().getDrawable(WearableConstants.getArtResourceForWeatherCondition(StringUtils.getInt(iconid)));
                        Bitmap icon = null;
                        if (((BitmapDrawable) b) != null) {
                            icon = ((BitmapDrawable) b).getBitmap();
                        }
                        mWeatherIcon = Bitmap.createScaledBitmap(icon, (int) iconDimen, (int) iconDimen, true);
                        float iconXOffset = xOffset + (int) getResources().getDimension(R.dimen.margin_70);
                        canvas.drawBitmap(mWeatherIcon, iconXOffset, height/2, null);

                        String skyCondition = (String) objWeatherDataDO.arrWeatheDescp.get(0).getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);
                        float highTextLen = mTPMinHigh.measureText(skyCondition);
                        canvas.drawText(skyCondition, xDayPos - highTextLen/2, height/2 + yOffsetSkyCond, mTPMinHigh);
                    }
                }
//                holder.ivWeather.setImageResource(AppConstants.getArtResourceForWeatherCondition(StringUtils.getInt(icon)));
            }
        }

        private void setDummyData(){

            objWeatherDataDO = new WeatherDataDO();
            objWeatherDataDO.saveData(StringUtils.getLong("1475044976417"), WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS);

            objWeatherDataDO.saveData(StringUtils.getDouble("29.57"), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX);
            objWeatherDataDO.saveData(StringUtils.getDouble("21.71"), WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN);
            objWeatherDataDO.saveData("800", WeatherDataDO.WEATHERDATA.TYPE_ICON);

            WeatherDescriptionDO objWeatherDescriptionDO = new WeatherDescriptionDO();
            objWeatherDescriptionDO.saveData("800", WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON);
            objWeatherDescriptionDO.saveData("Clear Sky", WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN);

            objWeatherDataDO.arrWeatheDescp.add(objWeatherDescriptionDO);
        }

        boolean mLowBitAmbient;
        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        boolean mAmbient;
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTPDayHigh.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
//            if (shouldTimerBeRunning()) {
//                long timeMs = System.currentTimeMillis();
//                long delayMs = INTERACTIVE_UPDATE_RATE_MS
//                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
//                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
//            }
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        private int setupBaseView(){
            String currentTime = CalendarUtils.getDateinPattern(CalendarUtils.TIME_FORMAT);
            String morningTime = "10:00 am";
            String noonTime = "4:00 pm";
            String eveningTime = "8:00 pm";
            String nightTime = "11:59 pm";
            String dawnTime = "4:00 am";

            int setColor = 0;

            if(CalendarUtils.getDiffBtwDatesPattern(dawnTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
                text_pattern = WearableConstants.TEXT_PATTERN_LIGHT;
                setColor = ColorUtils.getColor(context, R.color.colorNight);
            } else if(CalendarUtils.getDiffBtwDatesPattern(morningTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
                text_pattern = WearableConstants.TEXT_PATTERN_DARK;
                setColor = ColorUtils.getColor(context, R.color.colorMorning);
            } else if(CalendarUtils.getDiffBtwDatesPattern(noonTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
                text_pattern = WearableConstants.TEXT_PATTERN_NOON;
                setColor = ColorUtils.getColor(context, R.color.colorNoon);
            } else if(CalendarUtils.getDiffBtwDatesPattern(eveningTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
                text_pattern = WearableConstants.TEXT_PATTERN_LIGHT;
                setColor = ColorUtils.getColor(context, R.color.colorEvening);
            } else if(CalendarUtils.getDiffBtwDatesPattern(nightTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
                text_pattern = WearableConstants.TEXT_PATTERN_LIGHT;
                setColor = ColorUtils.getColor(context, R.color.colorNight);
            }

            return setColor;
        }
    }
}
