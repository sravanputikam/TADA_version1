/*
  An  object that contains information related to a recipe of a food item.
 */
package com.example.android.HelperClasses;


public class Method {

    private String[] mIngredients;
    private String[] mInstructions;
    private String mImgaeUrl;
    private String[] mNutrients;
    private String mPrepTime;
    private String sourceUrl;
    //Stores the ingredients list ,instructions,image url of
    // the recipe of a food item from the QueriesMethod activity.
    public Method(String[] Ingredients, String[] Instructions, String ImageUrl,int ingrSize, int instrSize, String[] Nutrients, String Time, String source ){
        mIngredients = new String[ingrSize];
        mNutrients = new String[8];
        for(int i =0; i < ingrSize; i++){
            mIngredients[i] = Ingredients[i];
        }

        if(instrSize == 1) {
            mInstructions = new String[1];
            mInstructions[0] = Instructions[0];
        }
        else
        {
            mInstructions = new String[instrSize-1];
            for (int i = 0; i < instrSize-1; i++) {
                mInstructions[i] = Instructions[i];
            }

        }
        for(int i =0; i < 8; i++){
            mNutrients[i] = Nutrients[i];
        }
        mImgaeUrl = ImageUrl;
        mPrepTime = Time;
        sourceUrl = source;
    }

    public String getmIngredients(int n){
        return mIngredients[n];
    }
    public int getmIngredientsLength() { return mIngredients.length;}
    public String getmInstructions(int n){
        return mInstructions[n];
    }
    public int getmInstructionLength() { return mInstructions.length;}
    public String getmImgaeUrl(){
        return mImgaeUrl;
    }
    public String getmNutrients(int n){
        return mNutrients[n];
    }
    public int getmNutrientsLength() { return mNutrients.length;}
    public String getmPrepTime(){
        return mPrepTime;
    }
    public String getSourceUrl(){
        return sourceUrl;
    }


}
