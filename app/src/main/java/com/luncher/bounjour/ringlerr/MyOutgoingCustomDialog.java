package com.luncher.bounjour.ringlerr;

/**
 * Created by santanu on 11/11/17.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.nkzawa.emitter.Emitter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luncher.bounjour.ringlerr.activity.FullScreenHolder;
import com.luncher.bounjour.ringlerr.model.Identity;
import com.luncher.bounjour.ringlerr.model.Message;
import com.soundcloud.android.crop.Crop;
import com.squareup.okhttp.OkHttpClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

public class MyOutgoingCustomDialog extends Activity {
    TextView name;
    TextView phone;
    TextView counter;
    TextView talk_time;
    String phone_no;
    String c_name;
    Boolean is_ringlerr;
    String message;
    String clear_type;
    String userChoosenTask;
    Bitmap imagePath = null;
    byte[] gifImage;
    String libImage = "none";
    String themeImage = "none";
    String image = "none";
    String stickerImage = "none";
    String talkTime = "";
    EditText sendMgs;
    ImageButton dialog_ok;
    LinearLayout img;
    MyDbHelper myDbHelper;
    int sim;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, PICK_ICON_REQUEST = 2, PICK_THEME_REQUEST = 3, FINAL_EDIT_IMG = 4, PICK_STICKER_REQUEST = 5;
    private ImageButton btnSelect;
    private ImageButton btnSelectGif;
    private ImageButton btnSelectStickers;
    private ImageButton btnSelectheme;
    private ImageView ivImage;
    private ImageView profile_image;
    private ImageView clear_image;
    private ImageView imageView5;

    private static final int PERMISSION_REQUEST_OUTGOING_CALLS = 12;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 124;
    public static final int REQUEST_CODE = 15;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private String mCurrentUserId;
    private String mPhoneNo;
    private String mName;

    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        SessionManager session = new SessionManager(MyOutgoingCustomDialog.this);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);
        mName = user.get(SessionManager.KEY_NAME);

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(false);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_outgoing);
            initializeContent();
            this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            //this.getWindow().addFlags(PixelFormat.TRANSPARENT);

            /*WindowManager.LayoutParams params = getWindow().getAttributes();
            params.x = -100;
            params.height = 70;
            params.width = 1000;
            params.y = -50;

            this.getWindow().setAttributes(params);*/

            Log.d("test","outgoing custom dialog");

            phone_no = getIntent().getExtras().getString("phone_no");
            c_name = getIntent().getExtras().getString("name");

            phone_no = "+91"+getLastnCharacters(phone_no,10);
            //sim = getIntent().getExtras().getInt("sim");
            sim = 0;

            btnSelect = (ImageButton) findViewById(R.id.btnSelectPhoto);
            btnSelectGif = (ImageButton) findViewById(R.id.btnSelectGif);
            //btnSelectheme = (ImageButton) findViewById(R.id.btnSelectheme);
            btnSelectStickers = (ImageButton) findViewById(R.id.btnSelectStickers);
            ivImage = (ImageView) findViewById(R.id.ivImage);
            profile_image = (ImageView) findViewById(R.id.profile_image);
            clear_image = (ImageView) findViewById(R.id.clear_image);
            imageView5 = (ImageView) findViewById(R.id.imageView5);
            name = (TextView) findViewById(R.id.name);
            phone = (TextView) findViewById(R.id.phone);
            talk_time = (TextView) findViewById(R.id.talk_time);
            img = (LinearLayout) findViewById(R.id.img);
            ImageButton closeButton = (ImageButton) findViewById(R.id.close_btn);

            if(phone_no.equals(c_name)){
                name.setText("");
            }else {
                name.setText(phone_no);
            }
            phone.setText(c_name);

