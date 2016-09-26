package com.arpaul.geoweather.service;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;

import com.arpaul.geoweather.R;
import com.arpaul.geoweather.common.WearableConstants;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.ColorUtils;
import com.arpaul.utilitieslib.LogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;

/**
 * Created by Aritra on 23-09-2016.
 */

public class GeoWeatherWatchService extends CanvasWatchFaceService {

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private static final String TAG = "GeoWeatherWatchService";

    private Paint mBackgroundPaint;
    private Bitmap mBackgroundBitmap;

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

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        GoogleApiClient apiClient = null;
        Context context;
        Paint mBackgroundPaint, mTextPaint, mTextPaintDate, linePaint;
        public int text_pattern = 0;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            context = getBaseContext();
            Resources resources = GeoWeatherWatchService.this.getResources();

            setWatchFaceStyle(new WatchFaceStyle.Builder(GeoWeatherWatchService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            int setBgColor = setupBaseView();
            int setTextColor = 0;
            if(text_pattern == WearableConstants.TEXT_PATTERN_DARK)
                setTextColor = ColorUtils.getColor(context, R.color.colorBlack);
            else
                setTextColor = ColorUtils.getColor(context, R.color.colorWhite);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(setBgColor);
            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sp_event_icon);

            mTextPaint = createTextPaint(ColorUtils.getColor(context, R.color.digital_text));
            mTextPaintDate = createTextPaint(setTextColor);

            linePaint = new Paint();
            linePaint.setColor(setTextColor);
            linePaint.setStrokeWidth(2);


            apiClient = new GoogleApiClient.Builder(getBaseContext())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            LogUtils.infoLog(TAG, "onConnected: ");
                            Wearable.DataApi.addListener(apiClient, new DataApi.DataListener() {
                                @Override
                                public void onDataChanged(DataEventBuffer dataEventBuffer) {
                                    for(DataEvent event : dataEventBuffer){
                                        if(event.getType() == DataEvent.TYPE_CHANGED){
                                            DataItem item = event.getDataItem();
                                            if(item.getUri().getPath().compareTo("/weather") == 0){
                                                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                                                double high = dataMap.getDouble("high");
                                                double low = dataMap.getDouble("low");
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            LogUtils.infoLog(TAG, "onConnectionSuspended: ");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            LogUtils.infoLog(TAG, "onConnectionFailed: ");
                        }
                    })
                    .build();

            apiClient.connect();

        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
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
                text_pattern = WearableConstants.TEXT_PATTERN_DARK;
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
