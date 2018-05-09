package com.telstar.box;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.telstar.box.adapter.LocalAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import reco.frame.tv.view.TvTabHost;

public class MainActivity extends FragmentActivity {

    private final String TAG="bill";
    private boolean is24hFormart = false;
    private final String SD_PATH = "/storage/external_storage/sdcard1";
    private final String USB_PATH ="/storage/external_storage";
    private final String net_change_action = "android.net.conn.CONNECTIVITY_CHANGE";
    private final String wifi_signal_action = "android.net.wifi.RSSI_CHANGED";
    private final String weather_request_action = "android.amlogic.launcher.REQUEST_WEATHER";
    private final String weather_receive_action = "android.amlogic.settings.WEATHER_INFO";
    private final String outputmode_change_action = "android.amlogic.settings.CHANGE_OUTPUT_MODE";
    private static int time_count = 0;
    private final int time_freq = 180;
    private GridView lv_status;
    private static int wifi_level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvtabhost);
        //监控程序安装卸载
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(appReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(net_change_action);
        filter.addAction(wifi_signal_action);
        // filter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(weather_receive_action);
        filter.addAction(outputmode_change_action);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        registerReceiver(netReceiver, filter);


        lv_status = (GridView)findViewById(R.id.list_status);
        lv_status.setFocusableInTouchMode(false);
        lv_status.setFocusable(false);
        lv_status.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        });

        loadFrag();
        displayDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayDate();
        displayStatus();
        updateWifiLevel();
    }

    /**
     * Destroy all fragments and loaders.
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(appReceiver);
        unregisterReceiver(netReceiver);
        super.onDestroy();
    }

    private void displayStatus() {
        LocalAdapter ad = new LocalAdapter(MainActivity.this,
                getStatusData(isEthernetOn()),
                R.layout.homelist_item,
                new String[] {"item_type", "item_name", "item_sel"},
                new int[] {R.id.item_type, 0, 0});
        lv_status.setAdapter(ad);
    }

    public List<Map<String, Object>> getStatusData(boolean is_ethernet_on) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        if (wifi_level != -1){
            switch (wifi_level + 1) {
                //case 0:
                //	map.put("item_type", R.drawable.wifi1);
                //	break;
                case 1:
                    map.put("item_type", R.drawable.wifi2);;
                    break;
                case 2:
                    map.put("item_type", R.drawable.wifi3);
                    break;
                case 3:
                    map.put("item_type", R.drawable.wifi4);
                    break;
                case 4:
                    map.put("item_type", R.drawable.wifi5);
                    break;
                default:
                    break;
            }
            list.add(map);
        }

        if(isSdcardExists() == true){
            map = new HashMap<String, Object>();
            map.put("item_type", R.drawable.img_status_sdcard);
            list.add(map);
        }

        if(isUsbExists() == true){
            map = new HashMap<String, Object>();
            map.put("item_type", R.drawable.img_status_usb);
            list.add(map);
        }

        if(is_ethernet_on == true){
            map = new HashMap<String, Object>();
            map.put("item_type", R.drawable.img_status_ethernet);
            list.add(map);
        }

        return list;
    }


    private void updateStatus() {
        ((BaseAdapter) lv_status.getAdapter()).notifyDataSetChanged();
    }


    public boolean isUsbExists(){
        File dir = new File(USB_PATH);
        if (dir.exists() && dir.isDirectory()) {
            if (dir.listFiles() != null) {
                if (dir.listFiles().length > 0) {
                    for (File file : dir.listFiles()) {
                        String path = file.getAbsolutePath();
                        if (path.startsWith(USB_PATH+"/sd")&&!path.equals(SD_PATH)) {
                            //	if (path.startsWith("/mnt/sd[a-z]")){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public  boolean isSdcardExists(){
       /* if(Environment.getExternalStorage2State().startsWith(Environment.MEDIA_MOUNTED)) {
            File dir = new File(SD_PATH);
            if (dir.exists() && dir.isDirectory()) {
                return true;
            }
        }*/
        return false;
    }

    private void updateWifiLevel(){
        ConnectivityManager connectivity = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()){
            WifiManager mWifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi_rssi = mWifiInfo.getRssi();

            if (wifi_rssi <= -100)
                wifi_level = -1;
            else
                wifi_level = WifiManager.calculateSignalLevel(wifi_rssi, 4);
        } else {
            wifi_level = -1;
        }
    }
    private boolean isEthernetOn(){
        ConnectivityManager connectivity = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        if (info.isConnected()){
            return true;
        } else {
            return false;
        }
    }


    private BroadcastReceiver appReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            final String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {

                updateAppList(intent);
            }
        }
    };



    private void updateAppList(Intent intent){
        Log.e("myitm","---------------------update app list ");
        TvTabHost tth_container = (TvTabHost) findViewById(R.id.tth_container);
        FragmentA fragmentA = (FragmentA)tth_container.fragList.get(0);
        fragmentA.LoadAll();
    }

    private void loadFrag() {

        /**
         * 添加页面
         */
        TvTabHost tth_container = (TvTabHost) findViewById(R.id.tth_container);
        tth_container.addPage(getSupportFragmentManager(), new FragmentA(),
                getResources().getString(R.string.class_apps), getResources().getDrawable(R.drawable.application));
       /* tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Videos");
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Music");
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Sports");
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Games");*/
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                getResources().getString(R.string.class_settings), getResources().getDrawable(R.drawable.setting));
        tth_container.addPage(getSupportFragmentManager(), new FragmentC(),
                getResources().getString(R.string.class_helps), getResources().getDrawable(R.drawable.help));
        tth_container.buildLayout();

        /**
         * 设监听
         */
        tth_container.setOnPageChangeListener(new TvTabHost.ScrollPageChangerListener() {

            @Override
            public void onPageSelected(int pageCurrent) {

                Log.i(TAG, "第 " + (pageCurrent + 1) + " 页");

            }
        });
        /**
         * 页面跳转
         */
        tth_container.setCurrentPage(0);
    }

    private void displayDate() {

        is24hFormart = DateFormat.is24HourFormat(this);
        TextView time = (TextView)findViewById(R.id.txtTime);
        TextView date = (TextView)findViewById(R.id.txtDate);
        time.setText(getTime());
        time.setTypeface(Typeface.DEFAULT_BOLD);
        date.setText(getDate());
    }

    public  String getTime(){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        is24hFormart = DateFormat.is24HourFormat(this);
        if (!is24hFormart && hour >= 12) {
            hour = hour - 12;
        }

        String time = "";
        if (hour >= 10) {
            time +=  Integer.toString(hour);
        }else {
            time += "0" + Integer.toString(hour);
        }
        time += ":";

        if (minute >= 10) {
            time +=  Integer.toString(minute);
        }else {
            time += "0" +  Integer.toString(minute);
        }

        return time;
    }

    private String getDate(){
        final Calendar c = Calendar.getInstance();
        int int_Month = c.get(Calendar.MONTH);
        String mDay = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        int int_Week = c.get(Calendar.DAY_OF_WEEK) -1;
        String str_week =  this.getResources().getStringArray(R.array.week)[int_Week];
        String mMonth =  this.getResources().getStringArray(R.array.month)[int_Month];

        String date;
        if (Locale.getDefault().getLanguage().equals("zh")){
            date = str_week + ", " + mMonth + " " + mDay + this.getResources().getString(R.string.str_day);
        }else {
            date = str_week + ", " + mMonth + " " + mDay;
        }

        //Log.d(TAG, "@@@@@@@@@@@@@@@@@@@ "+ date  + "week = " +int_Week);
        return date;
    }


    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            Log.d(TAG, "netReceiver         action = " + action);
            if (action.equals(Intent.ACTION_TIME_TICK)){
                displayDate();
            } else if(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)
                    || Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)){
                updateAppList(intent);
            }else {
                updateWifiLevel();
                displayStatus();
                updateStatus();
            }
        }
    };
}
