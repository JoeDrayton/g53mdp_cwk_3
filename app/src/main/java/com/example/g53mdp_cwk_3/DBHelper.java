package com.example.g53mdp_cwk_3;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        Log.d("g53mdp", "DBHelper made");
    }

    // Creates the three tables and inserts some dummy data into the database
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE recipes (" +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(128) NOT NULL, " +
                "instructions VARCHAR(128) NOT NULL, " +
                "rating INTEGER" +
                "); ");
        db.execSQL("CREATE TABLE ingredients ( " +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "ingredientname VARCHAR(128) NOT NULL" +
                "); ");
        db.execSQL("CREATE TABLE recipe_ingredients ( " +
                "recipe_id INT NOT NULL, " +
                "ingredient_id INT NOT NULL, " +
                "CONSTRAINT fk1 FOREIGN KEY (recipe_id) REFERENCES recipes (_id), " +
                "CONSTRAINT fk2 FOREIGN KEY (ingredient_id) REFERENCES ingredients (_id), " +
                "CONSTRAINT _id PRIMARY KEY (recipe_id, ingredient_id) " +
                "); ");

        db.execSQL("INSERT INTO recipes (name, instructions, rating) VALUES ('gloob', 'make it', 5)");
        db.execSQL("INSERT INTO recipes (name, instructions, rating) VALUES ('hello', 'd it', 4)");
        db.execSQL("INSERT INTO recipes (name, instructions, rating) VALUES ('food', 'ddd it', 3)");
        db.execSQL("INSERT INTO recipes (name, instructions, rating) VALUES ('other stuff', 'ddd it', 2)");

        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('Chunken')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (1, 1)");
        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('Onon')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (1, 2)");
        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('Choriz')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (1, 3)");
        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('Pep')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (1, 4)");
        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('Rickle')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (1, 5)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        onCreate(db);
    }
}
