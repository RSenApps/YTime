package com.RSen.YTime;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.melnykov.fab.FloatingActionButton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PebbleKit.isWatchConnected(this)) {
            startService(new Intent(this, PebbleListeningService.class));
        }
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add);
        floatingActionButton.attachToListView(expListView);
        View.OnClickListener addClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CreateAlarmActivity.class);
                startActivity(i);
            }
        };
        findViewById(R.id.add).setOnClickListener(addClickListener);
        findViewById(R.id.noalarms).setOnClickListener(addClickListener);
        findViewById(R.id.noalarmsText).setOnClickListener(addClickListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // get the listview


        AlarmsDataSource dataSource = new AlarmsDataSource(this);

        List<Alarm> alarms = null;
        try {
            dataSource.open();
            alarms = dataSource.getAllAlarms();
            if (alarms != null && alarms.size() > 0) {
                listAdapter = new ExpandableListAdapter(this, alarms, (ImageView) findViewById(R.id.noalarms), (TextView) findViewById(R.id.noalarmsText));

                // setting list adapter
                expListView.setAdapter(listAdapter);
                expListView.expandGroup(0);
                findViewById(R.id.noalarms).setVisibility(View.GONE);
                findViewById(R.id.noalarmsText).setVisibility(View.GONE);
            }
            else {
                findViewById(R.id.noalarms).setVisibility(View.VISIBLE);
                findViewById(R.id.noalarmsText).setVisibility(View.VISIBLE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.close();

    }
}
