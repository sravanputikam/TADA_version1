package com.example.android.groceries;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.data.GroceryContract.GroceryEntry;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
  Created by user on 20-Oct-17.
*/

public class GrocerySearch extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Grocery>> {

    private String Grocery_Search_URL = "https://api.nutritionix.com/v1_1/item?upc=";
    private TextView mTitle;
    private TextView results;
    private LinearLayout bar;
    private LinearLayout type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_search);
        Bundle bundle = getIntent().getExtras();
        bar = (LinearLayout) findViewById(R.id.bar);
        type = (LinearLayout) findViewById(R.id.layout_type);
        mTitle = (TextView) findViewById(R.id.titleText);
        int view = Integer.parseInt(bundle.getString("intent"));
        Log.v("GrocerySearch",view+"");
        if( view == 1)
        {
            Log.v("GrocerySearch",view+"");
            type.setVisibility(View.GONE);
            results = (TextView) findViewById(R.id.results);
            results.setVisibility(View.INVISIBLE);
        }
        else
        {
            bar.setVisibility(View.GONE);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionBarcode);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GrocerySearch.this, CameraBarcode.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data!=null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    EditText upcNumber = (EditText) findViewById(R.id.upc);
                    upcNumber.setText(barcode.displayValue);
                }else{
                    Toast.makeText(this, "No barcode found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onClick(View view){
        results.setVisibility(View.INVISIBLE);
        mTitle.setVisibility(View.GONE);
        EditText upcNumber = (EditText) findViewById(R.id.upc);
        String upc_number = upcNumber.getText().toString();
        if(upc_number.length() >= 12 && upc_number.length() <14 ) {
            Grocery_Search_URL += (upc_number + "&appId=9522432f&appKey=6dda490311038a2a413210f1b4b0bdb8");
            getLoaderManager().initLoader(3, null, this);
        }
        else
        {
            Toast.makeText(this,"Please enter a valid barcode number", Toast.LENGTH_SHORT).show();
        }
    }
    public void onSave(View view){
        EditText type = (EditText) findViewById(R.id.type);
        mTitle.setText(type.getText().toString());
        SaveGrocery();
    }
    public void showSaveConfirmationDialog(View view) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save this item");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the recipe.
                SaveGrocery();
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
    public void SaveGrocery(){

            // Read from input fields
            // Use trim to eliminate leading or trailing white space
            String titleString = mTitle.getText().toString().trim();
            String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date()); ;
            Log.v("MethodSearch", date);
            // Create a ContentValues object where column names are the keys,
            // and recipe attributes from the MethodSearch are the values.
            ContentValues values = new ContentValues();
            values.put(GroceryEntry.COLUMN_TITLE, titleString);
            values.put(GroceryEntry.COLUMN_DATE, date);

            // This is a NEW recipe, so insert a new recipe into the provider,
            // returning the content URI for the new recipe.
            Uri newUri = getContentResolver().insert(GroceryEntry.CONTENT_GROCERY_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this,"Unsucessful",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Grocery Saved Succesfully",
                        Toast.LENGTH_SHORT).show();
            }
        finish();
     }

    @Override
    public Loader<ArrayList<Grocery>> onCreateLoader(int id, Bundle args) {
        return new GroceryLoader(this, Grocery_Search_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Grocery>> loader, ArrayList<Grocery> data) {
        if(data!= null) {
            results.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.VISIBLE);
            Grocery currentGrocery = data.get(0);
            String title = currentGrocery.getmTitle();
            mTitle = (TextView) findViewById(R.id.titleText);
            mTitle.setText(title);
        }
        else
        {
            Toast.makeText(this, "No Information Found",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Grocery>> loader) {

    }
}
