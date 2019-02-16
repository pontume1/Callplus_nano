package com.luncher.bounjour.ringlerr.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.luncher.bounjour.ringlerr.MyRecentAdapter;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.services.RecentService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.TELECOM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    public EditText search;
    private Animation animationUp, animationDown;
    private boolean isBound = false;
    private Activity mActivity;
    private CallbackReciver callbackReciver;
    private MyContentObserver observer;
    private RelativeLayout default_app;
    public static final int REQUEST_CODE_SET_DEFAULT_DIALER = 22;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View myView = view;
        recyclerView = view.findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            default_app = view.findViewById(R.id.default_app);
            if (!isAlreadyDefaultDialer()) {
                default_app.setVisibility(View.VISIBLE);
                default_app.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkDefaultHandler();
                    }
                });
            }
        }

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

        animationUp = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.slide_down);
        mAdapter = new MyRecentAdapter(input, phone_no, types, ago, user_image, contact_id, c_id, animationUp, animationDown);
        recyclerView.setAdapter(mAdapter);

        if(!isBound) {
            startCashbackService(view);
        }
        registerCashbackReceiver();

        // Get a handler that can be used to post to the main thread
        Handler handler = new Handler(mActivity.getApplicationContext().getMainLooper());
        observer=new MyContentObserver(handler, view);
        mActivity.getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        android.provider.CallLog.Calls.CONTENT_URI, true, observer);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //runInBackground();

//        if(!isBound) {
//            list.clear();
//            myService.updateContact();
//            updateContact();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(callbackReciver);
        try {
            mActivity.getContentResolver().unregisterContentObserver(observer);
        } catch (IllegalStateException ise) {
            // Do Nothing.  Observer has already been unregistered.
        }
        mActivity = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDefaultHandler() {
        if (isAlreadyDefaultDialer()) {
            return;
        }
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getActivity().getPackageName());
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
        }
        else{
            throw new RuntimeException("Default phone functionality not found");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
                default_app.setVisibility(View.GONE);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isAlreadyDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getActivity().getSystemService(TELECOM_SERVICE);
        return getActivity().getPackageName().equals(telecomManager.getDefaultDialerPackage());
    }

    private void startCashbackService(View view){
        isBound = true;
        Intent cbIntent =  new Intent();
        cbIntent.setClass(mActivity, RecentService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //mActivity.startForegroundService(cbIntent);
            ContextCompat.startForegroundService(mActivity, new Intent(mActivity, RecentService.class));
        } else {
            mActivity.startService(cbIntent);
        }
    }

    private void registerCashbackReceiver(){
        callbackReciver = new CallbackReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RecentService.CASHBACK_INFO);

        mActivity.registerReceiver(callbackReciver, intentFilter);
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

            animationUp = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.slide_up);
            animationDown = AnimationUtils.loadAnimation(mActivity.getApplicationContext(), R.anim.slide_down);
            mAdapter = new MyRecentAdapter(input, phone_no, types, ago, user_image, contact_id, c_id, animationUp, animationDown);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            isBound = false;

        }
    }

//    public Bitmap openPhoto(long contactId) {
//        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
//        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
//        Cursor cursor = getContext().getContentResolver().query(photoUri,
//                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
//        if (cursor == null) {
//            return null;
//        }
//        try {
//            if (cursor.moveToFirst()) {
//                byte[] data = cursor.getBlob(0);
//                if (data != null) {
//                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
//                }
//            }
//        } finally {
//            cursor.close();
//        }
//        return null;
//
//    }

//    public int getContactIDFromNumber(String contactNumber)
//    {
//        int phoneContactID = new Random().nextInt();
//        if(contactNumber == null || contactNumber.equals("")){
//            phoneContactID = -1;
//        }else {
//            contactNumber = Uri.encode(contactNumber);
//            Cursor contactLookupCursor = getContext().getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
//            while (contactLookupCursor.moveToNext()) {
//                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
//            }
//            contactLookupCursor.close();
//        }
//
//        return phoneContactID;
//    }

    class MyContentObserver extends ContentObserver {

        public View view;
        MyContentObserver(Handler h, View view) {
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
            SessionManager session = new SessionManager(mActivity);
            session.setFrequentContacts(null);
            // here you call the method to fill the list
            if(!isBound) {
                startCashbackService(view);
            }
        }
    }
}



