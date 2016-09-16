package com.arpaul.geoweather.webServices;

/**
 * Created by Aritra on 01-08-2016.
 */
public class WebServiceConstant {
    public final static int STATUS_SUCCESS             = 200;
    public final static int STATUS_CREATED             = 201;
    public final static int STATUS_ACCEPTED            = 202;
    public final static int STATUS_NO_CONTENT          = 204;
    public final static int STATUS_FAILED              = 500;
    public final static int STATUS_INVALID_SERVICE     = 501;
    public final static int STATUS_INVALID_API_KEY     = 401;

    public final static String URL_OPENWEATHERMAP      = "http://api.openweathermap.org/data/2.5/";
    public final static String URL_DAILY_FORECAST      = "forecast/daily?";

    public final static String GET      = "GET";
    public final static String POST     = "POST";

    public final static String SUCCESS  = "SUCCESS";
    public final static String FAILURE  = "FAILURE";
}
