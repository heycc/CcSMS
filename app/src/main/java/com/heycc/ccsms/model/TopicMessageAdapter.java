package com.heycc.ccsms.model;

import android.content.Context;
import android.database.Cursor;
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
        // views in list
        ImageView iconView = (ImageView) view.findViewById(R.id.msg_icon);
        TextView timeView = (TextView) view.findViewById(R.id.msg_time);
        TextView addressView = (TextView) view.findViewById(R.id.msg_address);
        TextView textView = (TextView) view.findViewById(R.id.msg_text);

        // time view layout
        RelativeLayout.LayoutParams timeViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        timeViewLayout.setMargins(0,
                (int) context.getResources().getDimension(R.dimen.msg_time_margin_top),
                0,
                0);
        textView.setLayoutParams(timeViewLayout);
        timeView.setText(TopicMessageEntity.getNiceTime(cursor.getLong(cursor.getColumnIndex("date"))));

        // icon view layout
        RelativeLayout.LayoutParams iconViewLayout = new RelativeLayout.LayoutParams(
                (int) context.getResources().getDimension(R.dimen.msg_icon_length),
                (int) context.getResources().getDimension(R.dimen.msg_icon_length));
        iconViewLayout.setMargins((int) context.getResources().getDimension(R.dimen.msg_icon_margin_left),
                0,
                (int) context.getResources().getDimension(R.dimen.msg_icon_margin_right),
                0);
        iconViewLayout.addRule(RelativeLayout.BELOW, R.id.msg_time);
        iconView.setLayoutParams(iconViewLayout);

        // address view layout
        RelativeLayout.LayoutParams addressViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        addressViewLayout.addRule(RelativeLayout.ALIGN_TOP, R.id.msg_icon);
        addressViewLayout.addRule(RelativeLayout.RIGHT_OF, R.id.msg_icon);
        addressView.setLayoutParams(addressViewLayout);
        addressView.setText(cursor.getString(cursor.getColumnIndex("address")));

        // text view layout
        RelativeLayout.LayoutParams textViewLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewLayout.addRule(RelativeLayout.RIGHT_OF, R.id.msg_icon);
        textViewLayout.addRule(RelativeLayout.BELOW, R.id.msg_address);
        textViewLayout.setMargins(0,
                (int) context.getResources().getDimension(R.dimen.msg_text_margin_top),
                (int) context.getResources().getDimension(R.dimen.msg_text_margin_side),
                0);
        textView.setLayoutParams(textViewLayout);
        textView.setText(cursor.getString(cursor.getColumnIndex("body")));
    }


}
