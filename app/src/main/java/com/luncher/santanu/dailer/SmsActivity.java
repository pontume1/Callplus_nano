package com.luncher.santanu.dailer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luncher.santanu.dailer.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class SmsActivity extends AppCompatActivity {
    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    private ArrayAdapter<ChatBubble> adapter;
    EditText input;
    String phone_no;
    String name;
    SmsManager smsManager = SmsManager.getDefault();
    boolean myMessage = true;
    private static SmsActivity inst;
    private List<ChatBubble> ChatBubbles;

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    public static SmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_activity);

        phone_no = getIntent().getExtras().getString("address");
        name = getIntent().getExtras().getString("name");

        final ActionBar actionBar = getSupportActionBar();
        // actionBar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        // titleTextView
        TextView titleTextView = new TextView(actionBar.getThemedContext());

        titleTextView.setText(name);
        //titleTextView.setTypeface( your_typeface);

        //titleTextView.setOtherProperties();

        // Add titleTextView into ActionBar
        actionBar.setCustomView(titleTextView);

        ChatBubbles = new ArrayList<>();
        messages = (ListView) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.input);
        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
        messages.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox(phone_no);
        }


    }

    public void updateInbox(final String smsMessage) {
        //adapter.insert(smsMessage, 0);
        adapter.notifyDataSetChanged();
    }

    public void onSendClick(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            smsManager.sendTextMessage(phone_no, null, input.getText().toString(), null, null);
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPermissionToReadSMS() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_SMS)) {
                    Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        READ_SMS_PERMISSIONS_REQUEST);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox(phone_no);
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }



    }

    public void refreshSmsInbox(String phone_no) {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, "address='"+phone_no+"'", null, "date desc");
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        adapter.clear();
        do {
            String str = smsInboxCursor.getString(indexBody) + "\n";
            ChatBubble ChatBubble = new ChatBubble(str, myMessage);
            adapter.add(ChatBubble);
        } while (smsInboxCursor.moveToNext());
//messages.setSelection(arrayAdapter.getCount() - 1);
    }
}
