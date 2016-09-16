package com.arpaul.geoweather.dataObjects;

/**
 * Created by Aritra on 22-08-2016.
 */
public class LocationDO extends BaseDO {

    public String location_ID          = "";
    public String location_setting     = "";
    public String city_name            = "";
    public double coord_lat            = 0.0;
    public double coord_long           = 0.0;

    public void saveData(Object data, LOCATIONDATA type){
        switch (type){
            case TYPE_LOCATION_ID:
                location_ID = (String) data;
                break;
            case TYPE_LOCATION_SETTING:
                location_setting = (String) data;
                break;
            case TYPE_CITY_NAME:
                city_name = (String) data;
                break;
            case TYPE_COORD_LAT:
                coord_lat = (double)data;
                break;
            case TYPE_COORD_LON:
                coord_long = (double)data;
                break;
        }
    }

    public Object getData(LOCATIONDATA type){
        switch (type){
            case TYPE_LOCATION_ID:
                return location_ID;
            case TYPE_LOCATION_SETTING:
                return location_setting;
            case TYPE_CITY_NAME:
                return city_name;
            case TYPE_COORD_LAT:
                return coord_lat;
            case TYPE_COORD_LON:
                return coord_long;
            default:
                return location_ID;
        }
    }

    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_COORD = "coord";
    public static final String TAG_LON = "lon";
    public static final String TAG_LAT = "lat";

    public enum LOCATIONDATA {
        TYPE_LOCATION_ID,
        TYPE_LOCATION_SETTING,
        TYPE_CITY_NAME,
        TYPE_COORD_LAT,
        TYPE_COORD_LON
    }

    public static final String _ID                  = "_ID";
    public static final String LOCATION_ID          = "LOCATION_ID";
    public static final String LOCATION_SETTING     = "LOCATION_SETTING";
    public static final String CITY_NAME            = "CITY_NAME";
    public static final String COORD_LAT            = "COORD_LAT";
    public static final String COORD_LONG           = "COORD_LONG";
}
