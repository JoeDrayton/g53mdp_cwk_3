package com.example.g53mdp_cwk_3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    static boolean sortOrder = true;
    static boolean newRecipe = false;
    static final int ACTIVITY_TWO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayRecipies("");

    }

    public void displayRecipies(String sortOrder) {
        // Queries the recipe table and displays all recipes
        String columns[] = new String[]{
                ContentContract._ID,
                ContentContract.NAME,
                ContentContract.RATING
        };

        String colsToDisplay[] = new String[]{
                ContentContract._ID,
                ContentContract.NAME,
                ContentContract.RATING
        };

        int[] colResIds = new int[]{
                R.id._id,
                R.id.name,
                R.id.rating
        };
        Cursor c = getContentResolver().query(ContentContract.RECIPES_URI, columns, null, null, sortOrder);
        //Log.d("g53mdp", DatabaseUtils.dumpCursorToString(c));
        final ListView lv = findViewById(R.id.listView);
        lv.setAdapter(new SimpleCursorAdapter(this, R.layout.recipe, c, colsToDisplay, colResIds, 0));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("g53mdp", "position " + position + " id " + id);
                Intent intent = new Intent(MainActivity.this, recipeViewer.class);
                Bundle recipe = new Bundle();
                newRecipe = false;
                recipe.putBoolean("newRecipe", newRecipe);
                recipe.putInt("recipe id", (int) id);
                intent.putExtras(recipe);
                startActivityForResult(intent, ACTIVITY_TWO_REQUEST_CODE);
            }
        });
    }

    public void sortByRating(View v) {
        // depending on the sort order filters the recipes based on rating
        if (sortOrder == true) {
            displayRecipies("rating DESC");
            sortOrder = false;
        } else {
            displayRecipies("rating ASC");
            sortOrder = true;
        }
    }

    public void viewIngredients(View v) {
        // Starts the ingredient viewing activity
        Intent intent = new Intent(MainActivity.this, AllIngredients.class);
        startActivity(intent);
    }

    public void addNewRecipe(View v){
        // Puts a new recipe flag into the bundle and starts the recipeViewer activity
        newRecipe = true;
        Intent intent = new Intent(MainActivity.this, recipeViewer.class);
        Bundle recipe = new Bundle();
        recipe.putBoolean("newRecipe", newRecipe);
        intent.putExtras(recipe);
        startActivityForResult(intent, ACTIVITY_TWO_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if returning from activity two and the result is ok, reload the query
        if (requestCode == ACTIVITY_TWO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                displayRecipies("");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //do nothing
            }
        }
    }
}