package com.luncher.bounjour.ringlerr.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luncher.bounjour.ringlerr.MainActivity;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Identity;
import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

public class MySettings extends AppCompatActivity {

    public String name;
    public String email;
    public String profile_pic;
    public ImageView picture;
    public ImageView edit_image;

    private EditText profile_name;
    private EditText profile_email;
    private Button email_sign_in_button;

    private DatabaseReference mRootRef;
    private String mPhoneNo;
    private String mName;
    private String mEmail;
    SessionManager session;

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);
        mName = user.get(SessionManager.KEY_NAME);
        mEmail = user.get(SessionManager.KEY_EMAIL);

        picture = (ImageView) findViewById(R.id.profile_image);
        edit_image = (ImageView) findViewById(R.id.edit_image);
        profile_name = (EditText) findViewById(R.id.profile_name);
        profile_email = (EditText) findViewById(R.id.profile_email);
        email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);

        profile_name.setText(mName);
        profile_email.setText(mEmail);

        String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/my_profile.jpeg";
        File file = new File(filePath);
        if (file.exists()){
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                    .skipMemoryCache(true);
            Glide.with(this)
                    .load(filePath)
                    .apply(requestOptions)
                    .into(picture);
        }

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog = new ProgressDialog(MySettings.this);
                mProgressDialog.setTitle("Updating Profile...");
                mProgressDialog.setMessage("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                name = profile_name.getText().toString();
                email = profile_email.getText().toString();

                if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your Name", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if(!validatename(name)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid Name b", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your Email", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if(!isEmailValid(email)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid Email Id", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                session.addName(name);
                session.addEmail(email);

                DatabaseReference getkey = mRootRef.child("identity").child(mPhoneNo).child("uid");
                getkey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> user = session.getUserDetails();
                        String token = user.get(SessionManager.KEY_TOKEN);

                        String uid = dataSnapshot.getValue(String.class);
                        Identity identity = new Identity(uid, email, name, profile_pic, false, token);
                        Map<String, Object> postValues = identity.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/identity/" + mPhoneNo , postValues);

                        mRootRef.updateChildren(childUpdates);

                        mProgressDialog.dismiss();

//                        Intent mainintent = new Intent(Setting.this, MainActivity.class);
//                        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(mainintent);
//                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(MySettings.this, edit_image);
                //inflating menu from xml resource
                popup.inflate(R.menu.scheduler_option_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_menu:
                                //handle menu1 click
                                deleteImage();
                                break;
                            case R.id.edit_menu:
                                //handle menu2 click
                                cropeandeditImage();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    private void deleteImage() {

        String current_user_id = mPhoneNo;
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance().getReference().getStorage();
        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(current_user_id + ".jpg");

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/my_profile.jpeg";
                File fdelete = new File(filePath);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {

                    } else {

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getApplicationContext(), "Failed to delete the file", Toast.LENGTH_LONG).show();
            }
        });

        String uri = "@drawable/profile_picture_upload";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);

        picture.setImageDrawable(res);
    }

    private void cropeandeditImage() {

//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setAspectRatio(1, 1)
//                .setMinCropWindowSize(500, 500)
//                .start(MySettings.this);

        Crop.pickImage(MySettings.this);
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatename(String txt) {

        String regx = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();

    }

    public String getLastnCharacters(String inputString, int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    @Override
    public void onBackPressed() {

        final Intent settingintent = new Intent(MySettings.this, MainActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(settingintent);
                finish();
            }
        }, 100);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // the default resource ID of the actionBar's back button
                final Intent settingintent = new Intent(MySettings.this, MainActivity.class);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(settingintent);
                        finish();
                    }
                }, 100);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(MySettings.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mPhoneNo;


//                Bitmap thumb_bitmap = new Compressor(this)
//                        .setMaxWidth(200)
//                        .setMaxHeight(200)
//                        .setQuality(75)
//                        .compressToBitmap(thumb_filePath);

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                final byte[] thumb_byte = baos.toByteArray();


                final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                //final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            RequestOptions requestOptions = new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                                    .skipMemoryCache(true);
                            Glide.with(MySettings.this)
                                    .load(new File(resultUri.getPath())) // Uri of the picture
                                    .apply(requestOptions)
                                    .into(picture);
                            mProgressDialog.dismiss();
                            download_image(filepath);
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            //resultView.setImageURI(Crop.getOutput(result));
            mProgressDialog = new ProgressDialog(MySettings.this);
            mProgressDialog.setTitle("Uploading Image...");
            mProgressDialog.setMessage("Please wait while we upload and process the image.");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            final Uri resultUri = Crop.getOutput(result);

            File thumb_filePath = new File(resultUri.getPath());

            String current_user_id = mPhoneNo;


//                Bitmap thumb_bitmap = new Compressor(this)
//                        .setMaxWidth(200)
//                        .setMaxHeight(200)
//                        .setQuality(75)
//                        .compressToBitmap(thumb_filePath);

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                final byte[] thumb_byte = baos.toByteArray();


            final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
            //final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        RequestOptions requestOptions = new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                                .skipMemoryCache(true);
                        Glide.with(MySettings.this)
                                .load(new File(resultUri.getPath())) // Uri of the picture
                                .apply(requestOptions)
                                .into(picture);
                        mProgressDialog.dismiss();
                        download_image(filepath);
                    }
                }
            });
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void download_image(StorageReference filepath) {

        final long ONE_MEGABYTE = 1024 * 1024;

        //download file as a byte array
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                storeProfileImage(bitmap, "my_profile.jpeg");

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
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return e.getMessage();
        }

        return filePath;
    }

}
