package com.arpaul.geoweather.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.fragment.OneDayWeather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARPaul on 24-09-2016.
 */

public class GridPagerAdapter extends FragmentGridPagerAdapter {

    private Context context;
    private List<Row> mRows;
    private ArrayList<WeatherDataDO> arrWeatherDataDO;

    public GridPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        this.context = context;

        mRows = new ArrayList<GridPagerAdapter.Row>();

        if(arrWeatherDataDO != null && arrWeatherDataDO.size() > 0){
            for(WeatherDataDO objWeatherDataDO: arrWeatherDataDO){

                Bundle arguments = new Bundle();
                arguments.putSerializable("WEATHER_DETAIL", objWeatherDataDO);
                OneDayWeather fragment = new OneDayWeather();
                fragment.setArguments(arguments);
                mRows.add(new Row(fragment));
            }
        }
    }

    @Override
    public int getColumnCount(int rowNum) {
        if(mRows != null && mRows.get(rowNum) != null)
            return mRows.get(rowNum).getColumnCount();
        return 0;
    }

    @Override
    public int getRowCount() {
        if(mRows != null)
            return mRows.size();
        return 0;
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row adapterRow = mRows.get(row);
        return adapterRow.getColumn(col);
    }

    /** A convenient container for a row of fragments. */
    private class Row {
        final List<Fragment> columns = new ArrayList<Fragment>();

        public Row(Fragment... fragments) {
            for (Fragment f : fragments) {
                add(f);
            }
        }

        public void add(Fragment f) {
            columns.add(f);
        }

        Fragment getColumn(int i) {
            return columns.get(i);
        }

        public int getColumnCount() {
            return columns.size();
        }
    }

}
