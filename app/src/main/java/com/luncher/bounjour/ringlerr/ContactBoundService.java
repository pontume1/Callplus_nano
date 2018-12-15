package com.luncher.bounjour.ringlerr;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luncher.bounjour.ringlerr.model.Identity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.luncher.bounjour.ringlerr.activity.MyDexApplication.CHANNEL_ID;
import static com.luncher.bounjour.ringlerr.activity.MyDexApplication.CHANNEL_ID_BOUND;

public class ContactBoundService extends IntentService {
    List<Contact> contacts;

    MyDbHelper myDbHelper;
    private DatabaseReference mRootRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    SessionManager session;
    String mFt;

    public ContactBoundService() {
        super("ContactBoundService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    1000994, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_BOUND)
                    //.setContentTitle("Example Service")
                    //.setContentText("")
                    .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build();

            startForeground(1000994, notification);


        }

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
        updateContact();
    }

    public void updateContact() {

        mRootRef = FirebaseDatabase.getInstance().getReference();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        mFt = user.get(SessionManager.KEY_FIRST);

        Contacts.initialize(this);
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        contacts = q.find();

        myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);

        for (int i = 0; i < contacts.size(); i++) {

            final String phone_no;
            String phone_num;
            if(contacts.get(i).getPhoneNumbers().size()>0){
                if(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber()==null){
                    phone_num = contacts.get(i).getPhoneNumbers().get(0).getNumber();
                }else{
                    phone_num = contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber().toString();
                }

                String phone_no_a = phone_num.replace(".", "");
                String phone_no_b = phone_no_a.replace("$", "");
                String phone_no_c = phone_no_b.replace("[", "");
                String phone_no_d = phone_no_c.replace("]", "");
                phone_no = phone_no_d.replaceAll("[*#]", "");

                DatabaseReference users = mRootRef.child("identity").child(phone_no);
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Identity userIdentity = snapshot.getValue(Identity.class);
                        if (userIdentity != null) {
                            // run some code
                            MyDbHelper myDbHelper = new MyDbHelper(getBaseContext(), null, null, 1);
                            Boolean is_ringlerr = myDbHelper.checkRinglerrUser(phone_no);

                            if(userIdentity.app_remove!=null) {
                                if ((!is_ringlerr && userIdentity.app_remove == false)) {
                                    myDbHelper.addRinglerrUser(phone_no);
                                    if (null != mFt) {
                                        String name = getContactName(getApplicationContext(), phone_no);
                                        String notiMgs = name + " is now on Ringlerr";
                                        setAlarm(notiMgs, "Ringlerr Reminder");
                                    }
                                } else if (is_ringlerr && userIdentity.app_remove == true) {
                                    //delete from database
                                    myDbHelper.deleteRinglerrUser(phone_no);
                                }
                            }else{
                                if (!is_ringlerr) {
                                    myDbHelper.addRinglerrUser(phone_no);
                                    if (null != mFt) {
                                        String name = getContactName(getApplicationContext(), phone_no);
                                        String notiMgs = name + " is now on Ringlerr";
                                        setAlarm(notiMgs, "Ringlerr Reminder");
                                    }
                                }
                            }

                            if(userIdentity.app_remove!=null) {
                                if(userIdentity.app_remove==false) {
                                    final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(phone_no + ".jpg");
                                    download_image(filepath, phone_no);
                                }

                                if(userIdentity.app_remove==true) {
                                    String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone_no+".jpeg";
                                    File fdelete = new File(filePath);
                                    if (fdelete.exists()) {
                                        if (fdelete.delete()) {

                                        } else {

                                        }
                                    }
                                }
                            }else{
                                final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(phone_no + ".jpg");
                                download_image(filepath, phone_no);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        session.addFirstT("1");
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
                //Object does not exist at location.
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

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
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

    public void setAlarm(String notiMgs, String title){
        //set alarmn
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_05";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ringlerr Update", NotificationManager.IMPORTANCE_LOW);

            // Configure the notification channel.
            notificationChannel.setDescription("Ringlerr Update");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);

        String appName = getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setTicker(appName)
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(notiMgs)
                .setContentInfo("Info");

        //when this notification is clicked and the upload is running, open the upload fragment
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

}
