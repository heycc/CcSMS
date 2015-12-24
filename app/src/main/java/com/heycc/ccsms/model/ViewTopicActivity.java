package com.heycc.ccsms.model;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.heycc.ccsms.R;

public class ViewTopicActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_topic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_topic);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String topicTitle = intent.getStringExtra(TopicEntity.COLUMN_NAME);
//        Log.w("VIEWTOPIC",""+intent.getStringExtra(TopicEntity._ID));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(topicTitle);
        }

        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        Cursor cursor = db.query(TopicMessageEntity.TABLE_NAME,
                null,
                TopicMessageEntity.COLUMN_TOPIC_ID + "=?",
                new String[]{"" + intent.getStringExtra(TopicEntity._ID)},
                null,
                null,
                null,
                "10");
        Log.w("DEBUG", "TopicId: " + intent.getStringExtra(TopicEntity._ID) + " CNT: " + cursor.getCount());
        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return;
        }

        String[] smsIds = new String[cursor.getCount()];
        for (int i = 0; cursor.moveToNext(); i++) {
            smsIds[i] = cursor.getString(cursor.getColumnIndex(TopicMessageEntity.COLUMN_SMS_ID));
        }
        cursor.close();

        Cursor smsCursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
                null,
                "_ID in (" + makePlaceholders(smsIds.length) + ")",
                smsIds,
                "date desc");
        StringBuilder sb = new StringBuilder();
        while (smsCursor.moveToNext()) {
            sb.append(smsCursor.getString(smsCursor.getColumnIndex("body")) + "\n");
        }
        ((TextView) findViewById(R.id.message_view)).setText(sb);
        smsCursor.close();
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_view_group:
                startActivity(new Intent(this, EditTopicActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getIntent();
        String topicId = intent.getStringExtra(TopicEntity._ID);
//        return new CursorLoader
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
