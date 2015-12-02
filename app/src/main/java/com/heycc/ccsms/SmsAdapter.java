package com.heycc.ccsms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by cc on 11/28/15.
 * <p/>
 * _id:48607
 * thread_id:495
 * association_id:1448719653918
 * address:10690179090369
 * person:null
 * date:1448719653000
 * date_sent:0
 * protocol:0
 * read:1
 * status:-1
 * type:1
 * reply_path_present:0
 * subject:null
 * body:【豌豆荚】[RECOVERY]zk一致性监控,monitor18.hy01,STA
 * service_center:+8613800100500
 * locked:0
 * error_code:0
 * report_date:0
 * port:0
 * seen:1
 * sync_ver:2
 * uuid:58ac2eaf-2e24-46a3-8f8b-7d2d275e1c71
 * group_msg_id:58ac2eaf-2e24-46a3-8f8b-7d2d275e1c71
 * imsi:460022108945687
 * is_favorite:0
 * sim_id:-1
 */
public class SmsAdapter extends CursorAdapter {
    // { "address", "date", "body" };
    // private String[] fromString;
    // {R.id.row_title, R.id.row_time, R.id.row_msg};
    // private int[] toViews;
    // private int listView;
    private Uri inbox = Uri.parse("content://sms/inbox");

    public SmsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = (TextView) view.findViewById(R.id.row_title);
        TextView tvMessage = (TextView) view.findViewById(R.id.row_msg);
        TextView tvTime = (TextView) view.findViewById(R.id.row_time);
        String title = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        String time = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        tvTitle.setText(title);
        tvTime.setText(time);
        tvMessage.setText(message);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_list_main, parent, false);
    }

    public String readableTime(String time) {
        Date now = new Date();
        Log.i("SmsAdapter", now.toString());
        return now.toString();
        /*
        today:  hh:mm
        yes:    昨天
        (1, 7): 星期x
        [7, thisyear): mm:dd
        [last yead, *): yyyy
         */
    }

}
