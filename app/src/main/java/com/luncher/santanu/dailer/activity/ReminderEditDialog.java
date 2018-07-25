package com.luncher.santanu.dailer.activity;

/**
 * Created by santanu on 11/11/17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.model.Reminder;
import com.luncher.santanu.dailer.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class ReminderEditDialog extends Activity {
    TextView name;
    TextView phone;
    String phone_no;
    String c_name;
    String message;
    Long time_and_date;
    EditText sendMgs;
    Button dialog_save;
    Button dialog_share;
    Button closeButton;
    ImageButton date_pick;
    ImageButton time_pick;
    ImageButton btnRemindAgo;
    ImageButton btnRepeat;
    TextView date_time_sel;
    TextView time_sel;
    TextView ago_sel;
    TextView fec_sel;


    int minute;
    int hour;
    int year;
    int month;
    int day;
    Long time;
    int remindAgo = 0;
    String remUnit;

    private ImageView profile_image;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private String mCurrentUserId;
    private String mPhoneNo;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("test","outgoing custom dialog a");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference profile = mRootRef.child("users/"+ mCurrentUserId);
        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mPhoneNo = user.phone;

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

        phone_no = null;
        phone_no = getIntent().getExtras().getString("phone_no");
        message = getIntent().getExtras().getString("message");
        c_name = getIntent().getExtras().getString("name");
        time_and_date = getIntent().getExtras().getLong("timestamp");



        Calendar rnow = Calendar.getInstance();
        rnow.setTimeInMillis(time_and_date);
        year = rnow.get(Calendar.YEAR);
        month = rnow.get(Calendar.MONTH);
        day = rnow.get(Calendar.DAY_OF_MONTH);
        hour = rnow.get(Calendar.HOUR);
        minute = rnow.get(Calendar.MINUTE);

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(false);
            super.onCreate(savedInstanceState);
            if(phone_no.equals("null") && c_name.equals("null")){
                setContentView(R.layout.reminder_self);
                phone_no = "Self";
            }else{
                setContentView(R.layout.reminder);
            }

            initializeContent();

            sendMgs.setText(message, TextView.BufferType.EDITABLE);

            this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            //this.getWindow().addFlags(PixelFormat.TRANSPARENT);

            /*WindowManager.LayoutParams params = getWindow().getAttributes();
            params.x = -100;
            params.height = 70;
            params.width = 1000;
            params.y = -50;

            this.getWindow().setAttributes(params);*/
            final Context mContext = getApplicationContext();

            phone_no = "+91"+getLastnCharacters(phone_no,10);

            profile_image = (ImageView) findViewById(R.id.profile_image);
            name = (TextView) findViewById(R.id.name);
            phone = (TextView) findViewById(R.id.phone);
            date_time_sel = (TextView) findViewById(R.id.date_time_sel);
            time_sel = (TextView) findViewById(R.id.time_sel);
            ago_sel = (TextView) findViewById(R.id.ago_sel);
            fec_sel = (TextView) findViewById(R.id.fec_sel);
            closeButton = (Button) findViewById(R.id.close_btn_reminder);
            date_pick = (ImageButton) findViewById(R.id.date_pick);
            time_pick = (ImageButton) findViewById(R.id.time_pick);
            btnRemindAgo = (ImageButton) findViewById(R.id.btnRemindAgo);
            btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);

            Calendar calendar = new GregorianCalendar(year,
                    month,
                    day,
                    hour,
                    minute);

            time = calendar.getTimeInMillis();

            name.setText(phone_no);
            phone.setText(c_name);
            date_time_sel.setText(formateDates(time));
            time_sel.setText(formateTimes(time));
            ago_sel.setText("Remind me 0 min ago");
            fec_sel.setText("Once");

            Bitmap profile_bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
            if(profile_bitmap != null){
                profile_image.setImageBitmap(profile_bitmap);
            }

            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }

            });

            date_pick.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    final View dialogView = View.inflate(ReminderEditDialog.this, R.layout.date_time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderEditDialog.this).create();
                    alertDialog.setView(dialogView);
                    alertDialog.show();

                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                            year = datePicker.getYear();
                            month = datePicker.getMonth();
                            day = datePicker.getDayOfMonth();

                            Calendar calendar = new GregorianCalendar(year,
                                    month,
                                    day,
                                    hour,
                                    minute);

                            time = calendar.getTimeInMillis();
                            date_time_sel.setText(formateDates(time));
                            alertDialog.dismiss();
                        }});

                }
            });

            time_pick.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    final View dialogView = View.inflate(ReminderEditDialog.this, R.layout.time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderEditDialog.this).create();
                    alertDialog.setView(dialogView);
                    alertDialog.show();

                    dialogView.findViewById(R.id.time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker_r);

                            hour = timePicker.getCurrentHour();
                            minute = timePicker.getCurrentMinute();

                            Calendar calendar = new GregorianCalendar(year,
                                    month,
                                    day,
                                    hour,
                                    minute);

                            time = calendar.getTimeInMillis();
                            time_sel.setText(formateTimes(time));
                            alertDialog.dismiss();
                        }});

                }
            });

            btnRemindAgo.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    final View dialogView = View.inflate(ReminderEditDialog.this, R.layout.reminder_ago, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderEditDialog.this).create();
                    alertDialog.setView(dialogView);
                    alertDialog.show();

                    RadioRealButtonGroup group = (RadioRealButtonGroup) dialogView.findViewById(R.id.groupAgo);

                    // onClickButton listener detects any click performed on buttons by touch
                    group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
                        @Override
                        public void onClickedButton(RadioRealButton button, int position) {
                            switch (position) {
                                case 0:
                                    remindAgo = 0;
                                    remUnit = "minutes";
                                    break;

                                case 1:
                                    remindAgo = 5;
                                    remUnit = "minutes";
                                    break;

                                case 2:
                                    remindAgo = 10;
                                    remUnit = "minutes";
                                    break;

                                case 3:
                                    remindAgo = 15;
                                    remUnit = "minutes";
                                    break;

                                case 4:
                                    remindAgo = 30;
                                    remUnit = "minutes";
                                    break;

                                case 5:
                                    remindAgo = 1;
                                    remUnit = "hour";
                                    break;
                            }

                            ago_sel.setText("Remind me "+remindAgo+" "+ remUnit + " ago");
                        }
                    });

                    dialogView.findViewById(R.id.rem_ago_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                }
            });

            btnRepeat.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    final View dialogView = View.inflate(ReminderEditDialog.this, R.layout.repeat_reminder, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderEditDialog.this).create();
                    alertDialog.setView(dialogView);
                    alertDialog.show();

                    dialogView.findViewById(R.id.repeat_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//
//                            TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker_r);
//
//                            int hour = timePicker.getCurrentHour();
//                            int minute = timePicker.getCurrentMinute();
                            alertDialog.dismiss();
                        }
                    });

                }
            });

            dialog_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message = sendMgs.getText().toString();

                    Long tsLong = System.currentTimeMillis()/1000;

                    String key = mRootRef.child("reminder").child(mPhoneNo).push().getKey();
                    Reminder reminder = new Reminder(mPhoneNo, mPhoneNo, message, time, remindAgo, false, "none", false, tsLong.toString(), false);
                    Map<String, Object> postValues = reminder.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/reminder/" + mPhoneNo + "/" + key, postValues);

                    mRootRef.updateChildren(childUpdates);

                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(ReminderEditDialog.this, ReminderAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", time);
                    //myIntent.putExtra("alarm_mgs", message);
                    pendingIntent = PendingIntent.getActivity(ReminderEditDialog.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

                    message = "";

                    Toast.makeText(ReminderEditDialog.this, "Reminder Set Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            dialog_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    message = sendMgs.getText().toString();

//                    DatabaseReference mTo = mRootRef.child("reminder").child(phone_no);
//                    Reminder messages_to = new Reminder(mPhoneNo, phone_no, message, time, remindAgo, false, ""+ServerValue.TIMESTAMP);
//                    mTo.setValue(messages_to);
//
//                    DatabaseReference hTo = mRootRef.child("reminder").child(mPhoneNo);
//                    Reminder messages_ho = new Reminder(mPhoneNo, phone_no, message, time, remindAgo, false, ""+ServerValue.TIMESTAMP);
//                    hTo.setValue(messages_ho);

                    Long tsLong = System.currentTimeMillis()/1000;

                    String key = mRootRef.child("reminder").child(phone_no).push().getKey();
                    Reminder reminder = new Reminder(mPhoneNo, phone_no, message, time, remindAgo, false, "none", false, tsLong.toString(), false);
                    Map<String, Object> postValues = reminder.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/reminder/" + phone_no + "/" + key, postValues);
                    childUpdates.put("/reminder/" + mPhoneNo + "/" + key, postValues);

                    mRootRef.updateChildren(childUpdates);

                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(ReminderEditDialog.this, ReminderAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", time);
                    //myIntent.putExtra("alarm_mgs", message);
                    pendingIntent = PendingIntent.getActivity(ReminderEditDialog.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

                    message = "";

                    Toast.makeText(ReminderEditDialog.this, "Reminder Set Successfully", Toast.LENGTH_SHORT).show();
                    finish();

                }
            });
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
            //setResultData(phone_no);
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

    private void initializeContent()
    {
        //tv_client   = (TextView) findViewById(R.id.tv_client);
        dialog_save   = (Button) findViewById(R.id.dialog_save);
        dialog_share   = (Button) findViewById(R.id.dialog_share);
        sendMgs = (EditText)findViewById(R.id.editTextDialogUserInput);
        //TextView tview = (TextView)findViewById(R.id.textview1);
        //String result = sendMgs.getText().toString();
        //tview.setText(result);
    }

    private String formateDates(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy");
        String formattedDate = formatter.format(date);

        return formattedDate;
    }

    private String formateTimes(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = formatter.format(date);

        return formattedDate;
    }
    private Emitter.Listener onRecive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ReminderEditDialog.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }

                    ReminderEditDialog.this.finish();
                    System.exit(0);
                }
            });
        }
    };

}

