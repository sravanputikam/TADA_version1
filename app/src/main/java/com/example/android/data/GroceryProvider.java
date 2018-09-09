package com.example.android.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.data.GroceryContract.GroceryEntry;

/**
 * Created by user on 22-Oct-17.
 */

public class GroceryProvider extends ContentProvider {

    public static final String LOG_TAG = GroceryProvider.class.getSimpleName();
    /** URI matcher code for the content URI for the groceries table */
    private static final int GROCERIES = 100;

    /** URI matcher code for the content URI for a single grocery in the recipes table */
    private static final int GROCERIES_ID = 101;
    private GroceryDbHelper mDbHelper;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android/recipes" will map to the
        // integer code {@link #RECIPES}. This URI is used to provide access to MULTIPLE rows
        // of the recipes table.
        mUriMatcher.addURI(GroceryContract.CONTENT_GROCERY_AUTHORITY, GroceryContract.PATH_GROCERY, GROCERIES);
        //mUriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY,RecipeContract.PATH_RECIPE,100);
        // The content URI of the form "content://com.example.android/recipes/#" will map to the
        // integer code {@link #RECIPE_ID}. This URI is used to provide access to ONE single row
        // of the recipes table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android/recipes/3" matches, but
        // "content://com.example.android/recipes" (without a number at the end) doesn't match.
        mUriMatcher.addURI(GroceryContract.CONTENT_GROCERY_AUTHORITY, GroceryContract.PATH_GROCERY + "/#", GROCERIES_ID);
        //mUriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY,RecipeContract.PATH_RECIPE+"/#",101);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new GroceryDbHelper(getContext()) ;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = mUriMatcher.match(uri);
        switch (match) {
            case GROCERIES:
                // For the RECIPES code, query the recipe table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the recipes table.
                cursor = database.query(GroceryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case GROCERIES_ID:
                // For the RECIPE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android/recipes/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = GroceryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the recipes table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(GroceryEntry.TABLE_NAME, projection, selection, selectionArgs,
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

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case GROCERIES:
                return GroceryEntry.CONTENT_LIST_TYPE;
            case GROCERIES_ID:
                return GroceryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case GROCERIES:
                return insertGrocery(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertGrocery(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(GroceryEntry.COLUMN_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Grocery requires a name");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new recipe with the given values
        long id = database.insert(GroceryEntry.TABLE_NAME, null, values);
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
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case GROCERIES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(GroceryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GROCERIES_ID:
                // Delete a single row given by the ID in the URI
                selection = GroceryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(GroceryEntry.TABLE_NAME, selection, selectionArgs);
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
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
