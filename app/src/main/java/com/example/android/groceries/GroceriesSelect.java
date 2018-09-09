package com.example.android.groceries;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.Activities.IngredientSearch;
import com.example.android.R;
import com.example.android.data.GroceryContract;

/**
 * Created by user on 28-Oct-17.
 */

public class GroceriesSelect extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    GrocerySelectCursorAdapter mCursorAdapter;
    private String items ="";
    private String[] groceries = new String[20];
    private int index = 0;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSetting);
        fab.setVisibility(View.GONE);
        LinearLayout layoutFabBarcode = (LinearLayout) findViewById(R.id.layoutFabBarcode);
        LinearLayout layoutFabType = (LinearLayout) findViewById(R.id.layoutFabType);;
        layoutFabBarcode.setVisibility(View.GONE);
        layoutFabType.setVisibility(View.GONE);
        final ListView groceryListView = (ListView) findViewById(R.id.exp_list_view);
        View emptyView = findViewById(R.id.emptyview);
        groceryListView.setEmptyView(emptyView);
        mCursorAdapter = new GrocerySelectCursorAdapter(this, null);
        groceryListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(4, null, this);
        groceryListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        groceryListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int count = groceryListView.getCheckedItemCount();
                mode.setTitle(count+" Items Selected");
                items += groceries[position];
                items +=",";
                groceryListView.getChildAt(position).setBackgroundColor(Color.parseColor("#CFD8DC"));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.method_1, menu);
                MenuItem menuItem = menu.findItem(R.id.action_delete);
                menuItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                items = items.substring(0, items.length() - 1);
                switch (item.getItemId()) {
                    case R.id.action_save:
                        Log.v("GroceriesSelect",items);
                        Intent intent = new Intent(GroceriesSelect.this, IngredientSearch.class);
                        intent.putExtra("items", items);
                        intent.putExtra("ClassName","GroceriesSelect");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                GroceryContract.GroceryEntry._ID,
                GroceryContract.GroceryEntry.COLUMN_TITLE,
                GroceryContract.GroceryEntry.COLUMN_DATE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                GroceryContract.GroceryEntry.CONTENT_GROCERY_URI,   // Provider content URI to query
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

    public class GrocerySelectCursorAdapter extends CursorAdapter {

        public GrocerySelectCursorAdapter(Context context, Cursor c){
            super(context, c, 0);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Inflate a list item view using the layout specified in list_item.xml
            return LayoutInflater.from(context).inflate(R.layout.grocery_list_child_view, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find individual views that we want to modify in the list item layout
            TextView title = (TextView) view.findViewById(R.id.items);
            TextView date = (TextView) view.findViewById(R.id.date);
            //ImageView imageView = (ImageView) view.findViewById(R.id.image);
            // Find the columns of recipe attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_TITLE);
            // Read the recipe attributes from the Cursor for the current recipe
            String groceryName = cursor.getString(titleColumnIndex);
            Log.v("RecipeCursor",groceryName);
            //String cal = cursor.getString(caloriesColumnIndex);
            /*
            Picasso
                    .with(context)
                    .load(new File(imagePATH))
                    .resize(150,150)
                    .centerCrop()
                    .into(imageView);*/
            if(index < 20) {
                groceries[index] = groceryName;
            }
            title.setText(groceryName);
            index++;
        }
    }

}

