package com.example.g53mdp_cwk_3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    private DBHelper dbHelper = null;

    private static final UriMatcher uriMatcher;

    static {
        // URIs for different tables
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ContentContract.AUTHORITY, "recipes", 1);
        uriMatcher.addURI(ContentContract.AUTHORITY, "recipes/#",2);
        uriMatcher.addURI(ContentContract.AUTHORITY, "ingredients",3);
        uriMatcher.addURI(ContentContract.AUTHORITY, "ingredients/#",4);
        uriMatcher.addURI(ContentContract.AUTHORITY, "recipe_ingredients",5);
        uriMatcher.addURI(ContentContract.AUTHORITY, "recipe_ingredients/#",6);
        uriMatcher.addURI(ContentContract.AUTHORITY, "recipe_ingredients_special",8);
        uriMatcher.addURI(ContentContract.AUTHORITY, "*",7);
    }

    @Override
    public boolean onCreate() {
        Log.d("g53mdp", "contentProvider onCreate");
        this.dbHelper = new DBHelper(this.getContext(), "mydb", null, 8);
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Deletes from database depending on uri and selection
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numDeleted = 0;
        switch (uriMatcher.match(uri)){
            case 1:
                numDeleted = db.delete("recipes", selection, selectionArgs);
                break;
            case 3:
                numDeleted = db.delete("ingredients", selection, selectionArgs);
                break;
            case 5:
                numDeleted = db.delete("recipe_ingredients", selection, selectionArgs);
                break;
            default:
                break;
        }
        return numDeleted;
    }

    @Override
    public String getType(Uri uri) {
        String contentType;

        if(uri.getLastPathSegment() == null) {
            contentType = ContentContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = ContentContract.CONTENT_TYPE_SINGLE;
        }
        return contentType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // using uri puts content values into database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;
        switch (uriMatcher.match(uri)){
            case 1:
                tableName = "recipes";
                Log.d("g53mdp", "query uri mathched");
                break;
            case 3:
                tableName = "ingredients";
                break;
            case 5:
                tableName = "recipe_ingredients";
                break;
            default:
                tableName = "recipes";
                break;
        }
        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri, id);
        Log.d("g53mdp", nu.toString());
        getContext().getContentResolver().notifyChange(nu, null);
        return nu;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Using uri queries the database with specific selections
        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri)){
            case 2:
                selection = "_ID = " + uri.getLastPathSegment();
            case 1:
                Log.d("g53mdp", "recipe query");
                return db.query("recipes", projection, selection, selectionArgs, null, null, sortOrder);
            case 3:
                Log.d("g53mdp", "recipe ingredients query");
                return db.query("ingredients", projection, selection, selectionArgs, null, null, sortOrder);
            case 5:
                Log.d("g53mdp", "recipe ingredients query");
                return db.rawQuery("select r._id, r.name, ri.ingredient_id, i.ingredientname "+
                                "from recipes r "+
                                "join recipe_ingredients ri on (r._id = ri.recipe_id)"+
                                "join ingredients i on (ri.ingredient_id = i._id) where r._id == ?",
                                selectionArgs);
            case 8:
                Log.d("g53mdp", "recipe ingredients query");
                return db.rawQuery("select r._id, r.name, ri.ingredient_id, i.ingredientname "+
                                "from recipes r "+
                                "join recipe_ingredients ri on (r._id = ri.recipe_id)"+
                                "join ingredients i on (ri.ingredient_id = i._id) where i._id == ?",
                        selectionArgs);
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri)){
            case 1:
                // updates rating basically
                return db.update("recipes", values, selection, selectionArgs);
            default:
                return 0;
        }
    }
}
