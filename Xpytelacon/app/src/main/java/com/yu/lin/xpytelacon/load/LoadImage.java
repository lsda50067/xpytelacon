package com.yu.lin.xpytelacon.load;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.yu.lin.xpytelacon.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Lin-Yu on 27/10/2017.
 *
 */

public class LoadImage extends AsyncTask<Integer, Void, Bitmap> {

//    private ImageView mImv;
//    private String mPath;

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

//    private Integer[] mImage;
//    private String[] mData;
//    private int mPosition;

    private final WeakReference<ImageView> imageViewReference;

    public LoadImage(ImageView imageView, Context context) {
        this.mContext = context;
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Integer... integer) {
        try {
            return decodeBitmap(integer[0], 200);
        } catch (Exception e) {
            // log error
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
//                    Drawable placeholder = imageView.getContext().getResources().getDrawable();
//                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }

    public Bitmap decodeBitmap(Integer url, int maxWidth){

        Bitmap bitmap = null;
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxWidth);

            // getDrawable photo
            Drawable drawable = mContext.getResources().getDrawable(url);
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
            Bitmap b = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            //use the compression format of your need
            InputStream is = new ByteArrayInputStream(stream.toByteArray());

            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
