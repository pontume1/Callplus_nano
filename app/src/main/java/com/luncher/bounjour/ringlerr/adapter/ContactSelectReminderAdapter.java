package com.luncher.bounjour.ringlerr.adapter;

/**
 * Created by santanu on 21/1/18.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.RecyclerViewClickListener;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ContactSelectReminderAdapter extends RecyclerView.Adapter<ContactSelectReminderAdapter.ViewHolder> {
    private List<String> values;
    private List<String> phone_no;
    private List<Boolean> isSelected;
    private String sel_message;
    private Long sel_time;
    private Integer sel_timeAgo;
    public Context context;
    RecyclerViewClickListener itemListener;
    public ImageView profilePic;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public View layout;
        public ImageView imageView2;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            profilePic = (ImageView) v.findViewById(R.id.icon);
            txtHeader = (TextView) v.findViewById(com.luncher.bounjour.ringlerr.R.id.firstLine);
            txtFooter = (TextView) v.findViewById(com.luncher.bounjour.ringlerr.R.id.secondLine);
            imageView2 = (ImageView) v.findViewById(R.id.imageView2);
        }
    }

    public void add(int position, String item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ContactSelectReminderAdapter(List<String> myDataset, List<String> phoneNo, String message, Long time, Integer timeAgo, List<Boolean> selected, RecyclerViewClickListener itemListeners) {
        values = myDataset;
        phone_no = phoneNo;
        sel_message = message;
        sel_time = time;
        sel_timeAgo = timeAgo;
        isSelected = selected;
        itemListener = itemListeners;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactSelectReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {


        context = parent.getContext();
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(com.luncher.bounjour.ringlerr.R.layout.select_contact, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = values.get(position);
        final String phone = phone_no.get(position);
        boolean is_num_sel = isSelected.get(position);
        Boolean file_exist = false;

        MyDbHelper myDbHelper = new MyDbHelper(context, null, null, 1);
        Boolean is_ringlerr = myDbHelper.checkRinglerrUser(phone);

        holder.imageView2.setVisibility(View.GONE);
        holder.layout.setBackgroundColor(is_num_sel ? Color.CYAN : Color.WHITE);

        if(is_ringlerr) {
            holder.imageView2.setVisibility(View.VISIBLE);
            String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone+".jpeg";
            File file = new File(filePath);
            if (file.exists()){
                RequestOptions requestOptions = RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);
                file_exist = true;
                Glide.with(context)
                        .load(filePath)
                        .apply(requestOptions)
                        .into(profilePic);
            }
        }

        if(!file_exist) {
            String fl = name.substring(0, 1);
            if (fl.equals("+")) {
                fl = name.substring(1, 2);
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color1 = generator.getRandomColor();
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound(fl, color1);
            profilePic.setImageDrawable(drawable2);
        }

        holder.txtHeader.setText(name);
        holder.txtHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean is_selected = !isSelected.get(position);
                isSelected.set(position, is_selected);
                holder.layout.setBackgroundColor(is_selected ? Color.CYAN : Color.WHITE);
                itemListener.recyclerViewListClicked(v, phone, name, is_selected);

            }
        });

        holder.txtFooter.setText(phone);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
