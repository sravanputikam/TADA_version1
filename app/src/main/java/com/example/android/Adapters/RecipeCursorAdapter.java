package com.example.android.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.data.RecipeContract.RecipeEntry;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.example.android.R.id.foodName;

/**
 * Created by user on 11-Oct-17.
 */

public class RecipeCursorAdapter extends CursorAdapter {

    public RecipeCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView title = (TextView) view.findViewById(foodName);
        TextView time = (TextView) view.findViewById(R.id.prepTime);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        LinearLayout cal_layout = (LinearLayout) view.findViewById(R.id.layout_cal);
        cal_layout.setVisibility(View.GONE);
        // Find the columns of recipe attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_TITLE);
        int timeColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_PREP_TIME);
        int imageColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_IMAGE_PATH);
        // Read the recipe attributes from the Cursor for the current recipe
        String foodName = cursor.getString(titleColumnIndex);
        String imagePATH = cursor.getString(imageColumnIndex);
        String prep_time = ""+cursor.getInt(timeColumnIndex) + " min";
        Log.v("RecipeCursor",foodName);
        Picasso
                .with(context)
                .load(new File(imagePATH))
                .resize(150,150)
                .centerCrop()
                .into(imageView);
        title.setText(foodName);
        time.setText(prep_time);
    }
}
