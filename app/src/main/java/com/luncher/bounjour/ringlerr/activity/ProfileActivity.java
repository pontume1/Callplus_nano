package com.luncher.bounjour.ringlerr.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Blocks;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

    String phone;
    String name;
    Integer contact_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);
        phone = Objects.requireNonNull(getIntent().getExtras()).getString("phone_no");
        name = getIntent().getExtras().getString("name");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(name);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ImageView headerTop = findViewById(R.id.headerTop);
        TextView mobile = findViewById(R.id.mobile);
        TextView emailid = findViewById(R.id.email);
        TextView company = findViewById(R.id.company);
        TextView department = findViewById(R.id.department);
        TextView job = findViewById(R.id.job);
        TextView address = findViewById(R.id.address);
        TextView website = findViewById(R.id.website);
        CardView bottomContainer = findViewById(R.id.bottomContainer);

        contact_id = getContactIDFromNumber(phone);
        // Show Data
        MyDbHelper myDbHelper = new MyDbHelper(ProfileActivity.this, null, 9);
        String arrData[] = myDbHelper.SelectContactData(contact_id);

        mobile.setText(phone);
        if(arrData[0] != null)
        {
            if(!arrData[3].equals("")) {
                emailid.setText(arrData[3]);
                emailid.setVisibility(View.VISIBLE);
            }else{
                emailid.setVisibility(View.GONE);
            }
            if(!arrData[4].equals("")) {
                company.setText(arrData[4]);
                company.setVisibility(View.VISIBLE);
            }else{
                company.setVisibility(View.GONE);
            }
            if(!arrData[5].equals("")) {
                department.setText(arrData[5]);
                department.setVisibility(View.VISIBLE);
            }else{
                department.setVisibility(View.GONE);
            }
            if(!arrData[6].equals("")) {
                job.setText(arrData[6]);
                job.setVisibility(View.VISIBLE);
            }else{
                job.setVisibility(View.GONE);
            }
            if(!arrData[7].equals("")) {
                address.setText(arrData[7]);
                address.setVisibility(View.VISIBLE);
            }else{
                address.setVisibility(View.GONE);
            }
            if(!arrData[8].equals("")) {
                website.setText(arrData[8]);
                website.setVisibility(View.VISIBLE);
            }else{
                website.setVisibility(View.GONE);
            }
            if(!arrData[4].equals("") && !arrData[5].equals("") && !arrData[6].equals("") && !arrData[7].equals("") && !arrData[8].equals("")) {
                bottomContainer.setVisibility(View.GONE);
            }else{
                bottomContainer.setVisibility(View.VISIBLE);
            }
        }else{
            emailid.setVisibility(View.GONE);
            bottomContainer.setVisibility(View.GONE);
        }

        String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone+".jpeg";
        File file = new File(filePath);
        if (file.exists()) {
            Glide.with(ProfileActivity.this)
                    .load(filePath)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true))
                    .into(headerTop);
        }
    }

    public int getContactIDFromNumber(String contactNumber)
    {
        int phoneContactID = new Random().nextInt();
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = -1;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            assert contactLookupCursor != null;
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        if(contact_id < 0){
            menu.findItem(R.id.action_delete).setEnabled(false);
            menu.findItem(R.id.action_edit).setVisible(false);
        }else{
            menu.findItem(R.id.action_add).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editContact(phone, name, contact_id, ProfileActivity.this);
                break;
            case R.id.action_add:
                openContact(phone, ProfileActivity.this);
                break;
            case R.id.action_delete:
                deleteCall(ProfileActivity.this, phone, name);
                finish();
                break;
            case R.id.action_block:
                blockContact();
                break;
            default:
                break;
        }
        return true;
    }

    private void openContact(String phone, Context ctx) {
        Intent addIntent = new Intent(ctx, addContact.class);
        addIntent.putExtra("phone", phone);
        ctx.startActivity(addIntent);
    }

    private void editContact(String phone, String name, Integer contact_ids, Context ctx) {
        Intent addIntent = new Intent(ctx, editContact.class);
        addIntent.putExtra("phone", phone);
        addIntent.putExtra("name", name);
        addIntent.putExtra("contact_id", contact_ids);
        ctx.startActivity(addIntent);
    }

    private void deleteCall(Context ctx, String phone, String name) {

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        try (Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null)) {
            assert cur != null;
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception ignored) {

        }
    }

    private void blockContact() {

        new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Block")
                .setMessage("Are you sure you want to block " + name + " ?")
                .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Long tsLong = System.currentTimeMillis() / 1000;

                        MyDbHelper myDbHelper;
                        myDbHelper = new MyDbHelper(ProfileActivity.this, null, 1);
                        String bnumber = myDbHelper.checkBlockNumber(phone);
                        if (!bnumber.equals("null")) {
                            Toast.makeText(ProfileActivity.this, name + " is already in your block list", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                        SessionManager session = new SessionManager(ProfileActivity.this);
                        // get user data from session
                        HashMap<String, String> user = session.getUserDetails();
                        // phone
                        String mPhoneNo = user.get(SessionManager.KEY_PHONE);

                        assert mPhoneNo != null;
                        String key = mRootRef.child("blocks").child(mPhoneNo).push().getKey();
                        Blocks blocks = new Blocks(mPhoneNo, phone, name, tsLong.toString());
                        Map<String, Object> postValues = blocks.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/blocks/" + mPhoneNo + "/" + key, postValues);

                        mRootRef.updateChildren(childUpdates);

                        DatabaseReference blockrootRef = mRootRef.child("block_count/" + phone + "/count");
                        blockrootRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Object count = dataSnapshot.getValue();
                                        String bl_count = "0";
                                        if (count != null) {
                                            bl_count = count.toString();
                                        }
                                        int block_count = getCount(Integer.parseInt(bl_count));
                                        int total_block = block_count + 1;
                                        mRootRef.child("block_count").child(phone).child("count").setValue(total_block);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        myDbHelper.addBlockNumber(phone);

                        Toast.makeText(ProfileActivity.this, name + " added to your block list", Toast.LENGTH_SHORT).show();

                    }
                })
                .create()
                .show();
    }

    private int getCount(int count){
        return count;
    }

}