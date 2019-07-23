package com.ringlerr.callplus;

import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.widget.PopupMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.graphics.Bitmap;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.BitmapFactory;
import java.io.IOException;
import android.graphics.drawable.BitmapDrawable;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.net.URLEncoder;
import java.lang.StringBuilder;
import java.io.OutputStreamWriter;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Iterator;

public class DialogActivity extends AppCompatActivity {

    String[] languages = { "I will call you back.",
            "In a meeting, will call you back.",
            "I am driving.",
            "Stuck somewhere, call back later.",
            "Ok Sure"};

    String[] call_back = { "Call back between 11AM-12Noon.",
            "Call back between 12Noon-1PM",
            "Call back between 1PM-2PM",
            "Call back between 2PM-3PM",
            "Call back between 3PM-4PM",
            "Call back between 4PM-5PM",
            "Call back After 6PM", };

    Dialog dialog;
    String title = "New Title";
    String serverToken  = "your_server_token_here";
    String key  = "your_key_here";

    String background = "#212121";
    String title_text_color  = "#E57373";
    String name_text_color  = "#FFFFFF";
    String phone_text_color  = "#B0BEC5";
    String message_text_color  = "#37474F";

    String m_phone;
    String type;
    String recever_name;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        this.getWindow().setLayout( WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        setContentView(R.layout.activity_dialog);
        Intent intent = getIntent();
        mainRun(intent);

    }

