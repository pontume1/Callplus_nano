package com.luncher.bounjour.ringlerr.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.luncher.bounjour.ringlerr.activity.FullScreenHolder;
import com.luncher.bounjour.ringlerr.activity.FullScreenVideoHolder;
import com.luncher.bounjour.ringlerr.activity.GlideApp;
import com.luncher.bounjour.ringlerr.activity.MapsActivity;
import com.luncher.bounjour.ringlerr.activity.SelectCopyContact;
import com.luncher.bounjour.ringlerr.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Message> messages;
    private Context mContext;
    private String mPhone;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_message_body;
        public TextView text_message_time;
        public TextView text_message_body_img;
        public TextView text_message_time_img;
        public ImageButton deleteButton;
        ImageView photoView;
        public FrameLayout mail_message_layout;
        public RelativeLayout receiveImage;
        public RelativeLayout receiveLayout;

        MyViewHolder(View view) {
            super(view);
            receiveImage = itemView.findViewById(R.id.receiveImage);
            receiveLayout = itemView.findViewById(R.id.receiveLayout);
            text_message_body = itemView.findViewById(R.id.text_message_body);
            text_message_time = itemView.findViewById(R.id.text_message_time);
            text_message_body_img = itemView.findViewById(R.id.text_message_body_img);
            text_message_time_img = itemView.findViewById(R.id.text_message_time_img);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            photoView = itemView.findViewById(R.id.chat_image);
            mail_message_layout = itemView.findViewById(R.id.mail_message_layout);

        }
    }


    public MessageAdapter(Context context, List<Message> messages) {
        SessionManager session = new SessionManager(context);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        this.mContext = context;
        this.messages = messages;
        this.mPhone = user.get(SessionManager.KEY_PHONE);
    }

    @Override
    public int getItemViewType(int position) {

       if(messages.get(position).from.equals(mPhone)){
           return 1;
       }else{
           return 2;
       }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
        }

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Message message = messages.get(position);

        final int id = message.getId();
        final String from = message.getFrom();
        final String to = message.getTo();
        final String msg = message.getMessage();
        final String image = message.getImage();
        final String type = message.getType();
        final String talk_time = message.getTalk_time();
        //String seen = message.getSeen();
        String final_image = image;
        Long dateTime = Long.valueOf(message.getDateAndTime())*1000;

        Date date_time = new Date(dateTime);
        SimpleDateFormat date_time_formatter = new SimpleDateFormat("d MMM, HH:mm", Locale.US);
        String formattedDateTime = date_time_formatter.format(date_time);
        holder.receiveLayout.setVisibility(View.GONE);
        holder.receiveImage.setVisibility(View.GONE);

        if(type.equals("none") || type.equals("flash")) {
            holder.text_message_time.setText(formattedDateTime);
            holder.text_message_body.setText(msg);
            holder.receiveLayout.setVisibility(View.VISIBLE);
            holder.photoView.setImageDrawable(null);
        }else{
            holder.text_message_time_img.setText(formattedDateTime);
            if(msg.equals("")){
                holder.text_message_body_img.setVisibility(View.GONE);
            }else {
                holder.text_message_body_img.setText(msg);
            }
            holder.receiveImage.setVisibility(View.VISIBLE);

            switch (type) {
                case "gif": {
                    String imageStoragePath;
                    if (message.from.equals(mPhone)) {
                        imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/send/" + image;
                    } else {
                        imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/" + image;
                    }
                    final_image = imageStoragePath;
                    Glide.with(mContext).asGif()
                            .load(imageStoragePath)
                            .into(holder.photoView);

                    break;
                }
                case "libgif": {

                    String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/animation/" + image;
                    Glide.with(mContext).asGif()
                            .load(asseturl)
                            .into(holder.photoView);

                    holder.photoView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View itemView) {

                            if (FullScreenHolder.fsh != null) {
                                FullScreenHolder.fsh.finish();
                            }
                            final Intent intent = new Intent(mContext, FullScreenHolder.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            }
                            intent.putExtra("phone_no", from);
                            intent.putExtra("message", msg);
                            intent.putExtra("image", image);
                            intent.putExtra("type", type);
                            intent.putExtra("talk_time", "");
                            intent.putExtra("screen_type", "half");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mContext.startActivity(intent);
                                }
                            }, 200);

                        }

                    });

                    break;
                }
                case "sticker": {
                    String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/stickers/" + image;
                    Glide.with(mContext)
                            .load(asseturl)
                            .into(holder.photoView);

                    holder.photoView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View itemView) {

                            if (FullScreenHolder.fsh != null) {
                                FullScreenHolder.fsh.finish();
                            }
                            final Intent intent = new Intent(mContext, FullScreenHolder.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            }
                            intent.putExtra("phone_no", from);
                            intent.putExtra("message", msg);
                            intent.putExtra("image", image);
                            intent.putExtra("type", type);
                            intent.putExtra("talk_time", "");
                            intent.putExtra("screen_type", "half");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mContext.startActivity(intent);
                                }
                            }, 200);

                        }

                    });
                    break;
                }
                case "sos":
                    final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                    Query rootRef = mRootRef.child("location/" + from);
                    rootRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Object count = dataSnapshot.getValue();
                            if (count != null) {
                                try {

                                    JSONObject json = new JSONObject(count.toString());
                                    Double latitude = json.getDouble("latitude");
                                    Double longitude = json.getDouble("longitude");
                                    String url = "https://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=8&size=150x100&sensor=false&markers=markers=color:blue%7Clabel:S%7C" + latitude + "," + longitude + "&key=AIzaSyDIhsP9MJpeKFcMEtwBqrawxydlQdwE12c";
                                    GlideApp.with(mContext)
                                            .load(url)
                                            .apply(RequestOptions.skipMemoryCacheOf(true))
                                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                            .into(holder.photoView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    holder.photoView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View itemView) {

                            Intent fullmainintent = new Intent(mContext, MapsActivity.class);
                            fullmainintent.putExtra("phone_no", from);
                            mContext.startActivity(fullmainintent);

                        }

                    });

                    break;
                case "vid": {
                    final String imageStoragePath;
                    if (message.from.equals(mPhone)) {
                        imageStoragePath = image;
                    } else {
                        imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/videos/receive/" + image;
                    }
                    final_image = imageStoragePath;
                    Bitmap bmThumbnail;
                    bmThumbnail = ThumbnailUtils.createVideoThumbnail(imageStoragePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                    Glide.with(mContext)
                            .load(bmThumbnail)
                            .into(holder.photoView);

                    holder.photoView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View itemView) {

                            if (FullScreenHolder.fsh != null) {
                                FullScreenHolder.fsh.finish();
                            }
                            final Intent intent = new Intent(mContext, FullScreenVideoHolder.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            }
                            intent.putExtra("phone_no", from);
                            intent.putExtra("message", msg);
                            intent.putExtra("image", imageStoragePath);
                            intent.putExtra("type", type);
                            intent.putExtra("talk_time", "");
                            intent.putExtra("screen_type", "half_full");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mContext.startActivity(intent);
                                }
                            }, 200);

                        }

                    });

                    break;
                }
                case "snap":
                    byte[] b = Base64.decode(image, Base64.DEFAULT);
                    Glide.with(mContext)
                            .load(b)
                            .into(holder.photoView);

                    holder.photoView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View itemView) {

                            if (FullScreenHolder.fsh != null) {
                                FullScreenHolder.fsh.finish();
                            }
                            final Intent intent = new Intent(mContext, FullScreenHolder.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            }
                            intent.putExtra("phone_no", from);
                            intent.putExtra("message", msg);
                            intent.putExtra("image", image);
                            intent.putExtra("type", type);
                            intent.putExtra("talk_time", "");
                            intent.putExtra("screen_type", "half");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mContext.startActivity(intent);
                                }
                            }, 200);

                        }

                    });
                    break;
                default: {
                    final String imageStoragePath;
                    if (message.from.equals(mPhone)) {
                        imageStoragePath = image;
                    } else {
                        imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/" + image;
                    }
                    final_image = imageStoragePath;
                    Glide.with(mContext)
                            .load(imageStoragePath)
                            .into(holder.photoView);

                    holder.photoView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View itemView) {

                            if (FullScreenHolder.fsh != null) {
                                FullScreenHolder.fsh.finish();
                            }
                            final Intent intent = new Intent(mContext, FullScreenHolder.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            }
                            intent.putExtra("phone_no", from);
                            intent.putExtra("message", msg);
                            intent.putExtra("image", imageStoragePath);
                            intent.putExtra("type", type);
                            intent.putExtra("talk_time", "");
                            intent.putExtra("screen_type", "half");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mContext.startActivity(intent);
                                }
                            }, 200);

                        }

                    });
                    break;
                }
            }
        }

        final String final_image1 = final_image;
        holder.mail_message_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.text_message_time, Gravity.CENTER_VERTICAL, 0, R.style.PopupMenuMoreCentralized);
                //inflating menu from xml resource
                popup.inflate(R.menu.chat_option_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_menu:
                                deleteChat(id, mPhone, to, "", position);
                                break;
                            case R.id.copy_menu:

                                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", msg);
                                clipboard.setPrimaryClip(clip);

                                break;
                            case R.id.forward_menu:
                                forward_msg(from, msg, final_image1, type, talk_time);
                                break;
                        }
                        return false;
                    }
                });

                Menu popupMenu = popup.getMenu();
                if(msg.equals("")){
                    popupMenu.findItem(R.id.copy_menu).setEnabled(false);
                }
                //displaying the popup
                popup.show();

                return false;
            }
        });

        holder.photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.photoView, Gravity.CENTER_VERTICAL, 0, R.style.PopupMenuMoreCentralized);
                //inflating menu from xml resource
                popup.inflate(R.menu.chat_option_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_menu:
                                deleteChat(id, mPhone, to, "", position);
                                break;
                            case R.id.copy_menu:

                                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", msg);
                                clipboard.setPrimaryClip(clip);

                                break;
                            case R.id.forward_menu:
                                forward_msg(from, msg, final_image1, type, talk_time);
                                break;
                        }
                        return false;
                    }
                });

                Menu popupMenu = popup.getMenu();
                if(msg.equals("")){
                    popupMenu.findItem(R.id.copy_menu).setEnabled(false);
                }
                //displaying the popup
                popup.show();

                return false;
            }
        });
    }

    private void forward_msg(String number, String cmessage, String image, String type, String talk_time) {
        final Intent intent = new Intent(mContext, SelectCopyContact.class);
        intent.putExtra("phone_no", number);
        intent.putExtra("message", cmessage);
        intent.putExtra("image", image);
        intent.putExtra("type", type);
        intent.putExtra("talk_time", talk_time);
        intent.putExtra("screen_type", "full");

        mContext.startActivity(intent);
    }

    private void deleteChat(final int id, final String mPhoneNo, final String to, final String reminderKey, final int position) {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete Message")
                .setMessage("Are you sure ?")
                .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        //DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                        //mRootRef.child("chats").child(mPhoneNo).child(to).child(reminderKey).removeValue();

                        MyDbHelper myDbHelper = new MyDbHelper(mContext, null, 5);
                        myDbHelper.removeMessage(id);

                        messages.remove(position);
                        MessageAdapter.this.notifyDataSetChanged();
                    }
                })
                .create()
                .show();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
