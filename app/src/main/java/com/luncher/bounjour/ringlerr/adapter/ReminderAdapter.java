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
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.luncher.bounjour.ringlerr.activity.ReminderDetail;
import com.luncher.bounjour.ringlerr.activity.ReminderEditDialog;
import com.luncher.bounjour.ringlerr.activity.ReminderList;
import com.luncher.bounjour.ringlerr.model.Blocks;
import com.luncher.bounjour.ringlerr.model.Reminder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private List<Reminder> reminders;
    private Context mContext;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    MyDbHelper myDbHelper;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView messageView;
        TextView date_timeView;
        TextView sharedView;
        public LinearLayout rej_acc_layout;
        public Button button_missed;
        private ImageView status_icon;
        private RelativeLayout main_layout;
        private LinearLayout left_r_layout;
        private TextView buttonViewOption;

        public MyViewHolder(View view) {
            super(view);
            messageView = view.findViewById(R.id.message_rem_list);
            date_timeView = view.findViewById(R.id.date_time);
            sharedView = view.findViewById(R.id.shared);
            rej_acc_layout = view.findViewById(R.id.rej_acc_layout);
            button_missed = view.findViewById(R.id.button_missed);
            main_layout = view.findViewById(R.id.main_layout);
            left_r_layout = view.findViewById(R.id.left_r_layout);
            //alarm_del = view.findViewById(R.id.alarm_del);
            status_icon = view.findViewById(R.id.status_icon);
            buttonViewOption = view.findViewById(R.id.textViewOptions);
        }
    }


    public ReminderAdapter(Context context, List<Reminder> reminders) {
        this.mContext = context;
        this.reminders = reminders;
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
                .inflate(R.layout.reminder_single_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Reminder reminder = reminders.get(position);

        String share_text = "";
        String statusIcon = "";
        Boolean showAcceptBtn = false;
        Date date = new Date(reminder.time);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.US);
        final String formattedDate = formatter.format(date);
        final String from = reminder.from;
        final String message = reminder.message;
        final String is_accepted = reminder.is_accepted;
        final String shared_with = reminder.shared_with;

        if(shared_with.equals("") && from.equals(mPhoneNo)){
            share_text = mContext.getResources().getString(R.string.set_for_myswlf);
            statusIcon = "accepted";
        }else if(!from.equals(mPhoneNo)) {
            share_text = mContext.getResources().getString(R.string.share_by) + " " + getContactName(mContext, from);
            switch (is_accepted) {
                case "1":
                    showAcceptBtn = false;
                    statusIcon = "accepted";
                    break;
                case "2":
                    statusIcon = "rejected";
                    break;
                default:
                    statusIcon = "pending";
                    break;
            }
        }else{
            String my_shared_text = "";
            try {

                JSONObject object = new JSONObject(reminder.shared_with.trim());
                JSONArray keys = object.names ();

                for (int i = 0; i < keys.length (); ++i) {

                    String ph_key = keys.getString (i); // Here's your key
                    //String ac_value = object.getString (ph_key); // Here's your value
                    String current_name = getContactName(mContext, ph_key);

                    if(i==0){
                        my_shared_text += current_name;
                    }

                    if(i==1){
                        my_shared_text += " and "+current_name;
                    }

                    if(i==2){
                        int rem_total = keys.length()-2;
                        my_shared_text += " +"+rem_total+" others";
                    }

                }

            } catch (Throwable ignored) {

            }
            share_text = mContext.getResources().getString(R.string.share_with)+" "+my_shared_text;
            statusIcon = "hide";
        }

        holder.messageView.setText(message);
        holder.date_timeView.setText(formattedDate);
        holder.sharedView.setText(share_text);
        Long tsLong = System.currentTimeMillis();

        String filterHeaderStat = "All";
        if(statusIcon.equals("rejected") && !filterHeaderStat.equals("All")){
            holder.main_layout.setVisibility(View.GONE);
            holder.main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        if(showAcceptBtn) {
            if (tsLong < reminder.time) {
                holder.rej_acc_layout.setVisibility(View.VISIBLE);
                // alarm_del.setVisibility(View.GONE);
                holder.status_icon.setVisibility(View.GONE);
                holder.button_missed.setVisibility(View.GONE);
                //if(filterHeaderStat.equals("Missed") || filterHeaderStat.equals("Pending")){
                holder.main_layout.setVisibility(View.GONE);
                holder.main_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                //}
            } else {
                holder.rej_acc_layout.setVisibility(View.GONE);
                // alarm_del.setVisibility(View.VISIBLE);
                holder.status_icon.setVisibility(View.GONE);
                holder.button_missed.setVisibility(View.VISIBLE);

            }
        }else{
            if(statusIcon.equals("pending") && tsLong > reminder.time) {
                holder.rej_acc_layout.setVisibility(View.GONE);
                //alarm_del.setVisibility(View.VISIBLE);
                holder.button_missed.setVisibility(View.VISIBLE);
                holder.status_icon.setVisibility(View.GONE);
            }else {
                holder.rej_acc_layout.setVisibility(View.GONE);
                //alarm_del.setVisibility(View.VISIBLE);
                holder.status_icon.setVisibility(View.VISIBLE);
                holder.button_missed.setVisibility(View.GONE);
            }
        }

        switch (statusIcon) {
            case "accepted":
                holder.status_icon.setImageResource(R.drawable.ic_accepted);
                break;
            case "rejected":
                holder.status_icon.setImageResource(R.drawable.ic_rejected);
                break;
            case "hide":
                //status_icon.setVisibility(View.GONE);
                holder.status_icon.setImageResource(R.drawable.ic_blank);
                break;
            default:
                holder.status_icon.setImageResource(R.drawable.ic_panding);
                break;
        }

        holder.left_r_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View itemView) {
                final Intent detail_intent = new Intent(mContext, ReminderDetail.class);
                detail_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detail_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                detail_intent.putExtra("message", message);
                detail_intent.putExtra("formattedDate", formattedDate);
                detail_intent.putExtra("timestamp", reminder.time);
                detail_intent.putExtra("shared_with", reminder.shared_with);
                detail_intent.putExtra("reminderkey", reminder.reminderKey);
                detail_intent.putExtra("backpress", 0);
                //context.startActivity(intent1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContext.startActivity(detail_intent);
                    }
                }, 100);
            }

        });

        final String finalShare_text = share_text;
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.reminder_option_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_menu:
                                //handle menu1 click
                                mRootRef.child("reminder").child(mPhoneNo).child(reminder.reminderKey).removeValue();
                                myDbHelper.removeReminder(reminder.reminderKey);
                                reminders.remove(position);
                                ReminderAdapter.this.notifyDataSetChanged();
                                if (mPhoneNo.equals(from)) {

                                    try {

                                        JSONObject object = new JSONObject(reminder.shared_with.trim());
                                        JSONArray keys = object.names ();

                                        for (int i = 0; i < keys.length (); ++i) {

                                            String ph_key = keys.getString (i); // Here's your key
                                            mRootRef.child("reminder").child(ph_key).child(reminder.reminderKey).removeValue();

                                        }

                                    } catch (Throwable ignored) {

                                    }

                                } else {
                                    mRootRef.child("reminder").child(from).child(reminder.reminderKey).child("is_deleted").setValue(true);
                                }
                                break;
                            case R.id.edit_menu:
                                //handle menu2 click
                                if(mPhoneNo.equals(from)) {
                                    String ph_key = "self";

                                    try {
                                        JSONObject object = new JSONObject(reminder.shared_with.trim());
                                        JSONArray keys = object.names ();
                                        int final_key = keys.length()-1;
                                        ph_key = keys.getString (final_key); // Here's your key
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    final Intent intent = new Intent(mContext, ReminderEditDialog.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    intent.putExtra("phone_no", ph_key);
                                    intent.putExtra("message", message);
                                    intent.putExtra("timestamp", reminder.time);
                                    intent.putExtra("share_text", finalShare_text);
                                    intent.putExtra("shared_with", reminder.shared_with);
                                    intent.putExtra("reminderKey", reminder.reminderKey);
                                    //context.startActivity(intent1);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mContext.startActivity(intent);
                                        }
                                    }, 100);
                                }else{
                                    Toast.makeText(mContext, "You don't have premission to edit", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case R.id.add_to_calender:

                                onAddEventClicked(message, finalShare_text, reminder.time);

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

    @Override
    public int getItemCount() {
        return reminders.size();
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

    public void onAddEventClicked(String message, String share_text, Long timestamp) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        long startTime = timestamp;
        long endTime = timestamp + 60;

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, share_text);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, message);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

        mContext.startActivity(intent);
    }

}
