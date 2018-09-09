package com.example.android.groceries;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
 * Created by user on 20-Oct-17.
 */

public class GroceryLoader extends AsyncTaskLoader<ArrayList<Grocery>> {
    private String mUrl;
    public GroceryLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading()  {
        forceLoad();
    }

    @Override
    public ArrayList<Grocery> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of recipes.
        ArrayList<Grocery> groceries = QueryGroceries.fetchGroceryData(mUrl);
        return groceries;
    }

    public static class QueryGroceries {
        private static final String LOG_TAG = QueryGroceries.class.getSimpleName();
        private QueryGroceries(){

        }
        public static ArrayList<Grocery> fetchGroceryData(String requestUrl) {
            URL url = createUrl(requestUrl);

            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
                if(jsonResponse == null){
                    return null;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making Http request",e);
            }

            return extractFeatureFromJson(jsonResponse);
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
                urlConnection.setReadTimeout(20000 /* milliseconds */);
                urlConnection.setConnectTimeout(30000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                    return null;
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
        private static ArrayList<Grocery> extractFeatureFromJson(String groceryJSON) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(groceryJSON)) {
                return null;
            }

            ArrayList<Grocery> groceries = new ArrayList<Grocery>();
            // Try to parse the JSON response string. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.

            try {
                JSONObject baseObject = new JSONObject(groceryJSON);
                //JSONArray groceryArray = baseObject.getJSONArray("items");
                //JSONObject currentGrocery = groceryArray.getJSONObject(0);
                    // Extract the value for the key called "title"
                    String title = baseObject.getString("item_name");
                    // Extract the value for the key called "image"
                    String image = baseObject.getString("brand_name");

                    Log.v("QueryGrocery","title = "+title);
                    // Create a new {@link Recipe} object with the id, title,used,missed
                    // Add the new {@link Recipe} to the list of recipes.
                    groceries.add(new Grocery(title,image));

            }
            catch (JSONException e) {
                Log.e("QueryRecipes", "Problem parsing the recipe JSON results", e);
            }

            return groceries;
        }
    }
}
