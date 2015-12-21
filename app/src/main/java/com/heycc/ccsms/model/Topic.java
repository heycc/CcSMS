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
    private List<TopicHolder> currentTopics = new ArrayList<TopicHolder>();

    public Topic(Context context) {
        this.context = context;
        DBHelper helper = new DBHelper(context);
        dbWrite = helper.getReadableDatabase();
        dbRead = helper.getReadableDatabase();

        // Load topics into memory
        Cursor currentCursor = dbWrite.query(TopicEntity.TABLE_NAME, null, null, null, null, null,
                TopicEntity.COLUMN_RECENT_TIME + " desc");
        if (currentCursor.getCount() > 0) {
            currentCursor.moveToFirst();
            while (currentCursor.moveToNext()) {
                currentTopics.add(new TopicHolder(currentCursor.getInt(currentCursor.getColumnIndex(TopicEntity._ID)),
                        currentCursor.getString(currentCursor.getColumnIndex(TopicEntity.COLUMN_RECENT_MSG)),
                        currentCursor.getLong(currentCursor.getColumnIndex(TopicEntity.COLUMN_RECENT_TIME)),
                        currentCursor.getString(currentCursor.getColumnIndex(TopicEntity.COLUMN_CONDITION))));
            }
        }
        currentCursor.close();

        mga = new TopicAdapter(context,
                dbRead.query(TopicEntity.TABLE_NAME, null, null, null, null, null,
                        TopicEntity.COLUMN_RECENT_TIME + " desc"),
                0);
    }

    public int getCount() {
        return currentTopics.size();
    }

    public void addAdapterTo(ListView view) {
        view.setAdapter(mga);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent(context, ViewTopicActivity.class);
                intent.putExtra(TopicEntity.COLUMN_NAME, cursor.getString(cursor.getColumnIndex(TopicEntity.COLUMN_NAME)));
                intent.putExtra(TopicEntity._ID, cursor.getString(cursor.getColumnIndex(TopicEntity._ID)));
                context.startActivity(intent);
            }
        });
    }

    public void addMessage(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return;
        }
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            boolean matched = false;
            for (TopicHolder tp : currentTopics) {
                // The new message match some topic, update recent_time and recent_msg if necessary
                if (tp.matchMessage(cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getString(cursor.getColumnIndex("body")))) {
                    if (tp.recent_time < cursor.getLong(cursor.getColumnIndex("date"))) {
                        tp.recent_time = cursor.getLong(cursor.getColumnIndex("date"));
                        tp.recent_msg = cursor.getString(cursor.getColumnIndex("body"));
                    }
                    // Add to topicMessage
                    addTopicMessage(tp._id,
                            cursor.getLong(cursor.getColumnIndex("_ID")),
                            cursor.getLong(cursor.getColumnIndex("date")),
                            "inbox");
                    matched = true;
                }
            }
            // The new message match none, create a topic, and insert into db to get the id
            if (!matched) {
                ContentValues cv = new ContentValues();
                cv.put(TopicEntity.COLUMN_NAME, cursor.getString(cursor.getColumnIndex("address")));
                cv.put(TopicEntity.COLUMN_RECENT_TIME, cursor.getLong(cursor.getColumnIndex("date")));
                cv.put(TopicEntity.COLUMN_RECENT_MSG, cursor.getString(cursor.getColumnIndex("body")));
                long theId = dbWrite.insert(TopicEntity.TABLE_NAME, null, cv);

                currentTopics.add(new TopicHolder(theId,
                        cursor.getString(cursor.getColumnIndex("body")),
                        cursor.getLong(cursor.getColumnIndex("date")),
                        TopicEntity.CONDITION_ADDRESS + TopicEntity.CONDITION_VALUE_SEP +
                                cursor.getString(cursor.getColumnIndex("address"))));

                addTopicMessage(theId,
                        cursor.getLong(cursor.getColumnIndex("_ID")),
                        cursor.getLong(cursor.getColumnIndex("date")),
                        "inbox");
            }
        }

        writeBackTopic();

        mga.changeCursor(dbRead.query(TopicEntity.TABLE_NAME, null, null, null, null, null,
                TopicEntity.COLUMN_RECENT_TIME + " desc"));
    }

    private void writeBackTopic() {
        // Write back to topic db and reload list adapter
        for (TopicHolder tp : currentTopics) {
            ContentValues cv = new ContentValues();
            cv.put(TopicEntity.COLUMN_NAME, (tp.addressList).get(0));
            cv.put(TopicEntity.COLUMN_RECENT_TIME, tp.recent_time);
            cv.put(TopicEntity.COLUMN_RECENT_MSG, tp.recent_msg);

            // generate condition string
            String condition = "";
            String tmp = "";
            for (String address : tp.addressList) {
                tmp += TopicEntity.CONDITION_VALUE_SEP + address;
            }
            condition += TopicEntity.CONDITION_ADDRESS + TopicEntity.CONDITION_VALUE_SEP + tmp.trim();
            tmp = "";
            for (String keyword : tp.keywordList) {
                tmp += TopicEntity.CONDITION_VALUE_SEP + keyword;
            }
            condition += TopicEntity.CONDITION_LINE_SEP
                    + TopicEntity.CONDITION_KEYWORD
                    + TopicEntity.CONDITION_VALUE_SEP
                    + tmp.trim();
            cv.put(TopicEntity.COLUMN_CONDITION, condition);

            if (tp._id == TopicEntity.DEFAULT_ID) {
                dbWrite.insert(TopicEntity.TABLE_NAME, null, cv);
            } else {
                dbWrite.update(TopicEntity.TABLE_NAME,
                        cv,
                        TopicEntity._ID + "=?",
                        new String[]{tp._id + ""});
            }
        }
    }

    private void addTopicMessage(long id, long smsId, long time, String box) {
        ContentValues cv = new ContentValues();
        cv.put(TopicMessageEntity.COLUMN_TOPIC_ID, id);
        cv.put(TopicMessageEntity.COLUMN_SMS_ID, smsId);
        cv.put(TopicMessageEntity.COLUMN_TIME, time);
        cv.put(TopicMessageEntity.COLUMN_BOX, box);
        dbWrite.insert(TopicMessageEntity.TABLE_NAME, null, cv);
    }

    private class TopicHolder extends TopicEntity {
        long _id;
        String recent_msg;
        long recent_time;
        List<String> addressList = new ArrayList<>();
        List<String> keywordList = new ArrayList<>();

        public TopicHolder(long id, String body, long time, String condition) {
            this._id = id;
            this.recent_msg = body;
            this.recent_time = time;
            for (String line : condition.split(TopicEntity.CONDITION_LINE_SEP)) {
                String[] values = line.split(TopicEntity.CONDITION_VALUE_SEP);
                switch (values[0]) {
                    case TopicEntity.CONDITION_ADDRESS:
                        for (int i = 1; i < values.length; i++) {
                            this.addressList.add(values[i]);
                        }
                        break;
                    case TopicEntity.CONDITION_KEYWORD:
                        for (int i = 1; i < values.length; i++) {
                            this.keywordList.add(values[i]);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        public boolean matchMessage(String address, String body) {
            for (String addr : this.addressList) {
                if (addr.equals(address)) {
                    return true;
                }
            }
            for (String keyword : this.keywordList) {
                if (body.matches(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }
}
