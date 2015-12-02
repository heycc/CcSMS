package com.heycc.ccsms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.heycc.ccsms.model.MessageGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Define ListView
    ListView mListView;

    ArrayList<Conversation> convs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MessageGroup mg = new MessageGroup(this);
        mListView = (ListView) findViewById(R.id.list);
        mg.addAdapterTo(mListView);


//        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
//                null, null, null, null);

        //Cursor cus = new MessageGroup(this).query();

//        mListView = (ListView) findViewById(R.id.list);
//        mListView.setAdapter(new SmsAdapter(this, cursor));
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> av, View v, int p, long id) {
//                Cursor item = (Cursor) av.getItemAtPosition(p);
//                Toast.makeText(getApplicationContext(),
//                        item.getString(item.getColumnIndexOrThrow("body")), Toast.LENGTH_LONG).show();
//            }
//        });
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
