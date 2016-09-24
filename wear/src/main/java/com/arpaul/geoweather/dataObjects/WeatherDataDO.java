package com.arpaul.geoweather.dataObjects;

import java.util.ArrayList;

/**
 * Created by ARPaul on 04-04-2016.
 */
public class WeatherDataDO extends BaseDO {

    private String location_name = "";

    private long dt = 0;
    private String date = "";
    private String weather_id = "";
    private double temperatureDay = 0;
    private double temperatureMin = 0;
    private double temperatureMax = 0;
    private double temperatureNight = 0;
    private double temperatureEve = 0;
    private double temperatureMorn = 0;
    private double pressure = 0;
    private double sea_level = 0;
    private double grnd_level = 0;
    private double humidity = 0;

    private double speed = 0;
    private double deg = 0;
    private double clouds = 0;
    private double rain = 0;

    public ArrayList<WeatherDescriptionDO> arrWeatheDescp = new ArrayList<>();

    public void saveData(Object data, WEATHERDATA type){
        switch (type){
            case TYPE_LOCATION_NAME:
                location_name = (String) data;
                break;
            case TYPE_WEATHER_ID:
                weather_id = (String) data;
                break;
            case TYPE_DATE:
                date = (String) data;
                break;
            case TYPE_DATE_MILIS:
                dt = (long) data;
                break;
            case TYPE_TEMP:
                temperatureDay = (double) data;
                break;
            case TYPE_TEMP_MIN:
                temperatureMin = (double)data;
                break;
            case TYPE_TEMP_MAX:
                temperatureMax = (double)data;
                break;
            case TYPE_TEMP_NIGHT:
                temperatureNight = (double)data;
                break;
            case TYPE_TEMP_EVE:
                temperatureEve = (double)data;
                break;
            case TYPE_TEMP_MORN:
                temperatureMorn = (double)data;
                break;
            case TYPE_PRESSURE:
                pressure = (double)data;
                break;
            case TYPE_SEA_LEVEL:
                sea_level = (double)data;
                break;
            case TYPE_GRN_LEVEL:
                grnd_level = (double)data;
                break;
            case TYPE_HUMIDITY:
                humidity = (double)data;
                break;
            case TYPE_WIND:
                speed = (double)data;
                break;
            case TYPE_DEG:
                deg = (double)data;
                break;
            case TYPE_CLOUDS:
                clouds = (double)data;
                break;
            case TYPE_RAIN:
                rain = (double)data;
                break;
        }
    }

    public Object getData(WEATHERDATA type){
        switch (type){
            case TYPE_LOCATION_NAME:
                return location_name;
            case TYPE_WEATHER_ID:
                return weather_id;
            case TYPE_DATE:
                return date;
            case TYPE_DATE_MILIS:
                return dt;
            case TYPE_TEMP:
                return temperatureDay;
            case TYPE_TEMP_MIN:
                return temperatureMin;
            case TYPE_TEMP_MAX:
                return temperatureMax;
            case TYPE_TEMP_NIGHT:
                return temperatureNight;
            case TYPE_TEMP_EVE:
                return temperatureEve;
            case TYPE_TEMP_MORN:
                return temperatureMorn;
            case TYPE_PRESSURE:
                return pressure;
            case TYPE_SEA_LEVEL:
                return sea_level;
            case TYPE_GRN_LEVEL:
                return grnd_level;
            case TYPE_HUMIDITY:
                return humidity;
            case TYPE_WIND:
                return speed;
            case TYPE_DEG:
                return deg;
            case TYPE_CLOUDS:
                return clouds;
            case TYPE_RAIN:
                return rain;
            default:
                return dt;
        }
    }

    private double convertTempFromKelvin(double temperature){
        return 273-temperature;
    }

    public static final String TAG_CITY = "city";
    public static final String TAG_LIST = "list";
    public static final String TAG_WEATHER = "weather";

    public static final String TAG_DT = "dt";
    public static final String TAG_TEMP = "temp";
    public static final String TAG_MAIN = "main";
    public static final String TAG_TEMP_MIN = "min";
    public static final String TAG_TEMP_MAX = "max";
    public static final String TAG_TEMP_NIGHT = "night";
    public static final String TAG_TEMP_DAY = "day";
    public static final String TAG_TEMP_EVE = "eve";
    public static final String TAG_TEMP_MORN = "morn";
    public static final String TAG_PRESSURE = "pressure";
    public static final String TAG_SEA_LEVEL = "sea_level";
    public static final String TAG_GRND_LEVEL = "grnd_level";
    public static final String TAG_HUMIDITY = "humidity";
    public static final String TAG_WIND = "wind";
    public static final String TAG_SPEED = "speed";
    public static final String TAG_DEG = "deg";
    public static final String TAG_CLOUDS = "clouds";
    public static final String TAG_CLOUDS_ALL = "all";
    public static final String TAG_RAIN = "rain";
    public static final String TAG_RAIN_3H = "3h";

    public enum WEATHERDATA {
        TYPE_LOCATION_NAME,
        TYPE_WEATHER_ID,
        TYPE_DATE,
        TYPE_DATE_MILIS,
        TYPE_TEMP,
        TYPE_DAY,
        TYPE_TEMP_MIN,
        TYPE_TEMP_MAX,
        TYPE_TEMP_NIGHT,
        TYPE_TEMP_EVE,
        TYPE_TEMP_MORN,
        TYPE_PRESSURE,
        TYPE_SEA_LEVEL,
        TYPE_GRN_LEVEL,
        TYPE_HUMIDITY,
        TYPE_WIND,
        TYPE_DEG,
        TYPE_CLOUDS,
        TYPE_RAIN
    }

    public static final String _ID              = "_ID";
    public static final String WEATHER_ID       = "WEATHER_ID";
    public static final String LOCATION_ID      = "LOCATION_ID";
    public static final String DATE             = "DATE";
    public static final String DATE_MILLIS      = "DATE_MILLIS";
    public static final String TEMP_DAY         = "TEMP_DAY";
    public static final String TEMP_MINIMUM     = "TEMP_MINIMUM";
    public static final String TEMP_MAXIMUM     = "TEMP_MAXIMUM";
    public static final String TEMP_NIGHT       = "TEMP_NIGHT";
    public static final String TEMP_EVE         = "TEMP_EVE";
    public static final String TEMP_MORN        = "TEMP_MORN";
    public static final String PRESSURE         = "PRESSURE";
    public static final String SEA_LEVEL        = "SEA_LEVEL";
    public static final String GRN_LEVEL        = "GRN_LEVEL";
    public static final String HUMIDITY         = "HUMIDITY";
    public static final String WIND             = "WIND";
    public static final String DEG              = "DEG";
    public static final String CLOUDS           = "CLOUDS";
    public static final String RAIN             = "RAIN";
}
