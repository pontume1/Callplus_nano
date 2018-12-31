package com.luncher.bounjour.ringlerr.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.activity.ReminderDetail;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.luncher.bounjour.ringlerr.activity.MyDexApplication.CHANNEL_ID;

public class SchedularNotificationAlarmService extends Service {

    String message;
    String formattedDate;
    String shared_with;
    Long time_and_date;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                message = bundle.getString("message");
                formattedDate = bundle.getString("formattedDate");
                shared_with = bundle.getString("shared_with");
                time_and_date = intent.getLongExtra("timestamp", 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Intent notificationIntent = new Intent(this, SchedularNotificationAlarmService.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this,
                            1000994, notificationIntent, 0);

                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                            //.setContentTitle("Example Service")
                            //.setContentText("")
                            .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                            .setContentIntent(pendingIntent)
                            .build();

                    startForeground(1000994, notification);
                }

                setAlarm("You have an upcoming reminder");
            }
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
    }

    public void setAlarm(String notiMgs){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ringlerr Reminder", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(SchedularNotificationAlarmService.this, NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(appName+" reminder")
                .setContentText(notiMgs)
                .setContentInfo("Info");

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(SchedularNotificationAlarmService.this, ReminderDetail.class);
        notificationIntent.putExtra("message", message);
        notificationIntent.putExtra("formattedDate", formattedDate);
        notificationIntent.putExtra("timestamp", time_and_date);
        notificationIntent.putExtra("shared_with", shared_with);
        //notificationIntent.setAction(Intent.ACTION_MAIN);
        //notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(SchedularNotificationAlarmService.this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }
}
