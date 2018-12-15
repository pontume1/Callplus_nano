package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderAlarmDialog extends Activity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    Ringtone ringtone;
    private PowerManager.WakeLock wl;
    private boolean is_seen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);

        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

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

        Button closeBtn = (Button) findViewById(R.id.dialog_close);
        Button send_btn = (Button) findViewById(R.id.send_btn);
        final TextView message = (TextView) findViewById(R.id.message);
        TextView text_date = (TextView) findViewById(R.id.text_date);

        final String alarm_message = getIntent().getExtras().getString("alarm_mgs");
        final Long date_time = getIntent().getExtras().getLong("date_time", 0);
        String phone = getIntent().getExtras().getString("phone");
        String name = getIntent().getExtras().getString("name");
        final int id = getIntent().getExtras().getInt("alarm_id", 0);
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
                snooze(alarm_message, date_time, id);
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
    public void onResume(){
        super.onResume();
        wl.acquire();

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


    public void snooze(String message, Long time, int id){
        Long alarm_time = time + (5 * 60 * 1000);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(ReminderAlarmDialog.this, ReminderAlarmDialog.class);
        myIntent.putExtra("alarm_mgs", message);
        myIntent.putExtra("date_time", alarm_time);
        //myIntent.putExtra("alarm_mgs", message);
        pendingIntent = PendingIntent.getActivity(ReminderAlarmDialog.this, id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent);
    }
}
