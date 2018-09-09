package com.example.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.data.RecipeContract.RecipeEntry;

/**
 * Created by user on 10-Oct-17.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = RecipeDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "recipes.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_RECIPE = "CREATE TABLE "+ RecipeEntry.TABLE_NAME + " ("
                + RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecipeEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + RecipeEntry.COLUMN_INGREDIENTS + " TEXT, "
                + RecipeEntry.COLUMN_INSTRUCTIONS + " TEXT, "
                + RecipeEntry.COLUMN_NUTRIENTS + " TEXT, "
                + RecipeEntry.COLUMN_PREP_TIME + " TEXT, "
                + RecipeEntry.COLUMN_SOURCE_URL + " TEXT, "
                + RecipeEntry.COLUMN_HTML + " TEXT, "
                + RecipeEntry.COLUMN_IMAGE_PATH + " TEXT);";
        db.execSQL(SQL_CREATE_RECIPE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
