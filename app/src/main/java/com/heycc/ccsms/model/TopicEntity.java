package com.heycc.ccsms.model;

import android.provider.BaseColumns;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final String COLUMN_UNREAD = "have_unread";

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
                TopicEntity.COLUMN_CONDITION + " TEXT," +
                TopicEntity.COLUMN_HIDDEN + " TEXT," +
                TopicEntity.COLUMN_UNREAD + " INTEGER" +
                ")";
        return sql;
    }

    static String getDropSQL() {
        return "DROP TABLE IF EXISTS " + TopicEntity.TABLE_NAME;
    }

    static String getNiceTime(long millis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);

        Calendar now = new GregorianCalendar();
        long nowMillis = System.currentTimeMillis();
        now.setTimeInMillis(nowMillis);

        Calendar midNightCalendar = Calendar.getInstance();
        midNightCalendar.set(Calendar.HOUR_OF_DAY, 0);
        midNightCalendar.set(Calendar.MINUTE, 0);
        midNightCalendar.set(Calendar.SECOND, 0);
        midNightCalendar.set(Calendar.MILLISECOND, 0);
        long midNight = midNightCalendar.getTimeInMillis();

        if (millis >= nowMillis) {
            // This shouldn't happen
            return millis + "";
        } else if (millis >= midNight) {
            return String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
                    + ":"
                    + String.format("%02d", calendar.get(Calendar.MINUTE));
        } else if (millis >= midNight - 24 * 60 * 60 * 1000) {
            return "昨天";
        } else if (millis >= midNight - 6 * 24 * 60 * 60 * 1000) {
            return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CHINA);
        } else if (calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR)) {
            return calendar.get(Calendar.YEAR) + "";
        } else {
            return calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CHINA) +
                    calendar.get(Calendar.DAY_OF_MONTH) + "日";
        }
    }

    static String getSmartTitle(String body) {
        String[] regs = new String[]{"^【(.*)】",
                "【(.*)】$",
                "^\\[(.*)\\]",
                "\\[(.*)\\]$",
                "【(.*)】",
                "\\[(.*)\\]"};
        for (String reg : regs) {
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
