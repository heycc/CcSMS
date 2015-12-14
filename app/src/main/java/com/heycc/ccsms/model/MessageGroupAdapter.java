package com.heycc.ccsms.model;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.heycc.ccsms.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by cc on 12/1/15.
 */
public class MessageGroupAdapter extends CursorAdapter {
    public MessageGroupAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_list_main, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = (TextView) view.findViewById(R.id.row_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.row_msg);
        TextView tvTime = (TextView) view.findViewById(R.id.row_time);

        tvTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(MessageGroupEntity.COLUMN_GROUP_NAME)));
        tvMsg.setText(cursor.getString(cursor.getColumnIndexOrThrow(MessageGroupEntity.COLUMN_RECENT_MSG)));
        tvTime.setText(getNiceTime(cursor.getLong(cursor.getColumnIndexOrThrow(MessageGroupEntity.COLUMN_RECENT_TIME))));
    }

    public String getNiceTime(long millis) {
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
            return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
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
}
