package com.luncher.santanu.dailer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by santanu on 13/1/18.
 */

public class GifDataDownloader extends AsyncTask<String, Void, byte[]> {
    private Context context;
    private static final String TAG = "GifDataDownloader";
    InputStream inputStream;

    public GifDataDownloader(Context context) {
        this.context = context;
    }

    @Override protected byte[] doInBackground(final String... params) {
        final String gifUrl = params[0];

        if (gifUrl == null)
            return null;

        try {
            inputStream = context.getAssets().open(gifUrl);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return bytes;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "GifDecode OOM: " + gifUrl, e);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
