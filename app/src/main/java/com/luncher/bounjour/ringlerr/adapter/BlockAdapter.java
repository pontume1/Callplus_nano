package com.luncher.bounjour.ringlerr.adapter;

/**
 * Created by santanu on 31/1/18.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.activity.SchedulerAlarmDialog;
import com.luncher.bounjour.ringlerr.activity.SchedulerEditDialog;
import com.luncher.bounjour.ringlerr.model.Blocks;
import com.luncher.bounjour.ringlerr.model.Scheduler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.ALARM_SERVICE;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.MyViewHolder> {

    private List<Blocks> blocks;
    private Context mContext;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    MyDbHelper myDbHelper;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView firstLine;
        public TextView secondLine;
        public ImageButton deleteButton;
        public ImageView profile_icon;

        public MyViewHolder(View view) {
            super(view);
            firstLine = itemView.findViewById(R.id.firstLine);
            secondLine = itemView.findViewById(R.id.secondLine);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            profile_icon = itemView.findViewById(R.id.profile_icon);
        }
    }


    public BlockAdapter(Context context, List<Blocks> blocks) {
        this.mContext = context;
        this.blocks = blocks;
        this.mRootRef = FirebaseDatabase.getInstance().getReference();
        // Session class instance
        SessionManager session = new SessionManager(mContext);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        this.mPhoneNo = user.get(SessionManager.KEY_PHONE);
        this.myDbHelper = new MyDbHelper(mContext, null, 1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_single_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Blocks block = blocks.get(position);
        final String block_no = block.block_no;
        String name = getContactName(mContext, block.block_no);

        holder.firstLine.setText(name);
        holder.secondLine.setText(block_no);
        Boolean is_ringlerr = myDbHelper.checkRinglerrUser(block_no);

        if(is_ringlerr) {
            String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+block_no+".jpeg";
            File file = new File(filePath);
            if (file.exists()){
                RequestOptions requestOptions = RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);
                Glide.with(mContext)
                        .load(filePath)
                        .apply(requestOptions)
                        .into(holder.profile_icon);
            }else{
                String fl = name.substring(0, 1);
                if (fl.equals("+")) {
                    fl = name.substring(1, 2);
                }
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                // generate random color
                int color1 = generator.getRandomColor();
                TextDrawable drawable2 = TextDrawable.builder()
                        .buildRound(fl, color1);
                holder.profile_icon.setImageDrawable(drawable2);
            }
        }else{
            String fl = name.substring(0, 1);
            if (fl.equals("+")) {
                fl = name.substring(1, 2);
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color1 = generator.getRandomColor();
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound(fl, color1);
            holder.profile_icon.setImageDrawable(drawable2);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query messageRef = mRootRef.child("blocks").child(mPhoneNo).orderByChild("block_no").equalTo(block_no);
                messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          for (DataSnapshot child : dataSnapshot.getChildren()) {
                              String reminderKey = child.getKey();
                              DatabaseReference blockrootRef = mRootRef.child("block_count/"+ block_no +"/count");
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
                                              int total_block = block_count-1;
                                              mRootRef.child("block_count").child(block_no).child("count").setValue(total_block);
                                          }

                                          @Override
                                          public void onCancelled(DatabaseError databaseError) {

                                          }
                                      });

                              mRootRef.child("blocks").child(mPhoneNo).child(reminderKey).removeValue();
                          }
                      }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myDbHelper.removeBlockNumber(block_no);
                blocks.remove(position);
                BlockAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return blocks.size();
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNumber;
        }
        String contactName = phoneNumber;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    private int getCount(int count){
        return count;
    }

}
