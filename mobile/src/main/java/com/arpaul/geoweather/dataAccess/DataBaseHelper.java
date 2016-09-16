package com.arpaul.geoweather.dataAccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arpaul.geoweather.dataObjects.LocationDO;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.dataObjects.WeatherDescriptionDO;

/**
 * Created by ARPaul on 09-05-2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;

    static final String CREATE_LOCATION_DB_TABLE =
            " CREATE TABLE IF NOT EXISTS " + SSCPConstants.LOCATION_TABLE_NAME +
                    " (" + LocationDO._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LocationDO.LOCATION_ID              + " VARCHAR  NOT NULL, " +
                    LocationDO.LOCATION_SETTING         + " VARCHAR, " +
                    LocationDO.CITY_NAME                + " VARCHAR  NOT NULL, " +
                    LocationDO.COORD_LAT                + " DOUBLE  NOT NULL, " +
                    LocationDO.COORD_LONG               + " DOUBLE  NOT NULL);";

    static final String CREATE_WEATHER_DB_TABLE =
            " CREATE TABLE IF NOT EXISTS " + SSCPConstants.WEATHER_TABLE_NAME +
                    " (" + WeatherDataDO._ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WeatherDataDO.WEATHER_ID        + " INTEGER NOT NULL, " +
                    WeatherDataDO.LOCATION_ID       + " VARCHAR NOT NULL, " +
                    WeatherDataDO.DATE              + " DATETIME , " +
                    WeatherDataDO.DATE_MILLIS       + " LONG , " +
                    WeatherDataDO.TEMP_DAY          + " DOUBLE , " +
                    WeatherDataDO.TEMP_MINIMUM      + " DOUBLE , " +
                    WeatherDataDO.TEMP_MAXIMUM      + " DOUBLE , " +
                    WeatherDataDO.TEMP_NIGHT        + " DOUBLE , " +
                    WeatherDataDO.TEMP_EVE          + " DOUBLE , " +
                    WeatherDataDO.TEMP_MORN         + " DOUBLE , " +
                    WeatherDataDO.PRESSURE          + " DOUBLE , " +
                    WeatherDataDO.SEA_LEVEL         + " DOUBLE , " +
                    WeatherDataDO.GRN_LEVEL         + " DOUBLE , " +
                    WeatherDataDO.HUMIDITY          + " DOUBLE," +
                    WeatherDataDO.WIND              + " DOUBLE," +
                    WeatherDataDO.DEG               + " DOUBLE," +
                    WeatherDataDO.CLOUDS            + " DOUBLE," +
                    WeatherDataDO.RAIN              + " DOUBLE );";

    static final String CREATE_WEATHER_DESCRIP_DB_TABLE =
            " CREATE TABLE IF NOT EXISTS " + SSCPConstants.WEATHER_DESCRIP_TABLE_NAME +
                    " (" + WeatherDescriptionDO.WEATHER_DESCRIP_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WeatherDescriptionDO.WEATHER_ID                 + " VARCHAR  NOT NULL, " +
                    WeatherDescriptionDO.WEATHER_ICON_ID            + " VARCHAR, " +
                    WeatherDescriptionDO.MAIN                       + " VARCHAR, " +
                    WeatherDescriptionDO.DESCRIPTION                + " VARCHAR, " +
                    WeatherDescriptionDO.ICON                       + " VARCHAR);";

    DataBaseHelper(Context context){
        super(context, SSCPConstants.DATABASE_NAME, null, SSCPConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION_DB_TABLE);
        db.execSQL(CREATE_WEATHER_DB_TABLE);
        db.execSQL(CREATE_WEATHER_DESCRIP_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SSCPConstants.LOCATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SSCPConstants.WEATHER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SSCPConstants.WEATHER_DESCRIP_TABLE_NAME);
        onCreate(db);
    }
}
