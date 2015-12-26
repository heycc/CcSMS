package com.heycc.ccsms.model;

import android.provider.BaseColumns;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

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
                TopicMessageEntity.COLUMN_TOPIC_ID + " INTEGER," +
                TopicMessageEntity.COLUMN_SMS_ID + " INTEGER," +
                TopicMessageEntity.COLUMN_TIME + " INTEGER," +
                TopicMessageEntity.COLUMN_BOX + " TEXT" +
                ")";
        return sql;
    }

    static String getDropSQL() {
        return "DROP TABLE IF EXISTS " + TopicMessageEntity.TABLE_NAME;
    }

    static String getNiceTime(long millis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        return calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CHINA)
                + calendar.get(Calendar.DAY_OF_MONTH) + "日"
                + String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
                + ":"
                + String.format("%02d", calendar.get(Calendar.MINUTE));
    }
}
