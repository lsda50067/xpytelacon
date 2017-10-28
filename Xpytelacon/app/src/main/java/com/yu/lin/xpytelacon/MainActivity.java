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

import com.yu.lin.xpytelacon.adapter.CustomAdapter;
import com.yu.lin.xpytelacon.provider.Images;

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

    private Integer[] mImages;
    private String[] mDate;
    private ListView mListView;
    private CustomAdapter mCustomAdapter;
//    private LruCache<String, Bitmap> mLruCache;
//    private HandlerThread mHandlerThread;
//    private Handler mHandler;
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
        // init Toggle
        initToggle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // setData
        serData();
        // setListener
        setListener();
        // setAdapter
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

    private void serData() {
        mImages = Images.images;
        mDate = Images.date;
    }

    private void reSetToolBar(){
        setSupportActionBar(toolbar);
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
        mCustomAdapter = new CustomAdapter(getApplicationContext(), mImages, mDate);
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

}
