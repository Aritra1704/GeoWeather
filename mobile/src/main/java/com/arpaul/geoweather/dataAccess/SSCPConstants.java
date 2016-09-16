package com.arpaul.geoweather.dataAccess;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by ARPaul on 07-01-2016.
 */
public class SSCPConstants {
    public static final String CONTENT_AUTHORITY = "com.arpaul.geoweather.dataAccess.ContentProviderHelper";

    public static final String DATABASE_NAME                    = "Sunshine.db";

    public static final String WEATHER_TABLE_NAME               = "tblWeather";
    public static final String LOCATION_TABLE_NAME              = "tblLocation";
    public static final String WEATHER_DESCRIP_TABLE_NAME       = "tblWeatherDescrip";

    public static final String AS_WEATHER_TABLE                  = " tW";
    public static final String AS_WEATHER_DESC_TABLE             = " tWD";
    public static final String AS_LOCATION_TABLE                 = " tL";

    public static final int DATABASE_VERSION                   = 1;

    public static final String PATH_RELATIONSHIP_JOIN          = "relationship_join";

    public static final String DELIMITER = "/";
    public static final String TAG_ID = "/#";
    public static final String TAG_ID_ALL = "/*";

    public static final String TABLE_INNER_JOIN = " INNER JOIN ";
    public static final String TABLE_LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
    public static final String TABLE_ON = " ON ";
    public static final String TABLE_DOT = ".";
    public static final String TABLE_EQUAL = " = ";
    public static final String TABLE_WHERE = " WHERE ";
    public static final String TABLE_AND = " AND ";
    public static final String TABLE_OR = " OR ";
    public static final String TABLE_IN = " IN ";
    public static final String TABLE_NOT_IN = " NOT IN ";
    public static final String TABLE_DISTINCT = " DISTINCT ";
    public static final String TABLE_QUES  = " = ? ";

    public static final String CONTENT = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT + CONTENT_AUTHORITY);

    public static final Uri CONTENT_URI_LOCATION = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + LOCATION_TABLE_NAME);
    public static final Uri CONTENT_URI_WEATHER = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + WEATHER_TABLE_NAME);
    public static final Uri CONTENT_URI_WEATHER_DESCRIP = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + WEATHER_DESCRIP_TABLE_NAME);

    public static final Uri CONTENT_URI_RELATIONSHIP_JOIN = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + PATH_RELATIONSHIP_JOIN);

    public static final String PROVIDER_NAME = CONTENT_AUTHORITY;

    // create cursor of base type directory for multiple entries
    public static final String CONTENT_USERNAME_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + DELIMITER + CONTENT_AUTHORITY + DELIMITER + DATABASE_NAME;
    // create cursor of base type item for single entry
    public static final String CONTENT_LOCATIONID_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + DELIMITER + CONTENT_AUTHORITY + DELIMITER + DATABASE_NAME;

    public static final String CONTENT_WEATHERID_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + DELIMITER + CONTENT_AUTHORITY + DELIMITER + DATABASE_NAME;

    public static Uri buildUri(Uri uri, long id){
        return ContentUris.withAppendedId(uri, id);
    }

    public static Uri buildFarmUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI_WEATHER, id);
    }
}
