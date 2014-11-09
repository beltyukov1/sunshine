package me.beltyukov.sunshine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

import me.beltyukov.sunshine.data.WeatherContract;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PrefsFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public PrefsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            setLocationSummary();
            setTemperatureUnitsSummary();
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.prefs_location_key))) {
                setLocationSummary();
                FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
                String location = ((EditTextPreference)findPreference(key)).getText();
                weatherTask.execute(location);
            } else if (key.equals(getString(R.string.prefs_temperature_key))) {
                setTemperatureUnitsSummary();
                getActivity().getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
            }
        }

        private void setLocationSummary() {
            EditTextPreference location = (EditTextPreference) findPreference(getString(R.string.prefs_location_key));
            location.setSummary(location.getText());
        }

        private void setTemperatureUnitsSummary() {
            ListPreference temperatureUnits = (ListPreference) findPreference(getString(R.string.prefs_temperature_key));
            temperatureUnits.setSummary(temperatureUnits.getEntry());
        }
    }
}
