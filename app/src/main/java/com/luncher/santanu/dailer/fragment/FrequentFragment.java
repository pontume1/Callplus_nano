package com.luncher.santanu.dailer.fragment;


import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.github.tamir7.contacts.Contact;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.SessionManager;
import com.luncher.santanu.dailer.TimeShow;
import com.luncher.santanu.dailer.adapter.MyFrequentAdapter;
import com.luncher.santanu.dailer.model.MyFrequent;
import com.luncher.santanu.dailer.model.MyResolvFrequent;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrequentFragment extends Fragment {


    private RecyclerView recyclerView;
    private MyFrequentAdapter mAdapter;
    public EditText search;
    List<Contact> contacts;
    private AnimatorSet animationUp, animationDown;
    private Cursor cursor;
    Integer cm_profile_pic;
    String cm_input;
    String cm_phone_no;
    Long cm_duration;
    Integer cm_count;
    // Session Manager Class
    SessionManager session;
    ArrayList<MyFrequent> all_lists = new ArrayList<MyFrequent>();
    ArrayList<MyFrequent> head_all_lists;

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

                cursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, android.provider.CallLog.Calls.DATE + " > ?", whereValue, sortOrder);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String number = cursor.getString(1);
                    Long duration = cursor.getLong(4);

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
