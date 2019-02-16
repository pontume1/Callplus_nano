package com.luncher.bounjour.ringlerr.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Coupons;

import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CouponsActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<Coupons, CouponsActivity.CouponViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coupons);

        SessionManager session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        String mPhoneNo = user.get(SessionManager.KEY_PHONE);

        RecyclerView coupon_list = findViewById(R.id.coupons_view);
        coupon_list.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        coupon_list.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("My Rewards");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        assert mPhoneNo != null;
        String new_phone = mPhoneNo.replace("+", "");
        Query mReminderDatabase = FirebaseDatabase.getInstance().getReference().child("coupons").child(new_phone).orderByChild("name");
        mReminderDatabase.keepSynced(true);

        FirebaseRecyclerOptions<Coupons> options =
                new FirebaseRecyclerOptions.Builder<Coupons>()
                        //.setLayout(R.layout.reminder_single_list)
                        .setQuery(mReminderDatabase, Coupons.class)
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Coupons, CouponsActivity.CouponViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CouponViewHolder couponViewHolder, int position, @NonNull Coupons coupons) {

                String name = coupons.getName();
                String address = coupons.getAddress();
                String description = coupons.getDescription();
                String store = coupons.getStore();

                couponViewHolder.setDisplayMessage(name, address, description, store);

            }

            @NonNull
            @Override
            public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.coupon_grid, parent, false);

                return new CouponViewHolder(view);
            }
        };

        coupon_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    public class CouponViewHolder extends RecyclerView.ViewHolder{

        TextView txt_name;
        TextView txt_address;
        TextView txt_description;
        Button redeem;

        CouponViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_name = itemView.findViewById(R.id.name);
            txt_address = itemView.findViewById(R.id.address);
            txt_description = itemView.findViewById(R.id.description);
            redeem = itemView.findViewById(R.id.redeem);
        }

        void setDisplayMessage(final String name, final String address, final String description, final String store){
            String full_name = name+" @ "+store;
            txt_name.setText(full_name);
            txt_address.setText(address);
            txt_description.setText(description);

            redeem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View itemView) {
                    final Intent intent = new Intent(CouponsActivity.this, CouponWeb.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //intent.putExtra("phone_no", "null");
                    startActivity(intent);
                }
            });
        }
    }
}
