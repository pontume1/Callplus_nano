package com.luncher.bounjour.ringlerr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.luncher.bounjour.ringlerr.MyOutgoingCustomDialog;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.RecyclerViewClickListener;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.Utility;
import com.luncher.bounjour.ringlerr.adapter.ContactSelectReminderAdapter;
import com.luncher.bounjour.ringlerr.adapter.ContactSelectSosAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelectCopyContact extends Activity implements RecyclerViewClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public EditText search;
    List<Contact> contacts;
    List<String> sel_contacts = new ArrayList<>();
    String message;
    Long time;
    Integer timeAgo;

    String phone_no;
    String msg;
    String image;
    String type;
    String talk_time;

    public SelectCopyContact() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sos_contact);

        phone_no = getIntent().getExtras().getString("phone_no");
        msg = getIntent().getExtras().getString("message");
        image = getIntent().getExtras().getString("image");
        type = getIntent().getExtras().getString("type");
        talk_time = getIntent().getExtras().getString("talk_time");

        recyclerView = findViewById(R.id.my_recycler_view);
        search = findViewById( R.id.search);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        final RecyclerViewClickListener itemListener = SelectCopyContact.this;
        Contacts.initialize(SelectCopyContact.this);
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        contacts = q.find();

        List<String> input = new ArrayList<>();
        List<String> phoneNo = new ArrayList<>();
        List<Boolean> selected = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getPhoneNumbers().size()>0) {
                input.add(contacts.get(i).getDisplayName());
                selected.add(false);
                //photo.add(contacts.get(i).getPhotoUri().toString());
                if (contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber() == null) {
                    phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNumber());
                } else {
                    phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber());
                }
            }
        }// define an adapter

        mAdapter = new ContactSelectSosAdapter(input, phoneNo, message, time, timeAgo, selected, SelectCopyContact.this);
        recyclerView.setAdapter(mAdapter);

        //addTextListener
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                List<String> input = new ArrayList<>();
                List<String> phoneNo = new ArrayList<>();
                List<Boolean> selected = new ArrayList<>();

                for (int i = 0; i < contacts.size(); i++) {
                    String name = contacts.get(i).getDisplayName();
                    if(name.toLowerCase().contains(query)) {
                        input.add(name);
                        String phNo = null;
                        if (contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber() == null) {
                            phNo = contacts.get(i).getPhoneNumbers().get(0).getNumber();
                        } else {
                            phNo = contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber();
                        }

                        if(sel_contacts.contains(phNo)){
                            selected.add(true);
                        }else{
                            selected.add(false);
                        }
                        phoneNo.add(phNo);
                    }
                }// define an adapter

                mAdapter = new ContactSelectReminderAdapter(input, phoneNo, message, time, timeAgo, selected, itemListener);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void recyclerViewListClicked(View v, final String phone, String name, Boolean is_selected) {

        if(is_selected) {
            SessionManager session = new SessionManager(SelectCopyContact.this);

            HashMap<String, String> user = session.getUserDetails();
            // phone
            String mPhoneNo = user.get(SessionManager.KEY_PHONE);
            String videoPath = "";

            if(type.equals("none") || type.equals("flash") || type.equals("libgif") || type.equals("sticker") || type.equals("sos") || type.equals("snap")) {

            }else if(type.equals("gif")){

            }else if(type.equals("vid")){
                videoPath = image;
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(videoPath);
                    image = convertVideoToBase(fileInputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else{
                videoPath = image;
                File imagez = new File(videoPath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bm = BitmapFactory.decodeFile(imagez.getAbsolutePath(),bmOptions);
                image = encodeImage(bm);
            }

            if(!image.equals("none")){

                Utility.sendMessage(SelectCopyContact.this, mPhoneNo, phone, image, msg, talk_time, type, videoPath);
                if(type.equals("vid")) {

                    final Intent intent = new Intent(SelectCopyContact.this, FullScreenVideoHolder.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intent.putExtra("phone_no",phone);
                    intent.putExtra("message",msg);
                    intent.putExtra("image",videoPath);
                    intent.putExtra("type", type);
                    intent.putExtra("talk_time",talk_time);
                    intent.putExtra("screen_type","half");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run () {
                            startActivity(intent);
                        }
                    },7000);


                }else{
                    final Intent intent = new Intent(SelectCopyContact.this, FullScreenHolder.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intent.putExtra("phone_no", phone);
                    intent.putExtra("message", msg);
                    intent.putExtra("image", image);
                    intent.putExtra("type", type);
                    intent.putExtra("talk_time", talk_time);
                    if(type.equals("jpg")){
                        intent.putExtra("screen_type", "sender_full");
                    }else {
                        intent.putExtra("screen_type", "half");
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    }, 2000);
                }

                if(type.equals("vid")) {
                    final ProgressDialog progress = new ProgressDialog(SelectCopyContact.this);
                    progress.setTitle("Connecting");
                    progress.setMessage("Please wait while we connect to devices...");
                    progress.show();

                    Runnable progressRunnable = new Runnable() {

                        @SuppressLint("MissingPermission")
                        @Override
                        public void run() {
                            progress.cancel();
                            //  PackageManager pm = mContext.getPackageManager();
                            //  ComponentName componentName = new ComponentName(mContext, MyOutgoingCallHandler.class);
                            //  pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                            //setResultData(phone_no);
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            int simSlot = getDefaultSimSlot(SelectCopyContact.this);
                            callIntent.putExtra("com.android.phone.force.slot", true);
                            callIntent.putExtra("com.android.phone.extra.slot", simSlot);
                            startActivity(callIntent);
                            finish();
                        }
                    };

                    Handler pdCanceller = new Handler();
                    pdCanceller.postDelayed(progressRunnable, 5000);

                }else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone));
                    int simSlot = getDefaultSimSlot(SelectCopyContact.this);
                    callIntent.putExtra("com.android.phone.force.slot", true);
                    callIntent.putExtra("com.android.phone.extra.slot", simSlot);
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(callIntent);
                    finish();
                }
            }else{
                final Intent intent1 = new Intent(SelectCopyContact.this, MyOutgoingCustomDialog.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("phone_no", phone);
                intent1.putExtra("sim", 1);
                intent1.putExtra("name", name);
                intent1.putExtra("message", msg);
                //context.startActivity(intent1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent1);
                    }
                }, 100);
            }
        }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,90,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    private String encodeGifImage(byte[] b){
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;
    }

    public String convertVideoToBase(FileInputStream inputStream){

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int len = 0;
        try
        {
            while ((len = inputStream.read(buffer)) != -1)
            {
                byteBuffer.write(buffer, 0, len);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("converted!");

        String videoData="";
        //Converting bytes into base64
        videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);

        return videoData;
    }

    public int  getDefaultSimSlot(Context context) {

        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getDefaultSim;
        int defaultSimm = -1;
        try {
            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
            method_getDefaultSim.setAccessible(true);
            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Method method_getSmsDefaultSim;
        int smsDefaultSim = -1;
        try {
            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return defaultSimm;
    }
}

