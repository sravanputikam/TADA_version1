package com.example.android.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.HelperClasses.QueryRecipes;
import com.example.android.HelperClasses.Recipe;

import java.util.ArrayList;

/*
  Loads a list of food items by using an AsyncTask to perform the
  network request to the given URL.
*/

public class RecipeLoader extends AsyncTaskLoader<ArrayList<Recipe>> {
    private String mUrl;
    private int search;
    public RecipeLoader(Context context, String url, int urlSearch) {
        super(context);
        mUrl = url;
        search = urlSearch;
    }


    @Override
    protected void onStartLoading()  {
        forceLoad();
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of recipes.
        ArrayList<Recipe> recipes = QueryRecipes.fetchRecipesData(mUrl,search);
        return recipes;
    }
}
