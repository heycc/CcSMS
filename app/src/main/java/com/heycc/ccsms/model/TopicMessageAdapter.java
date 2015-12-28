package com.heycc.ccsms.model;

import android.content.Context;
import android.database.Cursor;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heycc.ccsms.R;

/**
 * Created by cc on 12/28/15.
 */
public class TopicMessageAdapter extends CursorAdapter {
    public TopicMessageAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_list_message, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iconView = (ImageView) view.findViewById(R.id.msg_icon);
        TextView timeView = (TextView) view.findViewById(R.id.msg_time);
        TextView textView = (TextView) view.findViewById(R.id.msg_text);

        int iconLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                context.getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams ivLayout = new RelativeLayout.LayoutParams(iconLength, iconLength);
        ivLayout.addRule(RelativeLayout.BELOW, R.id.msg_time);
        iconView.setLayoutParams(ivLayout);

        RelativeLayout.LayoutParams textViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewLayout.setMargins(0, 0, iconLength, 0);
        textViewLayout.addRule(RelativeLayout.RIGHT_OF, R.id.msg_icon);
        textViewLayout.addRule(RelativeLayout.BELOW, R.id.msg_time);
        textView.setLayoutParams(textViewLayout);

        timeView.setText(TopicMessageEntity.getNiceTime(cursor.getLong(cursor.getColumnIndex("date"))));
        textView.setText(cursor.getString(cursor.getColumnIndex("body")));
    }
}