//            Bitmap profile_bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
//            if(profile_bitmap != null){
//                profile_image.setImageBitmap(profile_bitmap);
//            }

            String img_type = getIntent().getExtras().getString("type");
            if(img_type != null) {
                String img_res = getIntent().getExtras().getString("image");
                if(img_type.equals("gif_library")){
                    onSelectFromLibraryResult(img_res, "libImage");
                }else if(img_type.equals("theme")){
                    onSelectFromLibraryResult(img_res, "themeImage");
                }else if(img_type.equals("sticker")) {
                    onSelectFromStickerResult(img_res);
                }
            }

            imageView5.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(MyOutgoingCustomDialog.this, imageView5);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.talktime_option_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.optionA:
                                    talkTime = "0-30Sec";
                                    talk_time.setText(talkTime);
                                    break;
                                case R.id.optionB:
                                    talkTime = "30Sec - 1Min";
                                    talk_time.setText(talkTime);
                                    break;
                                case R.id.optionC:
                                    talkTime = "1Min - 2Min";
                                    talk_time.setText(talkTime);
                                    break;
                                case R.id.optionD:
                                    talkTime = "2Min - 5Min";
                                    talk_time.setText(talkTime);
                                    break;
                                case R.id.optionE:
                                    talkTime = "5Min - 10Min";
                                    talk_time.setText(talkTime);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });

            btnSelect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CharSequence[] items = { "Take Photo", "Choose from Gallery","Cancel" };
                    selectImage(items);
                    //@color/fui_transparent
                    btnSelect.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    btnSelectGif.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    btnSelectStickers.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            });

            clear_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (clear_type.equals("img")) {

                        imagePath = null;
                        libImage = "none";
                        themeImage = "none";
                        ivImage.setImageDrawable(null);
                    }

                    if (clear_type.equals("gif")) {
                        gifImage = null;

//                        gifView.stopAnimation();
//                        gifView.setBytes(null);
//                        gifView.setVisibility(View.GONE);
                    }
                    clear_image.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                }
            });


            btnSelectGif.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectGifLibrary();
                    btnSelect.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    btnSelectGif.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    btnSelectStickers.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            });

//            btnSelectheme.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    selectThemeLibrary();
//                }
//            });

            btnSelectStickers.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectStickerLibrary();
                    btnSelect.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    btnSelectGif.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    btnSelectStickers.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }

            });

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_DENIED) {

                    Log.d("permission", "permission denied to SEND_SMS - requesting it");
                    String[] permissions = {Manifest.permission.CALL_PHONE};

                    requestPermissions(permissions, PERMISSION_REQUEST_CALL_PHONE);

                }

                if (checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
                        == PackageManager.PERMISSION_DENIED) {

                    Log.d("permission", "permission denied to SEND_SMS - requesting it");
                    String[] permissions = {Manifest.permission.PROCESS_OUTGOING_CALLS};

                    requestPermissions(permissions, PERMISSION_REQUEST_OUTGOING_CALLS);

                }
            }

            dialog_ok.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    message = sendMgs.getText().toString();
                    String type = "none";
                    //tv_client.setText(message);
                    if (imagePath != null) {
                        image = encodeImage(imagePath);
                        type = "jpg";
                    } else if (gifImage != null && gifImage.length > 0) {
                        image = encodeGifImage(gifImage);
                        type = "gif";
                    } else if (!libImage.equals("none")) {
                        image = libImage;
                        type = "libgif";
                    } else if (!themeImage.equals("none")) {
                        image = themeImage;
                        type = "themgif";
                    } else if(!stickerImage.equals("none")){
                        image = stickerImage;
                        type = "sticker";
                    }else{
                        image = "none";
                    }
                    //result = "{'from':'"+mPhoneNo+"','to':'"+phone_no+"', 'message':'"+message+"','image':'"+image+"'}";

