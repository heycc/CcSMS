package com.heycc.ccsms;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.heycc.ccsms.model.Topic;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int SMS_LOADER = 0;
    //Define ListView
    ListView mListView;
    Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        topic = new Topic(this);
        mListView = (ListView) findViewById(R.id.list_main);
        topic.addAdapterTo(mListView);

        getLoaderManager().initLoader(SMS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SMS_LOADER:
                Log.w("onCreateLoader", topic.getLastest() + "");
                return new CursorLoader(this,
                        Uri.parse("content://sms/inbox"),
                        null,
                        "date > ?",
                        new String[]{"" + topic.getLastest()},
                        "date desc");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        topic.loadMessage(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.w("onStop", "called");
        topic.writeBackTopic();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w("onResume", "called");
        topic.changeCursor();
    }
}
