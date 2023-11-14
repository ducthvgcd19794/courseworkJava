package com.android.mhike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mhike";
    private static final int DB_VERSION = 1;

    private static final String HIKE_TABLE = "hikes";
    private static final String HIKE_ID = "id";
    private static final String HIKE_NAME = "name";
    private static final String HIKE_LOCATION = "location";
    private static final String HIKE_DATE = "date";
    private static final String HIKE_PARKING = "parking";
    private static final String HIKE_LENGTH = "length";
    private static final String HIKE_LEVEL = "level";
    private static final String HIKE_START_POINT = "start";
    private static final String HIKE_NUMBER = "number";
    private static final String HIKE_DESCRIPTION = "description";

    private static final String OBSERVATION_TABLE = "observations";
    private static final String OBSERVATION_ID = "id";
    private static final String OBSERVATION_OBSERVATION = "observation";
    private static final String OBSERVATION_TIME = "time";
    private static final String OBSERVATION_COMMENT = "comment";
    private static final String OBSERVATION_HIKE_ID = "hike_id";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + HIKE_TABLE + "(" +
                HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HIKE_NAME + " TEXT, " +
                HIKE_LOCATION + " TEXT, " +
                HIKE_DATE + " TEXT, " +
                HIKE_PARKING + " INTEGER, " +
                HIKE_LENGTH + " INTEGER, " +
                HIKE_LEVEL + " INTEGER, " +
                HIKE_DESCRIPTION + " TEXT" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS hikes");
    }

    public long insertHike(Hike hike) {
        if (hike == null) {
            return -1;
        }

        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(HIKE_NAME, hike.getName());
            values.put(HIKE_LOCATION, hike.getLocation());
            values.put(HIKE_DATE, hike.getDate());
            values.put(HIKE_PARKING, hike.getParkingAvailable());
            values.put(HIKE_LENGTH, hike.getLength());
            values.put(HIKE_LEVEL, hike.getLevel());
            values.put(HIKE_DESCRIPTION, hike.getDescription());

            return database.insert(HIKE_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Hike getHike(int id) {
        try (SQLiteDatabase database = this.getReadableDatabase(); Cursor cursor = database.query(HIKE_TABLE, null, HIKE_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            cursor.moveToFirst();
            return new Hike(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Hike> getAllHikes() {
        try (SQLiteDatabase database = this.getReadableDatabase(); Cursor cursor = database.rawQuery("SELECT * FROM " + HIKE_TABLE, null)) {
            cursor.moveToFirst();

            List<Hike> hikes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                Hike hike = new Hike(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7));
                hikes.add(hike);
                cursor.moveToNext();
            }

            return hikes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateHike(Hike hike) {
        if (hike == null) {
            return false;
        }

        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(HIKE_NAME, hike.getName());
            values.put(HIKE_LOCATION, hike.getLocation());
            values.put(HIKE_DATE, hike.getDate());
            values.put(HIKE_PARKING, hike.getParkingAvailable());
            values.put(HIKE_LENGTH, hike.getLength());
            values.put(HIKE_LEVEL, hike.getLevel());
            values.put(HIKE_DESCRIPTION, hike.getDescription());

            database.update(HIKE_TABLE, values, HIKE_ID + " = ?", new String[]{String.valueOf(hike.getId())});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteHike(Hike hike) {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            database.delete(HIKE_TABLE, HIKE_ID + " = ?", new String[]{String.valueOf(hike.getId())});
             return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllHikes() {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            database.execSQL("DELETE FROM " + HIKE_TABLE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
