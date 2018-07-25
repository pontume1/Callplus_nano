package com.luncher.santanu.dailer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import com.luncher.santanu.dailer.activity.FullScreenHolder;
import com.luncher.santanu.dailer.activity.callEndDialog;

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

    @Override
    protected void onIncomingCallStarted(final Context ctx, String number, Date start) {

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

                if (type.equals("themgif")) {

                    final Intent intent1 = new Intent(ctx, AnimationActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intent1.putExtra("phone_no", number);
                    intent1.putExtra("message", cmessage);
                    intent1.putExtra("image", image);
                    intent1.putExtra("type", type);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ctx.startActivity(intent1);
                        }
                    }, 1500);

                } else {

                    if (cmessage.equals("") || cmessage == null) {

                        final Intent intent = new Intent(ctx, FullScreenHolder.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        intent.putExtra("phone_no", number);
                        intent.putExtra("message", cmessage);
                        intent.putExtra("image", image);
                        intent.putExtra("type", type);
                        intent.putExtra("screen_type", "full");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ctx.startActivity(intent);
                            }
                        }, 2000);

                    } else {

                        final Intent intent = new Intent(ctx, MyCustomDialog.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("phone_no", number);
                        intent.putExtra("message", cmessage);
                        intent.putExtra("image", image);
                        intent.putExtra("type", type);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ctx.startActivity(intent);
                            }
                        }, 1500);
                    }
                }
            }
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

    private void openWhatsApp(String phone, Context ctx) {
        String smsNumber = phone; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "sent @Ringerrr.");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");

        ctx.startActivity(sendIntent);
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
