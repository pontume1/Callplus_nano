package com.luncher.bounjour.ringlerr.activity;

/**
 * Created by santanu on 11/11/17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Reminder;
import com.luncher.bounjour.ringlerr.services.MyReminderNotificationReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class ReminderDialogGroup extends Activity {
    TextView name;
    TextView phone;
    List<String> phone_no = new ArrayList<>();
    List<String> c_name = new ArrayList<>();
    String message;
    EditText sendMgs;
    Button dialog_save;
    Button dialog_share;
    //Button dialog_contact;
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
    int timeAgo;

    private ImageView profile_image;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private String mCurrentUserId;
    private String mPhoneNo;
    SessionManager session;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private PendingIntent pendingNotiIntent;
    JSONObject shared_with = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("test","outgoing custom dialog a");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference profile = mRootRef.child("users/"+ mCurrentUserId);

        session = new SessionManager(ReminderDialogGroup.this);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        mPhoneNo = user.get(SessionManager.KEY_PHONE);

        message = getIntent().getExtras().getString("message");
        time = getIntent().getExtras().getLong("timestamp");
        remindAgo = getIntent().getExtras().getInt("timeAgo");

        Calendar rnow = Calendar.getInstance();
        rnow.setTimeInMillis(time);
        year = rnow.get(Calendar.YEAR);
        month = rnow.get(Calendar.MONTH);
        day = rnow.get(Calendar.DAY_OF_MONTH);
        hour = rnow.get(Calendar.HOUR);
        minute = rnow.get(Calendar.MINUTE);


        phone_no = getIntent().getStringArrayListExtra("phone_nos");
        String share_name = "Sharing with ";

        for (int i=0; i<phone_no.size(); i++) {
            String c_no = phone_no.get(i);
            try {
                shared_with.put(c_no, "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String c_na = getContactName(this, c_no);
            String name = "";
            if(c_no == null){
                c_name.add(c_no);
                name = c_no;
            }else{
                c_name.add(c_na);
                name = c_na;
            }

            if(i==0){
                share_name = name;
            }else if(i==1){
                share_name += " and "+name;
            }else if(i==2){
                int num_s = phone_no.size()-2;
                share_name += " +"+num_s+" others";
            }

        }

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(false);
            super.onCreate(savedInstanceState);

            setContentView(R.layout.reminder_group);

            initializeContent();
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

            profile_image = (ImageView) findViewById(R.id.profile_image);
            name = (TextView) findViewById(R.id.name);
            //phone = (TextView) findViewById(R.id.phone);
            date_time_sel = (TextView) findViewById(R.id.date_time_sel);
            time_sel = (TextView) findViewById(R.id.time_sel);
            ago_sel = (TextView) findViewById(R.id.ago_sel);
            fec_sel = (TextView) findViewById(R.id.fec_sel);
            closeButton = (Button) findViewById(R.id.close_btn_reminder);
            date_pick = (ImageButton) findViewById(R.id.date_pick);
            time_pick = (ImageButton) findViewById(R.id.time_pick);
            btnRemindAgo = (ImageButton) findViewById(R.id.btnRemindAgo);
            btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);

            sendMgs.setText(message);
            name.setText(share_name);
//            phone.setText(c_name);
            date_time_sel.setText(formateDates(time));
            time_sel.setText(formateTimes(time));

            switch (remindAgo) {
                case 0:
                    remindAgo = 0;
                    remUnit = "minutes";
                    break;

                case 5:
                    remindAgo = 5;
                    remUnit = "minutes";
                    break;

                case 10:
                    remindAgo = 10;
                    remUnit = "minutes";
                    break;

                case 15:
                    remindAgo = 15;
                    remUnit = "minutes";
                    break;

                case 30:
                    remindAgo = 30;
                    remUnit = "minutes";
                    break;

                case 1:
                    remindAgo = 1;
                    remUnit = "hour";
                    break;
            }

            ago_sel.setText("Remind me "+remindAgo+" "+ remUnit + " ago");

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

//            dialog_contact.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    choosePhoneNo();
//                }
//
//            });

            date_pick.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    final View dialogView = View.inflate(ReminderDialogGroup.this, R.layout.date_time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderDialogGroup.this).create();
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

                    final View dialogView = View.inflate(ReminderDialogGroup.this, R.layout.time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderDialogGroup.this).create();
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

                    final View dialogView = View.inflate(ReminderDialogGroup.this, R.layout.reminder_ago, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderDialogGroup.this).create();
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

                    final View dialogView = View.inflate(ReminderDialogGroup.this, R.layout.repeat_reminder, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ReminderDialogGroup.this).create();
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

                    Date date = new Date(time);
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
                    String formattedDate = formatter.format(date);

                    if(message.equals("")){
                        Toast.makeText(ReminderDialogGroup.this, "Please type your message", Toast.LENGTH_SHORT).show();
                        sendMgs.requestFocus();
                        return;
                    }

                    Long tsLong = System.currentTimeMillis()/1000;

                    String key = mRootRef.child("reminder").child(mPhoneNo).push().getKey();
                    Reminder reminder = new Reminder(mPhoneNo, mPhoneNo, message, time, remindAgo, "", false, "none", false, tsLong.toString(), false);
                    Map<String, Object> postValues = reminder.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/reminder/" + mPhoneNo + "/" + key, postValues);

                    mRootRef.updateChildren(childUpdates);

                    MyDbHelper myDbHelper = new MyDbHelper(ReminderDialogGroup.this, null, null, 1);
                    long Id = myDbHelper.addReminder(message, time, "", key);

                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(ReminderDialogGroup.this, ReminderAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", time);
                    //myIntent.putExtra("alarm_mgs", message);
                    pendingIntent = PendingIntent.getActivity(ReminderDialogGroup.this, (int)Id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

                    Long noti_time = time;
                    if(remindAgo == 1){
                       noti_time = time - (60 * 60 * 1000);
                    }else if(remindAgo == 0){
                        noti_time = time - (5 * 60 * 1000);
                    }else {
                        noti_time = time - (remindAgo * 60 * 1000);
                    }

//                    Intent notifyIntent = new Intent(ReminderDialogGroup.this, MyReminderNotificationReceiver.class);
//                    notifyIntent.putExtra("message", message);
//                    notifyIntent.putExtra("formattedDate", formattedDate);
//                    notifyIntent.putExtra("timestamp", time);
//                    notifyIntent.putExtra("shared_with", shared_with.toString());
//                    pendingNotiIntent = PendingIntent.getBroadcast(ReminderDialogGroup.this, (int)Id+120, notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//                    alarmManager.setExact(AlarmManager.RTC, noti_time, pendingNotiIntent);

                    message = "";

                    Toast.makeText(ReminderDialogGroup.this, "Reminder Set Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            dialog_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    message = sendMgs.getText().toString();

                    Date date = new Date(time);
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
                    String formattedDate = formatter.format(date);

                    if(message.equals("")){
                        Toast.makeText(ReminderDialogGroup.this, "Please type your message", Toast.LENGTH_SHORT).show();
                        sendMgs.requestFocus();
                        return;
                    }

                    Long tsLong = System.currentTimeMillis()/1000;

                    String key = mRootRef.child("reminder").child(mPhoneNo).push().getKey();

                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = null;
                    for (int i=0; i<phone_no.size(); i++) {
                        String c_no = phone_no.get(i);

                        Reminder reminder = new Reminder(mPhoneNo, c_no, message, time, remindAgo, shared_with.toString(), false, "none", false, tsLong.toString(), false);
                        postValues = reminder.toMap();
                        
                        childUpdates.put("/reminder/" + c_no + "/" + key, postValues);
                    }

                    childUpdates.put("/reminder/" + mPhoneNo + "/" + key, postValues);

                    mRootRef.updateChildren(childUpdates);

                    MyDbHelper myDbHelper = new MyDbHelper(ReminderDialogGroup.this, null, null, 1);
                    long Id = myDbHelper.addReminder(message, time, shared_with.toString(), key);


                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(ReminderDialogGroup.this, ReminderAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", time);
                    myIntent.putExtra("alarm_id", Id);
                    //myIntent.putExtra("alarm_mgs", message);
                    pendingIntent = PendingIntent.getActivity(ReminderDialogGroup.this, (int)Id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

                    Long noti_time = time;
                    if(remindAgo == 1){
                        noti_time = time - (60 * 60 * 1000);
                    }else if(remindAgo == 0){
                        noti_time = time - (5 * 60 * 1000);
                    }else {
                        noti_time = time - (remindAgo * 60 * 1000);
                    }

//                    Intent notifyIntent = new Intent(ReminderDialogGroup.this, MyReminderNotificationReceiver.class);
//                    notifyIntent.putExtra("message", message);
//                    notifyIntent.putExtra("formattedDate", formattedDate);
//                    notifyIntent.putExtra("timestamp", time);
//                    notifyIntent.putExtra("shared_with", shared_with.toString());
//                    pendingNotiIntent = PendingIntent.getBroadcast(ReminderDialogGroup.this, (int)Id+120, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//                    alarmManager.setExact(AlarmManager.RTC, noti_time, pendingNotiIntent);

                    message = "";

                    Toast.makeText(ReminderDialogGroup.this, "Reminder Set Successfully", Toast.LENGTH_SHORT).show();
                    finish();

                }
            });
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
            //setResultData(phone_no);
        }
    }

    private void choosePhoneNo() {
        Intent intent = new Intent(this, SelectReminderContact.class);
        intent.putExtra("message", message);
        intent.putExtra("time", time);
        intent.putExtra("timeago", remindAgo);
        startActivity(intent);
        finish();
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
        //dialog_contact   = (Button) findViewById(R.id.dialog_contact);
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

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    private Emitter.Listener onRecive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ReminderDialogGroup.this.runOnUiThread(new Runnable() {
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

                    ReminderDialogGroup.this.finish();
                    System.exit(0);
                }
            });
        }
    };

}

