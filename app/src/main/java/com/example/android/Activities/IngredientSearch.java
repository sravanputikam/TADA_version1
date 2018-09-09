/*
 Programmer : Sai Venkata Sravan Sravan
 Program: Recipe Extraction

 This java file is used to take input ingredients from the user
 and generate a URL that can be sent to FoodSearch java file
 */

package com.example.android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.groceries.GroceriesSelect;

import static com.example.android.R.id.intolerances;
import static com.example.android.R.id.maxFat;
import static com.example.android.R.id.maxProtein;
import static com.example.android.R.id.minProtein;
import static com.example.android.R.id.seekMaxFats;

/**
 * Created by Sravan Putikam on 22-Sep-17
 */

public class IngredientSearch extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    //Variables for the seek bars
    private SeekBar seekMaxCalorie;
    private SeekBar seekMinCalorie;
    private SeekBar seekMaxProtein;
    private SeekBar seekMinProtein;
    private SeekBar seekMaxFat;
    private SeekBar seekMinFat;
    private SeekBar seekMaxCarbs;
    private SeekBar seekMinCarbs;

    //Variable for textview displaying the amount
    private TextView minCalories;
    private TextView maxCalories;
    private TextView minProteins;
    private TextView maxProteins;
    private TextView minCarbs;
    private TextView maxCarbs;
    private TextView minFats;
    private TextView maxFats;

    private AutoCompleteTextView mAutocompletTextView;
    private static int index = 0;
    private String url;
    private String key = "&mashape-key=XZ60mx8ZNsmshUELnPXZO7Ath1sxp1gxIDOjsnoNdgILz9yH9d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle bundle = getIntent().getExtras();
        String className = bundle.getString("ClassName");
        Log.v("IngredientSearch", className);
        mAutocompletTextView = (AutoCompleteTextView) findViewById(R.id.ingredients);
        String data = bundle.getString("items");
        if (data == null) {
            ;
        } else {
            Log.v("IngredientSearch", data);
            String values = mAutocompletTextView.getText().toString();
            mAutocompletTextView.setText(data+","+values);
        }

        mAutocompletTextView.setThreshold(2);

        mAutocompletTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    //Autocompletes ingredients
                    String temp = s.toString();
                    String[] ingredientsList = temp.split("\\s*,\\s*");
                    int length = ingredientsList.length;
                    Log.v("IngredientSearch", "" + length);
                    if (length > 1)
                        index = (length - 1);
                    else
                        index = 0;
                    Log.v("IngredientSearch", ingredientsList[index]);
                    if (ingredientsList[index].length() > 1) {
                        url = "";
                        url += "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/ingredients/autocomplete?metaInformation=false&number=10&query=";
                        url += ingredientsList[index];
                        url += key;
                        Log.v("IngredientSearch", url);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //Assigning seekbars an inital value
        seekMaxCalorie = (SeekBar) findViewById(R.id.seekMaxCalories);
        seekMinCalorie = (SeekBar) findViewById(R.id.seekMinCalories);
        seekMaxProtein = (SeekBar) findViewById(R.id.seekMaxProteins);
        seekMinProtein = (SeekBar) findViewById(R.id.seekMinProteins);
        seekMaxCarbs = (SeekBar) findViewById(R.id.seekMaxCarbs);
        seekMinCarbs = (SeekBar) findViewById(R.id.seekMinCarbs);
        seekMinFat = (SeekBar) findViewById(R.id.seekMinFats);
        seekMaxFat = (SeekBar) findViewById(seekMaxFats);
        //Assigning textviews an inital value
        maxCalories = (TextView) findViewById(R.id.maxCal);
        maxCalories.setText("0");
        minCalories = (TextView) findViewById(R.id.minCal);
        minCalories.setText("0");
        maxProteins = (TextView) findViewById(maxProtein);
        maxProteins.setText("0");
        minProteins = (TextView) findViewById(minProtein);
        minProteins.setText("0");
        minCarbs = (TextView) findViewById(R.id.minCarbs);
        minCarbs.setText("0");
        maxCarbs = (TextView) findViewById(R.id.maxCarbs);
        maxCarbs.setText("0");
        minFats = (TextView) findViewById(R.id.minFat);
        minFats.setText("0");
        maxFats = (TextView) findViewById(maxFat);
        maxFats.setText("0");
        //calling listener
        seekMaxCalorie.setOnSeekBarChangeListener(this);
        seekMinCalorie.setOnSeekBarChangeListener(this);
        seekMaxProtein.setOnSeekBarChangeListener(this);
        seekMinProtein.setOnSeekBarChangeListener(this);
        seekMaxCarbs.setOnSeekBarChangeListener(this);
        seekMinCarbs.setOnSeekBarChangeListener(this);
        seekMaxFat.setOnSeekBarChangeListener(this);
        seekMinFat.setOnSeekBarChangeListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.select);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(IngredientSearch.this, GroceriesSelect.class);
                startActivity(search);
            }
        });

    }

    //Keeps track of seek bar movement
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekMinCalories:
                minCalories.setText("" + progress * 10);
                break;
            case R.id.seekMaxCalories:
                maxCalories.setText("" + progress * 10);
                break;
            case R.id.seekMaxProteins:
                maxProteins.setText("" + progress * 2);
                break;
            case R.id.seekMinProteins:
                minProteins.setText("" + progress * 2);
                break;
            case R.id.seekMaxCarbs:
                maxCarbs.setText("" + progress * 2);
                break;
            case R.id.seekMinCarbs:
                minCarbs.setText("" + progress * 2);
                break;
            case seekMaxFats:
                maxFats.setText("" + progress * 2);
                break;
            case R.id.seekMinFats:
                minFats.setText("" + progress * 2);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private static final String LOG_TAG = IngredientSearch.class.getName();

    //When Search button is clicked Url is generated
    public void onClick(View view) {
        //base URL
        String IngredientSearch_URL =
                "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?fillIngredients=false&ingredients=";
        String ComplexSearch_URL =
                "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex?addRecipeInformation=false&";

        String Ingredients = "";
        Ingredients += mAutocompletTextView.getText().toString();

        AutoCompleteTextView intolorances = (AutoCompleteTextView) findViewById(intolerances);
        String Intolerances = "";
        Intolerances += intolorances.getText().toString();
        //splits the ingredients list into individual ingridient
        //Then, store the individual ingridient in the ingredientsList array
        if (Ingredients == "") {
            Toast.makeText(IngredientSearch.this, "Atleast one ingredient Should be included", Toast.LENGTH_LONG).show();
        } else {
            String[] ingredientsList = Ingredients.split("\\s*,\\s*");
            //Generates URL by attaching all the list of specifications to the base URL
            ComplexSearch_URL += "fillIngredients=false&includeIngredients=";

            for (int i = 0; i < ingredientsList.length; i++) {
                if (i == (ingredientsList.length) - 1)
                    ComplexSearch_URL += ingredientsList[i];
                else
                    ComplexSearch_URL += (ingredientsList[i] + "%2C");
            }
            if (Intolerances != "") {
                ComplexSearch_URL += "&instructionsRequired=false&intolerances=";
                String[] intolerancesList = Intolerances.split("\\s*,\\s*");
                for (int i = 0; i < intolerancesList.length; i++) {
                    if (i == (intolerancesList.length) - 1)
                        ComplexSearch_URL += intolerancesList[i];
                    else
                        ComplexSearch_URL += (intolerancesList[i] + "%2C");
                }
            }
            String maxCalorie = maxCalories.getText().toString();
            String minCalorie = minCalories.getText().toString();
            String maxCarb = maxCarbs.getText().toString();
            String minCarb = minCarbs.getText().toString();
            String maxProtein = maxProteins.getText().toString();
            String minProtein = minProteins.getText().toString();
            String maxFat = maxFats.getText().toString();
            String minFat = minFats.getText().toString();
            if (maxCalorie == "0" && minCalorie == "0" && maxCarb == "0" && minCarb == "0" && maxProtein == "0" && minProtein == "0" && maxFat == "0" && minFat == "0") {

                if (Intolerances == "") {
                    Log.v("IngredientsSearch","Hi");
                    for (int i = 0; i < ingredientsList.length; i++) {
                        if (i == (ingredientsList.length) - 1)
                            IngredientSearch_URL += ingredientsList[i];
                        else
                            IngredientSearch_URL += (ingredientsList[i] + "%2C");
                    }
                    IngredientSearch_URL += "&limitLicense=false&number=10&ranking=1";
                    IngredientSearch_URL += key;
                    Intent search = new Intent(IngredientSearch.this, FoodSearch.class);
                    search.putExtra("url", IngredientSearch_URL);
                    search.putExtra("urlSearch", "2");
                    startActivity(search);
                    return;
                } else {
                    ComplexSearch_URL += "&limitLicense=false&number=10&offset=0&ranking=1" + key;
                    Intent search = new Intent(IngredientSearch.this, FoodSearch.class);
                    search.putExtra("url", ComplexSearch_URL);
                    search.putExtra("urlSearch", "1");
                    startActivity(search);
                    return;
                }
            }
            ComplexSearch_URL += "&limitLicense=false&";
            ComplexSearch_URL += ("maxCalories=" + maxCalorie + "&maxCarbs" + maxCarb + "&maxFat=" + maxFat + "&maxProtein=" + maxProtein + "&minCalories=" +
                    minCalorie + "&minCarbs=" + minCarb + "&minFat=" + minFat + "&minProtein=" + minProtein);
            ComplexSearch_URL += "&number=10&offset=0&ranking=1" + key;
            Log.v("IngredientSearch", ComplexSearch_URL);
            //final URL that indicates number of recipes to be shown
            //Move from this activity to FoodSearch activity
            //send the final URL to the FoodSearch activity
            Intent search = new Intent(IngredientSearch.this, FoodSearch.class);
            search.putExtra("url", ComplexSearch_URL);
            search.putExtra("urlSearch", "1");
            startActivity(search);
        }

    }
}