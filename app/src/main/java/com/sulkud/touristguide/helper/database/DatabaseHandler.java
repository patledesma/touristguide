package com.sulkud.touristguide.helper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sulkud.touristguide.models.PlaceModel;

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

        String CREATE_BOOKMARK_TABLE = "CREATE TABLE " + TABLE_VISITED_PLACES + "("
                + KEY_PLACE_ID + " INTEGER PRIMARY KEY,"
                + KEY_PLACE_NAME + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_PLACE_DESCRIPTION + " TEXT,"
                + KEY_PLACE_TAG + " TEXT" + ")";
        db.execSQL(CREATE_BOOKMARK_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITED_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKED_PLACES);
        // Create tables again
        onCreate(db);
    }

    // TODO: Sep 27, 027 DUPLICATE THIS WITH BOOKMARKED PLACES
    public void addPlace(PlaceModel placeModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_PLACE_NAME, contact.getName()); // Contact Name
//        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone Number
            values.put(KEY_PLACE_NAME, placeModel.placeName);
            values.put(KEY_LATITUDE, placeModel.latitude);
            values.put(KEY_LONGITUDE, placeModel.longitude);
            values.put(KEY_PLACE_DESCRIPTION, placeModel.placeDescription);
            values.put(KEY_PLACE_TAG, placeModel.placeTag);

        // Inserting Row
        if (placeModel.placeTag.equals("visited")) {
            db.insert(TABLE_VISITED_PLACES, null, values);
        } else if (placeModel.placeTag.equals("bookmarked")){
            db.insert(TABLE_BOOKMARKED_PLACES, null, values);
        }
        db.close(); // Closing database connection
    }

    // TODO: Sep 27, 027 DUPLICATE THIS WITH BOOKMARKED PLACES
    public PlaceModel getVisitedPlace(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VISITED_PLACES, new String[] { KEY_PLACE_ID,
                        KEY_PLACE_NAME,
                        KEY_LATITUDE,
                        KEY_LONGITUDE,
                        KEY_PLACE_DESCRIPTION,
                        KEY_PLACE_TAG }, KEY_PLACE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        PlaceModel place = new PlaceModel();
        place.placeID = cursor.getString(0);
        place.placeName = cursor.getString(1);
        place.latitude = cursor.getString(2);
        place.longitude = cursor.getString(3);
        place.placeDescription = cursor.getString(4);
        place.placeTag = cursor.getString(5);
        // return place
        return place;
    }

    // Getting All Contacts
    // TODO: Sep 27, 027 DUPLICATE THIS WITH BOOKMARKED PLACES
    public List<PlaceModel> getAllVisitedPlaces() {
        List<PlaceModel> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VISITED_PLACES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PlaceModel place = new PlaceModel();
                place.placeID = cursor.getString(0);
                place.placeName = cursor.getString(1);
                place.latitude = cursor.getString(2);
                place.longitude = cursor.getString(3);
                place.placeDescription = cursor.getString(4);
                place.placeTag = cursor.getString(5);
                // Adding contact to list
                contactList.add(place);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Getting contacts Count
    // TODO: Sep 27, 027 DUPLICATE THIS WITH BOOKMARKED PLACES
    public int getVisitedPlacesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_VISITED_PLACES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single contact
    // TODO: Sep 27, 027 DUPLICATE THIS WITH BOOKMARKED PLACES
    public int updateVisitedPlace(PlaceModel placeModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_NAME, placeModel.placeName);
        values.put(KEY_LATITUDE, placeModel.latitude);
        values.put(KEY_LONGITUDE, placeModel.longitude);
        values.put(KEY_PLACE_DESCRIPTION, placeModel.placeDescription);
        values.put(KEY_PLACE_TAG, placeModel.placeTag);

        // updating row
        return db.update(TABLE_VISITED_PLACES, values, KEY_PLACE_ID + " = ?",
                new String[] { String.valueOf(placeModel.placeID) });
    }

    // Deleting single contact
    // TODO: Sep 27, 027 DUPLICATE THIS WITH BOOKMARKED PLACES
    public void deleteVisitedPlace(PlaceModel placeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VISITED_PLACES, KEY_PLACE_ID + " = ?",
                new String[] { String.valueOf(placeModel.placeID) });
        db.close();
    }

}
