package com.luncher.santanu.dailer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luncher.santanu.dailer.activity.ReminderAlarmDialog;
import com.luncher.santanu.dailer.activity.ReminderDialog;
import com.luncher.santanu.dailer.activity.ReminderList;
import com.luncher.santanu.dailer.model.Message;
import com.luncher.santanu.dailer.model.Reminder;
import com.luncher.santanu.dailer.model.User;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class MessageService extends Service implements SocketEventListener.Listener, HeartBeat.HeartBeatListener {

    MyDbHelper myDbHelper;
    String myPhone;
    private Socket mSocket;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HeartBeat heartBeat;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private CountDownTimer timer;

    @Override
    public void onHeartBeat() {
        if (mSocket != null && !mSocket.connected()) {
            //mSocket.connect();
        }
    }

    @Override
    public void onEventCall(String event, Object... objects) {

    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:
                    Log.w(TAG, "Connected");
                    /*Toast.makeText(SocketIOService.this,
                            R.string.connect, Toast.LENGTH_LONG).show();*/
                    break;
                case 2:
                    Log.w(TAG, "Disconnected");
                    /*Toast.makeText(SocketIOService.this,ss
                            R.string.disconnect, Toast.LENGTH_LONG).show();*/
                    break;
                case 3:
                    Log.w(TAG, "Error in Connection");
                    /*Toast.makeText(SocketIOService.this,
                            R.string.error_connect, Toast.LENGTH_LONG).show();*/
                    break;
            }
        }
    }

    public MessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread(TAG + "Args",
                THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference profile = mRootRef.child("users/"+ mCurrentUserId);
        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                myPhone = user.phone;
                loadMessages(myPhone);
                loadAlarms(myPhone);
                loadAcceptRejectNotification(myPhone);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        timer = new CountDownTimer(1800000, 20) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    if(null != myPhone) {
                        repeatNotification(myPhone);
                    }
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();

        heartBeat = new HeartBeat(this);
        heartBeat.start();

    }

    private void repeatNotification(String myPhone) {
        //loadAlarms(myPhone);
        loadAcceptRejectNotification(myPhone);
        timer.start();
    }

    private void loadMessages(final String myPhone) {

        //Query messageRef = mRootRef.child("message").child(myPhone).orderByChild("seen").equalTo("false");
        Query messageRef = mRootRef.child("message").child(myPhone);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Message messages = dataSnapshot.getValue(Message.class);
            //9560631652
                if(messages != null && messages.seen.equals("false")) {
                    String from = messages.from;
                    String msg = messages.message;
                    String image = messages.image;
                    String type = messages.type;
                    String to = messages.to;
                    String filename = "none";

                    if (!image.equals("none")) {
                        if (type.equals("gif")) {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            filename = "IMG_" + timeStamp + ".gif";
                            String img_path = storeGifImage(image, filename);
                        } else if (type.equals("libgif") || type.equals("themgif") || type.equals("sticker")) {
                            filename = image;
                        } else {
                            Bitmap rImage = decodeImage(image);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            filename = "IMG_" + timeStamp + ".jpg";
                            String img_path = storeImage(rImage, filename);
                        }

                    }

                    int duration = Toast.LENGTH_LONG;
                    // Context pcontext;
                    Toast toast;
                    toast = Toast.makeText(getApplicationContext(), "mgs recived from firebase", duration);
                    toast.show();

                    if (!filename.equals("none") || !msg.isEmpty()) {
                        myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                        myDbHelper.addMessages(msg, from, filename, type);
                    }

                    mRootRef.child("message").child(myPhone).child("seen").setValue("true");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadAlarms(final String myPhone) {

        Query messageRef = mRootRef.child("reminder").child(myPhone).orderByChild("is_seen").equalTo(false);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren())
                {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {
                        Reminder reminder = child.getValue(Reminder.class);
                        String reminderKey = child.getKey();
                        if(reminder != null && !myPhone.equals(reminder.from)) {

                            String notiMgs = getContactName(getApplicationContext(), reminder.from) + " has set a reminder for you.";
                            //mRootRef.child("reminder").child(dataSnapshot.getKey()).child(reminderKey).child("is_seen").setValue(true);
                            setAlarm(notiMgs);
                        }
                        if(reminder != null && myPhone.equals(reminder.from) && !reminder.is_accepted.equals("none")) {

                            String notiMgs = "";
                            //mRootRef.child("reminder").child(dataSnapshot.getKey()).child(reminderKey).child("is_seen").setValue(true);
                            if(reminder.is_accepted.equals("false")){
                                notiMgs = getContactName(getApplicationContext(), reminder.to) + " has decline your reminder.";
                            }else{
                                notiMgs = getContactName(getApplicationContext(), reminder.to) + " has accepted your reminder.";
                            }
                            setAlarm(notiMgs);
                        }
                    }
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadAcceptRejectNotification(final String myPhone) {

        Query messageRef = mRootRef.child("reminder").child(myPhone).orderByChild("taken").equalTo(false);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren())
                {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {
                        Reminder reminder = child.getValue(Reminder.class);
                        String reminderKey = child.getKey();
                        if(reminder != null && myPhone.equals(reminder.from) && !reminder.is_accepted.equals("none")) {

                            String notiMgs = "";
                            //mRootRef.child("reminder").child(dataSnapshot.getKey()).child(reminderKey).child("is_seen").setValue(true);
                            if(reminder.is_accepted.equals("false")){
                                notiMgs = getContactName(getApplicationContext(), reminder.to) + " has decline your reminder.";
                            }else{
                                notiMgs = getContactName(getApplicationContext(), reminder.to) + " has accepted your reminder.";
                            }
                            setAlarm(notiMgs);
                            mRootRef.child("reminder").child(myPhone).child("taken").setValue(true);
                        }
                    }
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setAlarm(String notiMgs){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MessageService.this, NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo_main)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(appName+" reminder")
                .setContentText(notiMgs)
                .setContentInfo("Info");

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(MessageService.this, ReminderList.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(MessageService.this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //TODO do something useful

//        myDbHelper = new MyDbHelper(MessageService.this, null, null, 1);
//        myPhone = myDbHelper.getMyPhoneNo();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //mSocket.disconnect();
        heartBeat.stop();

//        Toast.makeText(getBaseContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
//        Intent restartService = new Intent("RestartService");
//        sendBroadcast(restartService);
    }

    private Bitmap decodeImage(String data)
    {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
        return bmp;
    }

    private String storeGifImage(String imageData, String filename){

        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images";
        File sdIconStorageDir = new File(iconsStoragePath);
        String filePath = "";

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename;
            File myFile = new File(filePath);

            byte[] gifBytes = Base64.decode(imageData, 0);
            FileOutputStream os = new FileOutputStream(filePath, true);
            os.write(gifBytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    private String storeImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images";
        File sdIconStorageDir = new File(iconsStoragePath);
        String filePath = "";

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return e.getMessage();
        }

        return filePath;
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNumber;
        }
        String contactName = phoneNumber;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    private Emitter.Listener onReciveCall = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String msg;
//                    String from;
//                    try {
//                        from = data.getString("from");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }

                    String from = (String) args[0];
                    String msg = (String) args[1];
                    String image = (String) args[2];
                    String type = (String) args[3];
                    String filename = "none";

                    if(!image.equals("none")){
                        if(type.equals("gif")) {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            filename = "IMG_" + timeStamp + ".gif";
                            String img_path = storeGifImage(image, filename);
                        }else if(type.equals("libgif") || type.equals("themgif")){
                            filename = image;
                        }else{
                            Bitmap rImage = decodeImage(image);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            filename = "IMG_" + timeStamp + ".jpg";
                            String img_path = storeImage(rImage, filename);
                        }

                    }

                    int duration = Toast.LENGTH_LONG;
                    // Context pcontext;
                    Toast toast;
                    toast = Toast.makeText(getApplicationContext(), "mgs recived", duration);
                    toast.show();

                    if(!filename.equals("none") || !msg.isEmpty()) {
                        myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                        myDbHelper.addMessages(msg, from, filename, type);
                    }
                }
            });
        }
    };
}
