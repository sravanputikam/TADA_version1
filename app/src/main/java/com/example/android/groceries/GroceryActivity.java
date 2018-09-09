package com.example.android.groceries;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.data.GroceryContract.GroceryEntry;

/**
 * Created by user on 19-Oct-17.
 */

public class GroceryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings;
    private LinearLayout layoutFabBarcode;
    private LinearLayout layoutFabType;
    private Uri mCurrentGroceryUri;
    private String UPC_SEARCH_URL = "https://api.upcitemdb.com/prod/trial/lookup?upc=";
    private GroceryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        fabSettings = (FloatingActionButton) findViewById(R.id.fabSetting);
        layoutFabBarcode = (LinearLayout) findViewById(R.id.layoutFabBarcode);
        layoutFabType = (LinearLayout) findViewById(R.id.layoutFabType);
        FloatingActionButton fabBarcode = (FloatingActionButton) findViewById(R.id.fabBarcode);
        FloatingActionButton fabType = (FloatingActionButton) findViewById(R.id.fabType);
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
        fabBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSubMenusFab();
                Intent intent = new Intent(GroceryActivity.this,GrocerySearch.class);
                intent.putExtra("intent","1");
                startActivity(intent);
            }
        });
        fabType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSubMenusFab();
                Intent intent = new Intent(GroceryActivity.this,GrocerySearch.class);
                intent.putExtra("intent","2");
                startActivity(intent);
            }
        });
        final ListView groceryListView = (ListView)findViewById(R.id.exp_list_view);
        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentGroceryUri = ContentUris.withAppendedId(GroceryEntry.CONTENT_GROCERY_URI, id);
                showDeleteConfirmationDialog();
            }
        });
        View emptyView = findViewById(R.id.emptyview);
        groceryListView.setEmptyView(emptyView);
        mCursorAdapter = new GroceryCursorAdapter(GroceryActivity.this, null);
        groceryListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(1, null,this);
        closeSubMenusFab();
    }
    /*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_groceries, container, false);

        fabSettings = (FloatingActionButton) view.findViewById(R.id.fabSetting);
        layoutFabBarcode = (LinearLayout) view.findViewById(R.id.layoutFabBarcode);
        layoutFabType = (LinearLayout) view.findViewById(R.id.layoutFabType);
        FloatingActionButton fabBarcode = (FloatingActionButton) view.findViewById(R.id.fabBarcode);
        FloatingActionButton fabType = (FloatingActionButton) view.findViewById(R.id.fabType);
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
        fabBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),GrocerySearch.class);
                intent.putExtra("intent","1");
                startActivity(intent);
            }
        });
        fabType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),GrocerySearch.class);
                intent.putExtra("intent","2");
                startActivity(intent);
            }
        });
        final ListView groceryListView = (ListView) view.findViewById(R.id.exp_list_view);
        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentGroceryUri = ContentUris.withAppendedId(GroceryEntry.CONTENT_GROCERY_URI, id);
                showDeleteConfirmationDialog();
            }
        });
        View emptyView = view.findViewById(R.id.emptyview);
        groceryListView.setEmptyView(emptyView);
        mCursorAdapter = new GroceryCursorAdapter(getActivity(), null);
        groceryListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(1, null, this);
        closeSubMenusFab();
        return view;
    }
    */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(GroceryActivity.this);
        builder.setMessage("Delete this item");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteGrocery();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteGrocery() {
        // Only perform the delete if this is an existing item.
        if (mCurrentGroceryUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentGroceryUri
            // content URI already identifies the item that we want.
            int rowsDeleted = this.getContentResolver().delete(mCurrentGroceryUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(GroceryActivity.this, "Error with deleting Reicpe",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(GroceryActivity.this, "Item Deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabBarcode.setVisibility(View.INVISIBLE);
        layoutFabType.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.ic_action_name);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabBarcode.setVisibility(View.VISIBLE);
        layoutFabType.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fabSettings.setImageResource(R.drawable.ic_close_black_24dp);
        fabExpanded = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_recipes:
                deleteAllGroceries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteAllGroceries(){
        int rowsDeleted = getContentResolver().delete(GroceryEntry.CONTENT_GROCERY_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from groceries database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                GroceryEntry._ID,
                GroceryEntry.COLUMN_TITLE,
                GroceryEntry.COLUMN_DATE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                GroceryEntry.CONTENT_GROCERY_URI,   // Provider content URI to query
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

    public class GroceryCursorAdapter extends CursorAdapter {

        public GroceryCursorAdapter(Context context, Cursor c){
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
            // Find the columns of grocery attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_TITLE);
            int dateColumnIndex = cursor.getColumnIndex(GroceryEntry.COLUMN_DATE);
            // Read the grocery attributes from the Cursor for the current grocery
            String groceryName = cursor.getString(titleColumnIndex);
            String mdate = cursor.getString(dateColumnIndex);
            title.setText(groceryName);
            date.setText(mdate);
        }
    }
}

