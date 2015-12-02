package com.heycc.ccsms.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

/**
 * Created by cc on 12/2/15.
 */
public class MessageGroup {
    private final Context context;
    private MessageGroupHelper dbH;
    private SQLiteDatabase db;
    private MessageGroupAdapter mga;

    public MessageGroup(Context context) {
        this.context = context;
        dbH = new MessageGroupHelper(context);
        db = dbH.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MessageGroupEntity.COLUMN_GROUP_ID, 1);
        cv.put(MessageGroupEntity.COLUMN_GROUP_NAME, "Xi");
        cv.put(MessageGroupEntity.COLUMN_RECENT_MSG, "LoveのSeason");
        cv.put(MessageGroupEntity.COLUMN_RECENT_TIME, System.currentTimeMillis() - 10000);
        db.insert(MessageGroupEntity.TABLE_NAME, null, cv);

        mga = new MessageGroupAdapter(context,
                db.query(MessageGroupEntity.TABLE_NAME, null, null, null, null, null, null),
                0);
    }

    public ListView addAdapterTo(ListView view) {
        view.setAdapter(mga);
        return view;
    }
}
