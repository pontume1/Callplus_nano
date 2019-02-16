package com.luncher.bounjour.ringlerr.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.TimeShow;
import com.luncher.bounjour.ringlerr.model.Identity;
import com.luncher.bounjour.ringlerr.model.MyFrequent;
import com.luncher.bounjour.ringlerr.model.MyRecent;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.luncher.bounjour.ringlerr.activity.MyDexApplication.CHANNEL_ID_BOUND;

public class RecentService extends IntentService {
    // Session Manager Class
    SessionManager session;
    ArrayList<MyFrequent> all_lists = new ArrayList<MyFrequent>();
    public final static String CASHBACK_INFO = "cashback_info";
    public boolean is_ringlerr;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    MyDbHelper myDbHelper;

    ArrayList<List> list = new ArrayList<List>();

    String[] projection = new String[] {
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls._ID
    };

    String sortOrder = String.format("%s limit 50 ", android.provider.CallLog.Calls.DATE + " DESC");

    public RecentService() {
        super("RecentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent notificationIntent = new Intent(this, RecentService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    1000994, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_BOUND)
                    //.setContentTitle("Example Service")
                    //.setContentText("")
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1000994, notification);
        }

        //TODO do something useful

//        myDbHelper = new MyDbHelper(MessageService.this, null, null, 1);
//        myPhone = myDbHelper.getMyPhoneNo();

        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //stopForeground(true); //true will remove notification
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ArrayList list = updateFrequentContact();
        sendCashbackInfoToClient(list);
    }

    public ArrayList updateFrequentContact() {

        myDbHelper = new MyDbHelper(getApplicationContext(), null, 7);
        List<String> input = new ArrayList<>();
        List<String> phone_no = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> ago = new ArrayList<>();
        List<Bitmap> user_image = new ArrayList<>();
        List<Integer> contact_id = new ArrayList<>();
        List<Integer> c_id = new ArrayList<>();

        List<MyRecent> callLog = myDbHelper.getAllCallLog();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            Calendar calender = Calendar.getInstance();
            calender.set(Calendar.DATE, -4);
            String toDate = String.valueOf(calender.getTimeInMillis());

            String[] whereValue = {toDate};

            Cursor cursor = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, android.provider.CallLog.Calls.DATE + " > ?", whereValue, sortOrder);
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                String type = cursor.getString(2); //1 = incoming, 2 = outgoing, 3 missed, new = new
                Long time = cursor.getLong(3); // epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.String
                Integer _id = cursor.getInt(4);

                //not in contact check and save is_ringlerr
                number = "+91"+getLastnCharacters(number, 10);
                Boolean is_ringlerr = myDbHelper.checkRinglerrUser(number);
                if(!is_ringlerr) {
                    number = number.replaceAll("[*#]", "");

                    DatabaseReference users = mRootRef.child("identity");
                    final String finalPhone_no = number;
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(finalPhone_no)) {
                                // run some code
                                myDbHelper.addRinglerrUser(finalPhone_no);

                                final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(finalPhone_no + ".jpg");
                                download_image(filepath, finalPhone_no);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    //update profile pics
//                    number = number.replaceAll("[*#]", "");
//                    final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(number + ".jpg");
//                    download_image(filepath, number);

                    if(!input.equals(number)) {
                        number = number.replaceAll("[*#]", "");
                        DatabaseReference users = mRootRef.child("identity").child(number);
                        final String finalNumber = number;
                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Identity userIdentity = snapshot.getValue(Identity.class);
                                if (userIdentity != null) {

                                    if (userIdentity.app_remove != null) {
//                                        if (userIdentity.app_remove == false) {
//                                            final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(phone_no + ".jpg");
//                                            download_image(filepath, phone_no);
//                                        }

                                        if (userIdentity.app_remove == true) {
                                            String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+finalNumber+".jpeg";
                                            File fdelete = new File(filePath);
                                            if (fdelete.exists()) {
                                                if (fdelete.delete()) {

                                                } else {

                                                }
                                            }
                                        }
                                    } else {
//                                        final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(phone_no + ".jpg");
//                                        download_image(filepath, phone_no);
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                if(name == null){
                    name = getContactNameFromNumber(number);
                    if(name == null) {
                        input.add(number);
                    }else{
                        input.add(name);
                    }
                }else{
                    input.add(name);
                }

                phone_no.add(number);
                types.add(type);
                c_id.add(_id);

                TimeShow time_ago = new TimeShow();
                String tAgo = time_ago.DateDifference(time);
                ago.add(tAgo);

                int contactId = getContactIDFromNumber(number);
                contact_id.add(contactId);

                Bitmap profile_pic;
                if(contactId > 0){
                    //profile_pic = openPhoto(contactId);
                    profile_pic = null;
                }else{
                    profile_pic = null;
                }

                user_image.add(profile_pic);

            }
            cursor.close();

            list.add(input);
            list.add(phone_no);
            list.add(types);
            list.add(ago);
            list.add(user_image);
            list.add(contact_id);
            list.add(c_id);
        }

        return (ArrayList) list;
    }

    private void download_image(StorageReference filepath, final String phone_no) {

        final long ONE_MEGABYTE = 1024 * 1024;

        //download file as a byte array
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                storeProfileImage(bitmap, phone_no+".jpeg");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                String mgs = exception.getMessage();
                if(mgs.equals("Object does not exist at location.")){
                    String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone_no+".jpeg";
                    File fdelete = new File(filePath);
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {

                        } else {

                        }
                    }
                }
            }
        });
    }

    private String storeProfileImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/";
        File sdIconStorageDir = new File(iconsStoragePath);
        String filePath = "";

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }

        return filePath;
    }

    public Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getApplication().getContentResolver().query(photoUri,
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

    private Boolean checkRinglerrUser(String phone_no) {

        phone_no = phone_no.replaceAll("[*#]", "");
        is_ringlerr = false;

        DatabaseReference users = mRootRef.child("identity");
        final String finalPhone_no = phone_no;
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(finalPhone_no)) {
                    // run some code
                    is_ringlerr = true;
                }else{
                    is_ringlerr = false;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return is_ringlerr;
    }

    private void sendCashbackInfoToClient(ArrayList list){
        Intent intent = new Intent();
        intent.setAction(CASHBACK_INFO);
        intent.putStringArrayListExtra("input", (ArrayList) list.get(0));
        intent.putStringArrayListExtra("phone_no", (ArrayList) list.get(1));
        intent.putStringArrayListExtra("types", (ArrayList) list.get(2));
        intent.putStringArrayListExtra("ago", (ArrayList) list.get(3));
        intent.putParcelableArrayListExtra("user_image", (ArrayList) list.get(4));
        intent.putStringArrayListExtra("contact_id", (ArrayList) list.get(5));
        intent.putStringArrayListExtra("c_id", (ArrayList) list.get(6));
        sendBroadcast(intent);
    }

    public int getContactIDFromNumber(String contactNumber)
    {
        int phoneContactID = new Random().nextInt();
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = -1;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getApplicationContext().getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    public String getLastnCharacters(String inputString, int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    public String getContactNameFromNumber(String contactNumber)
    {
        String phoneContactID = null;
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = null;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }
}
