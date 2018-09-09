package com.example.android.Fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.Activities.HeartRateMonitor;
import com.example.android.Activities.IngredientSearch;
import com.example.android.Activities.SavedRecipes;
import com.example.android.Adapters.RecipeCursorAdapter;
import com.example.android.R;
import com.example.android.data.RecipeContract.RecipeEntry;
import com.example.android.groceries.GroceryActivity;
import static com.example.android.R.id.fab;
import static com.example.android.R.id.fab_grocery;
import static com.example.android.R.id.fab_heart;

/**
 * Created by user on 15-Sep-17.
 */

public class RecipeActivity extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private RecipeCursorAdapter mCursorAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_recipe, container, false);
        FloatingActionButton fabRecipes = (FloatingActionButton) view.findViewById(fab);
        FloatingActionButton fabGroceries = (FloatingActionButton) view.findViewById(fab_grocery);
        fabRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getActivity(), IngredientSearch.class);
                search.putExtra("ClassName","RecipesActivity");
                startActivity(search);
            }
        });
        fabGroceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getActivity(), GroceryActivity.class);
                startActivity(search);
            }
        });
        FloatingActionButton fabHeartMonitor = (FloatingActionButton) view.findViewById(fab_heart);
        fabHeartMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getActivity(), HeartRateMonitor.class);
                startActivity(search);
            }
        });
        fabHeartMonitor.setVisibility(View.INVISIBLE);
        ListView recipeListView = (ListView) view.findViewById(R.id.list);
        View emptyView = view.findViewById(R.id.empty_view);
        recipeListView.setEmptyView(emptyView);
        mCursorAdapter = new RecipeCursorAdapter(getActivity(), null);
        recipeListView.setAdapter(mCursorAdapter);
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SavedRecipes.class);
                Uri currentRecipeUri = ContentUris.withAppendedId(RecipeEntry.CONTENT_URI, id);
                intent.setData(currentRecipeUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, null, this);
        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_recipes:
                deleteAllRecipes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllRecipes() {
        int rowsDeleted = getActivity().getContentResolver().delete(RecipeEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from recipe database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                RecipeEntry._ID,
                RecipeEntry.COLUMN_TITLE,
                RecipeEntry.COLUMN_INGREDIENTS,
                RecipeEntry.COLUMN_NUTRIENTS,
                RecipeEntry.COLUMN_PREP_TIME,
                RecipeEntry.COLUMN_SOURCE_URL,
                RecipeEntry.COLUMN_IMAGE_PATH};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getActivity(),   // Parent activity context
                RecipeEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
