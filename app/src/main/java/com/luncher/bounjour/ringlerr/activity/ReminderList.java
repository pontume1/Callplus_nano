package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
//import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.model.Reminder;
import com.luncher.bounjour.ringlerr.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
//import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReminderList extends Activity {

    private RecyclerView reminder_list;
    private FirebaseRecyclerAdapter<Reminder, ReminderViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    private String filterHeader = "Today";
    private String search_text = "";

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    //private PendingIntent pendingNotiIntent;
    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reminder_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        EditText contact_search = findViewById(R.id.contact_search);
        TextView no_data = findViewById(R.id.no_data);
        Button pending_button = findViewById(R.id.pending_button);

        no_data.setVisibility(View.GONE);
        contact_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                search_text = query.toString().toLowerCase();
                if (firebaseRecyclerAdapter!= null) {
                    firebaseRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.filter);
        //create a list of items for the spinner.
        String[] items = new String[]{"Today", "Upcoming", "Past"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_selected, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                filterHeader = (String) parent.getItemAtPosition(position);
                if (firebaseRecyclerAdapter!= null) {
                    firebaseRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


//        //get the spinner from the xml.
//        Spinner dropdown_stat = findViewById(R.id.filter_stat);
//        //create a list of items for the spinner.
//        String[] items_stat = new String[]{"All", "Missed", "Accepted", "Pending"};
//        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//        //There are multiple variations of this, but this is the basic variant.
//        ArrayAdapter<String> adapter_stat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_stat);
//        //set the spinners adapter to the previously created one.
//        dropdown_stat.setAdapter(adapter_stat);
//        dropdown_stat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                filterHeaderStat = (String) parent.getItemAtPosition(position);
//                firebaseRecyclerAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//            }
//        });

        pending_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View itemView) {
                final Intent intent1 = new Intent(ReminderList.this, PendingReminderList.class);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent1);
                    }
                }, 100);
            }
        });

        mRootRef = FirebaseDatabase.getInstance().getReference();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);

        reminder_list = findViewById(R.id.reminder_list);

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ProgressDialog mProgressDialog = new ProgressDialog(ReminderList.this);
        mProgressDialog.setTitle("Updating Profile...");
        mProgressDialog.setMessage("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        reminder_list.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        reminder_list.setLayoutManager(layoutManager);

        Query mReminderDatabase = FirebaseDatabase.getInstance().getReference().child("reminder").child(mPhoneNo).orderByChild("time");
        mReminderDatabase.keepSynced(true);

        FirebaseRecyclerOptions<Reminder> options =
                new FirebaseRecyclerOptions.Builder<Reminder>()
                        //.setLayout(R.layout.reminder_single_list)
                        .setQuery(mReminderDatabase, Reminder.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Reminder, ReminderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder reminderViewHolder, int position, @NonNull Reminder reminder) {

                Long timestamp = reminder.getTime();
                String from = reminder.getFrom();
                String to = reminder.getTo();
                String shared_with = reminder.getShared_with();
                String Is_accepted = reminder.getIs_accepted();
                String reminderKey = firebaseRecyclerAdapter.getRef(position).getKey();
                String statusIcon = "pending";
                //Long dateTime = Long.valueOf(reminder.getReminderTime())*1000;

                Calendar now = Calendar.getInstance();
                Calendar postTime = Calendar.getInstance();
                postTime.setTimeInMillis(timestamp);
                String header;

                if (now.get(Calendar.DATE) == postTime.get(Calendar.DATE) && now.get(Calendar.MONTH) == postTime.get(Calendar.MONTH)) {
                    header = "Today";
                }else if (System.currentTimeMillis() - timestamp > 0  ){
                    header =  "Past";
                }else{
                    header =  "Upcoming";
                }

                Date date = new Date(timestamp);
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.US);
                String formattedDate = formatter.format(date);

//                Date date_time = new Date(dateTime);
//                SimpleDateFormat date_time_formatter = new SimpleDateFormat("d MMM, HH:mm", Locale.US);
//                String formattedDateTime = date_time_formatter.format(date_time);
                Boolean showAcceptBtn = false;

                String share_text = "";
                if(from.equals(mPhoneNo) && to.equals(mPhoneNo)){
                    share_text = getResources().getString(R.string.set_for_myswlf);
                    statusIcon = "accepted";
                }else if(!from.equals(mPhoneNo)){
                    share_text = getResources().getString(R.string.share_by)+" "+getContactName(getApplicationContext(), from);

                    mRootRef.child("reminder").child(from).child(reminderKey).child("is_seen").setValue(true);
                    mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).child("is_seen").setValue(true);

                    switch (Is_accepted) {
                        case "none":
                            showAcceptBtn = true;
                            break;
                        case "false":
                            statusIcon = "rejected";
                            break;
                        default:
                            statusIcon = "accepted";
                            break;
                    }
                }else if(from.equals(mPhoneNo) && !to.equals(mPhoneNo)){

                    String my_shared_text = "";
                    try {

                        JSONObject object = new JSONObject(shared_with.trim());
                        JSONArray keys = object.names ();

                        for (int i = 0; i < keys.length (); ++i) {

                            String ph_key = keys.getString (i); // Here's your key
                            //String ac_value = object.getString (ph_key); // Here's your value
                            String current_name = getContactName(getApplicationContext(), ph_key);

                            if(i==0){
                                my_shared_text += current_name;
                            }

                            if(i==1){
                                my_shared_text += " and "+current_name;
                            }

                            if(i==2){
                                int rem_total = keys.length()-2;
                                my_shared_text += " +"+rem_total+" others";
                            }

                        }

                    } catch (Throwable ignored) {

                    }
                    share_text = getResources().getString(R.string.share_with)+" "+my_shared_text;
                    statusIcon = "hide";
                }

                reminderViewHolder.setDisplayMessage(reminder.getMessage(), formattedDate, share_text, timestamp, showAcceptBtn, from, to, reminderKey, statusIcon, header, shared_with);

            }

            @Override
            public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reminder_single_list, parent, false);

                return new ReminderViewHolder(view);
            }

        };

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }, 3000);

        reminder_list.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();

