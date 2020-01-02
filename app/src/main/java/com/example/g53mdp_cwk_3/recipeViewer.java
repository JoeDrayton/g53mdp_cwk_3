package com.example.g53mdp_cwk_3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;

import java.util.List;

public class recipeViewer extends AppCompatActivity {
    private boolean newRecipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);
        EditText ingredients = findViewById(R.id.newIngredients);
        Bundle bundle = getIntent().getExtras();
        newRecipe = bundle.getBoolean("newRecipe");
        // Either loads recipe or loads blank version for new entry
        if(!newRecipe){
            int recipeId = bundle.getInt("recipe id");
            populateFields(recipeId);
            populateRecipeIngredients(recipeId);
            ingredients.setVisibility(View.GONE);
        }else {
            ingredients.setVisibility(View.VISIBLE);
        }
    }


    public void populateFields(final int recipeId) {
        // queries the database and populates the fields with relevant info
        String[] columns = new String[] {
                ContentContract.NAME,
                ContentContract.INSTRUCTIONS,
                ContentContract.RATING
        };
        final String[] args = new String[] {
                String.valueOf(recipeId)
        };
        final String whereClause = ContentContract._ID + "= ?";
        Cursor c = getContentResolver().query(ContentContract.RECIPES_URI, columns, whereClause, args, null);
        //Log.d("g53mdp", DatabaseUtils.dumpCursorToString(c));
        if(c.moveToFirst()){
            EditText name = findViewById(R.id.name);
            String recipeName = c.getString(0);
            name.setText(recipeName);
            EditText instructions = findViewById(R.id.instructions);
            String recipeInstructions = c.getString(1);
            instructions.setText(recipeInstructions);
            int recipeRating = c.getInt(2);
            SeekBar rating = findViewById(R.id.rating);
            rating.setProgress(recipeRating);
            // updates the rating in the content provider when
            rating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateRating(progress, recipeId);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }
    public void addNewRecipe(){
        // Extracts entries in all the fields
        final EditText nameField = findViewById(R.id.name);
        final EditText instructionsField = findViewById(R.id.instructions);
        final EditText ingredientsField = findViewById(R.id.newIngredients);
        final SeekBar ratingField = findViewById(R.id.rating);
        String name = nameField.getText().toString();
        String instructions = instructionsField.getText().toString();
        Cursor c;
        String ingredients[] = ingredientsField.getText().toString().split("\\r?\\n");
        int rating = ratingField.getProgress();
        int ingredientID;

        // Inserts new values as a new recipe and extracts the recipeID
        ContentValues newRecipe = new ContentValues();
        newRecipe.put(ContentContract.NAME, name);
        newRecipe.put(ContentContract.INSTRUCTIONS, instructions);
        newRecipe.put(ContentContract.RATING, rating);
        Uri result = getContentResolver().insert(ContentContract.RECIPES_URI, newRecipe);
        int recipeID = (int) Long.parseLong(result.getLastPathSegment());

        // for each ingredient in the list check that there is no preexisting entry, if so merely link the recipe with the ingredient. If not put it in the ingredients table
        ContentValues newIngredients = new ContentValues();
        ContentValues recipeIngredients = new ContentValues();
        for(String ingredient : ingredients) {
            Log.d("g53mdp", ingredient);
            c = getContentResolver().query(ContentContract.INGREDIENTS_URI, null, ContentContract.INGREDIENTNAME + " = " + DatabaseUtils.sqlEscapeString(ingredient), null, null);
            if(c.getCount() == 0){
                newIngredients.put(ContentContract.INGREDIENTNAME, ingredient);
                result = getContentResolver().insert(ContentContract.INGREDIENTS_URI, newIngredients);
                ingredientID = (int) Long.parseLong(result.getLastPathSegment());
                newIngredients.clear();
            } else {
                ingredientID = queryIngredients(ingredient);
            }
            recipeIngredients.put(ContentContract.RECIPE_ID, recipeID);
            recipeIngredients.put(ContentContract.INGREDIENT_ID, ingredientID);
            getContentResolver().insert(ContentContract.RECIPE_INGREDIENTS, recipeIngredients);

        }

    }

    public int queryIngredients(String ingredientName){
        // Checks ingredient exists in the table
        String[] columns = {
                ContentContract._ID,
        };
        String whereClause = ContentContract.INGREDIENTNAME + "= ?";
        int ingredientId;
        Cursor c = getContentResolver().query(ContentContract.INGREDIENTS_URI, columns, whereClause, new String[] {ingredientName}, null);
        if(c.moveToFirst()){
            ingredientId = c.getInt(0);
        } else {
            ingredientId = -1;
            Log.d("g53mdp", "ingredient not found");
        }
        return ingredientId;
    }

    public void deleteRecipeIngredients(int recipeId){
        // deletes the relationship between recipe and ingredient then verifies the ingredient has no other relationships
        String[] columns = {
                ContentContract.RECIPE_ID,
                ContentContract.INGREDIENT_ID
        };
        String whereClause = ContentContract.RECIPE_ID + "= ?";
        String whereClause2 = ContentContract.INGREDIENT_ID + "= ?";
        Cursor i;
        Cursor c = getContentResolver().query(ContentContract.RECIPE_INGREDIENTS, columns, whereClause, new String[]{String.valueOf(recipeId)}, null);
        Log.d("g53mdp", DatabaseUtils.dumpCursorToString(c));
        getContentResolver().delete(ContentContract.RECIPE_INGREDIENTS, ContentContract.RECIPE_ID + "=" + recipeId, null);
        if(c.moveToFirst()){
            do {
                int ingredientId = c.getInt(2);
                i = getContentResolver().query(ContentContract.RECIPE_INGREDIENTS_SPECIAL, columns, whereClause2, new String[]{String.valueOf(ingredientId)}, null);
                Log.d("g53mdp", DatabaseUtils.dumpCursorToString(i));
                if (i.getCount() == 0) {
                    getContentResolver().delete(ContentContract.INGREDIENTS_URI, ContentContract._ID + "=" + ingredientId, null);
                }
            } while(c.moveToNext());
        }
    }


    public void updateRating(int rating, int id){
        // Updates the rating
        ContentValues values = new ContentValues();
        values.put("rating", rating);
        getContentResolver().update(ContentContract.RECIPES_URI, values, ContentContract._ID+"=?", new String[]{String.valueOf(id)});
    }
    public void populateRecipeIngredients(int recipeId) {
        // Populates the list view with the recipe ingredients
        String [] id = { String.valueOf(recipeId) };
        String[] columns = {ContentContract.INGREDIENTNAME};
        String whereClause = ContentContract.RECIPE_ID + "= ?";
        Cursor c = getContentResolver().query(ContentContract.RECIPE_INGREDIENTS, columns, whereClause, id, null);
        String colsToDisplay[] = new String[]{
                ContentContract.INGREDIENTNAME
        };
        int[] colResIds = new int[]{
                R.id.ingredientname
        };
        ListView lv = findViewById(R.id.ingredientView);
        lv.setAdapter(new SimpleCursorAdapter(this, R.layout.ingredients, c, colsToDisplay, colResIds, 0));
        //Log.d("g53mdp", DatabaseUtils.dumpCursorToString(c));
    }

    public void deleteRecipe(View v){
        // Calls delete ingredients and deletes the recipe
        if(!newRecipe){
            Bundle bundle = getIntent().getExtras();
            int recipeId = bundle.getInt("recipe id");
            deleteRecipeIngredients(recipeId);
            getContentResolver().delete(ContentContract.RECIPES_URI, ContentContract._ID + "="+recipeId, null);

        }
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
    public void onReturn(View v){
        // either adds new recipe or returns to the main activity
        if(newRecipe){
            addNewRecipe();
        }
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
