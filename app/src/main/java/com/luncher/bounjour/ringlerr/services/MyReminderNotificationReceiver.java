package com.luncher.bounjour.ringlerr.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class MyReminderNotificationReceiver extends BroadcastReceiver {

    private Intent service1;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(service1 == null) {
            String message = intent.getExtras().getString("message");
            String formattedDate = intent.getExtras().getString("formattedDate");
            String shared_with = intent.getExtras().getString("shared_with");
            Long time_and_date = intent.getLongExtra("timestamp", 0);

            service1 = new Intent(context, SchedularNotificationAlarmService.class);
            service1.putExtra("message", message);
            service1.putExtra("formattedDate", formattedDate);
            service1.putExtra("timestamp", time_and_date);
            service1.putExtra("shared_with", shared_with);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, new Intent(context, SchedularNotificationAlarmService.class));
            } else {
                context.startService(service1);
            }
        }
    }
}