//        if (firebaseRecyclerAdapter != null) {
//            reminder_list.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(firebaseRecyclerAdapter.getItemCount() == 0){
//                        no_data.setVisibility(View.VISIBLE);
//                    }else{
//                        no_data.setVisibility(View.GONE);
//                    }
//
//                }
//            }, 1000);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if(!isRunning(ReminderList.this)) {
//            Intent mainintent = new Intent(this, MainActivity.class);
//            startActivity(mainintent);
//        }
    }

//    public boolean isRunning(Context ctx) {
//        ActivityManager activityManager =
//                (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
//
//        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
//
//        for (ActivityManager.RunningTaskInfo task : tasks) {
//            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
//                return true;
//        }
//
//        return false;
//    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder{

        public TextView messageView;
        TextView date_timeView;
        TextView sharedView;
        public LinearLayout rej_acc_layout;
        public Button button_missed;
        //public ImageButton alarm_del;
        private Button button_acc;
        private Button button_rej;
        private ImageView status_icon;
        private RelativeLayout main_layout;
        private LinearLayout left_r_layout;
        private TextView buttonViewOption;

        ReminderViewHolder(View itemView) {
            super(itemView);

            messageView = itemView.findViewById(R.id.message_rem_list);
            date_timeView = itemView.findViewById(R.id.date_time);
            sharedView = itemView.findViewById(R.id.shared);
            rej_acc_layout = itemView.findViewById(R.id.rej_acc_layout);
            button_missed = itemView.findViewById(R.id.button_missed);
            main_layout = itemView.findViewById(R.id.main_layout);
            left_r_layout = itemView.findViewById(R.id.left_r_layout);
//            alarm_del = (ImageButton) itemView.findViewById(R.id.alarm_del);
            button_acc = itemView.findViewById(R.id.button_acc);
            button_rej = itemView.findViewById(R.id.button_rej);
            status_icon = itemView.findViewById(R.id.status_icon);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);

        }

        public void setDisplayMessage(final String message, final String formattedDate, final String share_text,
                                      final Long timestamp, Boolean showAcceptBtn, final String from,
                                      final String to, final String reminderKey, String statusIcon, String header,
                                      final String shared_with){

            messageView.setText(message);
            date_timeView.setText(formattedDate);
            sharedView.setText(share_text);
            Long tsLong = System.currentTimeMillis();

            if(header.equals(filterHeader)){
                if(search_text.equals("")){
                    main_layout.setVisibility(View.VISIBLE);
                    main_layout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }else if(message.toLowerCase().contains(search_text) || share_text.toLowerCase().contains(search_text)){
                    main_layout.setVisibility(View.VISIBLE);
                    main_layout.setLayoutParams(new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }else{
                    main_layout.setVisibility(View.GONE);
                    main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }else{
                main_layout.setVisibility(View.GONE);
                main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            String filterHeaderStat = "All";
            if(statusIcon.equals("rejected") && !filterHeaderStat.equals("All")){
                main_layout.setVisibility(View.GONE);
                main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

            if(showAcceptBtn) {
                if (tsLong < timestamp) {
                    rej_acc_layout.setVisibility(View.VISIBLE);
                    // alarm_del.setVisibility(View.GONE);
                    status_icon.setVisibility(View.GONE);
                    button_missed.setVisibility(View.GONE);
                    //if(filterHeaderStat.equals("Missed") || filterHeaderStat.equals("Pending")){
                    main_layout.setVisibility(View.GONE);
                    main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    //}
                } else {
                    rej_acc_layout.setVisibility(View.GONE);
                    // alarm_del.setVisibility(View.VISIBLE);
                    status_icon.setVisibility(View.GONE);
                    button_missed.setVisibility(View.VISIBLE);
                    if(filterHeaderStat.equals("Accepted") || filterHeaderStat.equals("Pending")){
                        main_layout.setVisibility(View.GONE);
                        main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }

                }
            }else{
                if(statusIcon.equals("pending") && tsLong > timestamp) {
                    rej_acc_layout.setVisibility(View.GONE);
                    //alarm_del.setVisibility(View.VISIBLE);
                    button_missed.setVisibility(View.VISIBLE);
                    status_icon.setVisibility(View.GONE);
                    if(filterHeaderStat.equals("Accepted") || filterHeaderStat.equals("Pending")){
                        main_layout.setVisibility(View.GONE);
                        main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }

                }else {
                    rej_acc_layout.setVisibility(View.GONE);
                    //alarm_del.setVisibility(View.VISIBLE);
                    status_icon.setVisibility(View.VISIBLE);
                    button_missed.setVisibility(View.GONE);
                    if(filterHeaderStat.equals("Missed")){
                        main_layout.setVisibility(View.GONE);
                        main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }


                    if(statusIcon.equals("accepted") && filterHeaderStat.equals("Pending")){
                        main_layout.setVisibility(View.GONE);
                        main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }else if(statusIcon.equals("pending") && filterHeaderStat.equals("Accepted")){
                        main_layout.setVisibility(View.GONE);
                        main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }
                }
            }

            switch (statusIcon) {
                case "accepted":
                    status_icon.setImageResource(R.drawable.ic_accepted);
                    break;
                case "rejected":
                    status_icon.setImageResource(R.drawable.ic_rejected);
                    break;
                case "hide":
                    //status_icon.setVisibility(View.GONE);
                    status_icon.setImageResource(R.drawable.ic_blank);
                    break;
                default:
                    status_icon.setImageResource(R.drawable.ic_panding);
                    break;
            }

            left_r_layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {
                    final Intent detail_intent = new Intent(ReminderList.this, ReminderDetail.class);
                    detail_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    detail_intent.putExtra("message", message);
                    detail_intent.putExtra("formattedDate", formattedDate);
                    detail_intent.putExtra("timestamp", timestamp);
                    detail_intent.putExtra("shared_with", shared_with);
                    //context.startActivity(intent1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(detail_intent);
                        }
                    }, 100);
                }

            });
            button_acc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {

                    rej_acc_layout.setVisibility(View.GONE);
                    //alarm_del.setVisibility(View.VISIBLE);
                    status_icon.setVisibility(View.VISIBLE);

                    MyDbHelper myDbHelper = new MyDbHelper(ReminderList.this, null, null, 1);
                    long Id = myDbHelper.addReminder(message, timestamp, shared_with, reminderKey);

                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(ReminderList.this, ReminderAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", timestamp);
                    //myIntent.putExtra("alarm_mgs", message);
                    pendingIntent = PendingIntent.getActivity(ReminderList.this, (int) Id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);

//                    Long noti_time = timestamp;
//                    if(remindAgo == 1){
//                        noti_time = time - (60 * 60 * 1000);
//                    }else if(remindAgo == 0){
//                        noti_time = time - (5 * 60 * 1000);
//                    }else {
//                        noti_time = time - (remindAgo * 60 * 1000);
//                    }
//
//                    Intent notifyIntent = new Intent(ReminderList.this, MyReminderNotificationReceiver.class);
//                    pendingNotiIntent = PendingIntent.getBroadcast(ReminderList.this, (int)Id+120, notifyIntent,0);
//
//                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//                    alarmManager.setExact(AlarmManager.RTC, noti_time, pendingNotiIntent);


                    try {

                        JSONObject object = new JSONObject(shared_with.trim());
                        object.put(mPhoneNo, "1");
                        JSONArray keys = object.names ();

                        for (int i = 0; i < keys.length (); ++i) {

                            String ph_key = keys.getString (i); // Here's your key
                            mRootRef.child("reminder").child(ph_key).child(reminderKey).child("shared_with").setValue(object.toString());

                        }

                        mRootRef.child("reminder").child(from).child(reminderKey).child("shared_with").setValue(object.toString());

                    } catch (Throwable ignored) {

                    }

                    mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).child("is_accepted").setValue("true");
                    mRootRef.child("reminder").child(from).child(reminderKey).child("is_accepted").setValue("true");

                    mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).child("taken").setValue(true);
                    mRootRef.child("reminder").child(from).child(reminderKey).child("taken").setValue(true);

                }

            });

            button_rej.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {

                    rej_acc_layout.setVisibility(View.GONE);
                    //alarm_del.setVisibility(View.VISIBLE);
                    status_icon.setVisibility(View.VISIBLE);

                    try {

                        JSONObject object = new JSONObject(shared_with.trim());
                        object.put(mPhoneNo, "2");
                        JSONArray keys = object.names ();

                        for (int i = 0; i < keys.length (); ++i) {

                            String ph_key = keys.getString (i); // Here's your key
                            mRootRef.child("reminder").child(ph_key).child(reminderKey).child("shared_with").setValue(object.toString());

                        }

                        mRootRef.child("reminder").child(from).child(reminderKey).child("shared_with").setValue(object.toString());

                    } catch (Throwable ignored) {

                    }

                    mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).child("is_accepted").setValue("false");
                    mRootRef.child("reminder").child(from).child(reminderKey).child("is_accepted").setValue("false");

                    mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).child("taken").setValue(true);
                    mRootRef.child("reminder").child(from).child(reminderKey).child("taken").setValue(true);

                }

            });

