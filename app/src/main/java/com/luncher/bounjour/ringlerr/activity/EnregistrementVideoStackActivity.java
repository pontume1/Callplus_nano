package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.SessionType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class EnregistrementVideoStackActivity extends AppCompatActivity {

    Boolean camFacingBack = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_surface);

        final CameraView camera = findViewById(R.id.camera);
        ImageButton recButton = findViewById(R.id.recButton);
        final TextView count = findViewById(R.id.counterText);
        ImageView switchCam = findViewById(R.id.switchCam);
        camera.setLifecycleOwner(this);

        File dir = new File(Environment.getExternalStorageDirectory() + "/ringerrr/videos/sent");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        final String selectedImagePath =Environment.getExternalStorageDirectory() + "/ringerrr/videos/sent/vid_"+timeStamp+".mp4";
        // From fragments, use fragment.viewLifecycleOwner instead of this!

        camera.setSessionType(SessionType.VIDEO);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                // The File is the same you passed before.
                // Now it holds a MP4 video.
                count.setText("00:00:03");
                Intent data = new Intent();
                data.putExtra("POS_VIDEO", selectedImagePath);
                setResult(Activity.RESULT_OK, data);
                finish();

            }
        });

        // Select output file. Make sure you have write permissions.
        final File file = new File(selectedImagePath);

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.startCapturingVideo(file);
                // You can also use one of the video constraints:
                // videoMaxSize and videoMaxDuration will automatically stop recording when satisfied.
                camera.setVideoMaxSize(0);
                camera.setVideoMaxDuration(3000);
                count.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        Long Fin = 3 - (millisUntilFinished / 1000);
                        count.setText("00:00:0" + Fin);
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        count.setText("00:00:03");
                    }

                }.start();
            }

        });

        switchCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camFacingBack = !camFacingBack;
                if(camFacingBack) {
                    camera.setFacing(Facing.FRONT);
                }else{
                    camera.setFacing(Facing.BACK);
                }
            }
        });
    }

//    public void conversion(String[] cmd) {
//        FFmpeg ffmpeg = FFmpeg.getInstance(this);
//
//        try {
//            //Load the binary
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {}
//
//                @Override
//                public void onFailure() {}
//
//                @Override
//                public void onSuccess() {}
//
//                @Override
//                public void onFinish() {}
//            });
//        } catch (FFmpegNotSupportedException e) {
//            // Handle if FFmpeg is not supported by device
//        }
//
//        try {
//            // to execute "ffmpeg -version" command you just need to pass "-version"
//            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                    Toast.makeText(getApplicationContext(), "Vid to gif started", Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onSuccess(String message) {
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onFinish() {
//                    Toast.makeText(getApplicationContext(), "vid to gif finished", Toast.LENGTH_LONG).show();
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // Handle if FFmpeg is already running
//            e.printStackTrace();
//        }
//    }

}
