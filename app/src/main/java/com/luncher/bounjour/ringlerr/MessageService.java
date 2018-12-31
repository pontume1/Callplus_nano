package com.luncher.bounjour.ringlerr;

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
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luncher.bounjour.ringlerr.activity.ChatList;
import com.luncher.bounjour.ringlerr.activity.PendingReminderList;
import com.luncher.bounjour.ringlerr.activity.ReminderAlarmDialog;
import com.luncher.bounjour.ringlerr.activity.ReminderDetail;
import com.luncher.bounjour.ringlerr.activity.ReminderList;
import com.luncher.bounjour.ringlerr.model.Library;
import com.luncher.bounjour.ringlerr.model.Message;
import com.luncher.bounjour.ringlerr.model.Reminder;
import com.luncher.bounjour.ringlerr.model.User;
import com.luncher.bounjour.ringlerr.services.ContactBoundServiceExt;
import com.luncher.bounjour.ringlerr.services.MyReminderNotificationReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.luncher.bounjour.ringlerr.activity.MyDexApplication.CHANNEL_ID;

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
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private PendingIntent pendingNotiIntent;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("assets");
    SessionManager session;

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

        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        myPhone = user.get(SessionManager.KEY_PHONE);

        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //myPhone = user.phone;
                checkLibrary();
                loadMessages(myPhone);
                loadAlarms(myPhone);
                loadAcceptRejectNotification(myPhone);
                chatNotification(myPhone);
                final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(myPhone + ".jpg");
                download_image(filepath);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        timer = new CountDownTimer(60000000, 600000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Intent cbIntent = new Intent();
                cbIntent.setClass(MessageService.this, ContactBoundServiceExt.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(MessageService.this, cbIntent);
                } else {
                    startService(cbIntent);
                }
            }

            @Override
            public void onFinish() {
                try{
                    if(null != myPhone) {

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
                    final String from = messages.from;
                    String msg = messages.message;
                    String image = messages.image;
                    String type = messages.type;
                    String to = messages.to;
                    String talk_time = messages.talk_time;
                    String filename = "none";

                    if(image == null){
                        image = "none";
                    }

                    if (!image.equals("none")) {
                        if (type.equals("gif")) {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            filename = "IMG_" + timeStamp + ".gif";
                            String img_path = storeGifImage(image, filename, "Images");
                        } else if (type.equals("libgif") || type.equals("themgif") || type.equals("sticker")) {
                            filename = image;
                        } else {
                            Bitmap rImage = decodeImage(image);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            filename = "IMG_" + timeStamp + ".jpg";
                            String img_path = storeImage(rImage, filename, "Images");
                        }

                    }

                    if (!filename.equals("none") || !msg.isEmpty()) {
                        myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                        final long id = myDbHelper.addMessages(msg, from, filename, type, talk_time);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                myDbHelper.updateMessageRead(""+id, from);
                            }
                        }, 30000);

                    }

                    mRootRef.child("message").child(myPhone).child("seen").setValue("true");

                    String name = getContactName(getApplicationContext(), from);
                    String notiMgs = "";
                    if (type.equals("none")) {
                        notiMgs = msg;
                        String arr[] = name.split(" ", 2);
                        name = arr[0]+" has sent a meassage with call";
                    } else {
                        notiMgs = name + " has sent an image with call";
                    }

                    setNotification(notiMgs, name, from);

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
                        Long crLong = System.currentTimeMillis();
                        Long time_and_date = Long.valueOf(reminder.time);
                        Date date = new Date(time_and_date);
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
                        String formattedDate = formatter.format(date);

                        if(reminder != null && !myPhone.equals(reminder.from) && time_and_date > crLong) {

                            String notiMgs = getContactName(getApplicationContext(), reminder.from) + " has set a reminder for you.";
                            //mRootRef.child("reminder").child(myPhone).child(reminderKey).child("is_seen").setValue(true);

                            if(reminder.is_accepted.equals("true")){
                                notiMgs = getContactName(getApplicationContext(), reminder.from) + " has updated the reminder details shared for you.";

                                MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                                int Id = myDbHelper.updateReminder(reminderKey, reminder.message, time_and_date, reminder.shared_with);

                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                Intent myIntent = new Intent(getApplicationContext(), ReminderAlarmDialog.class);
                                myIntent.putExtra("alarm_mgs", reminder.message);
                                myIntent.putExtra("date_time", time_and_date);
                                myIntent.putExtra("alarm_id", Id);
                                //myIntent.putExtra("alarm_mgs", message);
                                pendingIntent = PendingIntent.getActivity(getApplicationContext(), Id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time_and_date, pendingIntent);

                                Long noti_time = time_and_date;
                                if (reminder.remindAgo == 1) {
                                    noti_time = time_and_date - (60 * 60 * 1000);
                                } else if (reminder.remindAgo == 0) {
                                    noti_time = time_and_date - (5 * 60 * 1000);
                                } else {
                                    noti_time = time_and_date - (reminder.remindAgo * 60 * 1000);
                                }

//                                Intent notifyIntent = new Intent(getApplicationContext(), MyReminderNotificationReceiver.class);
//                                notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                notifyIntent.putExtra("message", reminder.message);
//                                notifyIntent.putExtra("formattedDate", formattedDate);
//                                notifyIntent.putExtra("timestamp", time_and_date);
//                                notifyIntent.putExtra("shared_with", reminder.shared_with);
//                                pendingNotiIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) Id + 120, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                                alarmManager.setExact(AlarmManager.RTC, noti_time, pendingNotiIntent);

                                setAlarmReminderDetail(notiMgs, "Ringlerr Reminder", reminder.message, formattedDate, time_and_date, reminder.shared_with);

                            }else {
                                setAlarm(notiMgs, "Ringlerr Reminder");
                            }
                        }
                        if(reminder != null && myPhone.equals(reminder.from) && !reminder.is_accepted.equals("none")) {

                            String notiMgs = "";
                            //mRootRef.child("reminder").child(dataSnapshot.getKey()).child(reminderKey).child("is_seen").setValue(true);
                            if(reminder.is_accepted.equals("false")){
                                notiMgs = getContactName(getApplicationContext(), reminder.to) + " has decline your reminder.";
                            }else{
                                notiMgs = getContactName(getApplicationContext(), reminder.to) + " has accepted your reminder.";
                            }
                            setAlarmReminderDetail(notiMgs, "Ringlerr Reminder", reminder.message, formattedDate, time_and_date, reminder.shared_with);
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

        Query messageRef = mRootRef.child("reminder").child(myPhone).orderByChild("taken").equalTo(true);;

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren())
                {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {
                        Reminder reminder = child.getValue(Reminder.class);
                        String reminderKey = child.getKey();
                        Long time_and_date = Long.valueOf(reminder.time);
                        Date date = new Date(time_and_date);
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
                        String formattedDate = formatter.format(date);
                        if(reminder != null && myPhone.equals(reminder.from) && !reminder.is_accepted.equals("none")) {

                            String shared_with = reminder.shared_with;

                            String my_shared_text = "";
                            try {

                                JSONObject object = new JSONObject(shared_with.trim());
                                JSONArray keys = object.names ();

                                for (int i = 0; i < keys.length (); ++i) {

                                    String ph_key = keys.getString (i); // Here's your key
                                    String ac_value = object.getString (ph_key); // Here's your value
                                    String current_name = getContactName(getApplicationContext(), ph_key);

                                    String a = "1";
                                    if(reminder.is_accepted.equals("false")){
                                        a = "2";
                                    }
                                    int j = 0;
                                    if(ac_value.equals(a)) {
                                        if (j == 0) {
                                            my_shared_text += " "+current_name;
                                        }

                                        if (j >= 1) {
                                            my_shared_text += " and " + current_name;
                                        }

//                                        if (j == 2) {
//                                            int rem_total = keys.length() - 2;
//                                            my_shared_text += " +" + rem_total + " others";
//                                        }
                                        j++;
                                    }

                                }

                            } catch (Throwable t) {

                            }

                            String notiMgs = "";
                            //mRootRef.child("reminder").child(dataSnapshot.getKey()).child(reminderKey).child("is_seen").setValue(true);
                            if(reminder.is_accepted.equals("false")){
                                notiMgs = my_shared_text + " has decline your reminder.";
                            }else{
                                notiMgs = my_shared_text + " has accepted your reminder.";
                            }

                            //setAlarm(notiMgs, "Ringlerr Reminder");
                            setAlarmReminderDetail(notiMgs, "Ringlerr Reminder", reminder.message, formattedDate, time_and_date, reminder.shared_with);
                            mRootRef.child("reminder").child(myPhone).child(reminderKey).child("taken").setValue(false);
                            mRootRef.child("reminder").child(myPhone).child(reminderKey).child("is_accepted").setValue("none");
                        }
                    }
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void chatNotification(final String myPhone){

        Query messageRef = mRootRef.child("notification").child(myPhone);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Message chat = dataSnapshot.getValue(Message.class);
                String chatKey = dataSnapshot.getKey();

                if (chat != null){
                    if (chat.seen != null && chat.seen.equals("false")) {
                        String name = getContactName(getApplicationContext(), chat.from);
                        String notiMgs = "";
                        if (chat.type.equals("none")) {
                            notiMgs = chat.message;
                        } else {
                            notiMgs = name + " has sent you an image";
                        }

                        MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                        myDbHelper.addChatNotification(chat.from, notiMgs);
                        int count = myDbHelper.getNotificationCount();

                        if(count == 1) {
                            setNotification(notiMgs, name, chat.from);
                        }else{
                            notiMgs = myDbHelper.getNotificationMessage();
                            setMultiNotification(notiMgs, "You have "+count+" unread messages", chat.from, chat.message);
                        }

                        mRootRef.child("notification").child(myPhone).child("seen").setValue("true");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setAlarm(String notiMgs, String title){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ringlerr Reminder", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Ringlerr Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MessageService.this, NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(notiMgs)
                .setContentInfo("Info");

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(MessageService.this, PendingReminderList.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(MessageService.this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

    public void setAlarmReminderDetail(String notiMgs, String title, String message, String formattedDate, Long timestamp, String shared_with){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ringlerr Reminder", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Ringlerr Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MessageService.this, NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(notiMgs)
                .setContentInfo("Info");

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(MessageService.this, ReminderDetail.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("message", message);
        notificationIntent.putExtra("formattedDate", formattedDate);
        notificationIntent.putExtra("timestamp", timestamp);
        notificationIntent.putExtra("shared_with", shared_with);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(MessageService.this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

    public void setNotification(String notiMgs, String title, String phone){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Ringlerr Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MessageService.this, NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(notiMgs)
                .setContentInfo("Info");

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(MessageService.this, ChatList.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        notificationIntent.putExtra("phone_no", phone);
        notificationIntent.putExtra("name", title);

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(MessageService.this, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/2, notificationBuilder.build());
    }

    public void setMultiNotification(String notiMgs, String title, String phone, String smlmgs){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Ringlerr Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MessageService.this, NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(smlmgs)
                .setContentInfo("Info")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notiMgs));

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(MessageService.this, ChatList.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        notificationIntent.putExtra("phone_no", phone);
        notificationIntent.putExtra("name", title);

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(MessageService.this, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/2, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    1000993, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    //.setContentTitle("Example Service")
                    //.setContentText("")
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build();

            startForeground(1000993, notification);


        }

        return START_STICKY;

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

    public void checkLibrary(){
        Query messageRef = mRootRef.child("library").orderByChild("name");

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren())
                {
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {
                        String type = data.getKey();

                        for (DataSnapshot child : data.getChildren()) {
                            Library library = child.getValue(Library.class);
                            if (library != null) {
                                String image_name = library.getName();
                                String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/"+type+"/"+image_name;
                                File file = new File(filePath);
                                if (!file.exists()){
                                    getAssetsImages(type, image_name);
                                }
                            }
                        }
                    }
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void download_image(StorageReference filepath) {

        final long ONE_MEGABYTE = 1024 * 1024;

        //download file as a byte array
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                storeProfileImage(bitmap, "my_profile.jpeg");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                String mgs = exception.getMessage();
                String h = mgs;
            }
        });
    }

    private String storeProfileImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/";
        File sdIconStorageDir = new File(iconsStoragePath);
        String filePath = "";

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();
        File resolveMe = new File(iconsStoragePath+".nomedia");
        try {
            resolveMe.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

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

    private void getAssetsImages(final String libType, final String image_name){

        StorageReference  islandRef = storageRef.child(libType).child(image_name);
        final long ONE_MEGABYTE = 1024 * 1024;

        //download file as a byte array
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                if(libType.equals("stickers")){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    storeImage(bitmap, image_name, libType);
                }else{
                    String imageData = Base64.encodeToString(bytes, Base64.DEFAULT);
                    storeGifImage(imageData, image_name, libType);
                }

            }
        });
    }

    private Bitmap decodeImage(String data)
    {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
        return bmp;
    }

    private String storeGifImage(String imageData, String filename, String folder){

        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/"+folder;
        File sdIconStorageDir = new File(iconsStoragePath);
        String filePath = "";

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();
        if(folder.equals("animation")) {
            File resolveMe = new File(iconsStoragePath + "/.nomedia");
            try {
                resolveMe.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    private String storeImage(Bitmap imageData, String filename, String folder) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/"+folder;
        File sdIconStorageDir = new File(iconsStoragePath);
        String filePath = "";

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();
        if(folder.equals("stickers")) {
            File resolveMe = new File(iconsStoragePath + "/.nomedia");
            try {
                resolveMe.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            //Log.w("TAG", "Error saving image file: " + e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            //Log.w("TAG", "Error saving image file: " + e.getMessage());
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
                            String img_path = storeGifImage(image, filename, "Images");
                        }else if(type.equals("libgif") || type.equals("themgif")){
                            filename = image;
                        }else{
                            Bitmap rImage = decodeImage(image);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            filename = "IMG_" + timeStamp + ".jpg";
                            String img_path = storeImage(rImage, filename, "Images");
                        }

                    }

                    int duration = Toast.LENGTH_LONG;
                    // Context pcontext;
                    Toast toast;
                    toast = Toast.makeText(getApplicationContext(), "mgs recived", duration);
                    toast.show();

                    if(!filename.equals("none") || !msg.isEmpty()) {
                        myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                        //myDbHelper.addMessages(msg, from, filename, type);
                    }
                }
            });
        }
    };
}
