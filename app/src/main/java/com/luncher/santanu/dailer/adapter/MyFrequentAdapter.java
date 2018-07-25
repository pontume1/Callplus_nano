package com.luncher.santanu.dailer.adapter;

/**
 * Created by santanu on 21/1/18.
 */

import android.Manifest;
import android.R;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luncher.santanu.dailer.MyOutgoingCustomDialog;
import com.luncher.santanu.dailer.activity.ReminderDialog;

import java.lang.reflect.Method;
import java.util.List;

public class MyFrequentAdapter extends RecyclerView.Adapter<MyFrequentAdapter.ViewHolder> {
    private final AnimatorSet animationOut, animationIn;;
    private List<String> values;
    private List<String> phone_no;
    private List<Integer> count;
    private List<Bitmap> image;
    private List<Long> duration;
    public Context context;
    private final int COUNTDOWN_RUNNING_TIME = 500;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView countText;
        public TextView durationText;
        public ImageButton rCallButton;
        public ImageButton callButton;
        public ImageButton whatsapp_btn;
        public TextView dnButton;
        public View layout;
        public RelativeLayout flipCard;
        public ImageView profilePic;
        private View mCardFrontLayout;
        private View mCardBackLayout;
        private boolean mIsBackVisible = true;
        public ImageButton reminderButton;


        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(com.luncher.santanu.dailer.R.id.firstLine);
            rCallButton = (ImageButton) v.findViewById(com.luncher.santanu.dailer.R.id.rCallButton);
            callButton = (ImageButton) v.findViewById(com.luncher.santanu.dailer.R.id.callButton);
            profilePic = (ImageView) v.findViewById(com.luncher.santanu.dailer.R.id.profile_icon);
            countText = (TextView) v.findViewById(com.luncher.santanu.dailer.R.id.countText);
            durationText = (TextView) v.findViewById(com.luncher.santanu.dailer.R.id.durationText);
            mCardBackLayout = v.findViewById(com.luncher.santanu.dailer.R.id.card_back);
            whatsapp_btn = (ImageButton) v.findViewById(com.luncher.santanu.dailer.R.id.whatsapp_btn);
            mCardFrontLayout = v.findViewById(com.luncher.santanu.dailer.R.id.card_front);
            flipCard = v.findViewById(com.luncher.santanu.dailer.R.id.flipCard);
            reminderButton = (ImageButton) v.findViewById(com.luncher.santanu.dailer.R.id.reminderButton);
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
    public MyFrequentAdapter(List<String> myDataset, List<String> phone, List<Integer> counts, List<Long> durations, List<Bitmap> user_image, AnimatorSet animationOut, AnimatorSet animationIn) {
        values = myDataset;
        phone_no = phone;
        count = counts;
        image = user_image;
        duration = durations;
        this.animationIn = animationIn;
        this.animationOut = animationOut;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyFrequentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {


        context = parent.getContext();
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(com.luncher.santanu.dailer.R.layout.row_frequent_layout, parent, false);
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
        final Integer call_count = count.get(position);
        float full_duration = (float)duration.get(position)/(60*60);
        final String duration_count = String.format("%.2f",full_duration);
        final Bitmap image_user = image.get(position);

        if(image_user == null || image_user.equals("")){
            holder.profilePic.setImageResource(com.luncher.santanu.dailer.R.drawable.my_profile);
        }else{
            holder.profilePic.setImageBitmap(image_user);
        }
        holder.txtHeader.setText(name);
        holder.countText.setText(call_count.toString()+ " calls");
        holder.durationText.setText(duration_count.toString()+ " hours");
        holder.rCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove(position);

                //String Wphone = "+91"+getLastnCharacters(phone, 10);
                // Get a reference to your user
                //final FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DatabaseReference ref = database.getReference("identity/"+Wphone);

                // Attach a listener to read the data at your profile reference
                //ref.addValueEventListener(new ValueEventListener() {
                    //@Override
                    //public void onDataChange(DataSnapshot dataSnapshot) {
                        //Identity identity = dataSnapshot.getValue(Identity.class);

                        //if(identity==null){
                            //openWhatsApp("91"+getLastnCharacters(phone, 10));
                        //}else{
                            final Intent intent1 = new Intent(context, MyOutgoingCustomDialog.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent1.putExtra("phone_no", phone);
                            intent1.putExtra("sim", 1);
                            intent1.putExtra("name", name);
                            //context.startActivity(intent1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    context.startActivity(intent1);
                                }
                            }, 100);

                            if (!holder.mIsBackVisible) {
                                holder.mIsBackVisible = true;
                            }else{
                                holder.mIsBackVisible = false;
                            }
                            flipCard(v, holder.mIsBackVisible, holder.mCardFrontLayout, holder.mCardBackLayout);
                        //}
                    //}

//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        System.out.println("The read failed: " + databaseError.getCode());
//                    }
                //});
            }
        });

        holder.reminderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent rem_intent = new Intent(context, ReminderDialog.class);
                rem_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                rem_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                rem_intent.putExtra("phone_no", phone);
                rem_intent.putExtra("name", name);
                //context.startActivity(intent1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(rem_intent);
                    }
                }, 100);
            }
        });

        holder.callButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone));
                    int simSlot = getDefaultSimSlot(context);
                    callIntent.putExtra("com.android.phone.force.slot", true);
                    callIntent.putExtra("com.android.phone.extra.slot", simSlot);
                    context.startActivity(callIntent);


                    if (!holder.mIsBackVisible) {
                        holder.mIsBackVisible = true;
                    }else{
                        holder.mIsBackVisible = false;
                    }
                    flipCard(v, holder.mIsBackVisible, holder.mCardFrontLayout, holder.mCardBackLayout);
                }
            }
        });

        holder.whatsapp_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp("91"+getLastnCharacters(phone, 10), context);

                if (!holder.mIsBackVisible) {
                    holder.mIsBackVisible = true;
                }else{
                    holder.mIsBackVisible = false;
                }
                flipCard(v, holder.mIsBackVisible, holder.mCardFrontLayout, holder.mCardBackLayout);
            }
        });

        holder.flipCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.mIsBackVisible) {
                    holder.mIsBackVisible = true;
                }else{
                    holder.mIsBackVisible = false;
                }
                flipCard(v, holder.mIsBackVisible, holder.mCardFrontLayout, holder.mCardBackLayout);
            }
        });
    }

    public void flipCard(View view, boolean mIsBackVisible, View mCardFrontLayout, View mCardBackLayout) {
        if (!mIsBackVisible) {
            animationOut.setTarget(mCardFrontLayout);
            animationIn.setTarget(mCardBackLayout);
            animationOut.start();
            animationIn.start();
        } else {
            animationOut.setTarget(mCardBackLayout);
            animationIn.setTarget(mCardFrontLayout);
            animationOut.start();
            animationIn.start();
        }
    }

    public int  getDefaultSimSlot(Context context) {

        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getDefaultSim;
        int defaultSimm = -1;
        try {
            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
            method_getDefaultSim.setAccessible(true);
            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Method method_getSmsDefaultSim;
        int smsDefaultSim = -1;
        try {
            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return defaultSimm;
    }

    private void openWhatsApp(String phone, Context ctx) {
        String smsNumber = phone; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, " ");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");

        ctx.startActivity(sendIntent);
    }

    public String getLastnCharacters(String inputString, int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
