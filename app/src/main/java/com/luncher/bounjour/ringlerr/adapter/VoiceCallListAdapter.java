package com.luncher.bounjour.ringlerr.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.RecyclerViewClickListener;
import com.luncher.bounjour.ringlerr.model.CallerList;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class VoiceCallListAdapter extends RecyclerView.Adapter<VoiceCallListAdapter.MyViewHolder> {

    private ArrayList<CallerList> dataSet;
    Context mContext;
    RecyclerViewClickListener itemListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtPhone;
        public MyViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.name);
            txtPhone = (TextView) view.findViewById(R.id.phone);
        }
    }

    @Override
    public VoiceCallListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voice_call_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VoiceCallListAdapter.MyViewHolder holder, final int position) {
        CallerList myList = dataSet.get(position);
        final String name = myList.getName();
        String phone = myList.getPhone();

        holder.txtName.setText(name);
        holder.txtPhone.setText(phone);

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemListener.recyclerViewListClicked(v, name.toLowerCase(),name.toLowerCase(), true);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public VoiceCallListAdapter(ArrayList<CallerList> data, Context context, RecyclerViewClickListener itemListener) {
        this.dataSet = data;
        this.mContext=context;
        this.itemListener = itemListener;

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private VoiceCallListAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final VoiceCallListAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
