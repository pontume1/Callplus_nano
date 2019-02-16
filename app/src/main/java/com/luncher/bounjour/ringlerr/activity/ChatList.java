package com.luncher.bounjour.ringlerr.activity;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.MainActivity;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.adapter.MessageAdapter;
import com.luncher.bounjour.ringlerr.model.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatList extends AppCompatActivity {

    private EditText chat_edit_text1;
    private MessageAdapter mAdapter;
    private String mPhoneNo;
    private String phone_no;
    private String chat_message;
    private String talkTime = "None";
    private List<Message> messageList = new ArrayList<>();
    private NestedScrollView scrollview;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_list);
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);
        phone_no = Objects.requireNonNull(getIntent().getExtras()).getString("phone_no");
        String name = getIntent().getExtras().getString("name");

        setTitle(name);

        chat_edit_text1 = findViewById(R.id.chat_edit_text1);
        CardView enter_chat1 = findViewById(R.id.enter_chat1);
        RecyclerView message_list = findViewById(R.id.message_list);
        scrollview = findViewById(R.id.scroll);
        message_list.setHasFixedSize(true);

        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        message_list.setLayoutManager(layoutManager);

        mAdapter = new MessageAdapter(this, messageList);

        message_list.setLayoutManager(layoutManager);
        message_list.setItemAnimator(new DefaultItemAnimator());
        //set adapter to recyclerview
        message_list.setAdapter(mAdapter);
        //call method to fetch data from db and add to recyclerview
        prepareData();

        enter_chat1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {

                    chat_message = chat_edit_text1.getText().toString();
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();

                    chat_edit_text1.setText("");

                    if(!chat_message.equals("")) {
                        String key = mRootRef.child("chats").child(mPhoneNo).child(phone_no).push().getKey();
                        //String notiKey = mRootRef.child("notification").child(phone_no).push().getKey();

                        MyDbHelper myDbHelper = new MyDbHelper(ChatList.this, null, 4);
                        final long id = myDbHelper.addMessages(chat_message, mPhoneNo, "none", "none", talkTime, tsLong, phone_no);

                        Message chat = new Message((int)id, mPhoneNo, phone_no, chat_message, "none", "none", "false", ts, talkTime);
                        Map<String, Object> postValues = chat.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/chats/" + phone_no + "/" + mPhoneNo + "/" + key, postValues);
                        childUpdates.put("/chats/" + mPhoneNo + "/" + phone_no + "/" + key, postValues);

                        childUpdates.put("/notification/" + phone_no, postValues);
                        prepareData();

                        mRootRef.updateChildren(childUpdates);
                    }

                }

            });
    }

    private void prepareData() {
        MyDbHelper myDbHelper = new MyDbHelper(ChatList.this, null, 5);
        List<Message> sec = myDbHelper.getAllMessages(phone_no);
        messageList.clear();

        if (sec.size() > 0) {
            //loop through contents
            for (int i = 0; i < sec.size(); i++) {
                //add data to list used in adapter
                messageList.add(sec.get(i));
                //notify data change
                mAdapter.notifyDataSetChanged();
            }
        }
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                //chat_edit_text1.requestFocus();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            Intent mainintent = new Intent(this, MainActivity.class);
            startActivity(mainintent);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainintent = new Intent(this, MainActivity.class);
        startActivity(mainintent);
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

    @Override
    public void onResume() {
        super.onResume();

        MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext(), null, 1);
        myDbHelper.removeNotification(phone_no);
        chat_edit_text1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                        chat_edit_text1.requestFocus();
                    }
                });
            }
            }
        });

        chatNotification(mPhoneNo);
    }

    private void chatNotification(final String myPhone){

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        Query messageRef = mRootRef.child("notification").child(myPhone);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Message chat = dataSnapshot.getValue(Message.class);

                if (chat != null){
                    if (chat.seen != null && chat.seen.equals("false")) {
                        prepareData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

