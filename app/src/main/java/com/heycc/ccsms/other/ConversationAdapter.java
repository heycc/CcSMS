package com.heycc.ccsms.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.heycc.ccsms.R;

import java.util.ArrayList;

/**
 * Created by cc on 11/26/15.
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

    /*
    public static class ViewHolder {
        TextView title;
        TextView right;
    }*/

    public ConversationAdapter(Context context, ArrayList<Conversation> cons) {
        super(context, 0, cons);
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        // Get data from this pos
        Conversation conv = getItem(pos);
        // Check if an existing view is being reused, otherwise inflate the view
        //ViewHolder viewHolder;
        if (view == null) {
            //viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.row_list_main, viewGroup, false);
            //viewHolder.title = (TextView) view.findViewById(R.id.row_title);
            //viewHolder.right = (TextView) view.findViewById(R.id.row_right);
            //view.setTag(viewHolder);
        } else {
            //viewHolder = (ViewHolder) view.getTag();
        }
        // Populate data into template view
        //viewHolder.title.setText(conv.title);
        //viewHolder.right.setText(conv.updateTime);

        TextView tvTitle = (TextView) view.findViewById(R.id.row_title);
        TextView tvTime = (TextView) view.findViewById(R.id.row_time);

        tvTitle.setText(conv.title);
        tvTime.setText(conv.time);

        return view;
    }

}
