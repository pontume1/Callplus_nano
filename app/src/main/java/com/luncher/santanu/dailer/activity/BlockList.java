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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.luncher.santanu.dailer.MyDbHelper;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.SessionManager;
import com.luncher.santanu.dailer.model.Blocks;
import com.luncher.santanu.dailer.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BlockList extends AppCompatActivity {

    private RecyclerView reminder_list;
    private LinearLayoutManager layoutManager;
    private Query mReminderDatabase;
    private FirebaseRecyclerAdapter<Blocks, ReminderViewHolder> firebaseRecyclerAdapter;
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

        setContentView(R.layout.block_list);

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

        mReminderDatabase = FirebaseDatabase.getInstance().getReference().child("blocks").child(mPhoneNo);
        mReminderDatabase.keepSynced(true);

        FirebaseRecyclerOptions<Blocks> options =
                new FirebaseRecyclerOptions.Builder<Blocks>()
                        //.setLayout(R.layout.reminder_single_list)
                        .setQuery(mReminderDatabase, Blocks.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blocks, ReminderViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder reminderViewHolder, int position, @NonNull Blocks blocks) {

                String from = blocks.getFrom();
                String block_no = blocks.getBlock_no();
                String name = blocks.getName();
                String reminderKey = firebaseRecyclerAdapter.getRef(position).getKey();
                Long dateTime = Long.valueOf(blocks.getReminderTime())*1000;

                Date date_time = new Date(dateTime);
                SimpleDateFormat date_time_formatter = new SimpleDateFormat("d MMM, HH:mm");
                String formattedDateTime = date_time_formatter.format(date_time);

                reminderViewHolder.setDisplayMessage(name, block_no, formattedDateTime, reminderKey);

            }

            @Override
            public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.block_single_list, parent, false);

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

        public TextView firstLine;
        public TextView secondLine;
        public ImageButton deleteButton;


        public ReminderViewHolder(View itemView) {
            super(itemView);

            firstLine = (TextView) itemView.findViewById(R.id.firstLine);
            secondLine = (TextView) itemView.findViewById(R.id.secondLine);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);

        }

        public void setDisplayMessage(String name, final String block_no, String formattedDateTime, final String reminderKey){

            firstLine.setText(name);
            secondLine.setText(block_no);
            Long tsLong = System.currentTimeMillis();

            deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {

                    MyDbHelper myDbHelper;
                    myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                    myDbHelper.removeBlockNumber(block_no);
                    mRootRef.child("blocks").child(mPhoneNo).child(reminderKey).removeValue();

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
