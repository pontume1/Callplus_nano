package com.luncher.bounjour.ringlerr.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Message;
import com.luncher.bounjour.ringlerr.services.TrackingService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SosActivity extends AppCompatActivity {

    private DatabaseReference mRootRef;
    MySOSContentObserver observer;
    int current_call = 0;
    String secondary_number;
    String mPhoneNo;
    String message;
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int GPS_ENABLE_REQUEST = 101;

    boolean isProviderEnabledGPS;
    boolean isProviderEnabledNetwork;
    LocationManager lm;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final float MIN_TIME_BW_UPDATES = 1000 * 60 * 60; // 60 minute

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);

        Handler handler = new Handler(getApplicationContext().getMainLooper());
        observer = new MySOSContentObserver(handler);
        this.getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        android.provider.CallLog.Calls.CONTENT_URI, true,
                        new MySOSContentObserver(handler));

        mRootRef = FirebaseDatabase.getInstance().getReference();
        SessionManager session = new SessionManager(SosActivity.this);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);

        startGPSTraacking();
    }

    private void startGPSTraacking() {

        //Check whether GPS tracking is enabled//
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        isProviderEnabledGPS = lm != null ? lm.isProviderEnabled(LocationManager.GPS_PROVIDER) : false;
        isProviderEnabledNetwork = lm != null ? lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) : false;

        if (isProviderEnabledGPS || isProviderEnabledNetwork) {
            resumeTracking();
            // if GPS Enabled get lat/long using GPS Services
//            if (isGPSEnabled) {
//                if (location == null) {
//                    lm.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                    if (locationManager != null) {
//                        location = locationManager
//                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                        if (location != null) {
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//                        }
//                    }
//                }
//            }

        }else{
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLE_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_ENABLE_REQUEST) {
            resumeTracking();
        }
    }


    public void resumeTracking(){
        //Check whether this app has access to the location permission//
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the TrackerService//
        if (permission == PackageManager.PERMISSION_GRANTED) {
            if (isProviderEnabledNetwork) {

                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }

            }

            if (isProviderEnabledGPS) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

            startTrackerService();

        } else {

            //If the app doesn’t currently have access to the user’s location, then request access//
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    public int getDefaultSimSlot(Context context) {

        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getDefaultSim;
        int defaultSimm = -1;
        try {
            assert tm != null;
            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
            method_getDefaultSim.setAccessible(true);
            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Method method_getSmsDefaultSim;
        int smsDefaultSim = -1;
        try {
            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultSimm;
    }

    void start_calling(){
        MyDbHelper myDbHelper = new MyDbHelper(SosActivity.this, null, 2);
        ArrayList settings = myDbHelper.getSosSetting();
        if (!settings.isEmpty()) {
            String phone_no = settings.get(1).toString();
            message = settings.get(3).toString();
            current_call = 1;

            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            DatabaseReference mTo = mRootRef.child("message").child(phone_no);
            Message messages_to = new Message(0, mPhoneNo, phone_no, message, "none", "sos", "false", ts, "");
            mTo.setValue(messages_to);

            String key = mRootRef.child("chats").child(mPhoneNo).child(phone_no).push().getKey();
            Message chat = new Message(0, mPhoneNo, phone_no, message, "none", "sos", "false", ts, "");
            Map<String, Object> postValues = chat.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/chats/" + phone_no + "/" + mPhoneNo + "/" + key, postValues);
            childUpdates.put("/chats/" + mPhoneNo + "/" + phone_no + "/" + key, postValues);

            mRootRef.updateChildren(childUpdates);

            String sms_message = message+" http://maps.google.com?q= "+longitude+","+latitude;
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone_no, null, sms_message, null, null);

            Boolean close_app = true;

            if (null != settings.get(2)) {
                close_app = false;
                secondary_number = settings.get(2).toString();

                sms = SmsManager.getDefault();
                sms.sendTextMessage(secondary_number, null, sms_message, null, null);

                DatabaseReference mTos = mRootRef.child("message").child(secondary_number);
                Message messages_tos = new Message(0, mPhoneNo, secondary_number, message, "none", "sos", "false", ts, "");
                mTos.setValue(messages_tos);

                String keys = mRootRef.child("chats").child(mPhoneNo).child(phone_no).push().getKey();
                Message chats = new Message(0, mPhoneNo, secondary_number, message, "none", "sos", "false", ts, "");
                Map<String, Object> postValuess = chats.toMap();

                Map<String, Object> childUpdatess = new HashMap<>();
                childUpdatess.put("/chats/" + secondary_number + "/" + mPhoneNo + "/" + keys, postValuess);
                childUpdatess.put("/chats/" + mPhoneNo + "/" + secondary_number + "/" + keys, postValuess);

                mRootRef.updateChildren(childUpdatess);
            }

            call_phone(phone_no, close_app);
        } else {

            call_phone("100", true);
        }
    }

    void call_phone(String phone_no, Boolean close_activity) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone_no));
        int simSlot = getDefaultSimSlot(SosActivity.this);
        callIntent.putExtra("com.android.phone.force.slot", true);
        callIntent.putExtra("com.android.phone.extra.slot", simSlot);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                startActivity(callIntent);
                if(close_activity) {
                    finish();
                }
            }
        }else {
            startActivity(callIntent);
            if(close_activity) {
                finish();
            }
        }
    }