//            alarm_del.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View itemView) {
//
//                    mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).removeValue();
//                    if(mPhoneNo.equals(from)){
//                        mRootRef.child("reminder").child(to).child(reminderKey).removeValue();
//                    }else{
//                        mRootRef.child("reminder").child(from).child(reminderKey).child("is_deleted").setValue(true);
//                    }
//
//                }
//
//            });

            buttonViewOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(ReminderList.this, buttonViewOption);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.reminder_option_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_menu:
                                    //handle menu1 click
                                    mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).removeValue();
                                    if (mPhoneNo.equals(from)) {

                                        try {

                                            JSONObject object = new JSONObject(shared_with.trim());
                                            JSONArray keys = object.names ();

                                            for (int i = 0; i < keys.length (); ++i) {

                                                String ph_key = keys.getString (i); // Here's your key
                                                mRootRef.child("reminder").child(ph_key).child(reminderKey).removeValue();

                                            }

                                        } catch (Throwable ignored) {

                                        }

                                    } else {
                                        mRootRef.child("reminder").child(from).child(reminderKey).child("is_deleted").setValue(true);
                                    }
                                    break;
                                case R.id.edit_menu:
                                    //handle menu2 click
                                    if(mPhoneNo.equals(from)) {
                                        String ph_key = "1234";

                                        try {
                                            JSONObject object = new JSONObject(shared_with.trim());
                                            JSONArray keys = object.names ();
                                            int final_key = keys.length()-1;
                                            ph_key = keys.getString (final_key); // Here's your key
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        final Intent intent = new Intent(ReminderList.this, ReminderEditDialog.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                        intent.putExtra("phone_no", ph_key);
                                        intent.putExtra("message", message);
                                        intent.putExtra("timestamp", timestamp);
                                        intent.putExtra("share_text", share_text);
                                        intent.putExtra("shared_with", shared_with);
                                        intent.putExtra("reminderKey", reminderKey);
                                        //context.startActivity(intent1);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(intent);
                                            }
                                        }, 100);
                                    }else{
                                        Toast.makeText(ReminderList.this, "You don't have premission to edit", Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                                case R.id.add_to_calender:

                                    onAddEventClicked(message, share_text, timestamp);

                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });


        }

    }

    public void onAddEventClicked(String message, String share_text, Long timestamp) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        long startTime = timestamp;
        long endTime = timestamp + 60;

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, share_text);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, message);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

        startActivity(intent);
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

        if(!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
