package com.luncher.santanu.dailer;

/**
 * Created by santanu on 11/11/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyCustomDialog extends Activity
{
    TextView tv_client;
    TextView sender;
    TextView sender_name;
    ImageView r_image;
    String phone_no;
    String message;
    String image;
    String type;
    String show_message;
    String result;
    Button dialog_ok;
    private GifImageView gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(false);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog);
            initializeContent();
            this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            /*WindowManager.LayoutParams params = getWindow().getAttributes();
            params.x = -100;
            params.height = 70;
            params.width = 1000;
            params.y = -50;

            this.getWindow().setAttributes(params);*/

            phone_no = getIntent().getExtras().getString("phone_no");
            message = getIntent().getExtras().getString("message");
            image = getIntent().getExtras().getString("image");
            type = getIntent().getExtras().getString("type");

            show_message = phone_no +" is calling you with message : "+message;
            sender.setText(phone_no);
            tv_client.setText(message);

            if(!image.equals("none")) {
                String imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/"+image;

                if(type.equals("gif")) {
                    File imageFile = new File(imageStoragePath);
                    FileInputStream fileInputStream = new FileInputStream(imageFile);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;

                    while ((len = fileInputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }

                    fileInputStream.close();
                    byte[] bytes = outStream.toByteArray();

                    gifView = findViewById(R.id.gifImageView);
                    gifView.setBytes(bytes);
                    gifView.startAnimation();
                }else if(type.equals("libgif")){
//                    InputStream inputStream = null;
//                    inputStream = getApplicationContext().getAssets().open(image);
//                    byte[] bytes = IOUtils.toByteArray(inputStream);
//                    gifView = findViewById(R.id.gifImageView);
//                    gifView.setBytes(bytes);
//                    gifView.startAnimation();
                    String asseturl = "file:///android_asset/"+image;
                    Glide.with(this).asGif()
                            .load(Uri.parse(asseturl))
                            .into(r_image);
                }else if(type.equals("sticker")){
                    AssetManager assetManager = getAssets();

                    InputStream istr = assetManager.open(image);
                    Bitmap bitmap = BitmapFactory.decodeStream(istr);
                    r_image.setImageBitmap(bitmap);
                }else{
                    //File imageFile = new File(imageStoragePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imageStoragePath);
                    //bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                    r_image.setImageBitmap(bitmap);
                }

//                int duration = Toast.LENGTH_LONG;
//                // Context pcontext;
//                Toast toast;
//                toast = Toast.makeText(getApplicationContext(), imageStoragePath, duration);
//                toast.show();
            }

//            ViewDialog alert = new ViewDialog();
//            alert.showDialog(MyCustomDialog.this, message);

            r_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Code to show image in full screen:
                    try {
                        new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, view, image, type, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            dialog_ok.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MyCustomDialog.this.finish();
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().post(new Runnable() {

            @Override
            public void run() {
                String name = getContactName(getApplicationContext(), phone_no);
                if(name != null){
                    sender_name.setText(name);
                }
            }

        });
    }

    private void initializeContent()
    {
        tv_client   = (TextView) findViewById(R.id.tv_client);
        sender   = (TextView) findViewById(R.id.sender);
        sender_name   = (TextView) findViewById(R.id.name);
        dialog_ok   = (Button) findViewById(R.id.dialog_ok);
        r_image = (ImageView) findViewById(R.id.r_image);
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

}

