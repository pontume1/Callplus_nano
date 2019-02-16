package com.luncher.bounjour.ringlerr.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.MainActivity;
import com.luncher.bounjour.ringlerr.MyOutgoingCustomDialog;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;

import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.luncher.bounjour.ringlerr.activity.MyDexApplication.CHANNEL_ID_BOUND;

public class TrackingService extends Service {

    private String mPhoneNo;
    @Override
    public void onCreate() {
        super.onCreate();
        loginToFirebase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_BOUND)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.tracking_enabled_notif))
                    .setSmallIcon(R.drawable.ic_place_col_24dp)
                    .setContentIntent(broadcastIntent)
                    .setOngoing(true)
                    .build();

            startForeground(1, notification);

        }else {
            // Create the persistent notification//
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.tracking_enabled_notif))

                    //Make this notification ongoing so it can’t be dismissed by the user//

                    .setOngoing(true)
                    .setContentIntent(broadcastIntent)
                    .setSmallIcon(R.drawable.ic_place_col_24dp);
            startForeground(1, builder.build());
        }

        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);

            //Stop the Service//
            stopSelf();
        }
    };

    private void loginToFirebase() {

        requestLocationUpdates();
    }

    //Initiate the request to track the device's location//

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        SessionManager session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);
        //Specify how often your app should request the device’s location//

        request.setInterval(10000);

        //Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            //If the app currently has access to the location permission...//

            if (permission == PackageManager.PERMISSION_GRANTED) {
                //...then request location updates//

                client.requestLocationUpdates(request, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        //Get a reference to the database, so your app can perform read and write operations//

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(path).child(mPhoneNo);
                        Location location = locationResult.getLastLocation();
                        if (location != null) {

                            //Save the location data to the database//

                            ref.setValue(location);
                        } else {
                            Toast.makeText(getApplicationContext(), "Your phone not allowing to recive location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, null);
            }
        }else{

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    //Get a reference to the database, so your app can perform read and write operations//

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(path).child(mPhoneNo);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        //Save the location data to the database//

                        ref.setValue(location);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your phone not allowing to recive location", Toast.LENGTH_SHORT).show();
                    }
                }
            }, null);

        }
    }
}
