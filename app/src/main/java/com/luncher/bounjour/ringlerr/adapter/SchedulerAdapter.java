package com.luncher.bounjour.ringlerr.adapter;

/**
 * Created by santanu on 31/1/18.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.activity.SchedulerAlarmDialog;
import com.luncher.bounjour.ringlerr.activity.SchedulerEditDialog;
import com.luncher.bounjour.ringlerr.model.Scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.ALARM_SERVICE;

public class SchedulerAdapter extends RecyclerView.Adapter<SchedulerAdapter.MyViewHolder> {

    private List<Scheduler> schedules;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView shared_via;
        public TextView message_sed_list;
        public TextView date_time;
        public TextView shared;
        private TextView buttonViewOption;

        public MyViewHolder(View view) {
            super(view);
            shared_via = (TextView) view.findViewById(R.id.shared_via);
            message_sed_list = (TextView) view.findViewById(R.id.message_sed_list);
            date_time = (TextView) view.findViewById(R.id.date_time);
            shared = (TextView) view.findViewById(R.id.shared);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);

        }
    }


    public SchedulerAdapter(Context context, List<Scheduler> schedules) {
        this.mContext = context;
        this.schedules = schedules;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scheduler_list_single, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Scheduler schedule = schedules.get(position);

        Long timestamp = schedule.getSTime();
        final Integer id = schedule.getSid();
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.US);
        String formattedDate = formatter.format(date);

        holder.shared_via.setText("Shared Via "+schedule.getSpinnerType());
        holder.message_sed_list.setText(schedule.getMessage());
        holder.date_time.setText(formattedDate);
        holder.shared.setText(toString().valueOf("Shared with "+schedule.getName()));

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.scheduler_option_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_menu:
                                MyDbHelper myDbHelper = new MyDbHelper(mContext, null, 1);
                                myDbHelper.removeScheduler(id);
                                schedules.remove(position);
                                SchedulerAdapter.this.notifyDataSetChanged();

                                Intent mypIntent = new Intent(mContext, SchedulerAlarmDialog.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, id, mypIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                pendingIntent.cancel();
                                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                                alarmManager.cancel(pendingIntent);

                                break;
                            case R.id.edit_menu:
                                //handle menu2 click
                                Intent myIntent = new Intent(mContext, SchedulerEditDialog.class);
                                myIntent.putExtra("alarm_mgs", schedule.getMessage());
                                myIntent.putExtra("date_time", schedule.getSTime());
                                myIntent.putExtra("phone", schedule.getPhone());
                                myIntent.putExtra("name", schedule.getName());
                                myIntent.putExtra("is_manual", schedule.getIsManual());
                                myIntent.putExtra("spinner_type", schedule.getSpinnerType());
                                myIntent.putExtra("id", schedule.getSid());
                                myIntent.putExtra("ago", schedule.getAgo());
                                mContext.startActivity(myIntent);
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
        return schedules.size();
    }

}
