package com.heycc.ccsms.model;

import android.provider.BaseColumns;

/**
 * Created by cc on 12/2/15.
 */
abstract class MessageGroupEntity implements BaseColumns {
    public static final String TABLE_NAME = "message_group";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_GROUP_NAME = "group_name";
    public static final String COLUMN_GROUP_ICON = "group_icon";
    public static final String COLUMN_UPDATE = "update_time";
    public static final String COLUMN_RECENT_MSG = "recent_msg";
    public static final String COLUMN_RECENT_TIME = "recent_time";
    public static final String COLUMN_RINGTONE_URI = "ringtone_uri";

    static String getCreateSQL() {
        String sql = "";
        sql += "CREATE TABLE " + MessageGroupEntity.TABLE_NAME + "(" +
                MessageGroupEntity._ID + " INTEGER PRIMARY KEY," +
                MessageGroupEntity.COLUMN_GROUP_ID + " TEXT," +
                MessageGroupEntity.COLUMN_GROUP_NAME + " TEXT," +
                MessageGroupEntity.COLUMN_GROUP_ICON + " TEXT," +
                MessageGroupEntity.COLUMN_UPDATE + " INTEGER," +
                MessageGroupEntity.COLUMN_RECENT_MSG + " TEXT," +
                MessageGroupEntity.COLUMN_RECENT_TIME + " INTEGER," +
                MessageGroupEntity.COLUMN_RINGTONE_URI + " TEXT" +
                ")";
        return sql;
    }

    static String getDropSQL() {
        return "DROP TABLE IF EXISTS " + MessageGroupEntity.TABLE_NAME;
    }
}
