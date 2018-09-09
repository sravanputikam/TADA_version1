package com.example.android.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.data.RecipeContract.RecipeEntry;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.example.android.R.id.image;
import static com.example.android.R.id.ingredients;
import static com.example.android.R.id.instructions;
import static com.example.android.R.id.prepTime;

/**
 * Created by user on 11-Oct-17.
 */

public class SavedRecipes extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String imagePath;
    private Uri mCurrentRecipeUri;
    private TextView mTitle;
    private TextView mInstructions;
    private TextView mIngredients;
    private TextView mPrepTime;
    private TextView mSource_Url;
    private ScrollView mScroll;
    private ImageView mImage;
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.method_activity);
        Intent intent = getIntent();
        mCurrentRecipeUri = intent.getData();
        // Initialize a loader to read the recipe data from the database
        getLoaderManager().initLoader(1, null, this);
        mTitle = (TextView) findViewById(R.id.title);
        mIngredients = (TextView) findViewById(ingredients);
        mInstructions = (TextView) findViewById(instructions);
        mImage = (ImageView) findViewById(image);
        mWebView = (WebView) findViewById(R.id.webview);
        mPrepTime = (TextView) findViewById(prepTime);
        mSource_Url = (TextView) findViewById(R.id.source_url);
        mSource_Url = (TextView) findViewById(R.id.source_url);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.method_1, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        menuItem.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all recipe attributes, define a projection that contains
        // all columns from the recipe table
        String[] projection = {
                RecipeEntry._ID,
                RecipeEntry.COLUMN_TITLE,
                RecipeEntry.COLUMN_INGREDIENTS,
                RecipeEntry.COLUMN_INSTRUCTIONS,
                RecipeEntry.COLUMN_IMAGE_PATH,
                RecipeEntry.COLUMN_PREP_TIME,
                RecipeEntry.COLUMN_NUTRIENTS,
                RecipeEntry.COLUMN_SOURCE_URL,
                RecipeEntry.COLUMN_HTML};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentRecipeUri,         // Query the content URI for the current recipe
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Find the columns of recipe attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_TITLE);
            int ingredietnsColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_INGREDIENTS);
            int instructionsColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_INSTRUCTIONS);
            int imagePathColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_IMAGE_PATH);
            int nutrientsColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_NUTRIENTS);
            int prepTimeColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_PREP_TIME);
            int sourceUrlColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_SOURCE_URL);
            int htmlColumnIndex = cursor.getColumnIndex(RecipeEntry.COLUMN_HTML);
            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String ingredients = cursor.getString(ingredietnsColumnIndex);
            String instructions = cursor.getString(instructionsColumnIndex);
            String prepTime = cursor.getString(prepTimeColumnIndex);
            String nutrients = cursor.getString(nutrientsColumnIndex);
            String source = cursor.getString(sourceUrlColumnIndex);
            String html = cursor.getString(htmlColumnIndex);
            if(instructions == "0")
            {
                mScroll.setVisibility(View.GONE);
                // Configure related browser settings
                mWebView.getSettings().setLoadsImagesAutomatically(true);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                // Configure the client to use when opening URLs
                mWebView.setWebViewClient(new MyBrowser());
                // Load the initial URL
                File file = new File(html);
                mWebView.loadUrl("file:///" + file);
                mWebView.getSettings().setSupportZoom(true);
                mWebView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
                mWebView.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page
            }
            else {

                imagePath = cursor.getString(imagePathColumnIndex);
                //Log.v("SavedRecipes",imagePath);
                // Update the views on the screen with the values from the database
                mTitle.setText(title);
                Picasso
                        .with(this)
                        .load(new File(imagePath))
                        .resize(350, 250)
                        .centerCrop()
                        .into(mImage);
                mIngredients.setText(ingredients);
                mInstructions.setText(instructions);
                mPrepTime.setText(prepTime);
                String splitNutri [] = nutrients.split("\n");
                String nutriValues = "";
                String nutriNames =
                        "Calories\n\n"+
                        "Fat\n\n"+
                        "Saturated Fat\n\n"+
                        "Carbohydrates\n\n"+
                        "Sugar\n\n"+
                        "Cholesterol\n\n"+
                        "Sodium\n\n"+
                        "Protein\n" ;
                for(int i = 0; i < 8;  i++)
                {
                    if(i!=7) {
                        nutriValues += (splitNutri[i].substring(splitNutri[i].indexOf("-") + 1, splitNutri[i].length()) + "\n\n");
                    }
                    else
                        nutriValues += (splitNutri[i].substring(splitNutri[i].indexOf("-") + 1, splitNutri[i].length()) + "\n");
                }
                TextView mNutriNames = (TextView) findViewById(R.id.nutrientsTitle);
                TextView mNutriValues = (TextView) findViewById(R.id.nutrientsValues);
                mNutriNames.setText(nutriNames);
                mNutriValues.setText(nutriValues);
                mSource_Url.setText(source + "\n\n");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitle.setText("");
        mIngredients.setText("");
        mInstructions.setText("");
    }

    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Recipe");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the recipe.
                deleteRecipe();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the recipe.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteRecipe() {
        // Only perform the delete if this is an existing recipe.
        if (mCurrentRecipeUri != null) {
            // Call the ContentResolver to delete the recipe at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentRecipeUri
            // content URI already identifies the recipe that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentRecipeUri, null, null);
            File ImageFile = new File(imagePath);
            ImageFile.delete();
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with deleting Reicpe",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Recipe Deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
