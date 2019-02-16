package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ReminderAlarmDialogSnooz extends Activity {

    AlarmManager alarmManager;
    Ringtone ringtone;
    private PowerManager.WakeLock wl;
    private boolean is_seen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);

        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag:ReminderAlarm");
        wl.acquire(10*60*1000L /*10 minutes*/);

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

        setContentView(R.layout.reminder_alarm);

        Button closeBtn = findViewById(R.id.dialog_close);
        Button send_btn = findViewById(R.id.send_btn);
        final TextView message = findViewById(R.id.message);
        TextView text_date = findViewById(R.id.text_date);
        TextView text_repeat = findViewById(R.id.text_repeat);

        final String alarm_message = getIntent().getExtras().getString("alarm_mgs");
        final Long date_time = getIntent().getExtras().getLong("date_time", 0);
        final String shared_with = getIntent().getExtras().getString("shared_with");
        String name = getIntent().getExtras().getString("name");
        final String from = getIntent().getExtras().getString("from");
        final int id = getIntent().getExtras().getInt("alarm_id", 0);
        String formattedDate = "";

        if(date_time > 0) {
            long timestamp = date_time;
            Date date = new Date(timestamp);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.US);
            formattedDate = formatter.format(date);
        }

        SessionManager session = new SessionManager(ReminderAlarmDialogSnooz.this);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        String mPhoneNo = user.get(SessionManager.KEY_PHONE);

        String my_shared_text = "Shared with ";
        if(shared_with.equals("")){
            my_shared_text = "Shared with self";
        }else if(!mPhoneNo.equals(from)){
            my_shared_text = "Shared by "+getContactName(getApplicationContext(), from);
        }else {
            try {
                JSONObject object = new JSONObject(shared_with.trim());
                JSONArray keys = object.names();

                for (int i = 0; i < keys.length(); ++i) {
                    String ph_key = keys.getString(i); // Here's your key
                    String ac_value = object.getString(ph_key); // Here's your value
                    String current_name = getContactName(getApplicationContext(), ph_key);
                    if (i == 0) {
                        my_shared_text += " " + current_name;
                    }

                    if (i >= 1) {
                        my_shared_text += " and " + current_name;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        message.setText(alarm_message);
        text_date.setText(formattedDate);
        text_repeat.setText(my_shared_text);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();

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
                ringtone.stop();
                snooze(alarm_message, date_time, id, shared_with, from);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            ringtone.stop();
            finish();
            if (wl.isHeld()) {
                wl.release();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        wl.acquire(10*60*1000L /*10 minutes*/);

    }

    @Override
    public void onPause(){
        super.onPause();
        if(is_seen){
            ringtone.stop();
            finish();
            if (wl.isHeld()) {
                wl.release();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        is_seen = true;
        if (wl.isHeld()) {
            wl.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ringtone.stop();
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

    public void snooze(String message, Long time, int id, String shared_with, String from){
        Long alarm_time = time + (5 * 60 * 1000);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(ReminderAlarmDialogSnooz.this, ReminderAlarmDialog.class);
        myIntent.putExtra("alarm_id", id);
        myIntent.putExtra("from", from);
        myIntent.putExtra("alarm_mgs", message);
        myIntent.putExtra("date_time", alarm_time);
        myIntent.putExtra("shared_with", shared_with);
        PendingIntent.getActivity(ReminderAlarmDialogSnooz.this, id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        PendingIntent pendingIntent = PendingIntent.getActivity(ReminderAlarmDialogSnooz.this, id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // Wakes up the device in Doze Mode
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
//
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // Wakes up the device in Idle Mode
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
//        } else {
//            // Old APIs
//            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
//        }
    }
}
