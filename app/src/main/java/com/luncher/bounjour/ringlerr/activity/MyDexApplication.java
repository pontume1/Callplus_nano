package com.luncher.bounjour.ringlerr.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
//import com.google.firebase.database.FirebaseDatabase;
//import com.squareup.leakcanary.LeakCanary;

//import com.squareup.leakcanary.LeakCanary;

import com.luncher.bounjour.ringlerr.CallManager;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class MyDexApplication extends MultiDexApplication {

    public static final String CHANNEL_ID = "ringlerrServiceChannel";
    public static final String CHANNEL_ID_BOUND = "ringlerrBoundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        // Normal app init code...
        createNotificationChannel();
        createBoundNotificationChannel();

        CallManager.init(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Ringlerr Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void createBoundNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID_BOUND,
                    "Ringlerr Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
