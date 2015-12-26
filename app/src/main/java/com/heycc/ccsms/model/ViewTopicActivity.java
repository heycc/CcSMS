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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.heycc.ccsms.R;

public class ViewTopicActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int onceLoadLimit = 50;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_topic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_topic);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String topicTitle = intent.getStringExtra(TopicEntity.COLUMN_NAME);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(topicTitle);
        }

        db = new DBHelper(this).getReadableDatabase();

        this.loadMessage(intent.getStringExtra(TopicEntity._ID));

        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scroll_view));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    private void loadMessage(String topicId) {
        Cursor cursor = db.query(TopicMessageEntity.TABLE_NAME,
                null,
                TopicMessageEntity.COLUMN_TOPIC_ID + "=?",
                new String[]{"" + topicId},
                null,
                null,
                TopicMessageEntity.COLUMN_TIME + " desc",
                onceLoadLimit + "");
        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return;
        }

        String[] smsIds = new String[min(cursor.getCount(), onceLoadLimit)];
        for (int i = 0; i < smsIds.length && cursor.moveToNext(); i++) {
            smsIds[i] = cursor.getString(cursor.getColumnIndex(TopicMessageEntity.COLUMN_SMS_ID));
        }
        cursor.close();

        Cursor smsCursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
                null,
                "_ID in (" + makePlaceholders(smsIds.length) + ")",
                smsIds,
                "date asc");

        TextView textView = (TextView) findViewById(R.id.message_view);
        while (smsCursor.moveToNext()) {
            textView.append(TopicMessageEntity.getNiceTime(smsCursor.getLong(smsCursor.getColumnIndex("date")))
                    + "\n" + smsCursor.getString(smsCursor.getColumnIndex("body")) + "\n");
        }
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

    private int min(int a, int b) {
        return a > b ? b : a;
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
