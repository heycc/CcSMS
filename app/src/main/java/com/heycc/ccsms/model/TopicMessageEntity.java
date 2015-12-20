package com.heycc.ccsms.model;

import android.provider.BaseColumns;

/**
 * Created by cc on 12/21/15.
 */
abstract class TopicMessageEntity implements BaseColumns {
    public static final String TABLE_NAME = "topic_message";
    public static final String COLUMN_TOPIC_ID = "topic_id";
    public static final String COLUMN_SMS_ID = "sms_id";
    public static final String COLUMN_BOX = "box";
    public static final String COLUMN_TIME = "time";

    static String getCreateSQL() {
        String sql = "";
        sql += "CREATE TABLE " + TopicMessageEntity.TABLE_NAME + "(" +
                TopicMessageEntity._ID + " INTEGER PRIMARY KEY," +
                TopicMessageEntity.COLUMN_TOPIC_ID + " " +
                TopicMessageEntity.COLUMN_SMS_ID + " INTEGER," +
                TopicMessageEntity.COLUMN_BOX + " TEXT," +
                TopicMessageEntity.COLUMN_TIME + " INTEGER," +
                ")";
        return sql;
    }

    static String getDropSQL() {
        return "DROP TABLE IF EXISTS " + TopicMessageEntity.TABLE_NAME;
    }
}
