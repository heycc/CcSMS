package com.heycc.ccsms.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cc on 12/2/15.
 */
public class Topic {
    private final Context context;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private TopicAdapter mga;

    public Topic(Context context) {
        this.context = context;
        TopicHelper helper = new TopicHelper(context);
        dbWrite = helper.getReadableDatabase();
        dbRead = helper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TopicEntity.COLUMN_NAME, "xi");
        cv.put(TopicEntity.COLUMN_RECENT_MSG, "Lo");
        cv.put(TopicEntity.COLUMN_RECENT_TIME, System.currentTimeMillis() - 120000);
        dbWrite.insert(TopicEntity.TABLE_NAME, null, cv);

        mga = new TopicAdapter(context,
                dbRead.query(TopicEntity.TABLE_NAME, null, null, null, null, null,
                        TopicEntity.COLUMN_RECENT_TIME + " desc"),
                0);
    }

    public ListView addAdapterTo(ListView view) {
        view.setAdapter(mga);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                context.startActivity(new Intent(context, ViewTopicActivity.class));
            }
        });
        return view;
    }

    public void addMessage(Cursor cursor) {
        List currentTopics = new ArrayList<TopicHolder>();
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return;
        }

        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            long time = cursor.getLong(cursor.getColumnIndex("date"));
        }
    }

    private class TopicHolder extends TopicEntity {
        private int _id;
        private String recent_msg;
        private long recent_time;
        private String codition;

        public TopicHolder(int id, String body, long time, String condition) {
            this._id = id;
            this.recent_msg = body;
            this.recent_time = time;
            this.codition = condition;
        }

        @Override
        boolean matchMessage(String address, String body) {
            return false;
        }
    }
}
