package com.luncher.santanu.dailer.fragment;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.github.tamir7.contacts.Contact;
import com.luncher.santanu.dailer.ContactBoundService;
import com.luncher.santanu.dailer.MainActivity;
import com.luncher.santanu.dailer.MyAdapter;
import com.luncher.santanu.dailer.MyRecentAdapter;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.SessionManager;
import com.luncher.santanu.dailer.TimeShow;
import com.luncher.santanu.dailer.model.MyContact;
import com.luncher.santanu.dailer.services.RecentService;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public EditText search;
    private Animation animationUp, animationDown;
    public static boolean isRecursionEnable = false;
    ContactBoundService myService;
    RecentService recentService;
    boolean isBound = false;

    private CallbackReciver callbackReciver;

    List<MyContact> list = new ArrayList<>();

    String[] projection = new String[] {
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls._ID
    };

    String sortOrder = String.format("%s limit 450 ", android.provider.CallLog.Calls.DATE + " DESC");
    //String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        List<String> input = new ArrayList<>();
        List<String> phone_no = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> ago = new ArrayList<>();
        List<Bitmap> user_image = new ArrayList<>();
        List<Integer> contact_id = new ArrayList<>();
        List<Integer> c_id = new ArrayList<>();

//        list = new ArrayList<>();
//
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
//
//            Calendar calender = Calendar.getInstance();
//            calender.set(Calendar.DATE, -4);
//            String toDate = String.valueOf(calender.getTimeInMillis());
//
//            String[] whereValue = {toDate};
//
//            Cursor cursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, android.provider.CallLog.Calls.DATE + " > ?", whereValue, sortOrder);
//            while (cursor.moveToNext()) {
//                String name = cursor.getString(0);
//                String number = cursor.getString(1);
//                String type = cursor.getString(2); //1 = incoming, 2 = outgoing, 3 missed, new = new
//                Long time = cursor.getLong(3); // epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.String
//                Integer _id = cursor.getInt(4);
//
//                if(name==null){
//                    input.add(number);
//                }else{
//                    input.add(name);
//                }
//
//                phone_no.add(number);
//                types.add(type);
//                c_id.add(_id);
//
//                TimeShow time_ago = new TimeShow();
//                String tAgo = time_ago.DateDifference(time);
//                ago.add(tAgo);
//
//                int contactId = getContactIDFromNumber(number);
//                contact_id.add(contactId);
//
//                Bitmap profile_pic;
//                if(contactId > 0){
//                    profile_pic = openPhoto(contactId);
//                }else{
//                    profile_pic = null;
//                }
//
//                user_image.add(profile_pic);
//
//                MyContact data = new MyContact(name, number, type, time, _id, tAgo, contactId);
//                list.add(data);
//
//            }
//            cursor.close();
//                myService.updateContact();
//                updateContact();
//        }

        animationUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
        mAdapter = new MyRecentAdapter(input, phone_no, types, ago, user_image, contact_id, c_id, animationUp, animationDown);
        recyclerView.setAdapter(mAdapter);

        startCashbackService(view);
        registerCashbackReceiver();

        // Get a handler that can be used to post to the main thread
        Handler handler = new Handler(getActivity().getApplicationContext().getMainLooper());
        getActivity().getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        android.provider.CallLog.Calls.CONTENT_URI, true,
                        new MyContentObserver(handler, view));
    }

    @Override
    public void onResume() {
        super.onResume();
        //runInBackground();

        if(isBound == true) {
//            list.clear();
//            myService.updateContact();
//            updateContact();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(callbackReciver);
    }

    public void startCashbackService(View view){

        Intent cbIntent =  new Intent();
        cbIntent.setClass(getActivity(), RecentService.class);
        getActivity().startService(cbIntent);
    }
    private void registerCashbackReceiver(){
        callbackReciver = new CallbackReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RecentService.CASHBACK_INFO);

        getActivity().registerReceiver(callbackReciver, intentFilter);
    }
    private class CallbackReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList input = intent.getStringArrayListExtra("input");
            ArrayList phone_no = intent.getStringArrayListExtra("phone_no");
            ArrayList types = intent.getStringArrayListExtra("types");
            ArrayList ago = intent.getStringArrayListExtra("ago");
            ArrayList user_image = intent.getParcelableArrayListExtra("user_image");
            ArrayList contact_id = intent.getStringArrayListExtra("contact_id");
            ArrayList c_id = intent.getStringArrayListExtra("c_id");

            animationUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
            animationDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
            mAdapter = new MyRecentAdapter(input, phone_no, types, ago, user_image, contact_id, c_id, animationUp, animationDown);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        }
    }

    public Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContext().getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }

    public int getContactIDFromNumber(String contactNumber)
    {
        int phoneContactID = new Random().nextInt();
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = -1;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getContext().getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    class MyContentObserver extends ContentObserver {

        public View view;
        public MyContentObserver(Handler h, View view) {
            super(h);
            this.view = view;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            // here you call the method to fill the list
            startCashbackService(view);
        }
    }


}



