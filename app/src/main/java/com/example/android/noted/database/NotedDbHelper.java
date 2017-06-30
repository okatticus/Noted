package com.example.android.noted.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Apoorva on 6/28/2017.
 */

public class NotedDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "NotedDb.db";
    public static final int DB_VERSION = 1;

    public NotedDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableClass.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableClass.onUpgrade(db, oldVersion, newVersion);
    }
}
