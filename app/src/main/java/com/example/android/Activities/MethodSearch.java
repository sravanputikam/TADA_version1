/*
  This activity calls MethodLoader to start background process of
  fetching recipe of a food item and also controls the XML layout for
  showing the recipe.
 */
package com.example.android.Activities;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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

import com.example.android.HelperClasses.Method;
import com.example.android.Loaders.MethodLoader;
import com.example.android.R;
import com.example.android.data.RecipeContract.RecipeEntry;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 30-Sep-17.
 */

public class MethodSearch extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Method>> {
    private String IngredientSearch_URL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/";
    private static final String key = "&mashape-key=XZ60mx8ZNsmshUELnPXZO7Ath1sxp1gxIDOjsnoNdgILz9yH9d";
    Context context;
    String title;
    //View variables
    private TextView mTitle;
    private TextView mIngredients;
    private TextView mInstructions;
    private TextView mPrepTime;
    private TextView mSource_Url;
    private String source;
    private String imageURL;
    private String imagePath;
    private int Calories;
    private WebView mWebView;
    private ScrollView mScroll;
    private String nutrients;
    private int web_scroll = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.method_activity);
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        Calories = Integer.parseInt(bundle.getString("calories"));
        Log.v("MethodSearch", "" + Calories);
        IngredientSearch_URL += bundle.getString("id");
        IngredientSearch_URL += "/information?includeNutrition=true";
        IngredientSearch_URL += key;
        Log.v("Prep_Method", IngredientSearch_URL);
        mTitle = (TextView) findViewById(R.id.title);
        mIngredients = (TextView) findViewById(R.id.ingredients);
        mInstructions = (TextView) findViewById(R.id.instructions);
        mPrepTime = (TextView) findViewById(R.id.prepTime);
        mSource_Url = (TextView) findViewById(R.id.source_url);
        mSource_Url.setMovementMethod(LinkMovementMethod.getInstance());
        mWebView = (WebView) findViewById(R.id.webview);
        mScroll = (ScrollView) findViewById(R.id.scroll);
        context = MethodSearch.this;
        mWebView.setVisibility(View.INVISIBLE);
        mScroll.setVisibility(View.INVISIBLE);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(2, null, this);
    }

    public Target getTarget(final Context mContext, final String name) {
        Target target = new Target() {
            ContextWrapper cw = new ContextWrapper(mContext);
            final File directory = cw.getDir("imageDir", Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(directory, name);
                        Log.v("MethodSearch", file.toString());
                        imagePath = file.toString();
                        Log.v("MethodSearch", file.toString());
                        try {
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                        Log.v("image", "image saved to >>>" + file.getAbsolutePath());
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        return target;
    }
    private String saveHtmlFile(String fileName, String html) {

        String path = Environment.getExternalStorageDirectory().getPath();
        fileName = fileName + ".html";
        File file = new File(path, fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] data = html.getBytes();
            out.write(data);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.toString();
    }

    private void saveImage() {

        String[] imageName = imageURL.split("/");
        String Image_Name = imageName[(imageName.length - 1)];
        Log.v("MethodSearch", Image_Name);

        Picasso
                .with(this)
                .load(imageURL)
                .into(getTarget(getApplicationContext(), Image_Name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/method_1.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.method_1, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new recipe, hide the "Delete" menu item.
        MenuItem menuItem = menu.findItem(R.id.action_delete);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save image
                saveImage();
                // Exit activity
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Save recipe to database
                        saveRecipe();
                    }
                }, 1500);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * save recipe into database.
     */
    private void saveRecipe() {
        // Read from input fields
        String ingredientsString ;
        String instructionsString = "" ;
        String nutritionString = "" ;
        final String html_title;
        final String[] html = new String[1];
        String titleString = mTitle.getText().toString().trim();
        if(web_scroll == 0)
        {
            html_title = title.replace(" ","_");
            Ion.with(getApplicationContext())
                    .load(source)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            html[0] = saveHtmlFile(html_title,result);
                        }
                    });
            ingredientsString = "0";
        }
        else {
            // Use trim to eliminate leading or trailing white space
            ingredientsString = mIngredients.getText().toString().trim();
            instructionsString = mInstructions.getText().toString().trim();
            nutritionString = nutrients.trim();
        }
        String prepTimeString = mPrepTime.getText().toString().trim();
        Log.v("MethodSearch", "" + Calories);
        // Create a ContentValues object where column names are the keys,
        // and recipe attributes from the MethodSearch are the values.
        ContentValues values = new ContentValues();
        values.put(RecipeEntry.COLUMN_TITLE, titleString);
        values.put(RecipeEntry.COLUMN_INGREDIENTS, ingredientsString);
        values.put(RecipeEntry.COLUMN_INSTRUCTIONS, instructionsString);
        values.put(RecipeEntry.COLUMN_NUTRIENTS, nutritionString);
        values.put(RecipeEntry.COLUMN_PREP_TIME, prepTimeString);
        values.put(RecipeEntry.COLUMN_IMAGE_PATH, imagePath);
        values.put(RecipeEntry.COLUMN_SOURCE_URL, source);
        values.put(RecipeEntry.COLUMN_HTML,html[0]);
        // This is a NEW recipe, so insert a new recipe into the provider,
        // returning the content URI for the new recipe.
        Uri newUri = getContentResolver().insert(RecipeEntry.CONTENT_URI, values);
        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Unsucessful",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Recipe Saved Succesfully",
                    Toast.LENGTH_SHORT).show();
        }
        Intent start = new Intent(MethodSearch.this, MainActivity.class);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
        finish();
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
    @Override
    public Loader<ArrayList<Method>> onCreateLoader(int id, Bundle args) {
        return new MethodLoader(this, IngredientSearch_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Method>> loader, ArrayList<Method> method) {
        Method currentMethod = method.get(0);
        source = currentMethod.getSourceUrl();
        if (currentMethod.getmInstructions(0) == "0") {
            web_scroll = 0;
            mWebView.setVisibility(View.VISIBLE);
            // Configure related browser settings
            mWebView.getSettings().setLoadsImagesAutomatically(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            // Configure the client to use when opening URLs
            mWebView.setWebViewClient(new MyBrowser());
            // Load the initial URL
            mWebView.loadUrl(currentMethod.getSourceUrl());
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
            mWebView.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page
            imageURL = currentMethod.getmImgaeUrl();
        } else {
            mScroll.setVisibility(View.VISIBLE);
            String ingredients = "";
            String instruct = "";
            String nutri = "";
            for (int i = 0; i < currentMethod.getmIngredientsLength(); i++) {
                ingredients += "- ";
                ingredients += currentMethod.getmIngredients(i);
                ingredients += "\n";
            }
            for (int i = 0; i < currentMethod.getmInstructionLength(); i++) {
                instruct += ((i + 1) + ") ");
                instruct += currentMethod.getmInstructions(i);
                instruct += "\n\n";
            }
            instruct = instruct.substring(0,instruct.length()-1);
            TextView mNutriNames = (TextView) findViewById(R.id.nutrientsTitle);
            TextView mNutriValues = (TextView) findViewById(R.id.nutrientsValues);
            String nutriValues[] = new String[8];

            for (int i = 0; i < currentMethod.getmNutrientsLength(); i++) {
                nutriValues[i]= currentMethod.getmNutrients(i);
            }
            String nutriNames[] = new String[]{
                    "Calories",
                    "Fat",
                    "Saturated Fat",
                    "Carbohydrates",
                    "Sugar",
                    "Cholesterol",
                    "Sodium",
                    "Protein"
            } ;
            for (int i = 0; i < 8; i++) {
                nutrients += (nutriNames[i]+" - "+nutriValues[i]+"\n");
            }
            String Nnames = "";
            for (int i = 0; i < 8; i++) {
               if(i == 7)
               {
                   Nnames += nutriNames[i]+"\n";
               }
               else
                   Nnames += nutriNames[i]+"\n\n";
            }
            String Nvalues = "";
            for (int i = 0; i < 8; i++) {
                if(i == 7)
                {
                    Nvalues += nutriValues[i]+"\n";
                }
                else
                    Nvalues += nutriValues[i]+"\n\n";
            }
            //ArrayAdapter<String> NutriTitlesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,nutriNames);
            //ArrayAdapter<String> NutrientsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_2,android.R.id.text2,nutriValues);
            mTitle.setText(title);
            ImageView imageView = (ImageView) findViewById(R.id.image);
            //Picasso library extracts the image from the URL,
            //resizes it to 300x300 pixels so that all images are same size
            //scales the image so that it fills the requested bounds of the ImageView
            Picasso
                    .with(context)
                    .load(currentMethod.getmImgaeUrl())
                    .resize(350, 250)
                    .centerCrop()
                    .into(imageView);
            imageURL = currentMethod.getmImgaeUrl();
            String time = currentMethod.getmPrepTime();
            mIngredients.setText(ingredients);
            mInstructions.setText(instruct);
            mNutriNames.setText(Nnames);
            mNutriValues.setText(Nvalues);
            mPrepTime.setText(time);
            mSource_Url.setText(source +"\n");
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Method>> loader) {

    }
}