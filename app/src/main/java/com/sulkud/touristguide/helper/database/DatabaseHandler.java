package com.sulkud.touristguide.helper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PlacesManager";

    // Contacts table name
    private static final String TABLE_VISITED_PLACES = "visited_places";
    private static final String TABLE_BOOKMARKED_PLACES = "bookmarked_places";
    public static final String TABLE_TAG_TYPE_VISITED = "visited";
    public static final String TABLE_TAG_TYPE_BOOKMARKED = "bookmarked";

    // FIELDS PLACES Columns names
    private static final String KEY_PLACE_ID = "id";
    private static final String KEY_PLACE_NAME = "placeName";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_PLACE_DESCRIPTION = "placeDescription";
    private static final String KEY_PLACE_TAG = "placeTag";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VISITED_TABLE = "CREATE TABLE " + TABLE_VISITED_PLACES + "("
                + KEY_PLACE_ID + " INTEGER PRIMARY KEY,"
                + KEY_PLACE_NAME + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_PLACE_DESCRIPTION + " TEXT,"
                + KEY_PLACE_TAG + " TEXT" + ")";
        db.execSQL(CREATE_VISITED_TABLE);

        String CREATE_BOOKMARK_TABLE = "CREATE TABLE " + TABLE_BOOKMARKED_PLACES + "("
                + KEY_PLACE_ID + " INTEGER PRIMARY KEY,"
                + KEY_PLACE_NAME + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_PLACE_DESCRIPTION + " TEXT,"
                + KEY_PLACE_TAG + " TEXT" + ")";
        db.execSQL(CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITED_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKED_PLACES);
        // Create tables again
        onCreate(db);
    }

    public void addPlace(PlaceModel placeModel, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_NAME, placeModel.placeName);
        values.put(KEY_LATITUDE, placeModel.latitude);
        values.put(KEY_LONGITUDE, placeModel.longitude);
        values.put(KEY_PLACE_DESCRIPTION, placeModel.placeDescription);
        values.put(KEY_PLACE_TAG, placeModel.placeTag);

        db.insert((type.equals(TABLE_TAG_TYPE_VISITED) ? TABLE_VISITED_PLACES : TABLE_BOOKMARKED_PLACES), null, values);
        db.close(); // Closing database connection
    }

    public PlaceModel getPlace(int id, String type) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query((type.equals(TABLE_TAG_TYPE_VISITED) ? TABLE_VISITED_PLACES : TABLE_BOOKMARKED_PLACES), new String[]{KEY_PLACE_ID,
                        KEY_PLACE_NAME,
                        KEY_LATITUDE,
                        KEY_LONGITUDE,
                        KEY_PLACE_DESCRIPTION,
                        KEY_PLACE_TAG}, KEY_PLACE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return new PlaceModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        }
        // return place
        return null;
    }

    public List<PlaceModel> getAllPlaces(String type) {
        List<PlaceModel> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + (type.equals(TABLE_TAG_TYPE_VISITED) ? TABLE_VISITED_PLACES : TABLE_BOOKMARKED_PLACES);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PlaceModel place = new PlaceModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                // Adding contact to list
                contactList.add(place);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public int getPlacesCount(String type) {
        String countQuery = "SELECT  * FROM " + (type.equals(TABLE_TAG_TYPE_VISITED) ? TABLE_VISITED_PLACES : TABLE_BOOKMARKED_PLACES);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int updatePlace(PlaceModel placeModel, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_NAME, placeModel.placeName);
        values.put(KEY_LATITUDE, placeModel.latitude);
        values.put(KEY_LONGITUDE, placeModel.longitude);
        values.put(KEY_PLACE_DESCRIPTION, placeModel.placeDescription);
        values.put(KEY_PLACE_TAG, placeModel.placeTag);

        // updating row
        return db.update((type.equals(TABLE_TAG_TYPE_VISITED) ? TABLE_VISITED_PLACES : TABLE_BOOKMARKED_PLACES), values, KEY_PLACE_ID + " = ?",
                new String[]{String.valueOf(placeModel.placeID)});
    }

    public void deletePlace(PlaceModel placeModel, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete((type.equals(TABLE_TAG_TYPE_VISITED) ? TABLE_VISITED_PLACES : TABLE_BOOKMARKED_PLACES), KEY_PLACE_ID + " = ?",
                new String[]{String.valueOf(placeModel.placeID)});
        db.close();
    }

    public boolean queryIfExistPlace(String placeLat, String placeLong, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + (type.equals(TABLE_TAG_TYPE_VISITED) ? TABLE_VISITED_PLACES : TABLE_BOOKMARKED_PLACES)
                + " where " + KEY_LATITUDE + " = " + placeLat
                + " AND " + KEY_LONGITUDE + " = " + placeLong;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        return false;
    }

}
