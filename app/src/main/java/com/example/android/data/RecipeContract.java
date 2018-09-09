package com.example.android.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 10-Oct-17.
 */

public final class RecipeContract {

    private RecipeContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.data.RecipeProvider";

    public  static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_RECIPE = "recipes";

    public static final class RecipeEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_RECIPE);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY + "/" +PATH_RECIPE;

        /** Name of database table for recipes */
        public final static String TABLE_NAME = "recipes";

        /*
          Unique ID number for the recipe (only for use in the database table).
          Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /*
          Title of the recipe
          Type: TEXT
         */
        public final static String COLUMN_TITLE ="title";

        /*
          List of ingredients
          Type: TEXT
         */
        public final static String COLUMN_INGREDIENTS = "ingredients";

        /*
            List of instructions
            Type:TEXT
         */
        public final static String COLUMN_INSTRUCTIONS ="instructions";

        /*
            Nutritio Facts
            Type: TEXT
         */
        public final static String COLUMN_NUTRIENTS = "nutrients";

        /*
            Image path
            Type: TEXT
         */
        public final static String COLUMN_IMAGE_PATH = "imagePath";
        /*
            Prepation Time
            Type: TEXT
         */
        public final static String COLUMN_PREP_TIME = "prepartionTime";
        /*
            Source
            Type: TEXT
         */
        public final static String COLUMN_SOURCE_URL = "sourceURL";
        /*
            Html title
            Type: TEXT
         */
        public final static String COLUMN_HTML = "htmlTitle";

    }
}

