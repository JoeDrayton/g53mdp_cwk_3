package com.example.g53mdp_cwk_3;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AllIngredients extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ingredients);
        // Queries the ingredients table and gets all entries
        String[] columns = {
                ContentContract._ID,
                ContentContract.INGREDIENTNAME
        };
        Cursor c = getContentResolver().query(ContentContract.INGREDIENTS_URI, columns, null, null, null);

        String colsToDisplay[] = new String[]{
                ContentContract._ID,
                ContentContract.INGREDIENTNAME
        };
        int[] colResIds = new int[]{
                R.id._id,
                R.id.ingredientname
        };
        // Sets the list view adapter to the cursor and relevant columns
        ListView lv = findViewById(R.id.fullIngredients);
        lv.setAdapter(new SimpleCursorAdapter(this, R.layout.ingredients, c, colsToDisplay, colResIds, 0));
        Log.d("g53mdp", DatabaseUtils.dumpCursorToString(c));
    }
}
