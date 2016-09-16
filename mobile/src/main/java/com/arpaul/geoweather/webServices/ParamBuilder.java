package com.arpaul.geoweather.webServices;

import android.net.Uri;

/**
 * Created by Aritra on 18-08-2016.
 */
public class ParamBuilder {

    public static String getDailyForecastParam(String url, String lat, String lon, String mode, String units, String cnt, String appid){
        Uri.Builder builder = Uri.parse(url).buildUpon();

        builder.appendQueryParameter("lat", lat);
        builder.appendQueryParameter("lon", lon);
        builder.appendQueryParameter("mode", mode);
        builder.appendQueryParameter("units", units);
        builder.appendQueryParameter("cnt", cnt);
        builder.appendQueryParameter("APPID", appid);

        builder.build();

        return builder.toString();
    }
}
