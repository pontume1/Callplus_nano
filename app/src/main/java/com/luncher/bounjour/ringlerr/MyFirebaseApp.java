package com.luncher.bounjour.ringlerr;

import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.activity.MyDexApplication;

public class MyFirebaseApp extends MyDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

