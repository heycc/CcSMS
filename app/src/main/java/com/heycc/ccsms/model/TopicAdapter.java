package com.heycc.ccsms.model;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.heycc.ccsms.R;

/**
 * Created by cc on 12/1/15.
 */
public class TopicAdapter extends CursorAdapter {
    public TopicAdapter(Context context, Cursor cursor, int flags) {
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
        TextView tvUnread = (TextView) view.findViewById(R.id.row_unread);

        tvTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(TopicEntity.COLUMN_NAME)));
        tvMsg.setText(cursor.getString(cursor.getColumnIndexOrThrow(TopicEntity.COLUMN_RECENT_MSG)));
        tvTime.setText(getNiceTime(cursor.getLong(cursor.getColumnIndexOrThrow(TopicEntity.COLUMN_RECENT_TIME))));

        String unread = cursor.getString(cursor.getColumnIndex(TopicEntity.COLUMN_UNREAD));
        tvUnread.setText(Integer.parseInt(unread) == 0 ? "" : unread + "");
    }

    public String getNiceTime(long millis) {
        return TopicEntity.getNiceTime(millis);
    }
}
