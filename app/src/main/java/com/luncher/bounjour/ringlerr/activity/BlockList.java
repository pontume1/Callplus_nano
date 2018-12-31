package com.luncher.bounjour.ringlerr.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Blocks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView no_data;

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
        no_data = (TextView) findViewById(R.id.no_data);

        reminder_list.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
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

        if (firebaseRecyclerAdapter != null) {
            reminder_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                if(firebaseRecyclerAdapter.getItemCount() == 0){
                    no_data.setVisibility(View.VISIBLE);
                }

                }
            }, 4000);
        }

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

        private int getCount(int count){
            return count;
        }

        public void setDisplayMessage(String name, final String block_no, String formattedDateTime, final String reminderKey){

            firstLine.setText(name);
            secondLine.setText(block_no);
            Long tsLong = System.currentTimeMillis();

            deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {

                    DatabaseReference blockrootRef = mRootRef.child("block_count/"+ block_no +"/count");
                    blockrootRef.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Object count = dataSnapshot.getValue();
                                    String bl_count = "0";
                                    if(count!=null){
                                        bl_count = count.toString();
                                    }
                                    int block_count = getCount(Integer.parseInt(bl_count));
                                    int total_block = block_count-1;
                                    mRootRef.child("block_count").child(block_no).child("count").setValue(total_block);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

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
