package com.heycc.ccsms.model;

import android.provider.BaseColumns;

/**
 * Created by cc on 12/2/15.
 */
abstract class TopicEntity implements BaseColumns {
    public static final String TABLE_NAME = "topic";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_RECENT_MSG = "recent_msg";
    public static final String COLUMN_RECENT_TIME = "recent_time";
    public static final String COLUMN_RINGTONE_URI = "ringtone_uri";
    // condition e.g. address\t111\t222\nkeyword\tabc\tfef
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_HIDDEN = "hidden";

    public static final String CONDITION_ADDRESS = "address";
    public static final String CONDITION_KEYWORD = "keyword";
    public static final String CONDITION_LINE_SEP = "\n";
    public static final String CONDITION_VALUE_SEP = "\t";
    public static final int DEFAULT_ID = -1;

    static String getCreateSQL() {
        String sql = "";
        sql += "CREATE TABLE " + TopicEntity.TABLE_NAME + "(" +
                TopicEntity._ID + " INTEGER PRIMARY KEY," +
                TopicEntity.COLUMN_NAME + " TEXT," +
                TopicEntity.COLUMN_ICON + " TEXT," +
                TopicEntity.COLUMN_RECENT_MSG + " TEXT," +
                TopicEntity.COLUMN_RECENT_TIME + " INTEGER," +
                TopicEntity.COLUMN_RINGTONE_URI + " TEXT," +
                TopicEntity.COLUMN_CONDITION + " TEXT" +
                TopicEntity.COLUMN_HIDDEN + " TEXT" +
                ")";
        return sql;
    }

    static String getDropSQL() {
        return "DROP TABLE IF EXISTS " + TopicEntity.TABLE_NAME;
    }
}
