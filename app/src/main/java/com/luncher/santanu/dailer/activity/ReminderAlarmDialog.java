package com.luncher.santanu.dailer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.luncher.santanu.dailer.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderAlarmDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_alarm);

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button closeBtn = (Button) findViewById(R.id.dialog_close);
        Button snooze_btn = (Button) findViewById(R.id.snooze_btn);
        TextView message = (TextView) findViewById(R.id.message);
        TextView text_date = (TextView) findViewById(R.id.text_date);

        String alarm_message = getIntent().getExtras().getString("alarm_mgs");
        Long date_time = getIntent().getExtras().getLong("date_time", 0);
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
        final Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();

        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ringtone.stop();
                finish();
            }

        });

        snooze_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ringtone.stop();
                finish();
            }

        });
    }
}
