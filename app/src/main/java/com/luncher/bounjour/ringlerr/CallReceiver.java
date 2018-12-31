package com.luncher.bounjour.ringlerr;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.activity.FullScreenHolder;
import com.luncher.bounjour.ringlerr.activity.callEndDialog;

import androidx.core.content.ContextCompat;

/**
 * Created by santanu on 11/11/17.
 */

public class CallReceiver extends PhonecallReceiver {

    private WindowManager wm;
    private static LinearLayout ly1;
    private WindowManager.LayoutParams params1;
    ArrayList message;
    MyDbHelper myDbHelper;
    Boolean isRingerrrUser = true;
    private DatabaseReference mRootRef;
    SessionManager session;

    @Override
    protected void onIncomingCallStarted(final Context ctx, final String number, final Date start) {

        Boolean service_running = isMyServiceRunning(MessageService.class, ctx);
        if(!service_running){
            Intent serviceIntent = new Intent(ctx, MessageService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(ctx, serviceIntent);
            } else {
                ctx.startService(serviceIntent);
            }

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    doShowMessage(ctx, number, start);
                }
            }, 5000);

        }else{
            doShowMessage(ctx, number, start);
        }

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        myDbHelper = new MyDbHelper(ctx, null, null, 1);

        String block_number = myDbHelper.checkBlockNumber(number);
        if(!block_number.equals("null")) {
            disconnectPhoneItelephony(ctx);
        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

        String name = getContactName(ctx, number);
        final Intent call_end = new Intent(ctx, callEndDialog.class);
        call_end.putExtra("phone_no", number);
        call_end.putExtra("name", name);
        call_end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        call_end.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        call_end.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        call_end.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        call_end.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ctx.startActivity(call_end);

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

        String name = getContactName(ctx, number);
        final Intent call_end = new Intent(ctx, callEndDialog.class);
        call_end.putExtra("phone_no", number);
        call_end.putExtra("name", name);
        call_end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        call_end.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        call_end.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        call_end.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        call_end.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ctx.startActivity(call_end);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

        String name = getContactName(ctx, number);
        final Intent call_end = new Intent(ctx, callEndDialog.class);
        call_end.putExtra("phone_no", number);
        call_end.putExtra("name", name);
        call_end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        call_end.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        call_end.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        call_end.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        call_end.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ctx.startActivity(call_end);
    }

    private void doShowMessage(final Context ctx, String number_s, Date start){

        number_s = number_s.replace(" ", "");
        final String number = "+91"+getLastnCharacters(number_s, 10);
        myDbHelper = new MyDbHelper(ctx, null, null, 1);

        String block_number = myDbHelper.checkBlockNumber(number);
        if(!block_number.equals("null")){
            disconnectPhoneItelephony(ctx);
        }else {

            message = myDbHelper.getMessage(number);

            if (!message.isEmpty()) {
                String cmessage = message.get(0).toString();
                String image = message.get(1).toString();
                String type = message.get(2).toString();
                String talk_time = message.get(3).toString();

                if (type.equals("themgif")) {

                    if(FullScreenHolder.fsh != null){
                        FullScreenHolder.fsh.finish();
                    }
                    if(AnimationActivity.aa != null){
                        AnimationActivity.aa.finish();
                    }

                    final Intent intent1 = new Intent(ctx, AnimationActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    }
                    intent1.putExtra("phone_no", number);
                    intent1.putExtra("message", cmessage);
                    intent1.putExtra("image", image);
                    intent1.putExtra("type", type);
                    intent1.putExtra("talk_time", talk_time);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ctx.startActivity(intent1);
                        }
                    }, 1500);

                } else {

                    if (cmessage.equals("") || cmessage == null) {
                        if(FullScreenHolder.fsh != null){
                            FullScreenHolder.fsh.finish();
                        }
                        if(AnimationActivity.aa != null){
                            AnimationActivity.aa.finish();
                        }
                        final Intent intent = new Intent(ctx, FullScreenHolder.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        }
                        intent.putExtra("phone_no", number);
                        intent.putExtra("message", cmessage);
                        intent.putExtra("image", image);
                        intent.putExtra("type", type);
                        intent.putExtra("talk_time", talk_time);
                        intent.putExtra("screen_type", "full");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ctx.startActivity(intent);
                            }
                        }, 2000);

                    } else {
                        if(MyCustomDialog.fa != null){
                            MyCustomDialog.fa.finish();
                        }
                        final Intent intent = new Intent(ctx, MyCustomDialog.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        }
                        intent.putExtra("phone_no", number);
                        intent.putExtra("message", cmessage);
                        intent.putExtra("image", image);
                        intent.putExtra("type", type);
                        intent.putExtra("talk_time", talk_time);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ctx.startActivity(intent);
                            }
                        }, 1500);
                    }
                }
            }else {

                mRootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference blockrootRef = mRootRef.child("block_count/"+ number +"/count");
                blockrootRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Object count = dataSnapshot.getValue();
                            String bl_count = "0";
                            if(count!=null){
                                bl_count = count.toString();
                            }
                            int block_count = getCount(Integer.parseInt(bl_count));
                            if(block_count>2){
                                final Intent intent = new Intent(ctx, MyCustomDialog.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                }
                                intent.putExtra("phone_no", number);
                                intent.putExtra("message", "");
                                intent.putExtra("image", "none");
                                intent.putExtra("type", "none");
                                intent.putExtra("talk_time", "");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                         ctx.startActivity(intent);
                                    }
                                }, 1500);
                            }else{
                                DatabaseReference spamrootRef = mRootRef.child("report/"+ number +"/count");
                                spamrootRef.addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Object count = dataSnapshot.getValue();
                                                String bl_count = "0";
                                                if(count!=null){
                                                    bl_count = count.toString();
                                                }
                                                int block_count = getCount(Integer.parseInt(bl_count));
                                                if(block_count>2){
                                                    final Intent intent = new Intent(ctx, MyCustomDialog.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    }
                                                    intent.putExtra("phone_no", number);
                                                    intent.putExtra("message", "");
                                                    intent.putExtra("image", "none");
                                                    intent.putExtra("type", "none");
                                                    intent.putExtra("talk_time", "");

                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ctx.startActivity(intent);
                                                        }
                                                    }, 1500);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        }
    }

    private int getCount(int count){
        return count;
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

    private void openWhatsApp(String phone, Context ctx) {
        String smsNumber = phone; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "sent @Ringerrr.");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");

        ctx.startActivity(sendIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context) {
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