    private void mainRun(Intent intent){

        String message = intent.getExtras().getString("message");
        final String from = intent.getExtras().getString("from");
        type = intent.getExtras().getString("type");
        name = intent.getExtras().getString("caller_name");
        m_phone = intent.getExtras().getString("to");
        recever_name = intent.getExtras().getString("recever_name");
        final String urlImage = intent.getExtras().getString("user_image");

        // SessionManager session = new SessionManager(getApplicationContext());
        // // get user data from session
        // HashMap<String, String> user = session.getUserDetails();
        // final String mPhoneNo = user.get(SessionManager.KEY_PHONE);
        // Long tsLong = System.currentTimeMillis()/1000;

        // DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference mTo = mRootRef.child("message/ionic").child(mPhoneNo);
        // Message messages_to = new Message(from, message, type, tsLong+"", true);
        // mTo.setValue(messages_to);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

        if(type.equals("image")) {
            dialog.setContentView(R.layout.dialog_info_image);

            final String bannerUrlImage = intent.getExtras().getString("banner_url");
            ImageView banner_image = dialog.findViewById(R.id.banner_image);
            getImage(bannerUrlImage, banner_image);
        }else if(type.equals("mix")){
            dialog.setContentView(R.layout.dialog_info_mix);

            final String bannerUrlImage = intent.getExtras().getString("banner_url");
            ImageView banner_image = dialog.findViewById(R.id.banner_image);
            getImage(bannerUrlImage, banner_image);   
        }else{
            dialog.setContentView(R.layout.dialog_info);
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        final EditText messageView = dialog.findViewById(R.id.messageView);
        TextView fromView = dialog.findViewById(R.id.phone);
        TextView caller_name = dialog.findViewById(R.id.caller_name);
        LinearLayout layout_body = dialog.findViewById(R.id.layout_body);
        TextView pop_title = dialog.findViewById(R.id.title);
        ImageView userImageView = dialog.findViewById(R.id.image);

        pop_title.setTextColor(Color.parseColor(title_text_color));
        caller_name.setTextColor(Color.parseColor(name_text_color));
        fromView.setTextColor(Color.parseColor(phone_text_color));
        messageView.setTextColor(Color.parseColor(message_text_color));

        getImage(urlImage, userImageView);
        
        if(null != message) {
            messageView.setText(message);
            messageView.setSelection(messageView.getText().length());
        }

        if(null != from) {
            fromView.setText(from);
        }

        if(null != name) {
            caller_name.setText(name);
        }

        if(null != title) {
            pop_title.setText(title);
        }

        if(null != background) {
            layout_body.setBackgroundColor(Color.parseColor(background));;
        }

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogActivity.this.finish();
            }
        });

        ((ImageView) dialog.findViewById(R.id.nt_interested)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertBack("Not Interested", from);
                dialog.dismiss();
                DialogActivity.this.finish();
            }
        });

        ((ImageView) dialog.findViewById(R.id.bt_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageView.getText().toString();
                revertBack(message, from);
                dialog.dismiss();
                DialogActivity.this.finish();
            }
        });

        final ImageView bt_revert = dialog.findViewById(R.id.bt_revert);

        bt_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                Context wrapper = new ContextThemeWrapper(DialogActivity.this, R.style.MyPopupMenu);
                final PopupMenu popup = new PopupMenu(wrapper, bt_revert);

                for (String s: languages) {
                    //Do your stuff here
                    popup.getMenu().add(s);
                }
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String mgs = item.getTitle().toString();
                        revertBack(mgs, from);
                        dialog.dismiss();
                        finish();
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        final ImageView callback = dialog.findViewById(R.id.bt_callback);
        callback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                Context wrapper = new ContextThemeWrapper(DialogActivity.this, R.style.MyPopupMenu);
                final PopupMenu popup = new PopupMenu(wrapper, callback);
                for (String s: call_back) {
                    //Do your stuff here
                    popup.getMenu().add(s);
                }
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String mgs = item.getTitle().toString();
                        revertBack(mgs, from);
                        dialog.dismiss();
                        finish();
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    public void getImage(final String urlImage, final ImageView imageView){
        new AsyncTask<String, Integer, Drawable>(){

            @Override
            protected Drawable doInBackground(String... strings) {
                Bitmap bmp = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(urlImage).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bmp = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new BitmapDrawable(bmp);
            }
    
            protected void onPostExecute(Drawable result) {
    
                //Add image to ImageView
                imageView.setImageDrawable(result);
    
            }
    
    
        }.execute();
    }

    private void revertBack(final String mgs, final String from) {
        new AsyncTask<String, Integer, Drawable>(){

            @Override
            protected Drawable doInBackground(String... strings) {

                Long tsLong = System.currentTimeMillis()/1000;
                JSONObject postDataParams = new JSONObject();
                try {
                    postDataParams = new JSONObject();
                    postDataParams.put("message", mgs);
                    postDataParams.put("phone_from", from);
                    postDataParams.put("phone_to", m_phone);
                    postDataParams.put("type", type);
                    postDataParams.put("time", tsLong+"");
                    postDataParams.put("caller_name", recever_name);
                    postDataParams.put("recever_name", name);
                    postDataParams.put("user_image", "");
                    postDataParams.put("banner_url", "");
                    postDataParams.put("serverToken", serverToken);
                    postDataParams.put("key", key);
                }catch (JSONException e) {
                    //Log.e(TAG, "Json Exception: " + e.getMessage());
                }

                Bitmap bmp = null;
                String urlImage = "http://api.ringlerr.com/v3/ionic.php";
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(urlImage).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(15000 /* milliseconds */);
                    connection.setConnectTimeout(15000 /* milliseconds */);
                    connection.setDoOutput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    //OutputStream outputPost = new BufferedOutputStream(connection.getOutputStream());
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();

                }catch (IOException e) {
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if(connection != null) // Make sure the connection is not null.
                    connection.disconnect();
                }
                return new BitmapDrawable(bmp);
            }
    
            protected void onPostExecute(Drawable result) {
    
                //Add image to ImageView
    
            }
    
    
        }.execute();
    }

    @Override
    public void onBackPressed() {
        DialogActivity.this.finish();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        dialog.dismiss();
        mainRun(intent);

    }

    public String getPostDataString(JSONObject params) throws Exception {
 
        StringBuilder result = new StringBuilder();
        boolean first = true;
 
        Iterator<String> itr = params.keys();
 
        while(itr.hasNext()){
 
            String key= itr.next();
            Object value = params.get(key);
 
            if (first)
                first = false;
            else
                result.append("&");
 
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
 
        }
        return result.toString();
    }
}
