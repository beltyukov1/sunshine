package me.beltyukov.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import me.beltyukov.sunshine.data.WeatherContract.LocationEntry;
import me.beltyukov.sunshine.data.WeatherContract.WeatherEntry;
import me.beltyukov.sunshine.data.WeatherDbHelper;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    public static final String TEST_CITY_NAME = "North Pole";
    public static final String TEST_LOCATION = "99705";
    public static final String TEST_DATE = "20141205";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(mContext)
                .getWritableDatabase();
        assertEquals(db.isOpen(), true);
        db.close();
    }

    public void testInsertReadDb() {
        // If there's an error in SQL create table string, it will be
        // thrown here when you try to get writable database
        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();

        ContentValues locationValues = createNorthPoleLocationValues();

        long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Cursor is primary interface to the query results
        // Cursor is control structure that enables traversal of records in db
        Cursor locationCursor = db.query(
                LocationEntry.TABLE_NAME,
                null, // custom projection; not required (would return all columns)
                null, // columns for where clause
                null, // values for where clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // moveToFirst populates cursor with row of data
        if (!locationCursor.moveToFirst()) {
            fail("No location data returned!");
        }

        validateCursor(locationCursor, locationValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        if (!weatherCursor.moveToFirst()) {
            fail("No weather data returned!");
        }

        validateCursor(weatherCursor, weatherValues);

        weatherCursor.close();
        locationCursor.close();
        db.close();
    }

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        testValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(LocationEntry.COLUMN_COORD_LONG, -147.353);

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
