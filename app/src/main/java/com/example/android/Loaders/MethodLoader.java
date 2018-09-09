package com.example.android.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.HelperClasses.Method;
import com.example.android.HelperClasses.QueryMethod;

import java.util.ArrayList;

/*
  Loads the recipe of a food item by using an AsyncTask to perform the
  network request to the given URL.
*/

public class MethodLoader extends AsyncTaskLoader<ArrayList<Method>> {
    private String mUrl;
    public MethodLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Method> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of recipes.
        ArrayList<Method> methods = QueryMethod.fetchMethodData(mUrl);
        return methods;
    }
}
