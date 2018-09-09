/*

 This java file parses JSON based on the URL that FoodSearch activity sends.
 It feteches the required data i.e. a list of food items
  and then adds it to the ArrayList
 */




package com.example.android.HelperClasses;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by user on 23-Sep-17.
 */

public final class QueryRecipes {
    private static final String LOG_TAG = QueryRecipes.class.getSimpleName();
    private QueryRecipes(){

    }
    public static ArrayList<Recipe> fetchRecipesData(String requestUrl, int search) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making Http request",e);
        }
        ArrayList<Recipe> recipes = extractFeatureFromJson(jsonResponse, search);
        return recipes;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(40000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the recipe JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /*
      Convert the {@link InputStream} into a String which contains the
      whole JSON response from the server.
     */
    private static ArrayList<Recipe> extractFeatureFromJson(String recipeJSON, int Search) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(recipeJSON)) {
            return null;
        }

        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {

            if(Search == 2)
            {
                JSONArray recipeArray = new JSONArray(recipeJSON);

                for (int i = 0; i < recipeArray.length(); i++) {

                    JSONObject currentRecipe = recipeArray.getJSONObject(i);

                    // Extract the value for the key called "id"
                    int id = currentRecipe.getInt("id");
                    // Extract the value for the key called "title"
                    String title = currentRecipe.getString("title");
                    // Extract the value for the key called "usedIngredientCount"
                    String used = currentRecipe.getString("usedIngredientCount");
                    // Extract the value for the key called "missedIngredientCount"
                    String missed =currentRecipe.getString("missedIngredientCount") ;
                    Log.v("QueryRecipes","title = "+title);
                    // Extract the value for the key called "image"
                    String image = currentRecipe.getString("image");
                    // Create a new {@link Recipe} object with the id, title,used,missed
                    // Add the new {@link Recipe} to the list of recipes.
                    recipes.add(new Recipe(id, title,used,missed,image,"0","0","0","0"));
                }
            }
            else {

                JSONObject baseObject = new JSONObject(recipeJSON);
                JSONArray recipeArray = baseObject.getJSONArray("results");
                for (int i = 0; i < recipeArray.length(); i++) {

                    JSONObject currentRecipe = recipeArray.getJSONObject(i);
                    // Extract the value for the key called "id"
                    int id = currentRecipe.getInt("id");
                    // Extract the value for the key called "title"
                    String title = currentRecipe.getString("title");
                    // Extract the value for the key called "image"
                    String image = currentRecipe.getString("image");
                    // Extract the value for the key called "usedIngredientCount"
                    String used = currentRecipe.getString("usedIngredientCount");
                    // Extract the value for the key called "missedIngredientCount"
                    String missed = currentRecipe.getString("missedIngredientCount");
                    //Extract the value for the key called "calories"
                    String calories = currentRecipe.getString("calories");
                    //Extract the value for the key called "protein"
                    String protein = currentRecipe.getString("protein");
                    //Extract the value for the key called "carbs"
                    String carbs = currentRecipe.getString("carbs");
                    //Extract the value for the key called "fat"
                    String fat = currentRecipe.getString("fat");
                    Log.v("QueryRecipes", "title = " + title);
                    // Create a new {@link Recipe} object with the id, title,used,missed
                    // Add the new {@link Recipe} to the list of recipes.
                    recipes.add(new Recipe(id, title, used, missed, image, calories, protein, fat, carbs));
                }
            }
        }
        catch (JSONException e) {
            Log.e("QueryRecipes", "Problem parsing the recipe JSON results", e);
        }

        return recipes;
    }
}