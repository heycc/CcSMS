package com.heycc.ccsms.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by cc on 12/2/15.
 */
public class Topic {
    private static final int onceLoadLimit = 999;
    private final Context context;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private TopicAdapter mga;
    private ArrayList<TopicHolder> currentTopics = new ArrayList<TopicHolder>();
    private long lastest = 0;

    public Topic(Context context) {
        this.context = context;
        DBHelper helper = new DBHelper(context);
        dbWrite = helper.getReadableDatabase();
        dbRead = helper.getReadableDatabase();

        // Load topics into memory
        loadTopic();

        mga = new TopicAdapter(context,
                dbRead.query(TopicEntity.TABLE_NAME,
                        null,
                        TopicEntity.COLUMN_HIDDEN + " = ?",
                        new String[]{"0"},
                        null,
                        null,
                        TopicEntity.COLUMN_RECENT_TIME + " desc"),
                0);
    }

    /**
     * loadTopic
     * load topics into memory
     */
    private void loadTopic() {
        Cursor currentCursor = dbRead.query(TopicEntity.TABLE_NAME,
                null, null, null, null, null, null);
        Log.w("loadTopic", currentCursor.getCount() + "");
        if (currentCursor.getCount() > 0) {
            while (currentCursor.moveToNext()) {
                this.currentTopics.add(new TopicHolder(currentCursor.getLong(currentCursor.getColumnIndex(TopicEntity._ID)),
                        currentCursor.getString(currentCursor.getColumnIndex(TopicEntity.COLUMN_NAME)),
                        currentCursor.getString(currentCursor.getColumnIndex(TopicEntity.COLUMN_RECENT_MSG)),
                        currentCursor.getLong(currentCursor.getColumnIndex(TopicEntity.COLUMN_RECENT_TIME)),
                        currentCursor.getString(currentCursor.getColumnIndex(TopicEntity.COLUMN_CONDITION)),
                        currentCursor.getInt(currentCursor.getColumnIndex(TopicEntity.COLUMN_UNREAD)),
                        currentCursor.getString(currentCursor.getColumnIndex(TopicEntity.COLUMN_IDS))));
                setLastest(currentCursor.getLong(currentCursor.getColumnIndex(TopicEntity.COLUMN_RECENT_TIME)));
                Log.w("loadTopic", currentCursor.getString(currentCursor.getColumnIndex(TopicEntity.COLUMN_CONDITION)) + " "
                        + currentCursor.getInt(currentCursor.getColumnIndex(TopicEntity.COLUMN_UNREAD)));
            }
        }
        currentCursor.close();
    }

    /**
     * getCount
     *
     * @return current topic count
     */
    public int getCount() {
        return this.currentTopics.size();
    }

    public long getLastest() {
        return lastest;
    }

    private void setLastest(long time) {
        lastest = max(lastest, time);
    }

    private long max(long a, long b) {
        return a < b ? b : a;
    }

