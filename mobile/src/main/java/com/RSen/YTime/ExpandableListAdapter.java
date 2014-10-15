package com.RSen.YTime;

/**
 * Created by Ryan on 9/20/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    public List<Alarm> alarms;
    ImageView noAlarmImage;
    TextView noAlarmText;

    public ExpandableListAdapter(Context context, List<Alarm> alarms, ImageView noAlarmImage, TextView noAlarmText) {
        this._context = context;
        this.alarms = alarms;
        this.noAlarmImage = noAlarmImage;
        this.noAlarmText = noAlarmText;
    }

    private void updateNoAlarmStatus() {
        if (alarms != null && alarms.size() > 0) {
            noAlarmImage.setVisibility(View.GONE);
            noAlarmText.setVisibility(View.GONE);
        } else {
            noAlarmImage.setVisibility(View.VISIBLE);
            noAlarmText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Alarm getChild(int groupPosition, int childPosititon) {
        return alarms.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Alarm alarm = (Alarm) getChild(groupPosition, childPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.activity_main_detail, null);
        }
        final View view = convertView;
        ((CheckBox) convertView.findViewById(R.id.repeat)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    view.findViewById(R.id.repeatButtons).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.repeatButtons).setVisibility(View.GONE);

                }
            }
        });
        ((TextView) convertView.findViewById(R.id.ringtone)).setText(alarm.getRingtoneName());
        convertView.findViewById(R.id.ringtone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(alarm.getRingtoneURI()));
                ((Activity) _context).startActivityForResult(intent, groupPosition);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Alarm getGroup(int groupPosition) {
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
        if (alarm.getWakeupHours() + alarm.getWakeupMinutes() == 0) {
            wakeupTimeString = " (calculating...)";
        } else {
            Calendar wakeupTime = Calendar.getInstance();
            wakeupTime.set(Calendar.HOUR_OF_DAY, alarm.getWakeupHours());
            wakeupTime.set(Calendar.MINUTE, alarm.getWakeupMinutes());
            wakeupTimeString = " (est. " + sdf.format((wakeupTime.getTime())) + ")";
        }

        ((TextView) convertView.findViewById(R.id.time)).setText(sdf.format(alarmTime.getTime()) + wakeupTimeString);
        ((TextView) convertView.findViewById(R.id.time)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {
                        alarm.setArriveHours(i);
                        alarm.setArriveMinutes(i2);
                        alarm.setWakeupHours(0);
                        alarm.setWakeupMinutes(0);
                        notifyDataSetChanged();
                        AlarmsDataSource dataSource = new AlarmsDataSource(radialPickerLayout.getContext());
                        try {
                            dataSource.open();
                            dataSource.updateAlarm(alarm);
                            AlarmHelper.setAlarms(radialPickerLayout.getContext(), null);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        dataSource.close();
                    }
                }, alarm.getArriveHours(), alarm.getArriveMinutes(), false);
                dialog.setThemeDark(true);
                dialog.show(((MainActivity) _context).getFragmentManager(), "timepicker");
            }
        });

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
                alarms.remove(alarm);
                notifyDataSetChanged();
                AlarmsDataSource dataSource = new AlarmsDataSource(view.getContext());
                try {
                    dataSource.open();
                    dataSource.deleteAlarm(alarm);
                    AlarmHelper.setAlarms(view.getContext(), null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                dataSource.close();
                updateNoAlarmStatus();
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