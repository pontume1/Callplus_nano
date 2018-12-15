package com.luncher.bounjour.ringlerr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.GridView;

import com.luncher.bounjour.ringlerr.adapter.StickerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by santanu on 12/1/18.
 */

public class StickerActivity extends Activity {

    private GridView gridView;
    File [] list;
    String [] file_list;
    final List<String> imagesPathArrayList = new ArrayList<>();
    String path = Environment.getExternalStorageDirectory() + "/ringerrr/stickers";
    private StickerAdapter mAdapter;
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
        mAdapter = new StickerAdapter(getApplicationContext(), getData());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Create intent
                Intent data = new Intent();
                data.putExtra("POS_STICKER", imagesPathArrayList.get(position));
                setResult(Activity.RESULT_OK, data);

                finish();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    // Prepare some data for gridview
    private List<String> getData() {

        //ArrayList<String> imagesPathArrayList = null;
        list = new File(path).listFiles();
        if (list != null && list.length > 0) {
            // This is a folder
            for (int i = 0; i < list.length; i++)
            {
                if(list[i].getName().startsWith("RNGR_STICKER_")){
                    imagesPathArrayList.add(list[i].getName());
                }
            }
        }

        return imagesPathArrayList;
    }
}
