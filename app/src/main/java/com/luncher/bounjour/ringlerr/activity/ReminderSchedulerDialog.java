package com.luncher.bounjour.ringlerr.activity;

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

import com.luncher.bounjour.ringlerr.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderSchedulerDialog extends Activity {

    Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduler_alarm);

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button closeBtn = (Button) findViewById(R.id.dialog_close);
        Button sent_btn = (Button) findViewById(R.id.send_btn);
        TextView message = (TextView) findViewById(R.id.message);
        TextView text_date = (TextView) findViewById(R.id.text_date);

        String alarm_message = getIntent().getExtras().getString("alarm_mgs");
        Long date_time = getIntent().getExtras().getLong("date_time", 0);
        String phone = getIntent().getExtras().getString("phone");
        String name = getIntent().getExtras().getString("name");
        String id = getIntent().getExtras().getString("id");
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

        sent_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

}
