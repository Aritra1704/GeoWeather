package com.arpaul.geoweather.common;

import android.app.Application;

/**
 * Created by Aritra on 27-07-2016.
 */
public class ApplicationInstance extends Application {

    public static final int LOADER_FETCH_DAILY_FORECAST_API     = 1;
    public static final int LOADER_FETCH_DAILY_FORECAST_DB      = 2;

    public static final int LOADER_FETCH_ALL_DATA   = 101;
    public static final int LOADER_DELETE_ALL_DATA  = 102;

//    public static final String REST_URL                 = "http://localhost:1234";
    private static final String REST_URL                = "http://dfb53180.ngrok.io";
    public static final String REST_API_URL             = REST_URL + "/api/v1/";
    public static final String IMAGE_URL                = REST_URL;

    public static final String URL_PARTICIPANT          = "participants/tour_participants";
    public static final String URL_PRODUCTS             = "products/participant_products";
    public static final String URL_TOURS                = "tour/current_tour";
    public static final String URL_FEATURES             = "features/participant_features";
    public static final String URL_NEARBY_PARTICIPANT   = "participants/nearby";

    public static final String LOCK_APP_DB              = "LOCK_APP_DB";
}
