package com.luncher.bounjour.ringlerr;

/**
 * Created by santanu on 21/1/18.
 */

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.activity.ChatList;
import com.luncher.bounjour.ringlerr.activity.ProfileActivity;
import com.luncher.bounjour.ringlerr.activity.ReminderDialog;
import com.luncher.bounjour.ringlerr.activity.addContact;
import com.luncher.bounjour.ringlerr.activity.editContact;
import com.luncher.bounjour.ringlerr.model.Blocks;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecentAdapter extends RecyclerView.Adapter<MyRecentAdapter.ViewHolder> {
    private final Animation animationUp, animationDown;
    private List<String> values;
    private List<String> phone_no;
    private List<String> type;
    private List<String> ago;
    private List<Bitmap> image;
    private List<Integer> contactId;
    private List<Integer> cid;
    private HashMap<Integer ,Boolean> is_shown = new HashMap<Integer,Boolean>();
    public Context context;
    private final int COUNTDOWN_RUNNING_TIME = 500;
    String shrTxt;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public ImageView call_type;
        public ImageButton rCallButton;
        public ImageButton callButton;
        public TextView dnButton;
        public View layout;
        public LinearLayout slideView;
        public LinearLayout main_layout;
        public ImageView profilePic;
        public ImageView imageView2;
        public ImageButton whatsapp_btn;
        public ImageButton msgButton;
        public ImageButton addButton;
        public ImageButton blockButton;
        public ImageButton editButton;
        public ImageButton reminderButton;
        public ImageButton deleteButton;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = v.findViewById(R.id.firstLine);
            txtFooter = v.findViewById(R.id.secondLine);
            call_type = v.findViewById(R.id.call_type_icon);
            rCallButton = v.findViewById(R.id.rCallButton);
            callButton = v.findViewById(R.id.callButton);
            dnButton = v.findViewById(R.id.dnButton);
            slideView = v.findViewById(R.id.slideView);
            main_layout = v.findViewById(R.id.main_layout);
            profilePic = v.findViewById(R.id.profile_icon);
            imageView2 = v.findViewById(R.id.imageView2);
            whatsapp_btn = v.findViewById(R.id.whatsapp_btn);
            msgButton = v.findViewById(R.id.msgButton);
            addButton = v.findViewById(R.id.addButton);
            blockButton = v.findViewById(R.id.blockButton);
            editButton = v.findViewById(R.id.editButton);
            reminderButton = v.findViewById(R.id.reminderButton);
            deleteButton = v.findViewById(R.id.deleteButton);
        }
    }

    public void swapItems(List<String> values, List<String> phone_no, List<String> type, List<String> ago, List<Bitmap> user_image, List<Integer> contact_id, List<Integer> c_id) {
        this.values = values;
        this.phone_no = phone_no;
        this.type = type;
        this.ago = ago;
        this.image = user_image;
        this.contactId = contact_id;
        this.cid = c_id;
        notifyDataSetChanged();
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
    public MyRecentAdapter(List<String> myDataset, List<String> phone, List<String> types, List<String> agos, List<Bitmap> user_image, List<Integer> contact_id, List<Integer> c_id, Animation animationUp, Animation animationDown) {
        values = myDataset;
        phone_no = phone;
        type = types;
        ago = agos;
        image = user_image;
        contactId = contact_id;
        cid = c_id;
        this.animationDown = animationDown;
        this.animationUp = animationUp;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyRecentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_recent_layout, parent, false);
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
        final String phone = "+91"+getLastnCharacters(phone_no.get(position), 10);
        final String time_ago = ago.get(position);
        final String type_call = type.get(position);
        final Bitmap image_user = image.get(position);
        final Integer contact_ids = contactId.get(position);
        final Integer c_ids = cid.get(position);

        if(phone_no.get(position).equals(name)){
            holder.editButton.setVisibility(View.GONE);
            holder.addButton.setVisibility(View.VISIBLE);
        }else{
            holder.editButton.setVisibility(View.VISIBLE);
            holder.addButton.setVisibility(View.GONE);
        }

        holder.imageView2.setVisibility(View.GONE);
        Boolean value = is_shown.get(position);
        if(value == null || value == false) {
            holder.slideView.setVisibility(View.GONE);
        }else{
            holder.slideView.setVisibility(View.VISIBLE);
        }

        MyDbHelper myDbHelper = new MyDbHelper(context, null, 1);
        Boolean is_ringlerr = myDbHelper.checkRinglerrUser(phone);
        Boolean file_exist = false;

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
                        .into(holder.profilePic);
            }

            int num_mgs = myDbHelper.getIndivNotificationCount(phone);
            if(num_mgs > 0){
                TextDrawable drawable_num_mgs = TextDrawable.builder()
                        .beginConfig()
                        .width(20)  // width in px
                        .height(20) // height in px
                        .bold()
                        .endConfig()
                        .buildRound(num_mgs+"", Color.parseColor("#fa6377"));
                holder.imageView2.setImageDrawable(drawable_num_mgs);
            }else{
                holder.imageView2.setImageResource(R.drawable.disc_jockey);
            }
        }

        if(!file_exist && (image_user == null || image_user.equals(""))){
            String fl = name.substring(0,1);
            if(fl.equals("+")){
                fl = name.substring(1,2);
            }
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            int color1 = generator.getRandomColor();
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound(fl, color1);
            holder.profilePic.setImageDrawable(drawable2);

            holder.imageView2.setVisibility(View.GONE);
            if(is_ringlerr) {
                int num_mgs = myDbHelper.getIndivNotificationCount(phone);
                holder.imageView2.setVisibility(View.VISIBLE);
                if(num_mgs > 0){
                    TextDrawable drawable_num_mgs = TextDrawable.builder()
                            .beginConfig()
                            .width(20)  // width in px
                            .height(20) // height in px
                            .bold()
                            .endConfig()
                            .buildRound(num_mgs+"", Color.parseColor("#fa6377"));
                    holder.imageView2.setImageDrawable(drawable_num_mgs);
                }else{
                    holder.imageView2.setImageResource(R.drawable.disc_jockey);
                }
            }
        }else if (!file_exist){
            holder.profilePic.setImageBitmap(image_user);
        }
        holder.txtHeader.setText(name);
        holder.rCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                        final Intent intent1 = new Intent(context, MyOutgoingCustomDialog.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent1.putExtra("phone_no", phone);
                        intent1.putExtra("sim", 1);
                        intent1.putExtra("name", name);
                        intent1.putExtra("BitmapImage", image_user);
                        //context.startActivity(intent1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.startActivity(intent1);
                            }
                        }, 100);
                    }

        });


        String block_number = myDbHelper.checkBlockNumber(phone);
        if(!block_number.equals("null")){
            holder.call_type.setImageResource(R.drawable.block);
        }else if(type_call.equals("1")){
            holder.call_type.setImageResource(R.drawable.icon_2);
        }else if(type_call.equals("2")){
            holder.call_type.setImageResource(R.drawable.icon_3);
        }else{
            holder.call_type.setImageResource(R.drawable.icon_4);
        }

        holder.txtFooter.setText(time_ago);

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
                }
            }
        });

        holder.whatsapp_btn.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   openWhatsApp("91"+getLastnCharacters(phone, 10), context);
               }
        });

        holder.txtHeader.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   final Intent intent1 = new Intent(context, ChatList.class);
                   intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   intent1.putExtra("phone_no", phone);
                   intent1.putExtra("name", name);
                   //context.startActivity(intent1);
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           context.startActivity(intent1);
                       }
                   }, 100);
               }
        });

        holder.blockButton.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   new AlertDialog.Builder(context)
                           .setTitle("Block")
                           .setMessage("Are you sure you want to block "+name+" ?")
                           .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                               @Override public void onClick(DialogInterface dialog, int which) {
                                   Long tsLong = System.currentTimeMillis()/1000;

                                   MyDbHelper myDbHelper;
                                   myDbHelper = new MyDbHelper(context, null, 1);
                                   String bnumber = myDbHelper.checkBlockNumber(phone);
                                   if(!bnumber.equals("null")){
                                       Toast.makeText(context, name+" is already in your block list", Toast.LENGTH_SHORT).show();
                                       return;
                                   }

                                   final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                                   SessionManager session = new SessionManager(context);
                                   // get user data from session
                                   HashMap<String, String> user = session.getUserDetails();
                                   // phone
                                   String mPhoneNo = user.get(SessionManager.KEY_PHONE);

                                   String key = mRootRef.child("blocks").child(mPhoneNo).push().getKey();
                                   Blocks blocks = new Blocks(mPhoneNo, phone, name, tsLong.toString());
                                   Map<String, Object> postValues = blocks.toMap();

                                   Map<String, Object> childUpdates = new HashMap<>();
                                   childUpdates.put("/blocks/" + mPhoneNo + "/" + key, postValues);

                                   mRootRef.updateChildren(childUpdates);

                                   DatabaseReference blockrootRef = mRootRef.child("block_count/"+ phone +"/count");
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
                                                   int total_block = block_count+1;
                                                   mRootRef.child("block_count").child(phone).child("count").setValue(total_block);
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });

                                   myDbHelper.addBlockNumber(phone);

                                   Toast.makeText(context, name+" added to your block list", Toast.LENGTH_SHORT).show();

                               }
                           })
                           .create()
                           .show();
               }
        });

        holder.addButton.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   openContact(phone, context);
               }
        });

        holder.editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editContact(phone, name, contact_ids, context);
            }
        });

        holder.profilePic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent1 = new Intent(context, ProfileActivity.class);