//                    JSONObject result = new JSONObject();
//                    try{
//                        result.put("from", mPhoneNo);
//                        result.put("to", phone_no);
//                        result.put("message", message);
//                        result.put("image", image);
//                        result.put("type", type);
//                    }catch(JSONException e){
//
//                    }

                    if(!image.equals("none") || !message.isEmpty()){
//                        mSocket.connect();
//                        mSocket.emit("message", result);
//                        DatabaseReference mfrom = mRootRef.child("message").child(mPhoneNo);
//                        Message messages_frm = new Message(mPhoneNo, phone_no, message, image, type, "false", ""+ServerValue.TIMESTAMP);
//                        mfrom.setValue(messages_frm);

                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();

                        if(talkTime.equals("Talk Time")){
                            talkTime = "";
                        }

                        DatabaseReference mTo = mRootRef.child("message").child(phone_no);
                        Message messages_to = new Message(mPhoneNo, phone_no, message, image, type, "false", ts, talkTime);
                        mTo.setValue(messages_to);

                        String key = mRootRef.child("chats").child(mPhoneNo).child(phone_no).push().getKey();
                        Message chat = new Message(mPhoneNo, phone_no, message, image, type, "false", ts, talkTime);
                        Map<String, Object> postValues = chat.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/chats/" + phone_no + "/" +mPhoneNo+ "/" + key, postValues);
                        childUpdates.put("/chats/" + mPhoneNo + "/" +phone_no+ "/" + key, postValues);

                        mRootRef.updateChildren(childUpdates);

                    }

                    //this.setFinishOnTouchOutside(false);
                    if(is_ringlerr) {
                        if (type.equals("gif")) {
                            final ProgressDialog progress = new ProgressDialog(MyOutgoingCustomDialog.this);
                            progress.setTitle("Connecting");
                            progress.setMessage("Please wait while we connect to devices...");
                            progress.show();

                            Runnable progressRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    progress.cancel();
                                    //                                PackageManager pm = mContext.getPackageManager();
                                    //                                ComponentName componentName = new ComponentName(mContext, MyOutgoingCallHandler.class);
                                    //                                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                                    //setResultData(phone_no);
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + phone_no));
                                    int simSlot = getDefaultSimSlot(MyOutgoingCustomDialog.this);
                                    callIntent.putExtra("com.android.phone.force.slot", true);
                                    callIntent.putExtra("com.android.phone.extra.slot", simSlot);
                                    startActivity(callIntent);
                                    finish();
                                }
                            };

                            Handler pdCanceller = new Handler();
                            pdCanceller.postDelayed(progressRunnable, 15000);

                        } else {
                            //                        PackageManager pm = mContext.getPackageManager();
                            //                        ComponentName componentName = new ComponentName(mContext, MyOutgoingCallHandler.class);
                            //                        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                            //setResultData(phone_no);
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone_no));
                            int simSlot = getDefaultSimSlot(MyOutgoingCustomDialog.this);
                            callIntent.putExtra("com.android.phone.force.slot", true);
                            callIntent.putExtra("com.android.phone.extra.slot", simSlot);
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(callIntent);
                            finish();

                            if(!image.equals("none")){
                                final Intent intent = new Intent(MyOutgoingCustomDialog.this, FullScreenHolder.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                intent.putExtra("phone_no", phone_no);
                                intent.putExtra("message", message);
                                intent.putExtra("image", image);
                                intent.putExtra("type", type);
                                intent.putExtra("talk_time", talkTime);
                                intent.putExtra("screen_type", "half");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                    }
                                }, 2000);
                            }
                        }

                        message = "";
                    }else{

                        Long tsLong = (System.currentTimeMillis()/1000);
                        String ts = tsLong.toString();
                        mName = getFirstWord(mName);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something here
                                String message_send = "";
                                String message_senda = "";
                                try {
                                    message_send = URLEncoder.encode(mName.toUpperCase()+" CALLING:"+message, "UTF-8");
                                    message_senda = URLEncoder.encode(mName.toUpperCase()+" CALLING:"+message, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String flash_number = getLastnCharacters(phone_no, 10);
                                String sUrl2 = "http://ringlerr.com/sms_api/?number="+flash_number+"&message="+message_send+"&messagea="+message_senda+"&key=ponring";

                                //new GetUrlContentTask().execute(sUrl);
                                new GetUrlContentTask().execute(sUrl2);
                                message = "";
                            }
                        }, 10);


