package com.arpaul.geoweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.arpaul.geoweather.dataObjects.WeatherDataDO;
import com.arpaul.geoweather.fragments.ItemWeatherDetailFragment;
import com.arpaul.geoweather.R;

/**
 * An activity representing a single ItemWeather detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link WeatherListActivity}.
 */
public class ItemWeatherDetailActivity extends BaseActivity {

    private View llWeatherDetailActivity;

    private WeatherDataDO objWeatherDataDO;

    @Override
    public void initialize(Bundle savedInstanceState) {
        llWeatherDetailActivity = baseInflater.inflate(R.layout.activity_weather_details,null);
        llBody.addView(llWeatherDetailActivity, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if(getIntent().hasExtra("WEATHER_DETAIL"))
            objWeatherDataDO = (WeatherDataDO) getIntent().getExtras().get("WEATHER_DETAIL");

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable("WEATHER_DETAIL", objWeatherDataDO);
            ItemWeatherDetailFragment fragment = new ItemWeatherDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.itemweather_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, WeatherListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
