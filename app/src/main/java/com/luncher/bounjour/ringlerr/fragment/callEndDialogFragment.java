package com.luncher.bounjour.ringlerr.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.MyOutgoingCustomDialog;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.activity.ReminderDialog;
import com.luncher.bounjour.ringlerr.activity.addContact;
import com.luncher.bounjour.ringlerr.activity.editContact;
import com.luncher.bounjour.ringlerr.model.Blocks;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

public class callEndDialogFragment extends BottomSheetDialogFragment {

    String phone_no;
    String c_name;
    int report_count;
    int block_count;
    TextView name;
    TextView phone;
    TextView reportText;
    ImageView profile_image;

    private ImageButton rCallButton;
    private ImageButton whatsapp_btn;
    private ImageButton blockButton;
    private ImageButton inviteButton;
    private ImageButton editButton;
    private ImageButton reminderButton;
    private ImageButton voiceButton;
    private ImageButton myFlagButton;
    private ImageButton dialog_ok;
    //Context mContext;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    SessionManager session;
    View myView;

    public callEndDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_after, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        session = new SessionManager(getActivity().getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);


        rCallButton = view.findViewById(R.id.rCallButton);
        whatsapp_btn = view.findViewById(R.id.whatsapp_btn);
        blockButton = view.findViewById(R.id.blockButton);
        inviteButton = view.findViewById(R.id.inviteButton);
        editButton = view.findViewById(R.id.editButton);
        voiceButton = view.findViewById(R.id.voiceButton);
        reminderButton = view.findViewById(R.id.reminderButton);
        myFlagButton = view.findViewById(R.id.myFlagButton);
        dialog_ok = view.findViewById(R.id.dialog_ok);
        profile_image = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.caller_name);
        phone = view.findViewById(R.id.phone);
        //reportText = (TextView) view.findViewById(R.id.reportText);

        phone_no = getActivity().getIntent().getExtras().getString("phone_no");
        c_name = getActivity().getIntent().getExtras().getString("name");

        if(c_name == null){
            editButton.setVisibility(View.GONE);
            inviteButton.setVisibility(View.VISIBLE);
        }

        name.setText(c_name);
        phone.setText(phone_no);

        //phone_no = "+91"+getLastnCharacters(phone_no,10);

//        close_btnz.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //finish();
//            }
//
//        });
        dialog_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
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
                final Intent intent1 = new Intent(getActivity().getApplicationContext(), MyOutgoingCustomDialog.class);
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

        myFlagButton.setOnClickListener(new View.OnClickListener() {

              @Override
              public void onClick(View v) {
                  PopupMenu popup = new PopupMenu(getActivity(), myFlagButton);
                  //inflating menu from xml resource
                  popup.inflate(R.menu.flag_option_menu);
                  //adding click listener
                  popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem item) {
                          switch (item.getItemId()) {
                              case R.id.spam:

                                  mRootRef.child("report").child(phone_no).child("spam").child(mPhoneNo).setValue(1);
                                  Toast.makeText(getActivity().getApplicationContext(), "User reported as spam", Toast.LENGTH_LONG).show();

                                  break;
                              case R.id.misleading:
                                  mRootRef.child("report").child(phone_no).child("misleading").child(mPhoneNo).setValue(1);
                                  Toast.makeText(getActivity().getApplicationContext(), "User reported as misleading/fake", Toast.LENGTH_LONG).show();

                                  break;
                              case R.id.harassment:
                                  mRootRef.child("report").child(phone_no).child("harassment").child(mPhoneNo).setValue(1);
                                  Toast.makeText(getActivity().getApplicationContext(), "User reported as harassment", Toast.LENGTH_LONG).show();

                                  break;
                          }

                          int total_report = report_count+1;
                          mRootRef.child("report").child(phone_no).child("count").setValue(total_report);
                          return false;
                      }

                  });
                  popup.show();
              }
          });

        whatsapp_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openWhatsApp("91"+getLastnCharacters(phone_no, 10), getActivity());
            }
        });

        blockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
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

                                int total_block = block_count+1;
                                mRootRef.child("block_count").child(phone_no).child("count").setValue(total_block);

                                MyDbHelper myDbHelper;
                                myDbHelper = new MyDbHelper(getActivity().getApplicationContext(), null, null, 1);
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
                //editContact(phone_no, c_name, callEndDialog.this);
                openContact(phone_no, c_name,getActivity().getApplicationContext());
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editContact(phone_no, c_name, getActivity().getApplicationContext());
                //openContact(phone_no, c_name,callEndDialog.this);
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri sms_uri = Uri.parse("smsto:"+phone_no);
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                startActivity(sms_intent);
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent rem_intent = new Intent(getActivity().getApplicationContext(), ReminderDialog.class);
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

    private int getCount(int count){
        return count;
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference rootRef = mRootRef.child("report/"+ phone_no +"/count");
        rootRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object count = dataSnapshot.getValue();
                        String spam_count = "0";
                        if(count!=null){
                            spam_count = count.toString();
                        }
                        report_count = getCount(Integer.parseInt(spam_count));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        DatabaseReference blockrootRef = mRootRef.child("block_count/"+ phone_no +"/count");
        blockrootRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object count = dataSnapshot.getValue();
                        String bl_count = "0";
                        if(count!=null){
                            bl_count = count.toString();
                        }
                        block_count = getCount(Integer.parseInt(bl_count));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone_no+".jpeg";
        File file = new File(filePath);
        if (file.exists()){
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            Glide.with(getActivity())
                    .load(filePath)
                    .apply(requestOptions)
                    .into(profile_image);
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent){
//        super.onNewIntent(intent);
//
////        phone_no = intent.getExtras().getString("phone_no");
////        c_name = intent.getExtras().getString("name");
////
////        name.setText(c_name);
////        phone.setText(phone_no);
//
//        setIntent(intent);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.get(getActivity()).clearMemory();
        profile_image = null;
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

    private void openContact(String phone, String name, Context ctx) {
        Intent addIntent = new Intent(ctx, addContact.class);
        addIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        addIntent.putExtra("phone", phone);
        addIntent.putExtra("name", name);
        ctx.startActivity(addIntent);
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
            Cursor contactLookupCursor = getActivity().getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        getActivity().finish();
    }
}
