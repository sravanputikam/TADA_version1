package com.example.android.groceries;

/**
 * Created by user on 20-Oct-17.
 */

public class Grocery {

    private String mTitle;
    private String mImage;

    public Grocery(String title, String image){
        mImage = image;
        mTitle = title;
    }

    public String getmTitle(){
        return mTitle;
    }
    public String getmImage(){
        return mImage;
    }
}

