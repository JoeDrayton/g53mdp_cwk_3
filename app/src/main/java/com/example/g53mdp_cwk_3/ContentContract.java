package com.example.g53mdp_cwk_3;
import android.net.Uri;

public class ContentContract {
    // Different URI/field name strings for consistency with the content provider
    public static final String AUTHORITY = "com.example.g53mdp_cwk_3.MyContentProvider";

    public static final Uri RECIPES_URI = Uri.parse("content://"+AUTHORITY+"/recipes");
    public static final Uri INGREDIENTS_URI = Uri.parse("content://"+AUTHORITY+"/ingredients");
    public static final Uri RECIPE_INGREDIENTS = Uri.parse("content://"+AUTHORITY+"/recipe_ingredients");
    public static final Uri RECIPE_INGREDIENTS_SPECIAL = Uri.parse("content://"+AUTHORITY+"/recipe_ingredients_special");

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String INSTRUCTIONS = "instructions";
    public static final String RATING = "rating";

    public static final String INGREDIENTNAME = "ingredientname";

    public static final String RECIPE_ID = "recipe_id";
    public static final String INGREDIENT_ID = "ingredient_id";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyContentProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyContentProvider.data.text";
}
