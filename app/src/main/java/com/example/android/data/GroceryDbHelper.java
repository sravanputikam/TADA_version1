package com.example.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.data.GroceryContract.GroceryEntry;
/**
 * Created by user on 22-Oct-17.
 */

public class GroceryDbHelper extends SQLiteOpenHelper {

    /* Name of the database file */
    private static final String DATABASE_NAME = "groceries.db";

    private static int DATABASE_VERSION = 1;

    public GroceryDbHelper(Context context){super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_GROCERY = "CREATE TABLE "+ GroceryEntry.TABLE_NAME + " ("
                + GroceryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GroceryEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + GroceryEntry.COLUMN_TITLE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_GROCERY);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
