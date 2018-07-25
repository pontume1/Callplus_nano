package com.luncher.santanu.dailer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.santanu.dailer.MyDbHelper;
import com.luncher.santanu.dailer.MyOutgoingCustomDialog;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.SessionManager;
import com.luncher.santanu.dailer.model.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class callEndDialog extends AppCompatActivity {

    String phone_no;
    String c_name;
    TextView name;
    TextView phone;

    private ImageButton rCallButton;
    private ImageButton whatsapp_btn;
    private ImageButton blockButton;
    private ImageButton inviteButton;
    private ImageButton editButton;
    private ImageButton reminderButton;
    private ImageButton voiceButton;
    private ImageButton close_btnz;
    private ImageButton dialog_ok;
    //Context mContext;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_after);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //mContext = getApplicationContext();

        rCallButton = (ImageButton) findViewById(R.id.rCallButton);
        whatsapp_btn = (ImageButton) findViewById(R.id.whatsapp_btn);
        blockButton = (ImageButton) findViewById(R.id.blockButton);
        inviteButton = (ImageButton) findViewById(R.id.inviteButton);
        editButton = (ImageButton) findViewById(R.id.editButton);
        voiceButton = (ImageButton) findViewById(R.id.voiceButton);
        reminderButton = (ImageButton) findViewById(R.id.reminderButton);
        close_btnz = (ImageButton) findViewById(R.id.close_btn);
        dialog_ok = (ImageButton) findViewById(R.id.dialog_ok);
        name = (TextView) findViewById(R.id.caller_name);
        phone = (TextView) findViewById(R.id.phone);

        phone_no = getIntent().getExtras().getString("phone_no");
        c_name = getIntent().getExtras().getString("name");

        name.setText(c_name);
        phone.setText(phone_no);

        //phone_no = "+91"+getLastnCharacters(phone_no,10);

         close_btnz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
        dialog_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(callEndDialog.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    callIntent.setData(Uri.parse("tel:" + phone_no));
                    startActivity(callIntent);
                }
            }

        });

        rCallButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent intent1 = new Intent(callEndDialog.this, MyOutgoingCustomDialog.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("phone_no", phone_no);
                intent1.putExtra("sim", 1);
                intent1.putExtra("name", c_name);
                //context.startActivity(intent1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent1);
                    }
                }, 100);
            }
        });

        whatsapp_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openWhatsApp("91"+getLastnCharacters(phone_no, 10), callEndDialog.this);
            }
        });

        blockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(callEndDialog.this)
                        .setTitle("Block")
                        .setMessage("Are you sure you want to block "+phone_no+" ?")
                        .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                Long tsLong = System.currentTimeMillis()/1000;

                                String key = mRootRef.child("blocks").child(mPhoneNo).push().getKey();
                                Blocks blocks = new Blocks(mPhoneNo, phone_no, c_name, tsLong.toString());
                                Map<String, Object> postValues = blocks.toMap();

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/blocks/" + mPhoneNo + "/" + key, postValues);

                                mRootRef.updateChildren(childUpdates);

                                MyDbHelper myDbHelper;
                                myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);
                                myDbHelper.addBlockNumber(phone_no);

                            }
                        })
                        .create()
                        .show();
            }
        });

        inviteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editContact(phone_no, c_name, callEndDialog.this);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editContact(phone_no, c_name, callEndDialog.this);
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent rem_intent = new Intent(callEndDialog.this, ReminderDialog.class);
                rem_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                rem_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                rem_intent.putExtra("phone_no", phone_no);
                rem_intent.putExtra("name", c_name);
                //context.startActivity(intent1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(rem_intent);
                    }
                }, 100);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

//        phone_no = intent.getExtras().getString("phone_no");
//        c_name = intent.getExtras().getString("name");
//
//        name.setText(c_name);
//        phone.setText(phone_no);

        setIntent(intent);
    }

    private void openWhatsApp(String phone, Context ctx) {
        String smsNumber = phone; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, " ");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");

        ctx.startActivity(sendIntent);
    }

    public String getLastnCharacters(String inputString, int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    private void editContact(String phone, String name, Context ctx) {
        Intent addIntent = new Intent(ctx, editContact.class);
        Integer contact_ids = getContactIDFromNumber(phone);
        addIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        addIntent.putExtra("phone", phone);
        addIntent.putExtra("name", name);
        addIntent.putExtra("contact_id", contact_ids);
        ctx.startActivity(addIntent);
    }

    public int getContactIDFromNumber(String contactNumber)
    {
        int phoneContactID = new Random().nextInt();
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = -1;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

}
