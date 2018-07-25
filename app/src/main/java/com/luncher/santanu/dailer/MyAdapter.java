package com.luncher.santanu.dailer;

/**
 * Created by santanu on 21/1/18.
 */

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.santanu.dailer.activity.ReminderDialog;
import com.luncher.santanu.dailer.activity.addContact;
import com.luncher.santanu.dailer.activity.editContact;
import com.luncher.santanu.dailer.model.Blocks;
import com.luncher.santanu.dailer.model.Identity;

import static com.luncher.santanu.dailer.fragment.TextEditorDialogFragment.TAG;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final Animation animationUp, animationDown;;
    private List<String> values;
    private List<String> phone_no;
    private List<Integer> contactId;
    private List<Long> cid;
    public Context context;
    private final int COUNTDOWN_RUNNING_TIME = 500;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public ImageButton rCallButton;
        public ImageButton callButton;
        public TextView dnButton;
        public View layout;
        public LinearLayout slideView;

        public LinearLayout main_layout;
        public ImageButton whatsapp_btn;
        public ImageButton msgButton;
        public ImageButton addButton;
        public ImageButton blockButton;
        public ImageButton shareButton;
        public ImageButton reminderButton;
        public ImageButton deleteButton;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            rCallButton = (ImageButton) v.findViewById(R.id.rCallButton);
            callButton = (ImageButton) v.findViewById(R.id.callButton);
            dnButton = (TextView) v.findViewById(R.id.dnButton);
            slideView = (LinearLayout) v.findViewById(R.id.slideView);

            main_layout = (LinearLayout) v.findViewById(R.id.main_layout);
            whatsapp_btn = (ImageButton) v.findViewById(R.id.whatsapp_btn);
            msgButton = (ImageButton) v.findViewById(R.id.msgButton);
            addButton = (ImageButton) v.findViewById(R.id.addButton);
            blockButton = (ImageButton) v.findViewById(R.id.blockButton);
            //shareButton = (ImageButton) v.findViewById(R.id.shareButton);
            reminderButton = (ImageButton) v.findViewById(R.id.reminderButton);
            deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
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
    public MyAdapter(List<String> myDataset, List<String> phoneNo, List<Integer> contact_id, List<Long> c_id, Animation animationUp, Animation animationDown) {
        values = myDataset;
        phone_no = phoneNo;
        contactId = contact_id;
        cid = c_id;
        this.animationDown = animationDown;
        this.animationUp = animationUp;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {


        context = parent.getContext();
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
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

        final Integer contact_ids = contactId.get(position);
        final Long c_ids = cid.get(position);

        holder.txtHeader.setText(name);
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
                        //}
                    //}

//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        System.out.println("The read failed: " + databaseError.getCode());
//                    }
                //});
            }
        });

        holder.txtFooter.setText(phone);

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

        holder.blockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Block")
                        .setMessage("Are you sure you want to block "+phone+" ?")
                        .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                Long tsLong = System.currentTimeMillis()/1000;

                                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
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

                                MyDbHelper myDbHelper;
                                myDbHelper = new MyDbHelper(context, null, null, 1);
                                myDbHelper.addBlockNumber(phone);

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

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", phone);
                smsIntent.putExtra("sms_body"," ");
                context.startActivity(smsIntent);
            }
        });


        holder.dnButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.slideView.isShown()) {
                    holder.slideView.startAnimation(animationUp);

                    CountDownTimer countDownTimerStatic = new CountDownTimer(COUNTDOWN_RUNNING_TIME, 16) {
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
                }

            }
        });
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

    private void deleteCall(Long log_id) {

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
