package com.example.android.noted.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android.noted.database.NotedDbHelper;
import com.example.android.noted.database.TableClass;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Apoorva on 6/28/2017.
 */

public class NotedContentProvider extends ContentProvider {

    public static final int NOTES = 1;
    public static final int NOTE_ID = 2;
    private static final String AUTHORITY = "com.example.android.noted.contentprovider";//fix
    private static final String BASE_PATH = "noted";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/noted";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/Note";
    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
      sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTE_ID);
    }

    private NotedDbHelper database;

    @Override
    public boolean onCreate() {
        database = new NotedDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);

        queryBuilder.setTables(TableClass.TABLE_NAME);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                break;
            case NOTE_ID:
                queryBuilder.appendWhere(TableClass.COLUMN_ID + " = "
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException();
        }
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);//for listener

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case NOTES:
                id = db.insert(TableClass.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        SQLiteDatabase db = database.getWritableDatabase();
        switch (uriType) {
            case NOTES:
                db.delete(TableClass.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(TableClass.TABLE_NAME,
                            TableClass.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(
                            TableClass.TABLE_NAME,
                            TableClass.COLUMN_ID + "=" + id
                                    + " AND " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsUpdated = 0;
        SQLiteDatabase db = database.getWritableDatabase();
        switch (uriType) {
            case NOTES:
                db.update(TableClass.TABLE_NAME, values, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(TableClass.TABLE_NAME,
                            values,
                            TableClass.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(
                            TableClass.TABLE_NAME,
                            values,
                            TableClass.COLUMN_ID + "=" + id
                                    + " AND " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        //Check if all columns are available
        String[] available = {
                TableClass.COLUMN_DESCRIPTION,
                TableClass.COLUMN_CATEGORY,
                TableClass.COLUMN_TITLE,
                TableClass.COLUMN_ID
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }

        }
    }
}

