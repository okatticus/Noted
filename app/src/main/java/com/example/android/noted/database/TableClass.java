package com.example.android.noted.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Apoorva on 6/28/2017.
 */

public class TableClass {
    //Separate table class
    //Db
    public static final String TABLE_NAME = "Note";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DESCRIPTION = "description";
    //Db creation SQL Statement
    private static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME + " ( "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_TITLE + " TEXT NOT NULL"
            + " );";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DATABASE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.v("TableClass : ", "Upgrading database version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
