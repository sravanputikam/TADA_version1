
package com.example.android.Activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.Adapters.FoodAdapter;
import com.example.android.Loaders.RecipeLoader;
import com.example.android.R;
import com.example.android.HelperClasses.Recipe;

import java.util.ArrayList;

/**
 * Created by user on 22-Sep-17.
 */

public class FoodSearch extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    private static final String LOG_TAG = FoodSearch.class.getName();
    private String ingredientSearch_URL;
    private int urlSearch;
    private static final String key = "&mashape-key=XZ60mx8ZNsmshUELnPXZO7Ath1sxp1gxIDOjsnoNdgILz9yH9d";
    private FoodAdapter mAdapter;
    private TextView mEmptyStateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes_activity);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        Bundle bundle = getIntent().getExtras();
        //URL for food items data from the spoonacular dataset
        ingredientSearch_URL = bundle.getString("url");
        String s = bundle.getString("urlSearch");
        urlSearch = Integer.parseInt(s);
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        // Find a reference to the {@link ListView} in the layout
        ListView recipeListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        recipeListView.setEmptyView(mEmptyStateTextView);
        /* Adapter for the list of recipes */
        // Create a new adapter that takes an empty list of recipes as input
        mAdapter = new FoodAdapter(this, recipes, urlSearch);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        recipeListView.setAdapter(mAdapter);
        // Start the AsyncTaskLoader to fetch the recipe data
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Recipe currentRecipe = mAdapter.getItem(position);
                Intent intent = new Intent(FoodSearch.this, MethodSearch.class);
                String mId = Integer.toString(currentRecipe.getmId());
                String title = currentRecipe.getTitle();
                String calories = currentRecipe.getmCalories();
                // sends the id and title to MethodSearch Activity
                // of the food item selected by the user
                Bundle extras= new Bundle();
                extras.putString("id", mId);
                extras.putString("title", title);
                extras.putString("calories", calories);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {
        return new RecipeLoader(this, ingredientSearch_URL,urlSearch);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> recipes) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No Recipe found."
        mEmptyStateTextView.setText("No Recipe found");

        // Clear the adapter of previous recipe data
        mAdapter.clear();

        // If there is a valid list of {@link Recipe}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (recipes != null && !recipes.isEmpty()) {
            mAdapter.addAll(recipes);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
