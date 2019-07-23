package com.ringlerr.callplus;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class CallPlus {

    private CallPlus() {
        // no direct instantiation
    }

    public static void showDialog(final Context context, JSONObject json) {
//        Intent dialogIntent = new Intent(context, DialogActivity.class);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        dialogIntent.putExtra("message", "hrllo");
//        dialogIntent.putExtra("from", "from me");
//        dialogIntent.putExtra("type", "call");
//        dialogIntent.putExtra("to", "to");
//        dialogIntent.putExtra("caller_name", "vickey");
//        dialogIntent.putExtra("recever_name", "mickey");
//        dialogIntent.putExtra("user_image", "");
//        dialogIntent.putExtra("banner_url", "");
//        context.startActivity(dialogIntent);
        handleDataMessage(context, json);
    }

    private static void handleDataMessage(final Context context, JSONObject json) {

        try {
            final String message = json.getString("message");
            final String from = json.getString("phone_from");
            final String type = json.getString("type");
            final String time = json.getString("time");
            final String to = json.getString("phone_to");
            final String caller_name = json.getString("caller_name");
            final String recever_name = json.getString("recever_name");
            final String user_image = json.getString("user_image");
            final String banner_url = json.getString("banner_url");

            Long recTime = Long.valueOf(time);
            Long tsLong = System.currentTimeMillis()/1000;
            //String timestamp = data.getString("timestamp");
            //JSONObject payload = data.getJSONObject("payload");

            if((tsLong - recTime)<35) {

                if (type.equals("call") || type.equals("image") || type.equals("mix")) {
                    final int[] i = {0};
                    final Handler ha = new Handler(Looper.getMainLooper());
                    ha.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            //call function
                            boolean callActive = isCallActive(context);
                            if (i[0] < 30 && !callActive) {
                                i[0]++;
                                ha.postDelayed(this, 1000);
                            }

                            if (callActive) {
                                Intent dialogIntent = new Intent(context, DialogActivity.class);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                dialogIntent.putExtra("message", message);
                                dialogIntent.putExtra("from", from);
                                dialogIntent.putExtra("type", type);
                                dialogIntent.putExtra("to", to);
                                dialogIntent.putExtra("caller_name", caller_name);
                                dialogIntent.putExtra("recever_name", recever_name);
                                dialogIntent.putExtra("user_image", user_image);
                                dialogIntent.putExtra("banner_url", banner_url);
                                context.startActivity(dialogIntent);
                            }
                        }
                    }, 1000);
                } else {
                    Intent dialogIntent = new Intent(context, DialogActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    dialogIntent.putExtra("message", message);
                    dialogIntent.putExtra("from", from);
                    dialogIntent.putExtra("type", type);
                    dialogIntent.putExtra("to", to);
                    dialogIntent.putExtra("caller_name", caller_name);
                    dialogIntent.putExtra("recever_name", recever_name);
                    dialogIntent.putExtra("user_image", user_image);
                    dialogIntent.putExtra("banner_url", banner_url);
                    context.startActivity(dialogIntent);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private static boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        assert manager != null;
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            return true;
        }else return manager.getMode() == AudioManager.MODE_RINGTONE;
    }

    public boolean updateToken(String key, String phone_no, String token){
          return true;
    }

    public boolean sendContext(String key, String phone_number, String message, String image, String image_url, String phone_from, String caller_name, String recever_name, String user_image, String serverToken){
         return true;
    }
}
