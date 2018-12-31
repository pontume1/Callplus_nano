package com.luncher.bounjour.ringlerr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.RecyclerViewClickListener;
import com.luncher.bounjour.ringlerr.model.CallerList;
import com.luncher.bounjour.ringlerr.model.Rvoice;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MultiViewTypeAdapter extends RecyclerView.Adapter {

    private ArrayList<Rvoice> dataSet;
    Context mContext;
    int total_types;
    private boolean fabStateVolume = false;
    private static RecyclerViewClickListener itemListener;

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {

        TextView txtType;
        TextView txtTypeMe;
        CardView cardView;

        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.txtTypeMe = (TextView) itemView.findViewById(R.id.type_me);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        TextView txtType;
        RecyclerView recycler_view;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.recycler_view = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        }
    }


    public MultiViewTypeAdapter(ArrayList<Rvoice>data, Context context, RecyclerViewClickListener itemListener) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case Rvoice.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_type, parent, false);
                return new TextTypeViewHolder(view);
            case Rvoice.TEXT_TYPE_SUGG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_type_sugg, parent, false);
                return new TextTypeViewHolder(view);
            case Rvoice.TEXT_TYPE_USER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_type_me, parent, false);
                return new TextTypeViewHolder(view);
            case Rvoice.LIST_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_type, parent, false);
                return new ImageTypeViewHolder(view);
            case Rvoice.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_type, parent, false);
                return new ImageTypeViewHolder(view);
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return Rvoice.TEXT_TYPE;
            case 1:
                return Rvoice.LIST_TYPE;
            case 2:
                return  Rvoice.IMAGE_TYPE;
            case 3:
                return Rvoice.TEXT_TYPE_USER;
            case 4:
                return Rvoice.TEXT_TYPE_SUGG;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        Rvoice object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.type) {
                case Rvoice.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).txtType.setText(object.text);

                    break;
                case Rvoice.TEXT_TYPE_USER:
                    ((TextTypeViewHolder) holder).txtTypeMe.setText(object.text);

                    break;
                case Rvoice.LIST_TYPE:
                    ((ImageTypeViewHolder) holder).txtType.setText(object.text);

                    ArrayList<CallerList> dataModels= new ArrayList<>();
                    VoiceCallListAdapter msAdapter = new VoiceCallListAdapter(dataModels, mContext, itemListener);

                    for (String key : object.data.keySet()) {
                        String call_number = object.data.get(key);
                        dataModels.add(new CallerList(key, call_number));
                    }

                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
                    ((ImageTypeViewHolder) holder).recycler_view.setLayoutManager(mLayoutManager);
                    //recyclerView.setItemAnimator(new DefaultItemAnimator());
                    ((ImageTypeViewHolder) holder).recycler_view.setAdapter(msAdapter);
                    //((ImageTypeViewHolder) holder).recycler_view.setImageResource(object.data);
                    break;
                case Rvoice.TEXT_TYPE_SUGG:
                    //((TextTypeViewHolder) holder).txtType.setText(object.text);

                    break;

            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
