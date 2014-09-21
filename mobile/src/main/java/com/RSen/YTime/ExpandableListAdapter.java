package com.RSen.YTime;

/**
 * Created by Ryan on 9/20/2014.
 */
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Alarm> alarms;
    public ExpandableListAdapter(Context context, List<Alarm> alarms) {
        this._context = context;
        this.alarms = alarms;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return alarms.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Alarm alarm = (Alarm) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.activity_main_detail, null);
        }
        /*
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        */
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return alarms.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return alarms.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final Alarm alarm = (Alarm) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.activity_main_header, null);
        }
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getArriveHours());
        alarmTime.set(Calendar.MINUTE, alarm.getArriveMinutes());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String wakeupTimeString;
        if (alarm.getWakeupHours() + alarm.getWakeupMinutes() == 0)
        {
            wakeupTimeString = " (calculating...)";
        }
        else {
            Calendar wakeupTime = Calendar.getInstance();
            wakeupTime.set(Calendar.HOUR_OF_DAY, alarm.getWakeupHours());
            wakeupTime.set(Calendar.MINUTE, alarm.getWakeupMinutes());
            wakeupTimeString = " (est. " + sdf.format((alarmTime.getTime())) + ")";
        }

        ((TextView) convertView.findViewById(R.id.time)).setText(sdf.format(alarmTime.getTime()) + wakeupTimeString);
        ((TextView) convertView.findViewById(R.id.location)).setText("at " + alarm.getPlaceName());
        Switch enabledSwitch = (Switch) convertView.findViewById(R.id.enabled);
        enabledSwitch.setChecked(alarm.isEnabled());
        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    alarm.setEnabled(b);
                    AlarmsDataSource dataSource = new AlarmsDataSource(compoundButton.getContext());
                    try {
                        dataSource.open();
                        dataSource.updateAlarm(alarm);
                        AlarmHelper.setAlarms(compoundButton.getContext(), null);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                   dataSource.close();

            }
        });
        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmsDataSource dataSource = new AlarmsDataSource(view.getContext());
                try {
                    dataSource.open();
                    dataSource.deleteAlarm(alarm);
                    AlarmHelper.setAlarms(view.getContext(), null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                dataSource.close();
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}