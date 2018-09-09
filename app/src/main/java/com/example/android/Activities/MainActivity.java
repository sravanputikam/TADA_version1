package com.example.android.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.Fragments.RecipeActivity;
import com.example.android.R;
import com.example.android.data.RecipeContract;

public class MainActivity extends AppCompatActivity {


    //private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    /*
    *
    * Previous UI variables
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.recipesbook,
            R.drawable.grocery_bag_1
    };
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        RecipeActivity recipeActivity = new RecipeActivity();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.RecipiesFragment,recipeActivity)
                .commit();
        /*
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),MainActivity.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();*/
    }


    /*
    *
    * Funtioncs And Classes used for standalone application.
    * Changed UI for integration purposes, so not needed
    *
    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
    public static String POSITION = "POSITION";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipe_1, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_recipes:
                int rowsDeleted = getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI, null, null);
                Log.v("MainActivity", rowsDeleted + " rows deleted from recipe database");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*
    private void deleteAll() {
        int rowsDeleted;
        switch(tabLayout.getSelectedTabPosition()){
            case 0 :
                rowsDeleted = getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI, null, null);
                Log.v("MainActivity", rowsDeleted + " rows deleted from recipe database");
                break;
            case 1 :
                rowsDeleted = getContentResolver().delete(GroceryContract.GroceryEntry.CONTENT_GROCERY_URI, null, null);
                Log.v("MainActivity", rowsDeleted + " rows deleted from grocery database");
                break;
        }

    }*/

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private String[] tabTitles = {"Recipes" ,"Groceries"};

        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0 :
                    RecipeActivity recipeActivity = new RecipeActivity();
                    return recipeActivity;
                case 1 :
                    GroceryActivity groceryActivity = new GroceryActivity();
                    return groceryActivity;

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            Drawable image = ResourcesCompat.getDrawable(getResources(), tabIcons[position], null);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            // Replace blank spaces with image icon
            SpannableString sb = new SpannableString("   " + tabTitles[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }
        */
}