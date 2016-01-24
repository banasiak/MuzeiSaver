package com.banasiak.android.muzeisaver;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiContract;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class DownloadActivity extends Activity {
    
    private static final String TAG = DownloadActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Artwork artwork = MuzeiContract.Artwork.getCurrentArtwork(this);
        Bitmap image = null;
        String title = null;
        String byline = null;
        
        if(artwork != null) {
            title = artwork.getTitle();
            byline = artwork.getByline();
            try {
                image = BitmapFactory.decodeStream(getContentResolver().openInputStream(MuzeiContract.Artwork.CONTENT_URI));
            } catch (FileNotFoundException e) {
                Log.w(TAG, "Unable to decode stream into bitmap");
            }  
        } else {
            String msg = getResources().getString(R.string.no_artwork);
            Log.w(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        if(image != null) {
            String filename = null;
            if(title != null) {
                filename = title.trim();
            }
            if (byline != null) {
                if(filename != null) {
                    filename = filename + " " + getResources().getString(R.string.by) + " " + byline.trim();
                } else {
                    filename = byline.trim();
                }
            }
            if(filename == null) {
                filename = new Date().toString();
            }

            // filter out bad characters
            filename = filename.replaceAll("\\W+", "_");

            filename = filename + ".png";

            saveBitmapToFile(filename, image);
        }

        finish();
    }
    
    private void saveBitmapToFile(String filename, Bitmap image) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            String msg = getResources().getString(R.string.unable_to_save);
            Log.e(TAG, msg + ": " + e.getMessage());
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }

        Log.i(TAG, "Image saved to: " + file.toString());
        
        // add the file to the gallery
        MediaScannerConnection.scanFile(this, new String[] {file.toString()}, null, null);

        Toast.makeText(this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
    }
}