//    void retriveCallSummary() {
//
//        StringBuffer sb = new StringBuffer();
//        Uri contacts = CallLog.Calls.CONTENT_URI;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//                Cursor managedCursor = getContentResolver().query(
//                        contacts, null, null, null, null);
//                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
//                int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
//                if (managedCursor.moveToFirst() == true) {
//                    String phNumber = managedCursor.getString(number);
//                    String callDuration = managedCursor.getString(duration1);
//                    String dir = null;
//                }
//                managedCursor.close();
//
//                return;
//            }
//        }else{
//            Cursor managedCursor = getContentResolver().query(
//                    contacts, null, null, null, null);
//            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
//            int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
//            if (managedCursor.moveToFirst() == true) {
//                String phNumber = managedCursor.getString(number);
//                String callDuration = managedCursor.getString(duration1);
//                String dir = null;
//            }
//            managedCursor.close();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            this.getContentResolver().unregisterContentObserver(observer);
        } catch (IllegalStateException ise) {
            // Do Nothing.  Observer has already been unregistered.
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]  grantResults) {

        //If the permission has been granted...//
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (isProviderEnabledNetwork) {

                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }

            }

            if (isProviderEnabledGPS) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

            //...then start the GPS tracking service//
            startTrackerService();
        } else {

            //If the user denies the permission request, then display a toast with some more information//
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    //Start the TrackerService//
    private void startTrackerService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, new Intent(this, TrackingService.class));
        } else {
            startService(new Intent(this, TrackingService.class));
        }

        start_calling();
        //Notify the user that tracking has been enabled//
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

    }

    class MySOSContentObserver extends ContentObserver {
        public MySOSContentObserver(Handler h) {
            super(h);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            // here you call the method to fill the list
            if(current_call==1 && null != secondary_number){
                current_call = 2;
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();

                DatabaseReference mTos = mRootRef.child("message").child(secondary_number);
                Message messages_tos = new Message(0, mPhoneNo, secondary_number, message, "none", "sos", "false", ts, "");
                mTos.setValue(messages_tos);

                String keys = mRootRef.child("chats").child(mPhoneNo).child(secondary_number).push().getKey();
                Message chats = new Message(0, mPhoneNo, secondary_number, message, "none", "sos", "false", ts, "");
                Map<String, Object> postValuess = chats.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/chats/" + secondary_number + "/" + mPhoneNo + "/" + keys, postValuess);
                childUpdates.put("/chats/" + mPhoneNo + "/" + secondary_number + "/" + keys, postValuess);

                mRootRef.updateChildren(childUpdates);
                call_phone(secondary_number, true);
            }
        }
    }
}
