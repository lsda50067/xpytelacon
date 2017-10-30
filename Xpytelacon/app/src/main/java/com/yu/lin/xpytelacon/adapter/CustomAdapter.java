package com.yu.lin.xpytelacon.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yu.lin.xpytelacon.MainActivity;
import com.yu.lin.xpytelacon.R;
import com.yu.lin.xpytelacon.load.LoadImage;
import com.yu.lin.xpytelacon.load.TaskExcutor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Lin-Yu on 27/10/2017.
 *
 */

public class CustomAdapter extends BaseAdapter implements LoadImage.OnLoadImageListener{

    private static final String TAG = CustomAdapter.class.getName();
    private int maxMemory;
    private int cacheSize;
    private LruCache<String, Bitmap> mLruCache;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private Map<String, Integer> mLoadingImageMap;
    private Map<String, String> mLoadingDataMap;

    private String[] mDate;
    private Integer[] mImage;
    private Context mContext;
    private LoadImage mLoadImage;

    public CustomAdapter(Context context, Integer[] image, String[] date){
        mLoadingImageMap = new HashMap<String, Integer>();
        mLoadingDataMap = new HashMap<String, String>();
        this.mContext = context;
        this.mImage = image;
        this.mDate = date;
        // init LruCache
        initLruCache();
        // create Handle;
        initHandle();
    }

    @Override
    public int getCount() {
        return mImage.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onLoadImageSuccess(String key,Bitmap bitmap) {
        mLruCache.put(key, bitmap);
        notifyDataSetChanged();
    }

    @Override
    public void onLoadImageError() {

    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
//        Bitmap bitmap;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_item, null);
            holder.imageView = (ImageView) view.findViewById(R.id.img);
            holder.textView = (TextView) view.findViewById(R.id.text_view);
            holder.imageView.setPadding(5, 5, 5, 5);
            holder.textView.setPadding(5, 5, 5, 5);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.imageView.setImageResource(R.drawable.default_img);
        holder.textView.setText("");

        final String key = position + "_cache";
        Bitmap b = mLruCache.get(key);
        holder.imageView.setTag(key);

        if (b == null) {
            Log.d("CustomAdapter","Image not in cache");
            Log.e("TestLru", "load pic" + position);
            LoadImage task = new LoadImage(mContext, this, key);
            //mLoadImage.execute(mImage[position]);

            task.executeOnExecutor(TaskExcutor.getInstance().getExcutor(),mImage[position]);

//            mHandler.post(new Runnable() {
//                Bitmap bmp;
//
//                @Override
//                public void run() {
//                    bmp = decodeBitmap(mImage[position], 200);
//                    mLruCache.put(key, bmp);
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            notifyDataSetChanged();
//                            mLoadingImageMap.remove(key);
//                            mLoadingDataMap.remove(key);
//                        }
//                    });
//                }
//            });
        } else {
            Log.d("CustomAdapter","Image in cache");

            Log.e("TestLru", "cache");
            holder.imageView.setImageBitmap(b);
            holder.textView.setText(mDate[position]);
        }

        return view;
    }

    // create Handle;
    private void initHandle() {
        mHandlerThread = new HandlerThread("LRU Cache Handler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    // init LruCache
    private void initLruCache() {
        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // use  int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 512); change 512 photo always cache ?（????）
        Log.d(TAG ," == maxMemory == " + maxMemory);
         cacheSize = maxMemory / 2;
        Log.d(TAG ," == cacheSize == " + cacheSize);
        // setLruCache
        mLruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
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
