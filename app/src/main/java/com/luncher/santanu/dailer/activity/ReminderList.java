package com.luncher.santanu.dailer.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luncher.santanu.dailer.MainActivity;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.model.Reminder;
import com.luncher.santanu.dailer.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReminderList extends Activity {

    private RecyclerView reminder_list;
    private LinearLayoutManager layoutManager;
    private Query mReminderDatabase;
    private FirebaseRecyclerAdapter<Reminder, ReminderViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    private String filterHeader = "Today";
    private String filterHeaderStat = "All";
    private String search_text = "";
    private EditText contact_search;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reminder_list);

        contact_search = (EditText) findViewById(R.id.contact_search);

        contact_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                search_text = query.toString().toLowerCase();
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }
        });

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.filter);
        //create a list of items for the spinner.
        String[] items = new String[]{"Today", "Upcoming", "Past"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                filterHeader = (String) parent.getItemAtPosition(position);
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        //get the spinner from the xml.
        Spinner dropdown_stat = findViewById(R.id.filter_stat);
        //create a list of items for the spinner.
        String[] items_stat = new String[]{"All", "Missed", "Accepted", "Pending"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter_stat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_stat);
        //set the spinners adapter to the previously created one.
        dropdown_stat.setAdapter(adapter_stat);
        dropdown_stat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                filterHeaderStat = (String) parent.getItemAtPosition(position);
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        mRootRef = FirebaseDatabase.getInstance().getReference();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);

        reminder_list = (RecyclerView) findViewById(R.id.reminder_list);
        reminder_list.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        reminder_list.setLayoutManager(layoutManager);

        mReminderDatabase = FirebaseDatabase.getInstance().getReference().child("reminder").child(mPhoneNo).orderByChild("time");
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
                String Is_accepted = reminder.getIs_accepted();
                String reminderKey = firebaseRecyclerAdapter.getRef(position).getKey();
                String statusIcon = "pending";
                Long dateTime = Long.valueOf(reminder.getReminderTime())*1000;

                Calendar now = Calendar.getInstance();
                Calendar postTime = Calendar.getInstance();
                postTime.setTimeInMillis(timestamp);
                String header = "";

                if (now.get(Calendar.DATE) == postTime.get(Calendar.DATE) && now.get(Calendar.MONTH) == postTime.get(Calendar.MONTH)) {
                    header = "Today";
                }else if (System.currentTimeMillis() - timestamp > 0  ){
                    header =  "Past";
                }else{
                    header =  "Upcoming";
                }

                Date date = new Date(timestamp);
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
                String formattedDate = formatter.format(date);

                Date date_time = new Date(dateTime);
                SimpleDateFormat date_time_formatter = new SimpleDateFormat("d MMM, HH:mm");
                String formattedDateTime = date_time_formatter.format(date_time);
                Boolean showAcceptBtn = false;

                String share_text = "";
                if(from.equals(mPhoneNo) && to.equals(mPhoneNo)){
                    share_text = getResources().getString(R.string.set_for_myswlf);
                    statusIcon = "accepted";
                }else if(!from.equals(mPhoneNo) && to.equals(mPhoneNo)){
                    share_text = getResources().getString(R.string.share_by)+" "+getContactName(getApplicationContext(), from)+" at "+formattedDateTime;

                    mRootRef.child("reminder").child(from).child(reminderKey).child("is_seen").setValue(true);
                    mRootRef.child("reminder").child(to).child(reminderKey).child("is_seen").setValue(true);

                    if(Is_accepted.equals("none")){
                        showAcceptBtn = true;
                    }else if(Is_accepted.equals("false")){
                        statusIcon = "rejected";
                    }else{
                        statusIcon = "accepted";
                    }
                }else if(from.equals(mPhoneNo) && !to.equals(mPhoneNo)){
                    share_text = getResources().getString(R.string.share_with)+" "+getContactName(getApplicationContext(), to)+" at "+formattedDateTime;
                    if(Is_accepted.equals("none")){
                        statusIcon = "pending";
                    }else if(Is_accepted.equals("false")){
                        statusIcon = "rejected";
                    }else{
                        statusIcon = "accepted";
                    }
                }

                reminderViewHolder.setDisplayMessage(reminder.getMessage(), formattedDate, share_text, timestamp, showAcceptBtn, from, to, reminderKey, statusIcon, header);

            }

            @Override
            public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reminder_single_list, parent, false);

                return new ReminderViewHolder(view);
            }
        };

        reminder_list.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder{

        public TextView messageView;
        public TextView date_timeView;
        public TextView sharedView;
        public LinearLayout rej_acc_layout;
        public Button button_missed;
        public ImageButton alarm_del;
        private Button button_acc;
        private Button button_rej;
        private ImageView status_icon;
        private RelativeLayout main_layout;
        private TextView buttonViewOption;

        public ReminderViewHolder(View itemView) {
            super(itemView);

            messageView = (TextView) itemView.findViewById(R.id.message_rem_list);
            date_timeView = (TextView) itemView.findViewById(R.id.date_time);
            sharedView = (TextView) itemView.findViewById(R.id.shared);
            rej_acc_layout = (LinearLayout) itemView.findViewById(R.id.rej_acc_layout);
            button_missed = (Button) itemView.findViewById(R.id.button_missed);
            main_layout = (RelativeLayout) itemView.findViewById(R.id.main_layout);
//            alarm_del = (ImageButton) itemView.findViewById(R.id.alarm_del);
            button_acc = (Button) itemView.findViewById(R.id.button_acc);
            button_rej = (Button) itemView.findViewById(R.id.button_rej);
            status_icon = (ImageView) itemView.findViewById(R.id.status_icon);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);

        }

        public void setDisplayMessage(final String message, String formattedDate, final String share_text,
                                      final Long timestamp, Boolean showAcceptBtn, final String from,
                                      final String to, final String reminderKey, String statusIcon, String header){

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
                    if(filterHeaderStat.equals("Missed") || filterHeaderStat.equals("Pending")){
                        main_layout.setVisibility(View.GONE);
                        main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    }
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

            if(statusIcon.equals("accepted")){
                status_icon.setImageResource(R.drawable.ic_accepted);
            }else if(statusIcon.equals("rejected")){
                status_icon.setImageResource(R.drawable.ic_rejected);
            }else{
                status_icon.setImageResource(R.drawable.ic_panding);
            }

            button_acc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {

                    rej_acc_layout.setVisibility(View.GONE);
                    //alarm_del.setVisibility(View.VISIBLE);
                    status_icon.setVisibility(View.VISIBLE);

                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent myIntent = new Intent(ReminderList.this, ReminderAlarmDialog.class);
                    myIntent.putExtra("alarm_mgs", message);
                    myIntent.putExtra("date_time", timestamp);
                    //myIntent.putExtra("alarm_mgs", message);
                    pendingIntent = PendingIntent.getActivity(ReminderList.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);

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
                                        mRootRef.child("reminder").child(to).child(reminderKey).removeValue();
                                    } else {
                                        mRootRef.child("reminder").child(from).child(reminderKey).child("is_deleted").setValue(true);
                                    }
                                    break;
                                case R.id.edit_menu:
                                    //handle menu2 click
                                    final Intent intent = new Intent(ReminderList.this, ReminderEditDialog.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.putExtra("phone_no", mPhoneNo);
                                        intent.putExtra("message", message);
                                        intent.putExtra("timestamp", timestamp);
                                        intent.putExtra("name", share_text);
                                        //context.startActivity(intent1);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(intent);
                                            }
                                        }, 100);

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
}
