/*
  An  object that contains information related to a single food item.
 */
package com.example.android.HelperClasses;
import android.util.Log;

/**
 * Created by user on 23-Sep-17.
 */

public class Recipe {

    private String recipeName ="";
    private int mId;
    private String mUsed;
    private String mMissed;
    private String mUrl;
    private String mImageUrl;
    private String mCalories;
    private String mProtein;
    private String mFat;
    private String mCarbs;
    //Stores the id ,title, used ingredients, missed ingredients, image url,
    // calories,protein,fat, and carbs of the food items from the QueriesRecipe activity.
    public Recipe(int id, String title, String used, String missed, String imageUrl, String calories,String protein, String fat, String carbs){
        this.mId = id;
        this.recipeName = title;
        this.mUsed = used;
        this.mMissed = missed;
        this.mImageUrl = imageUrl ;
        this.mCalories = calories;
        this.mCarbs = carbs;
        this.mProtein = protein;
        this.mFat = fat;
        Log.v("Recipe","title = "+recipeName);
    }

    public int getmId(){
        return mId;
    }
    public String getTitle(){
        return recipeName;
    }

    public String getmUsed(){
        return mUsed;
    }

    public String getmMissed(){
        return mMissed;
    }

    public String getmImageUrl(){
        return mImageUrl;
    }
    public String getmCalories() {
        return mCalories;
    }
}
