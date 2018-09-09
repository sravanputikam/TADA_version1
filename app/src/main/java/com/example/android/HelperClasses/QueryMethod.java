/*
 This java file parses JSON based on the URL that MethodSearch activity sends.
 It fetches the required data ie. Ingredients list, Instructions,
 ,and image url and then adds it to the ArrayList
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
 * Created by user on 30-Sep-17.
 */

public final class QueryMethod  {
    private static final String LOG_TAG = QueryMethod.class.getSimpleName();
    private QueryMethod(){

    }
    public static ArrayList<Method> fetchMethodData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making Http request",e);
        }
        Log.v("QueryMethod",jsonResponse);
        ArrayList<Method> methods = extractFeatureFromJson(jsonResponse);
        return methods;
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
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
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
            Log.e(LOG_TAG, "Problem retrieving the method JSON results.", e);
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
    /*
      Convert the {@link InputStream} into a String which contains the
      whole JSON response from the server.
     */
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

    private static ArrayList<Method> extractFeatureFromJson(String methodJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(methodJSON)) {
            return null;
        }

        ArrayList<Method> methods = new ArrayList<Method>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject methodObject = new JSONObject(methodJSON);
            JSONArray ingredientsArray = methodObject.getJSONArray("extendedIngredients");
            String[] ingredients = new String[20];
            int ingrSize = 0;
            for(int i =0; i< 20; i++){
                ingredients[i] = "0";
            }
            //Extract all the ingredients
            for(int i = 0; i < ingredientsArray.length(); i++){
                JSONObject currentIngredient = ingredientsArray.getJSONObject(i);
                ingredients[i] = currentIngredient.getString("originalString");
                ingrSize++;
            }
            Log.v("QueryMethod",""+ingredients.length);
            String[] instructions = new String[20];
            for(int i =0; i< 15; i++){
                instructions[i] = "0";
            }
            int instrSize =1;
            String instruction = methodObject.getString("instructions");
            if(instruction != "null")
            {
                JSONArray instrucArray = methodObject.getJSONArray("analyzedInstructions");
                JSONObject step = instrucArray.getJSONObject(0);
                JSONArray stepsArray = step.getJSONArray("steps");
                //Extract all the instructions
                for(int i = 0; i < stepsArray.length(); i++){
                    JSONObject currentStep = stepsArray.getJSONObject(i);
                    instructions[i] = currentStep.getString("step");
                    Log.v("QueryMethod",instructions[i]);
                    instrSize++;
                }
            }
            String sourceUrl = methodObject.getString("spoonacularSourceUrl");
            String imageUrl = methodObject.getString("image");
            Log.v("QueryMethod",imageUrl);
            JSONObject nutritionObject = methodObject.getJSONObject("nutrition");
            JSONArray nutrientsArray = nutritionObject.getJSONArray("nutrients");
            String[] nutrients = new String[10];
            for(int i =0; i< 10; i++){
                nutrients[i] = "0";
            }
            for(int i = 0; i < 8; i++){
                JSONObject nutrient = nutrientsArray.getJSONObject(i);
                nutrients[i] = (nutrient.getDouble("amount") + nutrient.getString("unit"));
                Log.v("QueryMethod",nutrients[i]);
            }
            String prepTime = methodObject.getInt("readyInMinutes") + " min";
            Log.v("QueryMethod",prepTime);
            // Create a new {@link Method} object with the id, title,used,missed
             // Add the new {@link Method} to the list of instructions.
             methods.add(new Method(ingredients, instructions, imageUrl,ingrSize,instrSize, nutrients,prepTime,sourceUrl));
            } catch (JSONException e) {
               Log.e("QueryMethod", "Problem parsing the method JSON results", e);
        }

        return methods;
    }
}