//                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.putExtra("phone_no", phone);
                intent1.putExtra("name", name);
                //context.startActivity(intent1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(intent1);
                    }
                }, 100);
            }
        });

//        holder.shareButton.setOnClickListener(new OnClickListener() {
//               @Override
//               public void onClick(View v) {
//                   if(name.equals(phone)){
//                       shrTxt = "Phone : "+phone;
//                   }else{
//                       shrTxt = "Name : "+ name + " Phone : "+phone;
//                   }
//                   Intent sendIntent = new Intent();
//                   sendIntent.setAction(Intent.ACTION_SEND);
//                   sendIntent.putExtra(Intent.EXTRA_TEXT, shrTxt);
//                   sendIntent.setType("text/plain");
//                   context.startActivity(sendIntent);
//               }
//        });

        holder.reminderButton.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   final Intent intent1 = new Intent(context, ReminderDialog.class);
                   intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   intent1.putExtra("phone_no", phone);
                   intent1.putExtra("name", name);
                   //context.startActivity(intent1);
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           context.startActivity(intent1);
                       }
                   }, 100);
               }
        });

        holder.deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCall(c_ids);
                holder.main_layout.setVisibility(View.GONE);
            }
        });

        holder.msgButton.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
//                   Intent smsIntent = new Intent(context, SmsActivity.class);
//                   smsIntent.putExtra("address", phone);
//                   smsIntent.putExtra("name", name);
//                   smsIntent.putExtra("sms_body","");
//                   context.startActivity(smsIntent);

                   Uri sms_uri = Uri.parse("smsto:"+phone);
                   Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                   context.startActivity(sms_intent);

