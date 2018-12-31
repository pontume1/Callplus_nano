package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.luncher.bounjour.ringlerr.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.app.NotificationCompat;

public class SchedulerAlarmDialog extends Activity {

    String alarm_message;
    String phone;
    String spinner_type;
    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);

        super.onCreate(savedInstanceState);

        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setLayout( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.scheduler_alarm);

        Button closeBtn = (Button) findViewById(R.id.dialog_close);
        Button send_btn = (Button) findViewById(R.id.send_btn);
        TextView message = (TextView) findViewById(R.id.message);
        TextView text_date = (TextView) findViewById(R.id.text_date);
        TextView text_repeat = (TextView) findViewById(R.id.text_repeat);

        alarm_message = getIntent().getExtras().getString("alarm_mgs");
        phone = getIntent().getExtras().getString("phone");
        Long date_time = getIntent().getExtras().getLong("date_time", 0);
        String is_manual = getIntent().getExtras().getString("is_manual");
        spinner_type = getIntent().getExtras().getString("spinner_type");

        text_repeat.setText("Send via "+spinner_type);

        String formattedDate = "";

        if(date_time > 0) {
            long timestamp = date_time;
            Date date = new Date(timestamp);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
            formattedDate = formatter.format(date);
        }

        message.setText(alarm_message);
        text_date.setText(formattedDate);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, uri);

        if(spinner_type.equals("SMS")) {
            if (is_manual.equals("1")) {
                ringtone.play();
            } else {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phone, null, alarm_message, null, null);
                setNotification("Schedule sms sent to "+phone+" via Ringlerr scheduler");
                finish();
            }
        }else{
            ringtone.play();
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ringtone.stop();
                finish();
            }

        });

        send_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(spinner_type.equals("SMS")) {
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phone, null, alarm_message, null, null);
                }else if(spinner_type.equals("Facebook")){
                    openFBmessenger();
                }else{
                    openWhatsApp();
                }
                ringtone.stop();
                finish();
            }

        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here

    }

    @Override
    public void onPause(){
        super.onPause();
        ringtone.stop();
        finish();
    }

    private void shareContent() {
        //Uri pictureUri = Uri.parse("file://my_picture");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, alarm_message);
        //shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        //shareIntent.setType("image/*");
        //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share from Ringlerr..."));
    }

    private void openWhatsApp() {
        String smsNumber = "91"+getLastnCharacters(phone, 10); // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, alarm_message);
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");

        if (sendIntent.resolveActivity(SchedulerAlarmDialog.this.getPackageManager()) == null) {
            Toast.makeText(this, "Whatsapp Not avalable", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(sendIntent);
    }

    private  void openFBmessenger(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "My message to send");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");

        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please Install Facebook Messenger", Toast.LENGTH_SHORT).show();
        }
    }

    public String getLastnCharacters(String inputString,
                                     int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    public void setNotification(String notiMgs){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(SchedulerAlarmDialog.this.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_03";

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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(SchedulerAlarmDialog.this, NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(appName+" Scheduler")
                .setContentText(notiMgs)
                .setContentInfo("Info");

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(SchedulerAlarmDialog.this, SchedulerList.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(SchedulerAlarmDialog.this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }
}