//                        final ProgressDialog progress = new ProgressDialog(MyOutgoingCustomDialog.this);
//                        progress.setTitle("Connecting");
//                        progress.setMessage("Please wait while we connect to devices...");
//                        progress.show();

                        Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                //progress.cancel();
                                //  PackageManager pm = mContext.getPackageManager();
                                //  ComponentName componentName = new ComponentName(mContext, MyOutgoingCallHandler.class);
                                //  pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                                //setResultData(phone_no);
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + phone_no));
                                int simSlot = getDefaultSimSlot(MyOutgoingCustomDialog.this);
                                callIntent.putExtra("com.android.phone.force.slot", true);
                                callIntent.putExtra("com.android.phone.extra.slot", simSlot);
                                startActivity(callIntent);
                                finish();
                            }
                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 100);

                    }
                    //
//                    callIntent.putExtra("com.android.phone.force.slot", true);
//                    callIntent.putExtra("Cdma_Supp", true);
//                    //Add all slots here, according to device.. (different device require different key so put all together)
//                    for (String s : simSlotName)
//                        callIntent.putExtra(s, 0); //0 or 1 according to sim.......
//
//                    //works only for API >= 21
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                        TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
//                        List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
//
//                        if (sim== 0) {   //0 for sim1
//                            for (String s : simSlotName)
//                                callIntent.putExtra(s, 0); //0 or 1 according to sim.......
//
//                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0)
//                                callIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
//
//                        } else {
//                            for (String s : simSlotName)
//                                callIntent.putExtra(s, 1); //0 or 1 according to sim.......
//
//                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1)
//                                callIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
//
//                        }
//                    }

                    Log.d("test", "stoping activity");
                    //Log.d("test", result);
                }
            });
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
            //setResultData(phone_no);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.get(MyOutgoingCustomDialog.this).clearMemory();
        profile_image = null;
    }

    private void download_image(StorageReference filepath, final String phone_no) {

        final long ONE_MEGABYTE = 1024 * 1024;

        //download file as a byte array
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String file_path = storeProfileImage(bitmap, phone_no+".jpeg");

                if(profile_image!=null) {
                    RequestOptions requestOptions = RequestOptions.circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(getApplicationContext())
                            .load(file_path)
                            .apply(requestOptions)
                            .into(profile_image);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                String mgs = exception.getMessage();
                String h = mgs;
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

    private Boolean checkRinglerrUser(String phone_no) {

        if(phone_no==null){
            return false;
        }
        phone_no = phone_no.replaceAll("[*#]", "");
        final String finalPhone_no = phone_no;
        is_ringlerr = true;

        DatabaseReference users = mRootRef.child("identity").child(finalPhone_no);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Identity userIdentity = snapshot.getValue(Identity.class);
                if (userIdentity == null) {
                    // run some code
                    is_ringlerr = false;
                }else{
                    if(userIdentity.app_remove==null){
                        is_ringlerr = false;
                    }else if(userIdentity.app_remove==true){
                        is_ringlerr = false;
                    }else{
                        is_ringlerr = true;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return is_ringlerr;
    }

    private String getFirstWord(String text) {
        int index = text.indexOf(' ');
        if (index > -1) { // Check if there is more than one word.
            return text.substring(0, index); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }

    public int  getDefaultSimSlot(Context context) {

        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getDefaultSim;
        int defaultSimm = -1;
        try {
            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
            method_getDefaultSim.setAccessible(true);
            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Method method_getSmsDefaultSim;
        int smsDefaultSim = -1;
        try {
            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return defaultSimm;
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

    private void initializeContent()
    {
        counter   = (TextView) findViewById(R.id.counter);
        dialog_ok   = (ImageButton) findViewById(R.id.dialog_ok);
        sendMgs = (EditText)findViewById(R.id.editTextDialogUserInput);
        sendMgs.addTextChangedListener(mTextEditorWatcher);
        //TextView tview = (TextView)findViewById(R.id.textview1);
        //String result = sendMgs.getText().toString();
        //tview.setText(result);
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            counter.setText(String.valueOf(s.length()-120));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        is_ringlerr = checkRinglerrUser(phone_no);

        String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone_no+".jpeg";
        File file = new File(filePath);
        if (file.exists()){
            Glide.with(getApplicationContext())
                    .load(filePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile_image);
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(phone_no + ".jpg");
        download_image(filepath, phone_no);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Gallery"))
                        galleryIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        try {
                            libraryIntent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage(CharSequence[] selectItems) {
        final CharSequence[] items = selectItems;
        AlertDialog.Builder builder = new AlertDialog.Builder(MyOutgoingCustomDialog.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MyOutgoingCustomDialog.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask="Choose from Gallery";
                    if(result)
                        galleryIntent();
                }else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        try {
                            libraryIntent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }else if (items[item].equals("Choose Theme")) {
                    userChoosenTask="Choose Theme";
                    if(result)
                        try {
                            themeIntent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectGifLibrary(){
        boolean result=Utility.checkPermission(MyOutgoingCustomDialog.this);
        if(result){
            try {
                libraryIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectThemeLibrary(){
        boolean result=Utility.checkPermission(MyOutgoingCustomDialog.this);
        if(result) {
            try {
                themeIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void selectStickerLibrary(){

        boolean result=Utility.checkPermission(MyOutgoingCustomDialog.this);
        if(result) {
            try {
                stickerIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        String[] mimeTypes = {"image/jpeg", "image/png", "image/gif"};
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void libraryIntent() throws IOException {
        Intent intent = new Intent(MyOutgoingCustomDialog.this, GridActivity.class);
        //Start details activity
        startActivityForResult(intent, PICK_ICON_REQUEST);

    }

    private void stickerIntent() throws IOException {
        Intent intent = new Intent(MyOutgoingCustomDialog.this, StickerActivity.class);
        //Start details activity
        startActivityForResult(intent, PICK_STICKER_REQUEST);

    }

    private void themeIntent() throws IOException {
        Intent intent = new Intent(MyOutgoingCustomDialog.this, ThemeActivity.class);
        //Start details activity
        startActivityForResult(intent, PICK_THEME_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            img.setVisibility(View.VISIBLE);
            clear_image.setVisibility(View.VISIBLE);
            if (requestCode == SELECT_FILE)
                try {
                    onSelectFromGalleryResult(data);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == PICK_ICON_REQUEST) {
                String res = data.getExtras().getString("POS_ICON");
                try {
                    onSelectFromLibraryResult(res, "libImage");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == PICK_THEME_REQUEST) {
                String res = data.getExtras().getString("POS_THEME");
                try {
                    onSelectFromLibraryResult(res, "themeImage");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == FINAL_EDIT_IMG){
                String res = data.getExtras().getString("FINAL_IMAGE");
                setEditImage(res);
            }else if (requestCode == PICK_STICKER_REQUEST){
                String res = data.getExtras().getString("POS_STICKER");
                try {
                    onSelectFromStickerResult(res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(requestCode == Crop.REQUEST_CROP){
                String imgPath = getPath(getApplicationContext(), Crop.getOutput(data));
                Intent in1 = new Intent(this, photoEditor.class);
                in1.putExtra("picture",imgPath);
                startActivityForResult(in1, FINAL_EDIT_IMG);
            }
        }
    }

    private void onSelectFromLibraryResult(String res, String dType) throws IOException {

        if(dType.equals("libImage")){
            libImage = res;
        }else{
            themeImage = res;
        }

          clear_type = "img";

        String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/animation/"+res;
        RequestOptions o = new RequestOptions();
        Glide.with(this).asGif()
                .load(asseturl)
                .apply(o.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(ivImage);
    }

    private void onSelectFromStickerResult(String res) throws IOException {
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/stickers/"+res;
        File istr = new  File(iconsStoragePath);
        Bitmap bitmap = BitmapFactory.decodeFile(istr.getAbsolutePath());
        stickerImage = res;
        clear_type = "img";
        ivImage.setImageBitmap(bitmap);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        String filePath = Environment.getExternalStorageDirectory()+"/"+System.currentTimeMillis() + ".jpg";
        File destination = new File(filePath);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgPath = filePath;
        Intent in1 = new Intent(this, photoEditor.class);
        in1.putExtra("picture",imgPath);
        startActivityForResult(in1, FINAL_EDIT_IMG);
//        imagePath = thumbnail;
//        ivImage.setImageBitmap(thumbnail);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) throws IOException {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //check if gif
        Uri selectedImageURI = data.getData();
        ContentResolver cr = this.getContentResolver();
        String mime = cr.getType(selectedImageURI);
        if(mime != null){

            Toast toast;
            toast = Toast.makeText(getApplicationContext(), mime, Toast.LENGTH_LONG);
            toast.show();
            //selected from gallarry
            if(mime.equals("image/gif")){

                String realPath = getPath(getApplicationContext(), selectedImageURI);
                File imageFile = new File(realPath);
                FileInputStream fileInputStream = new FileInputStream(imageFile);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();

                try {
                    byte[] buffer = new byte[1024];
                    int len = 0;

                    while ((len = fileInputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }

                    fileInputStream.close();
                    byte[] bytes = outStream.toByteArray();

                    gifImage = bytes;
                    clear_type = "gif";

                    Glide.with(this).asGif()
                            .load(realPath)
                            .into(ivImage);

                    //gifView.startAnimation();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    Toast.makeText(getApplicationContext(), "Too large GIF", Toast.LENGTH_LONG).show();
                }

            }else{
//                String imgPath = getPath(getApplicationContext(), selectedImageURI);
//                Intent in1 = new Intent(this, photoEditor.class);
//                in1.putExtra("picture",imgPath);
//                startActivityForResult(in1, FINAL_EDIT_IMG);
                beginCrop(data.getData());
            }

        }else {
            //selected from file manager
            File imageFile = new File(getRealPathFromURI(selectedImageURI));
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();


            try {

                byte[] buffer = new byte[1024];
                int len = 0;

                while ((len = fileInputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                fileInputStream.close();
                byte[] bytes = outStream.toByteArray();

                Movie gif = Movie.decodeByteArray(bytes, 0, bytes.length);
                //If the result is true, its a animated GIF
                if (gif != null) {

                    gifImage = bytes;
                    clear_type = "gif";
                    //gifView.startAnimation();

                    Glide.with(this).asGif()
                            .load(getRealPathFromURI(selectedImageURI))
                            .into(ivImage);

                } else {

//                String imgPath = getRealPathFromURI(selectedImageURI);
//                Intent in1 = new Intent(this, photoEditor.class);
//                in1.putExtra("picture",imgPath);
//                startActivityForResult(in1, FINAL_EDIT_IMG);

//                UCrop.of(selectedImageURI, selectedImageURI)
//                        .withAspectRatio(16, 9)
//                        .withMaxResultSize(maxWidth, maxHeight)
//                        .start(MyOutgoingCustomDialog.this);
                    beginCrop(data.getData());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                Toast.makeText(getApplicationContext(), "Too large GIF", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }


    private void setEditImage(String path){

        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

        Bitmap bMapScaled = resize(bm, 450, 450);
        imagePath = bMapScaled;
        clear_type = "img";
        ivImage.setImageBitmap(bMapScaled);
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,90,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    private String encodeGifImage(byte[] b){
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;
    }

    private Emitter.Listener onRecive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MyOutgoingCustomDialog.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }

                    MyOutgoingCustomDialog.this.finish();
                    System.exit(0);
                }
            });
        }
    };


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}

