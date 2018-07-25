package com.luncher.santanu.dailer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.felipecsl.gifimageview.library.GifImageView;

/**
 * Created by santanu on 12/1/18.
 */

public class GridActivity extends Activity {

    private GridView gridView;
    String [] list;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);

//        GifGridAdapter adapter = new GifGridAdapter(this, getData());
//        gridView = findViewById(R.id.gridView);
//        gridView.setAdapter(adapter);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                //Create intent
//                Intent data = new Intent();
//                data.putExtra("POS_ICON", list[position]);
//                setResult(Activity.RESULT_OK, data);
//
//                finish();
//            }
//        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new GalleryAdapter(getApplicationContext(), getData());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Create intent
                Intent data = new Intent();
                data.putExtra("POS_ICON", list[position+1]);
                setResult(Activity.RESULT_OK, data);

                finish();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    // Prepare some dummy data for gridview
    private List<String> getData() {
        final List<String> imagesPathArrayList = new ArrayList<>();
        String path = "";
        //ArrayList<String> imagesPathArrayList = null;
        try {
            list = getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if(file.startsWith("RNGR_")){
                        imagesPathArrayList.add(file);
                        Log.e("FAV_Images", file);
                    }
                }
            }
        } catch (IOException e) {
            //return false;
        }
        return imagesPathArrayList;
    }
}
