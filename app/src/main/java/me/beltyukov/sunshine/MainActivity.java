package me.beltyukov.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity implements ForecastFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mLocation;

    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;

            // In two-pane mode, show detail view in this activity by
            // adding or replacing detail fragment using fragment transaction
            if (savedInstanceState == null) { // let system handle restoring detail fragment
                getFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG, "onResume");
        super.onResume();
//        String location = Utility.getPreferredLocation(this);
//        if (location != null && !location.equals(mLocation)) {
//            ForecastFragment forecastFragment = (ForecastFragment)getFragmentManager().findFragmentById(R.id.fragment_forecast);
//            if (forecastFragment != null) {
//                forecastFragment.onLocationChanged();
//            }
//
//            DetailFragment detailFragment = (DetailFragment)getFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
//            if (detailFragment != null) {
//                detailFragment.onLocationChanged();
//            }
//
//            mLocation = location;
//        }
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if (dateUri != null) {
            if (mTwoPane) {
                Bundle args = new Bundle();
                args.putParcelable(DetailFragment.DETAIL_URI, dateUri);

                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                        .commit();
            } else {
                Intent intent = new Intent(this, DetailActivity.class).setData(dateUri);
                startActivity(intent);
            }
        }
    }
}
