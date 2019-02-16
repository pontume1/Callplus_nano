package com.luncher.bounjour.ringlerr;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.luncher.bounjour.ringlerr.activity.ReminderAlarmDialog;
import com.luncher.bounjour.ringlerr.activity.SchedulerAlarmDialog;
import com.luncher.bounjour.ringlerr.activity.SchedulerDialog;
import com.luncher.bounjour.ringlerr.model.Reminder;
import com.luncher.bounjour.ringlerr.model.Scheduler;

import java.util.List;

import androidx.core.content.ContextCompat;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by santanu on 19/11/17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // BOOT_COMPLETED” start Service
        if (intent.getAction().equals(ACTION)) {
            //Service
            Intent serviceIntent = new Intent(context, MessageService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }

            //restore alarms
            MyDbHelper myDbHelper = new MyDbHelper(context, null, 8);
            List<Reminder> sec = myDbHelper.getAllUpcomingReminder();

            if (sec.size() > 0) {
                //loop through contents
                for (int i = 0; i < sec.size(); i++) {
                    int id = sec.get(i).getId();
                    String message = sec.get(i).getMessage();
                    Long time = sec.get(i).getTime();
                    String shared_with = sec.get(i).getShared_with();
                    int remindAgo = sec.get(i).getRemindAgo();

                    Long noti_time;
                    if(remindAgo == 1){
                        noti_time = time - (60 * 60 * 1000);
                    }else {
                        noti_time = time - (remindAgo * 60 * 1000);
                    }

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(context, ReminderAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", time);
                    myIntent.putExtra("shared_with", shared_with.toString());
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, noti_time, pendingIntent);
                }
            }

            List<Scheduler> upcomingSchedular = myDbHelper.getAllUpcomingScheduler();
            if (upcomingSchedular.size() > 0) {
                //loop through contents
                for (int i = 0; i < upcomingSchedular.size(); i++) {

                    String message = upcomingSchedular.get(i).getMessage();
                    String phone_no = upcomingSchedular.get(i).getPhone();
                    String c_name = upcomingSchedular.get(i).getName();
                    String is_manual = upcomingSchedular.get(i).getIsManual();
                    String spinner_type = upcomingSchedular.get(i).getSpinnerType();
                    Long time = upcomingSchedular.get(i).getSTime();
                    Integer Id = upcomingSchedular.get(i).getSid();
                    String ago = upcomingSchedular.get(i).getAgo();

                    int remindAgo = Integer.parseInt(ago);
                    Long alarm_time = time;

                    if(remindAgo == 1){
                        alarm_time = time - (60 * 60 * 1000);
                    }else {
                        alarm_time = time - (remindAgo * 60 * 1000);
                    }

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(context, SchedulerAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", time);
                    myIntent.putExtra("phone", phone_no);
                    myIntent.putExtra("name", c_name);
                    myIntent.putExtra("is_manual", is_manual);
                    myIntent.putExtra("spinner_type", spinner_type);
                    myIntent.putExtra("id", Id);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, Id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);

                }
            }
        }

        int status = NetworkUtil.getConnectivityStatusString(context);
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if(status==NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
                //new ForceExitPause(context).execute();
                Boolean service_running = isMyServiceRunning(MessageService.class, context);
                if(!service_running) {
                    Intent serviceIntent = new Intent(context, MessageService.class);
                    context.stopService(serviceIntent);
                    Toast.makeText(context, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
                }

            }else{
                //new ResumeForceExitPause(context).execute();
                Intent serviceIntent = new Intent(context, MessageService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //mActivity.startForegroundService(cbIntent);
                    ContextCompat.startForegroundService(context, serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
                //Toast.makeText(context, "Service Started "+status, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
