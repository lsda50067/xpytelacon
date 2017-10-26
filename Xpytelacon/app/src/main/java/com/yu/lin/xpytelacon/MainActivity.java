package com.yu.lin.xpytelacon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.NavigationView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lin-Yu on 10/2017.
 * For Test Git Control
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainActivity.class.getName();
    private ListView mListView;
    private CustomAdapter mCustomAdapter;
    private LruCache<String, Bitmap> mLruCache;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init View
        initView();
        // reSet ToolBar
        reSetToolBar();
        // init LruCache
        initLruCache();
        // create Handle;
        initHandle();
        // init Toggle
        initToggle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListener();
        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // set Adapter null for onDestroy
        mListView.setAdapter(null);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mListView = (ListView) findViewById(R.id.list_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    private void reSetToolBar(){
        setSupportActionBar(toolbar);
    }

    private void initHandle() {
        mHandlerThread = new HandlerThread("LRU Cache Handler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private void initLruCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // use  int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 512); change 512 photo always cache ?（????）
        Log.d(TAG ," == maxMemory == " + maxMemory);
        int cacheSize = maxMemory / 2;
        Log.d(TAG ," == cacheSize == " + cacheSize);
        // setLruCache
        mLruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    private void initToggle(){
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void setListener(){
        floatingActionButton.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setAdapter() {
        mCustomAdapter = new CustomAdapter(getApplicationContext(), images, date);
        mListView.setAdapter(mCustomAdapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d(TAG , "onItemClick: " + position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                // Handle the camera action
                Log.d(TAG , "Item Camera OnClick");
                break;
            case R.id.nav_gallery:
                Log.d(TAG , "Item Gallery OnClick");
                break;
            case R.id.nav_slideshow:
                Log.d(TAG , "Item SlideShow OnClick");
                break;
            case R.id.nav_manage:
                Log.d(TAG , "Item Tool OnClick");
                break;
        }
        // OnClick then close drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public class CustomAdapter extends BaseAdapter {

        private Map<String, Integer> mLoadingImageMap;
        private Map<String, String> mLoadingDataMap;

        private String[] mDate;
        private Integer[] mImage;
        private Context mContext;

        private CustomAdapter(Context context, Integer[] image, String[] date){
            mLoadingImageMap = new HashMap<String, Integer>();
            mLoadingDataMap = new HashMap<String, String>();
            this.mContext = context;
            this.mImage = image;
            this.mDate = date;
        }


        private class ViewHolder{
            ImageView imageView;
            TextView textView;
        }

        @Override
        public int getCount() {
            // return image count
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if  (view == null){
                holder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.list_view_item, null);
                holder.imageView = (ImageView) view.findViewById(R.id.img);
                holder.textView = (TextView) view.findViewById(R.id.text_view);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.imageView.setImageResource(R.drawable.default_img);
            holder.imageView.setPadding(5, 5, 5, 5);
            holder.textView.setText("");
            holder.textView.setPadding(5, 5, 5, 5);

            final String key = position + "_cache";
            Bitmap b = mLruCache.get(key);
            if(b == null && !mLoadingImageMap.containsKey(key) && !mLoadingDataMap.containsKey(key)) {
                mLoadingImageMap.put(key, mImage[position]);
                mLoadingDataMap.put(key, mDate[position]);
                Log.e("TestLru", "load pic" + position);
                mHandler.post(new Runnable() {
                    Bitmap bmp;
                    @Override
                    public void run() {
                        bmp = decodeBitmap(mImage[position], 200);
                        mLruCache.put(key, bmp);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                mLoadingImageMap.remove(key);
                                mLoadingDataMap.remove(key);
                            }
                        });
                    }
                });
            } else{
                Log.e("TestLru", "cache");
                holder.imageView.setImageBitmap(b);
                holder.textView.setText(mDate[position]);
            }

            return view;
        }
    }

    public Bitmap decodeBitmap(Integer url, int maxWidth){

        Bitmap bitmap = null;
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxWidth);

            // getDrawable photo
            Drawable drawable = getResources().getDrawable(url);
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

    // image source form https://newevolutiondesigns.com/50-fresh-hd-wallpapers // no use business
    private Integer[] images = {
            R.drawable.hd_wallpaper_preview_1,
            R.drawable.hd_wallpaper_preview_2,
            R.drawable.hd_wallpaper_preview_3,
            R.drawable.hd_wallpaper_preview_4,
            R.drawable.hd_wallpaper_preview_5,
            R.drawable.hd_wallpaper_preview_6,
            R.drawable.hd_wallpaper_preview_7,
            R.drawable.hd_wallpaper_preview_8,
            R.drawable.hd_wallpaper_preview_9,
            R.drawable.hd_wallpaper_preview_10,
            R.drawable.hd_wallpaper_preview_11,
            R.drawable.hd_wallpaper_preview_12,
            R.drawable.hd_wallpaper_preview_13,
            R.drawable.hd_wallpaper_preview_14,
            R.drawable.hd_wallpaper_preview_15,
            R.drawable.hd_wallpaper_preview_16,
            R.drawable.hd_wallpaper_preview_17,
            R.drawable.hd_wallpaper_preview_18,
            R.drawable.hd_wallpaper_preview_19,
            R.drawable.hd_wallpaper_preview_20,
            R.drawable.hd_wallpaper_preview_21,
            R.drawable.hd_wallpaper_preview_22,
            R.drawable.hd_wallpaper_preview_23,
            R.drawable.hd_wallpaper_preview_24,
            R.drawable.hd_wallpaper_preview_25,
            R.drawable.hd_wallpaper_preview_26,
            R.drawable.hd_wallpaper_preview_27,
            R.drawable.hd_wallpaper_preview_28,
            R.drawable.hd_wallpaper_preview_29,
            R.drawable.hd_wallpaper_preview_30,
            R.drawable.hd_wallpaper_preview_31,
            R.drawable.hd_wallpaper_preview_32,
            R.drawable.hd_wallpaper_preview_33,
            R.drawable.hd_wallpaper_preview_34,
            R.drawable.hd_wallpaper_preview_35,
            R.drawable.hd_wallpaper_preview_36,
            R.drawable.hd_wallpaper_preview_37,
            R.drawable.hd_wallpaper_preview_38,
            R.drawable.hd_wallpaper_preview_39,
            R.drawable.hd_wallpaper_preview_40,
            R.drawable.hd_wallpaper_preview_41,
            R.drawable.hd_wallpaper_preview_42,
            R.drawable.hd_wallpaper_preview_43,
            R.drawable.hd_wallpaper_preview_44,
            R.drawable.hd_wallpaper_preview_45,
            R.drawable.hd_wallpaper_preview_47,
            R.drawable.hd_wallpaper_preview_48,
            R.drawable.hd_wallpaper_preview_49,
            R.drawable.hd_wallpaper_preview_50
    };

    private String[] date = {
            "1","2","3","4","5","6","7","8","9","10",
            "11","12","13","14","15","16","17","18","19","20",
            "21","22","23","24","25","26","27","28","29","30",
            "31","32","33","34","35","36","37","38","39","40",
            "41","42","43","44","45","47","48","49","50"
    };

}
