package com.example.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoadTask {

    private static final String TAG = "ImageLoadTask";
    private ImageView imageView;

    public ImageLoadTask(ImageView imageView) {
        this.imageView = imageView;
    }

    public void execute(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            final Bitmap finalBitmap = bitmap;

            handler.post(() -> {
                if (finalBitmap != null && imageView != null) {
                    imageView.setImageBitmap(finalBitmap);
                }
            });
        });
    }
}
