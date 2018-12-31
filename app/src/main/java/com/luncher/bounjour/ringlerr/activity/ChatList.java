package com.luncher.bounjour.ringlerr.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatList extends AppCompatActivity {

    private RecyclerView reminder_list;
    private EditText chat_edit_text1;
    private ImageButton enter_chat1;
    private LinearLayoutManager layoutManager;
    private Query mChatDatabase;
    private FirebaseRecyclerAdapter<Message, ChatViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    private String phone_no;
    private String name;
    private String chat_message;
    private String talkTime = "None";
    private EditText contact_search;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_list);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);
        phone_no = getIntent().getExtras().getString("phone_no");
        name = getIntent().getExtras().getString("name");

        setTitle(name);

        chat_edit_text1 = (EditText) findViewById(R.id.chat_edit_text1);
        enter_chat1 = (ImageButton) findViewById(R.id.enter_chat1);
        reminder_list = (RecyclerView) findViewById(R.id.reminder_list);
        reminder_list.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reminder_list.setLayoutManager(layoutManager);

        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(mPhoneNo).child(phone_no).limitToLast(6);
        mChatDatabase.keepSynced(true);

        enter_chat1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {

                    chat_message = chat_edit_text1.getText().toString();
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();

                    chat_edit_text1.getText().clear();

                    if(!chat_message.equals("")) {
                        String key = mRootRef.child("chats").child(mPhoneNo).child(phone_no).push().getKey();
                        String notiKey = mRootRef.child("notification").child(phone_no).push().getKey();
                        Message chat = new Message(mPhoneNo, phone_no, chat_message, "none", "none", "false", ts, talkTime);
                        Map<String, Object> postValues = chat.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/chats/" + phone_no + "/" + mPhoneNo + "/" + key, postValues);
                        childUpdates.put("/chats/" + mPhoneNo + "/" + phone_no + "/" + key, postValues);

                        childUpdates.put("/notification/" + phone_no, postValues);

                        mRootRef.updateChildren(childUpdates);

                        if(firebaseRecyclerAdapter.getItemCount() > 4) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    reminder_list.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount() + 2);
                                }
                            }, 100);
                        }
                    }

                }

            });

        chat_edit_text1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if(firebaseRecyclerAdapter.getItemCount() > 4) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reminder_list.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount());
                            }
                        }, 1000);
                    }
                    //Toast.makeText(getApplicationContext(), firebaseRecyclerAdapter.getItemCount()+"", Toast.LENGTH_LONG).show();
                } else {
                    chat_edit_text1.clearFocus();
                    //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        //.setLayout(R.layout.reminder_single_list)
                        .setQuery(mChatDatabase, Message.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, ChatViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int position, @NonNull Message messages) {

                String from = messages.getFrom();
                String to = messages.getTo();
                String message = messages.getMessage();
                String image = messages.getImage();
                String type = messages.getType();
                String seen = messages.getSeen();
                String reminderKey = firebaseRecyclerAdapter.getRef(position).getKey();
                Long dateTime = Long.valueOf(messages.getDateAndTime())*1000;

                Date date_time = new Date(dateTime);
                SimpleDateFormat date_time_formatter = new SimpleDateFormat("d MMM, HH:mm");
                String formattedDateTime = date_time_formatter.format(date_time);

                chatViewHolder.setDisplayMessage(from, to, message, image, type, seen, formattedDateTime, reminderKey);

            }

            @Override
            public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view;
                if (viewType == 1) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message_sent, parent, false);
                }else{
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message_received, parent, false);
                }

                return new ChatViewHolder(view);
            }

            @Override
            public int getItemViewType(int position){

                Message message = getItem(position);
                if(message.getFrom().equals(mPhoneNo)){
                    return 1;
                }else{
                    return 0;
                }

            }
        };

        reminder_list.setAdapter(firebaseRecyclerAdapter);
        if (firebaseRecyclerAdapter != null) {
            reminder_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(firebaseRecyclerAdapter.getItemCount() > 4)
                    reminder_list.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount()-1);
                }
            }, 2000);
        }
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

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public TextView text_message_body;
        public TextView text_message_time;
        public ImageButton deleteButton;
        public ImageView photoView;
        public RelativeLayout mail_message_layout;


        public ChatViewHolder(View itemView) {
            super(itemView);

            text_message_body = (TextView) itemView.findViewById(R.id.text_message_body);
            text_message_time = (TextView) itemView.findViewById(R.id.text_message_time);
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
            photoView = (ImageView) itemView.findViewById(R.id.chat_image);
            mail_message_layout = (RelativeLayout) itemView.findViewById(R.id.mail_message_layout);

        }

        public void setDisplayMessage(String from, final String to, String message, String image,
                                      String type, String seen, String formattedDateTime, final String reminderKey){

            text_message_time.setText(formattedDateTime);
            text_message_body.setText(message);
            Long tsLong = System.currentTimeMillis();
            photoView.setImageDrawable(null);

            if(!type.equals("none")){
                String imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/"+image;
                if(type.equals("gif")) {
                    byte[] b = Base64.decode(image, Base64.DEFAULT);
                    Glide.with(ChatList.this).asGif()
                            .load(b)
                            .into(photoView);

                }else if (type.equals("libgif")) {

                    String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/animation/" + image;
                    Glide.with(ChatList.this).asGif()
                            .load(asseturl)
                            .into(photoView);
                } else if (type.equals("sticker")) {
                    String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/stickers/" + image;
                    Glide.with(ChatList.this)
                            .load(asseturl)
                            .into(photoView);
                } else {
                    byte[] b = Base64.decode(image, Base64.DEFAULT);
                    Glide.with(ChatList.this)
                            .load(b)
                            .into(photoView);
                }
            }

//            deleteButton.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View itemView) {
//
//                    mRootRef.child("chats").child(mPhoneNo).child(to).child(reminderKey).removeValue();
//
//                }
//
//            });

            mail_message_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(ChatList.this)
                            .setTitle("Delete Message")
                            .setMessage("Are you sure ?")
                            .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    mRootRef.child("chats").child(mPhoneNo).child(to).child(reminderKey).removeValue();
                                }
                            })
                            .create()
                            .show();

                    return false;
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

    @Override
    public void onResume() {
        super.onResume();

        MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
        myDbHelper.removeNotification(phone_no);
    }
}
