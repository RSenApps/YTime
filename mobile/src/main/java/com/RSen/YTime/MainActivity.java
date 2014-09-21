package com.RSen.YTime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

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
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        AlarmsDataSource dataSource = new AlarmsDataSource(this);
        List<Alarm> alarms = null;
        try {
            dataSource.open();
            alarms = dataSource.getAllAlarms();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(this, alarms);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);
    }

}
