package com.luncher.bounjour.ringlerr.fragment;


import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.github.tamir7.contacts.Contact;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.TimeShow;
import com.luncher.bounjour.ringlerr.adapter.MyFrequentAdapter;
import com.luncher.bounjour.ringlerr.model.MyFrequent;
import com.luncher.bounjour.ringlerr.model.MyResolvFrequent;
import com.luncher.bounjour.ringlerr.services.FrequentService;
import com.luncher.bounjour.ringlerr.services.RecentService;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FrequentFragment extends Fragment {


    private RecyclerView recyclerView;
    private MyFrequentAdapter mAdapter;
    public EditText search;
    List<Contact> contacts;
    private AnimatorSet animationUp, animationDown;
    Integer cm_profile_pic;
    String cm_input;
    String cm_phone_no;
    Long cm_duration;
    Integer cm_count;
    // Session Manager Class
    SessionManager session;
    ArrayList<MyFrequent> all_lists = new ArrayList<MyFrequent>();
    ArrayList<MyFrequent> head_all_lists;
    Activity mActivity;
    private CallbackReciver callbackReciver;

    String[] projection = new String[] {
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
    };

    String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";

    public FrequentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frequent, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Session class instance
        session = new SessionManager(getContext());
        // get user data from session
        all_lists = session.getFrequentContacts();

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        //Toast.makeText(getContext(), "recent fag", Toast.LENGTH_LONG).show();
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        if (null == all_lists) {
            all_lists = new ArrayList<MyFrequent>();

            List<String> input = new ArrayList<>();
            List<String> phone_no = new ArrayList<>();
            List<String> types = new ArrayList<>();
            List<String> ago = new ArrayList<>();
            List<Long> durations = new ArrayList<>();
            List<Integer> user_image = new ArrayList<>();
            ArrayList<String> list = new ArrayList<String>();
            List<Integer> count = new ArrayList<Integer>();
            ArrayList<MyResolvFrequent> first_lists = new ArrayList<MyResolvFrequent>();

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.DATE, -7);
                String toDate = String.valueOf(calender.getTimeInMillis());

                String[] whereValue = {toDate};

                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, android.provider.CallLog.Calls.DATE + " > ?", whereValue, sortOrder);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String number = cursor.getString(1);
                    Long duration = cursor.getLong(4);
                    number = number.replace(" ","");
                    number = "+91"+getLastnCharacters(number, 10);

                    if (!list.contains(number)) {

                        list.add(number);
                        String type = cursor.getString(2); //1 = incoming, 2 = outgoing, 3 missed, new = new
                        Long time = cursor.getLong(3); // epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.String

                        if (name == null) {
                            input.add(number);
                        } else {
                            input.add(name);
                        }

                        phone_no.add(number);
                        types.add(type);
                        count.add(1);
                        durations.add(duration);

                        cm_count = 1;

                        TimeShow time_ago = new TimeShow();
                        ago.add(time_ago.DateDifference(time));

                        int contactId = getContactIDFromNumber(number);

                        Integer profile_pic;
                        if (contactId > 0) {
    //                    Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
    //                    InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContext().getContentResolver(), my_contact_Uri);
    //                    BufferedInputStream buf = new BufferedInputStream(photo_stream);
    //                    profile_pic = BitmapFactory.decodeStream(buf);
    //                    try {
    //                        photo_stream.close();
    //                    } catch (IOException e) {
    //                        e.printStackTrace();
    //                    }

                            profile_pic = contactId;
                        } else {
                            profile_pic = null;
                        }

                        user_image.add(profile_pic);
                    } else {
                        int posti = list.indexOf(number);
                        int cnt_now = count.get(posti);
                        Long duration_now = durations.get(posti);
                        count.set(posti, cnt_now + 1);
                        durations.set(posti, duration_now+duration);
                    }
                }
                cursor.close();

                first_lists.add(new MyResolvFrequent(input, phone_no, user_image, count, durations));

                for (int i = 0; i < first_lists.size(); i++) {
                    List name = first_lists.get(i).getName();
                    List number = first_lists.get(i).getNumber();
                    List user_img = first_lists.get(i).getImage();
                    List counts = first_lists.get(i).getCounts();
                    List call_duration = first_lists.get(i).getDuration();
                    for (int l = 0; l < name.size(); l++) {
                        cm_input = name.get(l).toString();
                        cm_phone_no = number.get(l).toString();
                        cm_profile_pic = (Integer) user_img.get(l);
                        cm_count = (Integer) counts.get(l);
                        cm_duration = (Long) call_duration.get(l);

                        all_lists.add(new MyFrequent(cm_input, cm_phone_no, cm_profile_pic, cm_count, cm_duration));
                        Collections.sort(all_lists, new MyFrequentComp());
                    }
                }
                if(all_lists.size()>10) {
                    head_all_lists = new ArrayList<MyFrequent>(all_lists.subList(0, 10));
                    session.setFrequentContacts(head_all_lists);
                }else{
                    session.setFrequentContacts(all_lists);
                }
            }
        }

        List<String> input_f = new ArrayList<>();
        List<String> phone_no_f = new ArrayList<>();
        List<Bitmap> user_image_f = new ArrayList<>();
        List<Integer> count_f = new ArrayList<Integer>();
        List<Long> duration_f = new ArrayList<Long>();

        for(int l = 0; l < all_lists.size(); l++) {

            String f_name = all_lists.get(l).getName();
            String f_phone = all_lists.get(l).getNumber();
            Long f_duration = all_lists.get(l).getDuration();
            Bitmap f_user_image = null;
            if(null != all_lists.get(l).getImage()) {
                f_user_image = openPhoto(all_lists.get(l).getImage());
            }
            Integer f_counts = all_lists.get(l).getCounts();
            if(f_counts == null){
                f_counts = 0;
            }

            input_f.add(f_name);
            phone_no_f.add(f_phone);
            user_image_f.add(f_user_image);
            count_f.add(f_counts);
            duration_f.add(f_duration);

            if(l >= 9){
                break;
            }

        }

        animationUp = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity().getApplicationContext(), R.animator.flip_out);
        animationDown = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity().getApplicationContext(), R.animator.flip_in);
        mAdapter = new MyFrequentAdapter(input_f, phone_no_f, count_f, duration_f, user_image_f, animationUp, animationDown);
        recyclerView.setAdapter(mAdapter);

