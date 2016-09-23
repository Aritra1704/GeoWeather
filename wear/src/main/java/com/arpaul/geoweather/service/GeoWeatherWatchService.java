package com.arpaul.geoweather.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import com.arpaul.geoweather.R;

/**
 * Created by Aritra on 23-09-2016.
 */

public class GeoWeatherWatchService extends CanvasWatchFaceService {

    private static final String TAG = "GeoWeatherWatchService";

    private Paint mBackgroundPaint;
    private Bitmap mBackgroundBitmap;

    @Override
    public Engine onCreateEngine() {

        return new Engine();
    }

    private class EngineHandler extends CanvasWatchFaceService.Engine {


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(GeoWeatherWatchService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);
            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sp_event_icon);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
        }
    }
}
