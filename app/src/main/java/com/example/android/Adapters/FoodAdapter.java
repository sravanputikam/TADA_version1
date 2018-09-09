/*
 Programmer : Sai Venkata Sravan Sravan
 Program: Recipe Extraction

 This java file FoddAdaper is a custom array adapter to populate
 the listview so that it displays recipes to the user.
 */
package com.example.android.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.HelperClasses.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user on 23-Sep-17.
 */

public class FoodAdapter extends ArrayAdapter<Recipe> {

    ArrayList<Recipe> recipes;
    Context context;
    int resource;

    public FoodAdapter(Context context, ArrayList<Recipe> Recipes, int Search) {
        super(context, 0, Recipes);
        this.recipes = Recipes;
        this.context = context;
        this.resource = Search;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.recipe_list_item, parent, false);
        }
        if (resource == 2) {
            LinearLayout cal = (LinearLayout) listView.findViewById(R.id.layout_cal);
            cal.setVisibility(View.GONE);
        }
        LinearLayout ready = (LinearLayout) listView.findViewById(R.id.layout_ready);
        ready.setVisibility(View.GONE);
        Recipe currentRecipe = getItem(position);
        ImageView imageView = (ImageView) listView.findViewById(R.id.image);

        //Picasso library extracts the image from the URL,
        //resizes it to 150x150 pixels so that all images are same size
        //scales the image so that it fills the requested bounds of the ImageView
        Picasso
                .with(context)
                .load(currentRecipe.getmImageUrl())
                .resize(150, 150)
                .centerCrop()
                .into(imageView);
        //Gives textviews and imageview values to display
        TextView recipeName = (TextView) listView.findViewById(R.id.foodName);
        recipeName.setText(currentRecipe.getTitle());
        TextView noOfCal = (TextView) listView.findViewById(R.id.calories);
        noOfCal.setText(currentRecipe.getmCalories());

        return listView;
    }
}
