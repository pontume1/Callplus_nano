package com.luncher.bounjour.ringlerr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.activity.ReminderDetail;

public class ListAdapter extends BaseAdapter {

    ReminderDetail main;
    Long timestamp;

    public ListAdapter(ReminderDetail main, Long timestamp)
    {
        this.main = main;
        this.timestamp = timestamp;
    }

    @Override
    public int getCount() {
        return  main.remDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolderItem {
        TextView name;
        TextView code;
        ImageView status_icon;
        Button button_missed;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolderItem holder = new ViewHolderItem();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.reminder_detail_list, null);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status_icon = (ImageView) convertView.findViewById(R.id.status_icon);
            holder.button_missed = (Button) convertView.findViewById(R.id.button_missed);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolderItem) convertView.getTag();
        }

        Long tsLong = System.currentTimeMillis();
        holder.name.setText(this.main.remDetails.get(position).name);
        String code = this.main.remDetails.get(position).code;
        if(code.equals("0") && tsLong<this.timestamp){
            holder.status_icon.setImageResource(R.drawable.ic_panding);
            holder.button_missed.setVisibility(View.GONE);
            holder.status_icon.setVisibility(View.VISIBLE);
        }else if(code.equals("1")){
            holder.status_icon.setImageResource(R.drawable.ic_accepted);
            holder.button_missed.setVisibility(View.GONE);
            holder.status_icon.setVisibility(View.VISIBLE);
        }else if(code.equals("0") && tsLong>this.timestamp){
            holder.status_icon.setVisibility(View.GONE);
            holder.button_missed.setVisibility(View.VISIBLE);
        }else if(code.equals("2")){
            holder.status_icon.setImageResource(R.drawable.ic_rejected);
            holder.button_missed.setVisibility(View.GONE);
            holder.status_icon.setVisibility(View.VISIBLE);
        }


        return convertView;
    }

}