    /**
     * add adapter to the ListView
     *
     * @param view
     */
    public void addAdapterTo(ListView view) {
        view.setAdapter(mga);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent(context, ViewTopicActivity.class);
                intent.putExtra(TopicEntity.COLUMN_NAME, cursor.getString(cursor.getColumnIndex(TopicEntity.COLUMN_NAME)));
                intent.putExtra(TopicEntity._ID, cursor.getString(cursor.getColumnIndex(TopicEntity._ID)));
                markRead(cursor.getInt(cursor.getColumnIndex(TopicEntity._ID)));
                context.startActivity(intent);
            }
        });
    }

    /**
     * load message from cursor
     *
     * @param cursor
     */
    public void loadMessage(Cursor cursor) {
        Log.d("loadMessage", cursor.getCount() + "");
        if (cursor.getCount() == 0) {
            return;
        }

        long tmpLatest = lastest;
        while (cursor.moveToNext()) {
            // This function is called when cursor data changed,
            // however the start time of the cursor is not changed
            if (cursor.getLong(cursor.getColumnIndex("date")) <= lastest) {
                continue;
            }

            tmpLatest = max(cursor.getLong(cursor.getColumnIndex("date")), tmpLatest);

            boolean matched = false;

            boolean unread = false;
            if (cursor.getInt(cursor.getColumnIndex("read")) == 0) {
                unread = true;
            }

            for (TopicHolder tp : currentTopics) {
                // The new message match some topic, update recent_time and recent_msg if necessary
                if (tp.matchMessage(cursor.getString(cursor.getColumnIndex("address")),
                        cursor.getString(cursor.getColumnIndex("body")))) {
                    if (tp.recent_time < cursor.getLong(cursor.getColumnIndex("date"))) {
                        tp.recent_time = cursor.getLong(cursor.getColumnIndex("date"));
                        tp.recent_msg = cursor.getString(cursor.getColumnIndex("body"));
                    }
                    // Add to topicMessage
//                    addTopicMessage(tp._id,
//                            cursor.getLong(cursor.getColumnIndex("_ID")),
//                            cursor.getLong(cursor.getColumnIndex("date")),
//                            "inbox");
                    if (!tp.smsList.contains(cursor.getInt(cursor.getColumnIndex("_ID")))) {
                        tp.smsList.add(cursor.getInt(cursor.getColumnIndex("_ID")));
                    }
                    matched = true;
                    if (unread) {
                        tp.increaseUnread();
                    }
                }
            }

            // The new message match none, create a topic, and insert into db to get the id
            if (!matched) {
                // For new topic, insert into db to get its _ID value
                ContentValues cv = new ContentValues();
                cv.put(TopicEntity.COLUMN_RECENT_TIME, cursor.getLong(cursor.getColumnIndex("date")));
                cv.put(TopicEntity.COLUMN_RECENT_MSG, cursor.getString(cursor.getColumnIndex("body")));
                cv.put(TopicEntity.COLUMN_HIDDEN, "0");
                cv.put(TopicEntity.COLUMN_UNREAD, unread ? "1" : "0");
                long theId = dbWrite.insert(TopicEntity.TABLE_NAME, null, cv);

                currentTopics.add(new TopicHolder(theId,
                        null,
                        cursor.getString(cursor.getColumnIndex("body")),
                        cursor.getLong(cursor.getColumnIndex("date")),
                        TopicEntity.CONDITION_ADDRESS
                                + TopicEntity.CONDITION_VALUE_SEP
                                + cursor.getString(cursor.getColumnIndex("address")),
                        unread ? 1 : 0,
                        cursor.getInt(cursor.getColumnIndex("_ID")) + ""));

//                addTopicMessage(theId,
//                        cursor.getLong(cursor.getColumnIndex("_ID")),
//                        cursor.getLong(cursor.getColumnIndex("date")),
//                        "inbox");
            }
        }
        writeBackTopic();
        setLastest(tmpLatest);
        changeCursor();
        //cursor.close();
    }

    /**
     * write back to db, and reload cursor
     */
    public void writeBackTopic() {
        Log.w("writeBackTopic", "called");
        for (TopicHolder tp : currentTopics) {
            ContentValues cv = new ContentValues();
            cv.put(TopicEntity.COLUMN_NAME, tp.title);
            cv.put(TopicEntity.COLUMN_RECENT_TIME, tp.recent_time);
            cv.put(TopicEntity.COLUMN_RECENT_MSG, tp.recent_msg);
            cv.put(TopicEntity.COLUMN_UNREAD, tp.unread);
            cv.put(TopicEntity.COLUMN_HIDDEN, tp.hidden);

            Collections.sort(tp.smsList);
            while (tp.smsList.size() > onceLoadLimit) {
                tp.smsList.remove(0);
            }
            cv.put(TopicEntity.COLUMN_IDS, TextUtils.join(",", tp.smsList));

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

            dbWrite.update(TopicEntity.TABLE_NAME,
                    cv,
                    TopicEntity._ID + "=?",
                    new String[]{tp._id + ""});
        }
    }

    public void changeCursor() {
        mga.changeCursor(dbRead.query(TopicEntity.TABLE_NAME, null, null, null, null, null,
                TopicEntity.COLUMN_RECENT_TIME + " desc"));
    }

    private void markRead(int id) {
        for (TopicHolder tp : currentTopics) {
            if (tp._id == id) {
                tp.unread = 0;
                break;
            }
        }
//        writeBackTopic();
//        changeCursor();
    }

    private void addTopicMessage(long id, long smsId, long time, String box) {
        Cursor checkCursor = dbWrite.query(TopicMessageEntity.TABLE_NAME,
                new String[]{"count(*)"},
                TopicMessageEntity.COLUMN_TOPIC_ID + "=? and " + TopicMessageEntity.COLUMN_SMS_ID + "=?",
                new String[]{"" + id, "" + smsId},
                null, null, null);
        checkCursor.moveToFirst();
        // The message already in db, ignore
        if (checkCursor.getInt(0) > 0) {
            checkCursor.close();
            return;
        }
        checkCursor.close();

        ContentValues cv = new ContentValues();
        cv.put(TopicMessageEntity.COLUMN_TOPIC_ID, id);
        cv.put(TopicMessageEntity.COLUMN_SMS_ID, smsId);
        cv.put(TopicMessageEntity.COLUMN_TIME, time);
        cv.put(TopicMessageEntity.COLUMN_BOX, box);
        dbWrite.insert(TopicMessageEntity.TABLE_NAME, null, cv);
    }

    /**
     * Inner class, for holding a topic
     */
    private class TopicHolder extends TopicEntity {
        long _id;
        String title;
        String recent_msg;
        long recent_time;
        int hidden = 0;
        int unread = 0;
        ArrayList<String> addressList = new ArrayList<>();
        ArrayList<String> keywordList = new ArrayList<>();
        ArrayList<Integer> smsList = new ArrayList<>();

        public TopicHolder(long id, String title, String body, long time, String condition, int unread, String sms_ids) {
            this._id = id;
            this.recent_msg = body;
            this.recent_time = time;
            this.unread = unread;

            for (String sms_id : sms_ids.split(",")) {
                this.smsList.add(Integer.parseInt(sms_id));
            }

            if (condition != null) {
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
            } else {
                Log.w("TopicHolder", "condition null. This should never happens!");
            }

            if (title == null || title.trim().length() == 0) {
                String[] titleKeyword = TopicEntity.getSmartTitle(body);
                if (titleKeyword != null) {
                    this.title = titleKeyword[0];
                    this.keywordList.add(titleKeyword[1]);
                } else {
                    this.title = this.addressList.get(0);
                }
            } else {
                this.title = title;
            }
        }

        public void increaseUnread() {
            this.unread += 1;
            Log.w("increaseUnread", this.title + " " + unread + "");
        }

        public boolean matchMessage(String address, String body) {
            for (String addr : this.addressList) {
                if (addr.equals(address)) {
                    return true;
                }
            }
            for (String keyword : this.keywordList) {
                if (body.matches("(.*)" + keyword + "(.*)")) {
                    return true;
                }
            }
            return false;
        }
    }
}
