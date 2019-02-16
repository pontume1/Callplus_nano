package com.luncher.bounjour.ringlerr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.adapter.BlockAdapter;
import com.luncher.bounjour.ringlerr.model.Blocks;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BlockList extends AppCompatActivity {
    private List<Blocks> blockList = new ArrayList<>();
    private TextView no_data;
    // Session Manager Class
    SessionManager session;
    BlockAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.block_list);

        RecyclerView block_list = findViewById(R.id.block_list);
        no_data = findViewById(R.id.no_data);

        //initialise adapter class
        mAdapter = new BlockAdapter(this, blockList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        block_list.setLayoutManager(mLayoutManager);
        block_list.setItemAnimator(new DefaultItemAnimator());
        //set adapter to recyclerview
        block_list.setAdapter(mAdapter);
        //call method to fetch data from db and add to recyclerview
        prepareData();
    }

    private void prepareData() {
        MyDbHelper myDbHelper = new MyDbHelper(BlockList.this, null, 1);
        List<Blocks> sec = myDbHelper.getAllBlocks();
        blockList.clear();

        if (sec.size() > 0) {
            //loop through contents
            for (int i = 0; i < sec.size(); i++) {
                //add data to list used in adapter
                blockList.add(sec.get(i));
                //notify data change
                mAdapter.notifyDataSetChanged();
            }
        }else{
            no_data.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
