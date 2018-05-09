package com.telstar.launcher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.telstar.launcher.adapter.TvGridAdapter;
import com.telstar.launcher.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

import reco.frame.tv.view.TvGridView;

/**
 * Created by bill on 2016/5/29.
 */
public class LinkAppList  extends Activity {

    public TvGridView linkapplist;
    public TvGridAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_app_list);
        linkapplist = (TvGridView) findViewById(R.id.applinkGridView);

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(appReceiver, filter);
        LoadAll();
    }


    private BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            final String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {

                LoadAll();
            }
        }
    };


    public void LoadAll() {
        adapter = new TvGridAdapter(this, LoadAllApp());
        linkapplist.setAdapter(adapter);
    }


    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public List<AppInfo> LoadAllApp() {
        List<AppInfo> applist = new ArrayList<AppInfo>();


       /* AppInfo application1 = new AppInfo();
        application1.setFromNetwork(true);
        //application1.apkDownloadUrl = "http://pool.apk.aptoide.com/appstv/com-google-android-youtube-googletv-6238-11775937-227458fb7c6930fee29ea059f499b77f.apk";
        application1.icon = getResources().getDrawable(R.drawable.youtube);
        application1.setApk_md5sum("48b563f45d8255f9fb5f196e13b4f55c");
        application1.Title = "YouTube";
        if (!isAppInstalled("com.google.android.youtube.tv")) {
            applist.add(application1);
        }*/


        AppInfo application2 = new AppInfo();
        application2.setFromNetwork(true);
        //com.netflix.mediaclient
        //application1.apkDownloadUrl = "http://pool.apk.aptoide.com/appstv/com-google-android-youtube-googletv-6238-11775937-227458fb7c6930fee29ea059f499b77f.apk";
        application2.icon = getResources().getDrawable(R.drawable.netflix);
        application2.setApk_md5sum("cec198b4506294111659e4ccb9df1b2d");
        application2.Title = "Netflix";
        if (!isAppInstalled("com.netflix.mediaclient")) {
            applist.add(application2);
        }

        AppInfo application3 = new AppInfo();
        application3.setFromNetwork(true);
        //com.netflix.mediaclient
        //application1.apkDownloadUrl = "http://pool.apk.aptoide.com/appstv/com-google-android-youtube-googletv-6238-11775937-227458fb7c6930fee29ea059f499b77f.apk";
        application3.icon = getResources().getDrawable(R.drawable.hulu);
        application3.setApk_md5sum("56c65f9ad1b06cf92a37b48948b6c73a");
        application3.Title = "Hulu";
        if (!isAppInstalled("com.hulu.plus")) {
            applist.add(application3);
        }



        AppInfo application4 = new AppInfo();
        application4.setFromNetwork(true);
        //com.netflix.mediaclient
        //application1.apkDownloadUrl = "http://pool.apk.aptoide.com/appstv/com-google-android-youtube-googletv-6238-11775937-227458fb7c6930fee29ea059f499b77f.apk";
        application4.icon = getResources().getDrawable(R.drawable.amazon);
        application4.setApk_md5sum("abdfc9375ec01675a4437cdf3e03b857");
        application4.Title = "Amazon Video";
        if (!isAppInstalled("com.amazon.avod.thirdpartyclient")) {
            applist.add(application4);
        }


        AppInfo application5 = new AppInfo();
        application5.setFromNetwork(true);
        //com.netflix.mediaclient
        //application1.apkDownloadUrl = "http://pool.apk.aptoide.com/appstv/com-google-android-youtube-googletv-6238-11775937-227458fb7c6930fee29ea059f499b77f.apk";
        application5.icon = getResources().getDrawable(R.drawable.hbo);
        application5.setApk_md5sum("6072031dc4f5869f53b12a927d85dd14");
        application5.Title = "HBO";
        if (!isAppInstalled("com.HBO")) {
            applist.add(application5);
        }



        AppInfo application6 = new AppInfo();
        application6.setFromNetwork(true);
        //com.netflix.mediaclient
        //application1.apkDownloadUrl = "http://pool.apk.aptoide.com/appstv/com-google-android-youtube-googletv-6238-11775937-227458fb7c6930fee29ea059f499b77f.apk";
        application6.icon = getResources().getDrawable(R.drawable.espn);
        application6.setApk_md5sum("238281a163b05589fd70fa9260fe4c1f");
        application6.Title = "ESPN";
        if (!isAppInstalled("com.espn.score_center")) {
            applist.add(application6);
        }

        return applist;

    }



    @Override
    protected void onDestroy() {
        unregisterReceiver(appReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