//                   Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//                   smsIntent.setType("vnd.android-dir/mms-sms");
//                   smsIntent.putExtra("address", phone);
//                   smsIntent.putExtra("sms_body"," ");
//                   context.startActivity(smsIntent);
               }
        });

        holder.dnButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.slideView.isShown()) {
                    holder.slideView.startAnimation(animationUp);
                    is_shown.put(position, false);

                    CountDownTimer countDownTimerStatic = new CountDownTimer(COUNTDOWN_RUNNING_TIME, 10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            holder.slideView.setVisibility(View.GONE);
                        }
                    };
                    countDownTimerStatic.start();

                    holder.dnButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);

                }else{

                    holder.slideView.setVisibility(View.VISIBLE);
                    holder.slideView.startAnimation(animationDown);
                    holder.dnButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                    is_shown.put(position, true);
                }

            }
        });
    }

    private int getCount(int count){
        return count;
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

    private void openWhatsApp(String phone, Context ctx) {
        String smsNumber = phone; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, " ");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");
        
        ctx.startActivity(sendIntent);
    }

    private void openContact(String phone, Context ctx) {
        Intent addIntent = new Intent(ctx, addContact.class);
        addIntent.putExtra("phone", phone);
        ctx.startActivity(addIntent);
    }

    private void editContact(String phone, String name, Integer contact_ids, Context ctx) {
        Intent addIntent = new Intent(ctx, editContact.class);
        addIntent.putExtra("phone", phone);
        addIntent.putExtra("name", name);
        addIntent.putExtra("contact_id", contact_ids);
        ctx.startActivity(addIntent);
    }

    private void deleteCall(Integer log_id) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            try {
                context.getContentResolver().delete( CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " = ? ", new String[] { String.valueOf(log_id) });
            }
            catch (Exception ex) {
                System.out.print("Exception here ");
            }
        }
    }

}
