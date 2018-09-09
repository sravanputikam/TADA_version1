package com.example.android.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.data.RecipeContract.RecipeEntry;
/**
 * Created by user on 11-Oct-17.
 */

public class RecipeProvider extends ContentProvider {

    public static final String LOG_TAG = RecipeProvider.class.getSimpleName();
    /** URI matcher code for the content URI for the recipes table */
    private static final int RECIPES = 100;

    /** URI matcher code for the content URI for a single recipe in the recipes table */
    private static final int RECIPE_ID = 101;
    private RecipeDbHelper mDbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android/recipes" will map to the
        // integer code {@link #RECIPES}. This URI is used to provide access to MULTIPLE rows
        // of the recipes table.
        sUriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY, RecipeContract.PATH_RECIPE, RECIPES);
        //sUriMatcher.addURI(GroceryContract.CONTENT_GROCERY_AUTHORITY, GroceryContract.PATH_GROCERY, 100);
        // The content URI of the form "content://com.example.android/recipes/#" will map to the
        // integer code {@link #RECIPE_ID}. This URI is used to provide access to ONE single row
        // of the recipes table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android/recipes/3" matches, but
        // "content://com.example.android/recipes" (without a number at the end) doesn't match.
        sUriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY, RecipeContract.PATH_RECIPE + "/#", RECIPE_ID);
        //sUriMatcher.addURI(GroceryContract.CONTENT_GROCERY_AUTHORITY, GroceryContract.PATH_GROCERY + "/#", 101);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new RecipeDbHelper(getContext()) ;
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                // For the RECIPES code, query the recipe table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the recipes table.
                cursor = database.query(RecipeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RECIPE_ID:
                // For the RECIPE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android/recipes/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = RecipeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the recipes table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(RecipeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                return RecipeEntry.CONTENT_LIST_TYPE;
            case RECIPE_ID:
                return RecipeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                return insertRecipe(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /*
      Insert a recipe into the database with the given content values. Return the new content URI
      for that specific row in the database.
     */
    private Uri insertRecipe(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(RecipeEntry.COLUMN_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Recipe requires a name");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new recipe with the given values
        long id = database.insert(RecipeEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the recipe content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPE_ID:
                // Delete a single row given by the ID in the URI
                selection = RecipeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
