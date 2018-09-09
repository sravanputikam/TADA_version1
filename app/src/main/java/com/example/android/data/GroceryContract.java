package com.example.android.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 21-Oct-17.
 */

public final class GroceryContract {

    private GroceryContract(){}

    public static final String CONTENT_GROCERY_AUTHORITY = "com.example.android.data.GroceryProvider";

    public  static final Uri BASE_CONTENT_GROCERY_URI = Uri.parse("content://"+ CONTENT_GROCERY_AUTHORITY);

    public static final String PATH_GROCERY = "groceries";

    public static final class GroceryEntry implements BaseColumns {

        public static final Uri CONTENT_GROCERY_URI = Uri.withAppendedPath(BASE_CONTENT_GROCERY_URI,PATH_GROCERY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_GROCERY_AUTHORITY + "/" + PATH_GROCERY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_GROCERY_AUTHORITY + "/" +PATH_GROCERY;

        /** Name of database table for groceries */
        public final static String TABLE_NAME = "groceries";

        /*
          Unique ID number for the grocery (only for use in the database table).
          Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /*
          Title of the grocery
          Type: TEXT
         */
        public final static String COLUMN_TITLE ="title";

        /*
           Date of Entry
           Type: TEXT
         */
        public final static String COLUMN_DATE = "Date";

        /*
           Price
           Type: TEXT
         */
        public final static String COLUMN_PRICE ="price";
    }
}