//        FloatingActionButton fab = view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                final android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragment);
//                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.setCustomAnimations(android.R.animator.fade_in,
//                        android.R.animator.fade_out);
//
//                getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commit();
//            }
//        });

//        registerCallbackReceiver();
//
//        // Get a handler that can be used to post to the main thread
//        Handler handler = new Handler(mActivity.getApplicationContext().getMainLooper());
//        mActivity.getApplicationContext()
//                .getContentResolver()
//                .registerContentObserver(
//                        android.provider.CallLog.Calls.CONTENT_URI, true,
//                        new FrequentFragment.MyContentObserver(handler, view));
    }

    public String getLastnCharacters(String inputString,
                                     int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    private void registerCallbackReceiver(){
        callbackReciver = new CallbackReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FrequentService.CALLBACK_INFO);

        mActivity.registerReceiver(callbackReciver, intentFilter);
    }
    private class CallbackReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        //mAdapter.notifyDataSetChanged();
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

    public void startCallService(View view){

        Intent cbIntent =  new Intent();
        cbIntent.setClass(mActivity, RecentService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //mActivity.startForegroundService(cbIntent);
            ContextCompat.startForegroundService(mActivity, new Intent(mActivity, RecentService.class));
        } else {
            mActivity.startService(cbIntent);
        }
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
            startCallService(view);
        }
    }

}

class MyFrequentComp implements Comparator<MyFrequent> {

    @Override
    public int compare(MyFrequent e1, MyFrequent e2) {
        if(e1.getCounts() < e2.getCounts()){
            return 1;
        } else {
            return -1;
        }
    }
}
