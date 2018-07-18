package ew.ecommerce.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class Downloader extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    private boolean needToCut;

    public Downloader(ImageView bmImage, boolean needToCut) {
        this.bmImage = bmImage;
        this.needToCut = needToCut;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            if (needToCut) {
                bmImage.setImageBitmap(getPreferencedBitmap(result));
            } else {
                bmImage.setImageBitmap(result);
            }
        }
    }

    private Bitmap getPreferencedBitmap(Bitmap oldBitmap) {
        int oldW = oldBitmap.getWidth();
        int oldH = oldBitmap.getHeight();
        int newW, newH;
        if (oldW > oldH) {
            newW = bmImage.getWidth();
            newH = newW * oldH / oldW;
        } else {
            newH = bmImage.getHeight();
            newW = oldW * newH / oldH;
        }
        return Bitmap.createScaledBitmap(oldBitmap, newW, newH, false);
    }
}