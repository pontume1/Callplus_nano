package com.luncher.bounjour.ringlerr.adapter;

/**
 * Created by santanu on 31/1/18.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.activity.ChatList;
import com.luncher.bounjour.ringlerr.activity.ReminderDetail;
import com.luncher.bounjour.ringlerr.activity.ReminderList;
import com.luncher.bounjour.ringlerr.activity.SchedulerList;
import com.luncher.bounjour.ringlerr.model.Notification;
import com.luncher.bounjour.ringlerr.model.Reminder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.ALARM_SERVICE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<Notification> notifications;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textDescription;
        public TextView time_send;
        public ImageView imgProfile;
        public RelativeLayout main_noti_layout;

        public MyViewHolder(View view) {
            super(view);
            textDescription = view.findViewById(R.id.textDescription);
            time_send = view.findViewById(R.id.time);
            imgProfile = view.findViewById(R.id.imgProfile1);
            main_noti_layout = view.findViewById(R.id.main_noti_layout);

        }
    }


    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.mContext = context;
        this.notifications = notifications;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_single, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Notification notification = notifications.get(position);

        Long timestamp = notification.getSTime()*1000;
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.US);
        String formattedDate = formatter.format(date);
        final String phone = notification.getFrom();
        final String name = getContactName(mContext, phone);
        final int type = notification.type;
        final String key = notification.getRemkey();

        String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone+".jpeg";
        File file = new File(filePath);
        if (file.exists()){
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            Glide.with(mContext)
                    .load(filePath)
                    .apply(requestOptions)
                    .into(holder.imgProfile);
        }else{
            String fl = name.substring(0,1);
            if(fl.equals("+")){
                fl = name.substring(1,2);
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color1 = generator.getRandomColor();
            TextDrawable drawable2 = TextDrawable.builder().buildRound(fl, color1);
            holder.imgProfile.setImageDrawable(drawable2);
        }

        holder.textDescription.setText(notification.getDescription());
        holder.time_send.setText(formattedDate);
        //holder.shared.setText(toString().valueOf("Shared with "+notification.getSeen()));

        holder.textDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == 1) {
                    goToChat(phone, name);
                }else if(type == 3){
                    goToSchedule();
                }else{
                    goToReminder(key);
                }
            }
        });

        holder.main_noti_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == 1) {
                    goToChat(phone, name);
                }else if(type == 3){
                    goToSchedule();
                }else{
                    goToReminder(key);
                }
            }
        });
    }

    private void goToReminder(String reminderKey) {
        MyDbHelper myDbHelper = new MyDbHelper(mContext, null, 8);
        List<Reminder> sec = myDbHelper.getReminderByKey(reminderKey);

        if (sec.size() > 0) {
            Long time_and_date = Long.valueOf(sec.get(0).getTime());
            Date date = new Date(time_and_date);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
            String formattedDate = formatter.format(date);

            final Intent intent1 = new Intent(mContext, ReminderDetail.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent1.putExtra("message", sec.get(0).getMessage());
            intent1.putExtra("formattedDate", formattedDate);
            intent1.putExtra("timestamp", sec.get(0).getReminderTime());
            intent1.putExtra("shared_with", sec.get(0).getShared_with());
            intent1.putExtra("reminderkey", reminderKey);
            intent1.putExtra("backpress", 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mContext.startActivity(intent1);
                }
            }, 100);
        }else{
            Toast.makeText(mContext, "This reminder has being deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToSchedule(){
        final Intent intent_sec = new Intent(mContext, SchedulerList.class);
        intent_sec.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_sec.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //context.startActivity(intent1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mContext.startActivity(intent_sec);
            }
        }, 100);
    }

    private void goToChat(String phone, String name){
        final Intent intent1 = new Intent(mContext, ChatList.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent1.putExtra("phone_no", phone);
        intent1.putExtra("name", name);
        //context.startActivity(intent1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mContext.startActivity(intent1);
            }
        }, 100);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
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

}
