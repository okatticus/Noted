package com.example.android.noted;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.noted.contentprovider.NotedContentProvider;
import com.example.android.noted.database.TableClass;

/**
 * Created by Apoorva on 6/29/2017.
 */

public class NotedDetailActivity extends Activity {
    private Spinner mCategory;
    private EditText mTitleText;
    private EditText mBodyText;

    private Uri uri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.edit_note);

        mCategory = (Spinner) findViewById(R.id.spinner);
        mTitleText = (EditText) findViewById(R.id.editText);
        mBodyText = (EditText) findViewById(R.id.editText3);
       // Log.v("NotedDetailActivity",mCategory.toString());
        Button confirmButton = (Button) findViewById(R.id.note_edit_button);

        Bundle extras = getIntent().getExtras();//retrieve the data
        uri = (bundle == null) ? null : (Uri) bundle.
                getParcelable(NotedContentProvider.CONTENT_ITEM_TYPE);//using key names
        if (extras != null) {
            uri = extras
                    .getParcelable(NotedContentProvider.CONTENT_ITEM_TYPE);
            fillData(uri);
        }
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void fillData(Uri uri) {
        String[] projection = {TableClass.COLUMN_TITLE,
                TableClass.COLUMN_DESCRIPTION,
                TableClass.COLUMN_CATEGORY,
                TableClass.COLUMN_ID};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String category = cursor.getString(cursor.getColumnIndexOrThrow(TableClass.COLUMN_CATEGORY));
         //   Log.v("Category: ",category);
            for (int i = 0; i < mCategory.getCount(); i++) {
                String s = (String) mCategory.getItemAtPosition(i);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }
            mTitleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TableClass.COLUMN_TITLE)));
            mBodyText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(TableClass.COLUMN_DESCRIPTION)));

            cursor.close();
        }
    }

    protected void onSavedStateInstant(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveState();
        bundle.putParcelable(NotedContentProvider.CONTENT_ITEM_TYPE, uri);
        // to send Java objects through intent
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String category = (String) mCategory.getSelectedItem();
        String title = mTitleText.getText().toString();
        String description = mBodyText.getText().toString();

        if (description.length() == 0 && title.length() == 0)
            return;
        ContentValues values = new ContentValues();
        values.put(TableClass.COLUMN_CATEGORY, category);
        values.put(TableClass.COLUMN_TITLE, title);
        values.put(TableClass.COLUMN_DESCRIPTION, description);
        Log.v("DetailActivity",category);
        Log.v("DetailActivity",title);
        if (uri == null) {
            uri = getContentResolver().insert(
                    NotedContentProvider.CONTENT_URI, values);
        } else {
            getContentResolver().update(uri, values, null, null);
        }
    }

    private void makeToast() {
       ;
        Toast.makeText(NotedDetailActivity.this, "No text in Title or description", Toast.LENGTH_LONG).show();
    }
}
