package com.arpaul.geoweather.webServices;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.arpaul.geoweather.R;
import com.arpaul.geoweather.activity.WeatherListActivity;
import com.arpaul.geoweather.dataAccess.SSCPConstants;
import com.arpaul.geoweather.dataObjects.LocationDO;
import com.arpaul.geoweather.dataObjects.ParserDO;
import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.dataObjects.WeatherDescriptionDO;
import com.arpaul.geoweather.parser.WeatherParser;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

/**
 * Created by ARPaul on 05-04-2016.
 */
public class WeatherLoader extends AsyncTaskLoader {

    private Context context;
    private LocationDO objLocationDO;

    public final static String BUNDLE_WEATHERLOADER      = "BUNDLE_WEATHERLOADER";

    public WeatherLoader(Context context, Bundle bundle){
        super(context);
        this.context = context;

        if(bundle != null)
            objLocationDO = (LocationDO) bundle.get(BUNDLE_WEATHERLOADER);
    }
    @Override
    public ArrayList<WeatherDataDO> loadInBackground() {
        ArrayList<WeatherDataDO> arrWeather = new ArrayList<>();
        String URL = getURL();
        LocationDO objLocationDO = null;
//        String response = executeRequest(URL);
        WebServiceResponse responseDO = new RestServiceCalls(URL, null, WEBSERVICE_TYPE.GET).getData();
        if(responseDO.getResponseCode() == WebServiceResponse.ResponseType.SUCCESS) {
            ParserDO parseDO = new WeatherParser().parseWeatherData(responseDO.getResponseMessage());

            ContentProviderResult[] insertWeather = new ContentProviderResult[arrWeather.size()];
            ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();
            ContentValues contentValues = null;
            ContentProviderOperation.Builder builder = null;

            if(parseDO.linkHash.containsKey(ParserDO.ParseType.TYPE_LOCATION)){
                objLocationDO = (LocationDO) parseDO.linkHash.get(ParserDO.ParseType.TYPE_LOCATION);
                if(objLocationDO != null){
                    contentValues = new ContentValues();
                    contentValues.put(LocationDO.LOCATION_ID, objLocationDO.location_ID);
                    contentValues.put(LocationDO.CITY_NAME, objLocationDO.city_name);
                    contentValues.put(LocationDO.COORD_LONG, objLocationDO.coord_long);
                    contentValues.put(LocationDO.COORD_LAT, objLocationDO.coord_lat);

                    builder = ContentProviderOperation.newInsert(SSCPConstants.CONTENT_URI_LOCATION);
                    builder.withValues(contentValues);
                    contentProviderOperations.add(builder.build());
                }
            }

            /*Cursor cursor = context.getContentResolver().query(SSCPConstants.CONTENT_URI_LOCATION,
                    new String[]{LocationDO.LOCATION_ID},
                    LocationDO.CITY_NAME + SSCPConstants.TABLE_QUES,
                    new String[]{(String) new LocationDO().getData(LocationDO.LOCATIONDATA.TYPE_CITY_NAME)},
                    null);*/

            arrWeather = (ArrayList<WeatherDataDO>) parseDO.linkHash.get(ParserDO.ParseType.TYPE_WEATHER);
            if(arrWeather != null && arrWeather.size() > 0){

                String location_id = objLocationDO.location_ID;

                Cursor cursor = context.getContentResolver().query(SSCPConstants.CONTENT_URI_WEATHER,
                        new String[]{"MAX(" + WeatherDataDO.WEATHER_ID + ") AS " + WeatherDataDO.WEATHER_ID},
                        null,
                        null,
                        null);

                int weatherIdMax = 0;
                if(cursor != null && cursor.moveToFirst()){
                    weatherIdMax = StringUtils.getInt(cursor.getString(cursor.getColumnIndex(WeatherDataDO.WEATHER_ID)));
                }
                cursor.close();

                for(WeatherDataDO objWeatherDataDO: arrWeather){

                    builder = ContentProviderOperation.newInsert(SSCPConstants.CONTENT_URI_WEATHER);
                    contentValues = new ContentValues();

                    weatherIdMax++;
                    contentValues.put(WeatherDataDO.LOCATION_ID, location_id);
                    contentValues.put(WeatherDataDO.WEATHER_ID, weatherIdMax);
                    contentValues.put(WeatherDataDO.DATE_MILLIS, (long) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS));
                    String date = CalendarUtils.getDatefromTimeinMilliesPattern((long) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DATE_MILIS), CalendarUtils.DATE_FORMAT1)+" ";
                    contentValues.put(WeatherDataDO.DATE, date);
                    contentValues.put(WeatherDataDO.TEMP_DAY, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP));
                    contentValues.put(WeatherDataDO.TEMP_MINIMUM, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MIN));
                    contentValues.put(WeatherDataDO.TEMP_MAXIMUM, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MAX));
                    contentValues.put(WeatherDataDO.TEMP_MORN, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_MORN));
                    contentValues.put(WeatherDataDO.TEMP_EVE, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_EVE));
                    contentValues.put(WeatherDataDO.TEMP_NIGHT, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_TEMP_NIGHT));

                    contentValues.put(WeatherDataDO.PRESSURE, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_PRESSURE));
                    contentValues.put(WeatherDataDO.HUMIDITY, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_HUMIDITY));
                    contentValues.put(WeatherDataDO.WIND, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_WIND));
                    contentValues.put(WeatherDataDO.DEG, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_DEG));
                    contentValues.put(WeatherDataDO.CLOUDS, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_CLOUDS));
                    contentValues.put(WeatherDataDO.RAIN, (double) objWeatherDataDO.getData(WeatherDataDO.WEATHERDATA.TYPE_RAIN));

                    builder.withValues(contentValues);
                    contentProviderOperations.add(builder.build());

                    if(objWeatherDataDO.arrWeatheDescp != null && objWeatherDataDO.arrWeatheDescp.size() > 0){
                        for (WeatherDescriptionDO objWeatherDescrpDO : objWeatherDataDO.arrWeatheDescp){
                            builder = ContentProviderOperation.newInsert(SSCPConstants.CONTENT_URI_WEATHER_DESCRIP);
                            contentValues = new ContentValues();

                            contentValues.put(WeatherDescriptionDO.WEATHER_ID, weatherIdMax);
                            contentValues.put(WeatherDescriptionDO.WEATHER_ICON_ID, (int) objWeatherDescrpDO.getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_WEATHER_ID));
                            contentValues.put(WeatherDescriptionDO.MAIN, (String) objWeatherDescrpDO.getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_MAIN));
                            contentValues.put(WeatherDescriptionDO.DESCRIPTION, (String) objWeatherDescrpDO.getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_DESCRIPTION));
                            contentValues.put(WeatherDescriptionDO.ICON, (String) objWeatherDescrpDO.getData(WeatherDescriptionDO.WEATHER_DESC_DATA.TYPE_ICON));

                            builder.withValues(contentValues);
                            contentProviderOperations.add(builder.build());
                        }
                    }
                }

                try {
                    insertWeather = context.getContentResolver().applyBatch(SSCPConstants.CONTENT_AUTHORITY, contentProviderOperations);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }

                return arrWeather;
            }
        }

        return null;
    }

    private String getURL(){
        return ParamBuilder.getDailyForecastParam(WebServiceConstant.URL_OPENWEATHERMAP+WebServiceConstant.URL_DAILY_FORECAST,
                objLocationDO.getData(LocationDO.LOCATIONDATA.TYPE_COORD_LAT) + "",
                objLocationDO.getData(LocationDO.LOCATIONDATA.TYPE_COORD_LON) + "",
                "json",
                "metric",
                "7",
                getContext().getResources().getString(R.string.api_key));
//        return "lat=35&lon=139&mode=json&units=metric&cnt=7&APPID="+getContext().getResources().getString(R.string.api_key);
    }
}
