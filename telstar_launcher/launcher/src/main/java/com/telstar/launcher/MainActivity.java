package com.telstar.launcher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.SystemWriteManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.ResolveInfo;
import android.hardware.input.InputManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.telstar.launcher.adapter.TvGridAdapter;
import com.telstar.launcher.entity.AppInfo;
import com.telstar.launcher.entity.BluetoothState;
import com.telstar.launcher.input.bean.DlnaInputEntity;
import com.telstar.launcher.input.bean.HdmiInputEntity;
import com.telstar.launcher.input.bean.InputBase;
import com.telstar.launcher.input.bean.MiracastInputEntity;
import com.telstar.launcher.input.receiver.PortKeyReceiver;
import com.telstar.launcher.input.util.AnimationFactory;
import com.telstar.launcher.input.util.TelstarLOG;
import com.telstar.launcher.input.widget.IListMenuView;
import com.telstar.launcher.input.widget.ListMenuView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Instrumentation;

import reco.frame.tv.view.TvGridView;

public class MainActivity extends Activity {

    private final String TAG = "bill";

    private static final String SPECIAL_DEVICE = "telstarU5";
    private static final String CHECK_NEW_VERSION_ACTION = "com.telstar.checkversion";
    private static final String FIND_NEW_VERSION_ACTION = "com.telstar.find.newversion";
    private static final int MSG_HDMI_PLUG_CHECK = 0xA0;
    private static final int MSG_INIT_HDMI_STATE = 0xA1;
    private static final int MSG_HDMI_OUT_KILL_APP = 0xA2;
    private static final int MSG_WIFI_CHECK = 0xA3;
    private static final int MSG_UPDATE_BTREMOTE = 0x1;
    private boolean is24hFormart = false;
    private final String SD_PATH = "/storage/external_storage/sdcard1";
    private final String USB_PATH = "/storage/external_storage";
    private final String BATTERY_PRESENT_PATH = "/sys/class/power_supply/battery/present";
    private static final String  HDMI_STATE_PATH = "/sys/class/hdmirx/cable_status";
    private final String DEBUG_PATH = "/sys/class/amlogic/debug";
    private static int time_count = 0;
    private final int time_freq = 180;
    private static int wifi_level;
    private BluetoothState mBluetoothState = new BluetoothState();
    public TvGridView app_gridlist;
    public TvGridAdapter adapter;
    private ImageView img_bluetooth;
    private ImageView img_btremote;
    private ImageView img_wifi;
    private ImageView img_banner;
    private BatteryView batteryMeterView;
    private SystemWriteManager mSW;
    static  final int BluetoothProfie_Input = 4 ;

    private MyApplication mInputApplication;

    private ListMenuView mMenuView;
    private View mLastSelectedView;
    private boolean hasNewVersion = false;

    private Timer checkTimer;
    private CheckBtRemoteTask checkTask;


    //软件类型判断软件
    //未知软件类型
    public static final int UNKNOW_APP = 0;
    //用户软件类型
    public static final int USER_APP = 1;
    //系统软件
    public static final int SYSTEM_APP = 2;
    //系统升级软件
    public static final int SYSTEM_UPDATE_APP = 4;
    //系统+升级软件
    public static final int SYSTEM_REF_APP = SYSTEM_APP | SYSTEM_UPDATE_APP;

    /**
     * 是否是系统软件或者是系统软件的更新软件
     * @return
     */
    public boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public boolean isUserApp(PackageInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    private int checkAppType(String pname) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(pname, 0);
            // 是系统软件或者是系统软件更新
            if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
                return SYSTEM_REF_APP;
            } else {
                return USER_APP;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return UNKNOW_APP;
    }


    private Handler mHdmiCheckHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(MSG_HDMI_PLUG_CHECK == msg.what) {
                // 检测HDMI是否有信号输入
                CheckHdmiState();
            } else if(MSG_INIT_HDMI_STATE == msg.what) {
                int hdmiInPortsNum = mSW.getPropertyInt("mbx.hdmiin.hdmiportsnum", 1);
                initHdmiState(hdmiInPortsNum);
            } else if(MSG_HDMI_OUT_KILL_APP == msg.what) {
                // HDMI 还在运行，关闭
                if(mInputApplication.isHdmiRunning()) {
                    forceStopPackage("com.amlogic.osdoverlay");
                }
            } else if(MSG_WIFI_CHECK == msg.what) {
                checkWiFiConnected();
            }
        }
    };


    private  Handler mMainHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(MSG_UPDATE_BTREMOTE == msg.what)
            {

                if(HasConnectBtRemote())
                {
                    //Log.d(TAG,"update bt remote .................. connect ");
                    img_btremote.setImageResource(R.drawable.btremote_connect);
                }
                else
                {
                    //Log.d(TAG,"update bt remote .................. disconnect ");
                    img_btremote.setImageResource(R.drawable.btremote_disconnect);
                }
            }
        }
    };

    public  void setBluetoothConnectState()
    {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter != null )
        {
            if (adapter.isEnabled()) {
                mBluetoothState.enabled = true ;
                int a2dp = adapter.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
                int headset = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);        //蓝牙头戴式耳机，支持语音输入输出
                int health = adapter.getProfileConnectionState(BluetoothProfile.HEALTH);          //蓝牙穿戴式设备
                int input = adapter.getProfileConnectionState(BluetoothProfie_Input);             //蓝牙输入设备

                //查看是否蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
                int flag = -1;
                if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                    flag = a2dp;
                } else if (headset == BluetoothProfile.STATE_CONNECTED) {
                    flag = headset;
                } else if (health == BluetoothProfile.STATE_CONNECTED) {
                    flag = health;
                } else if (input == BluetoothProfile.STATE_CONNECTED)
                {
                    flag = input;
                }
                //说明连接上了四种设备的一种
                if (flag != -1){
                    mBluetoothState.connected = true;
                }
            }

        }
    }

    public void Write2SysFile(String value) {

        File file = new File(DEBUG_PATH);
        if((file == null) || (!file.exists()) || (value == null))
        {
            return ;
        }

        try {
            FileOutputStream fout = new FileOutputStream(file);
            PrintWriter pWriter = new PrintWriter(fout);
            pWriter.println(value);
            pWriter.flush();
            pWriter.close();
            fout.close();
        } catch(IOException re) {
            return;
        }
    }


    private class CheckBtRemoteTask extends TimerTask {
        @Override
        public void run() {
            mMainHandler.sendEmptyMessage(MSG_UPDATE_BTREMOTE);
        }

    }


    public boolean HasConnectBtRemote()
    {
        boolean isConnect = false;
        InputManager manager  = (InputManager) getSystemService(Context.INPUT_SERVICE);
        int[] devices = manager.getInputDeviceIds();

        for (int i = 0 ; i < devices.length ; ++ i )
        {
            InputDevice device =  manager.getInputDevice(devices[i]);
            if(device.getName().equals(GetBtInputName()))
            {
                isConnect = true ;
                return  isConnect;
            }
        }
        return  isConnect;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_5x3);

        mSW = (SystemWriteManager) this.getSystemService("system_write");
        hasNewVersion = false;
        img_bluetooth = (ImageView) findViewById(R.id.bluetooth);
        img_btremote = (ImageView) findViewById(R.id.btremote);
        img_wifi = (ImageView) findViewById(R.id.wifi);
        batteryMeterView = (BatteryView) findViewById(R.id.battery);
        img_banner = (ImageView)findViewById(R.id.banner);
        if(Build.DEVICE.equals("telstarU5_cn") || Build.DEVICE.equals("telstarU5_cn_3d"))
        {
            img_banner.setImageResource(R.drawable.mlogo);
        }
        else if(Build.DEVICE.equals("telstarU5") ||  Build.DEVICE.equals("mp318"))
        {
            img_banner.setImageResource(R.drawable.logo);
        }
        if(Build.DEVICE.equals("telstarU5")|| Build.DEVICE.equals("mp318")|| Build.DEVICE.equals("telstarU5_cn") || Build.DEVICE.equals("telstarU5_cn_3d"))
        {
            img_banner.setVisibility(View.VISIBLE);
        }
        else
        {
            img_banner.setVisibility(View.GONE);
        }
        if (hasBattery()) {
            batteryMeterView.setVisibility(View.VISIBLE);
        } else {
            batteryMeterView.setVisibility(View.GONE);
        }

        if(HasConnectBtRemote())
        {
            img_btremote.setImageResource(R.drawable.btremote_connect);
        }
        else
        {
            img_btremote.setImageResource(R.drawable.btremote_disconnect);
        }

       /* setBluetoothConnectState();
        if(mBluetoothState.enabled)
        {
            img_bluetooth.setVisibility(View.GONE);
            if(mBluetoothState.connected)
            {
                img_bluetooth.setImageResource(R.drawable.ic_qs_bluetooth_on);
            }else
            {
                img_bluetooth.setImageResource(R.drawable.ic_qs_bluetooth_off);
            }
        }*/

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(appReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
/*        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);*/
        //filter.addAction(BluetoothInputDevice.ACTION_CONNECTION_STATE_CHANGED);
/*        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);*/
        filter.addAction(FIND_NEW_VERSION_ACTION);
        registerReceiver(netReceiver, filter);

        app_gridlist = (TvGridView) findViewById(R.id.applist);
        //loadFrag();
        displayDate();
        LoadAll();


        //start ota service
       /* ComponentName componentName = new ComponentName("com.amlapp.update.otaupgrade","com.amlapp.update.otaupgrade.CheckService");
        Intent intent = new Intent();
        intent.setComponent( componentName);
        startService(intent);*/


        //start autofocus service
   /*     ComponentName componentName1 = new ComponentName("com.android.settings","com.android.settings.autofocus.AutoFocusService");
        Intent intent1 = new Intent();
        intent1.setComponent(componentName1);
        startService(intent1);*/

        SetMenuView();
        // 1秒后检测HDMI接入情况
        mHdmiCheckHandler.sendEmptyMessageDelayed(MSG_INIT_HDMI_STATE, 1000);
        // 3秒后开始检测HDMI信号
        mHdmiCheckHandler.sendEmptyMessageDelayed(MSG_HDMI_PLUG_CHECK, 3000);

        // 注册HDMI插拔的广播
        registerHdmiInRecevier();
       /* // check bt remote
        ComponentName componentName2 = new ComponentName("com.android.settings","com.android.settings.bluetooth.BtRemoteWizard");
        Intent intent2 = new Intent();
        intent2.setComponent( componentName2);
        //startService(new Intent(this, BtRemoteCheckService.class));
        startActivity(intent2);
        finish();*/
        //pq set
        checkTimer = new Timer();
        checkTask = new CheckBtRemoteTask();
        checkTimer.schedule(checkTask, 1000, 1000 * 1);

        //pq setting 项 不能去掉
        //Write2SysFile("w 0x0 v 0x31c5");
        //Write2SysFile("w 0x1 v 0x31a7");
        //Write2SysFile("w 0x0 v 0x31a7");
        //Write2SysFile("w 0x9552104 v 0x31b4");
        //Write2SysFile("w 0xa555310 v 0x31ae");
        //Write2SysFile("w 0x2a90 v 0x31bf");



    }

    private  void SetMenuView()
    {

        mInputApplication = (MyApplication) getApplication();
        mMenuView = new ListMenuView(this);
        mMenuView.setOnMenuListener(new IListMenuView.OnMenuListener() {

            @Override
            public void onMenuItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                TelstarLOG.D(TAG, " ListMenuView  onItemSelected  position: " + position);
                if (mLastSelectedView != null) {
                    mLastSelectedView.clearAnimation();
                    AnimationSet zoomOutAnimation = AnimationFactory.getZoomOutAnimationSet();
                    mLastSelectedView.startAnimation(zoomOutAnimation);
                }
                AnimationSet zoomInAnimation = AnimationFactory.getZoomInAnimationSet();
                view.startAnimation(zoomInAnimation);
                mLastSelectedView = view;

            }

            @Override
            public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                TelstarLOG.D(TAG, " ListMenuView  onItemClick ");
                if (mLastSelectedView != null && !mLastSelectedView.isSelected()) {
                    mLastSelectedView.clearAnimation();
                    AnimationSet zoomOutAnimation = AnimationFactory.getZoomOutAnimationSet();
                    mLastSelectedView.startAnimation(zoomOutAnimation);
                }
                AnimationSet zoomInAnimation = AnimationFactory.getZoomInAnimationSet();
                view.startAnimation(zoomInAnimation);
                view.setSelected(true);
                mLastSelectedView = view;

                List<InputBase> inputs = mInputApplication.getInputSources();
                final InputBase input = inputs.get(position);
                if (input != null) {

                    if (input.getType() == InputBase.InputType.HDMI && input instanceof HdmiInputEntity) {
                        TelstarLOG.D(TAG, " isHdmiRunnig " + mInputApplication.isHdmiRunning());
                        // Kill 与HDMI冲突的Twitch,再打开HDMI
                        if(mInputApplication != null && mInputApplication.isAppInstalled("tv.twitch.android.app"))
                        {
                            TelstarLOG.D(TAG, "Twitch is installed, killing ------- ");
                            mInputApplication.forceStopPackage("tv.twitch.android.app");
                        }
                        if(!mInputApplication.isHdmiRunning()) {
                            // 保存这次点击的位置
                            if(mInputApplication.getHdmiNum() == 3) {
                                mInputApplication.saveLastSelectedInput(position);
                            }

                            // 等待动画完成后才打开Intent
                            mHdmiCheckHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent();
                                    i.setClassName("com.amlogic.osdoverlay", "com.amlogic.osdoverlay.FullActivity");
                                    i.putExtra("source", ((HdmiInputEntity) input).getInputSource());
                                    i.putExtra("source_type", ((HdmiInputEntity) input).getInputSourceType());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            }, 600);

                        }
//                        HdmiInFragment hdmiInFragment = new HdmiInFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("source", ((HdmiInputEntity) input).getInputSource());
//                        bundle.putInt("source_type", ((HdmiInputEntity) input).getInputSourceType());
//                        hdmiInFragment.setArguments(bundle);
                    } else if (input.getType() == InputBase.InputType.MIRACAST && input instanceof MiracastInputEntity) {
                        mHdmiCheckHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent();
                                i.setClassName("cn.telstar.miracast", "cn.telstar.miracast.ui.WifiErrorActivity");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }, 600);
                    } else if (input.getType() == InputBase.InputType.DLAN && input instanceof DlnaInputEntity) {
                        mHdmiCheckHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent();
                                i.setClassName("com.amlogic.mediacenter", "com.amlogic.mediacenter.MediaCenterActivity");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }, 600);

                    }
                }
                mMenuView.closeMenu();
            }
        });
    }

    /**
     * Take care of calling onBackPressed() for pre-Eclair platforms.
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Log.e("bill","keycode_back");
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mMenuView.toggleMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void LoadAll() {
        Log.d("test","-----load all -----");
        adapter = new TvGridAdapter(this, LoadAllApp());
        //tgv_imagelist.removeAllViews();
        app_gridlist.setAdapter(adapter);

        if(Build.DEVICE.equals("mp230_apeman")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("test", ">>>>>>>>>>>>>>> keyevent >>>>>>>>>>>>>>>>");
                        String keyCommand = "input keyevent  21";
                        Runtime runtime = Runtime.getRuntime();
                        Process proc = runtime.exec(keyCommand);

/*                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(21);*/
                        //app_gridlist.getChildAt(0).requestFocus();
                    } catch (Exception e) {
                        //Log.e(TAG, e.toString());
                    }
                }
            }, 500);
        }

        //tgv_imagelist.initGridView();
    }

    public static class InstallTimeComparator implements Comparator<ResolveInfo> {
        private PackageManager mPackageManager;
        List<ResolveInfo> mapps;
        InstallTimeComparator(PackageManager pm, List<ResolveInfo> apps)
        {
            mPackageManager = pm;
            mapps = apps;

        }

        public final int compare(ResolveInfo a, ResolveInfo b) {
            String packageNameA = a.activityInfo.applicationInfo.packageName;
            String packageNameB = b.activityInfo.applicationInfo.packageName;
            long firstInstallTimeA = 0, firstInstallTimeB = 0;
            try{
                firstInstallTimeA = mPackageManager.getPackageInfo(packageNameA, 0).firstInstallTime;
                firstInstallTimeB = mPackageManager.getPackageInfo(packageNameB, 0).firstInstallTime;
            }catch(Exception e){
                e.printStackTrace();
                return 0;
            }
            return firstInstallTimeA == firstInstallTimeB ? 0 : firstInstallTimeA > firstInstallTimeB ? 1:-1;
        }
    };


    public List<AppInfo> LoadAllApp() {
        List<AppInfo> applist = new ArrayList<AppInfo>();


       /* AppInfo application1 = new AppInfo();
        application1.setFromNetwork(true);
        //application1.apkDownloadUrl = "http://pool.apk.aptoide.com/appstv/com-google-android-youtube-googletv-6238-11775937-227458fb7c6930fee29ea059f499b77f.apk";
        application1.icon = getResources().getDrawable(R.drawable.youtube);
        application1.setApk_md5sum("227458fb7c6930fee29ea059f499b77f");
        application1.Title = "YouTube";
        if (!isAppInstalled("com.google.android.youtube.googletv")) {
            applist.add(application1);
        }
*/
        //add recommend
        AppInfo application1 = new AppInfo();
        application1.Title = " Recommend";
        application1.setActivity(new ComponentName(
                        "com.telstar.launcher", "com.telstar.launcher.LinkAppList"),
                Intent.FLAG_ACTIVITY_NEW_TASK);
        application1.icon =  getResources().getDrawable(R.drawable.linkapp);
        //applist.add(application1);


        PackageManager manager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);

        List<ResolveInfo>  systemApps = new  ArrayList<ResolveInfo>();
        List<ResolveInfo>  userApps = new  ArrayList<ResolveInfo>();
        List<ResolveInfo>  allApps = new ArrayList<ResolveInfo>();
        for(int i = 0; i < apps.size();++ i)
        {
            if (checkAppType(apps.get(i).activityInfo.packageName) == SYSTEM_REF_APP) {
                systemApps.add(apps.get(i));
            }else
            {
                userApps.add(apps.get(i));
            }

        }
        Collections.sort(systemApps, new ResolveInfo.DisplayNameComparator(manager));
        Collections.sort(userApps,  new InstallTimeComparator(manager, userApps));
        for (int i = 0; i < systemApps.size(); ++ i)
        {
            allApps.add(systemApps.get(i));
        }
        for (int i = 0; i < userApps.size(); ++ i)
        {
            allApps.add(userApps.get(i));
        }

        if (allApps != null) {
            final int count = allApps.size();
            Log.e("bill", " count :  " + count);
            for (int i = 0; i < count; i++) {
                AppInfo application = new AppInfo();
                ResolveInfo info = allApps.get(i);
                application.Title = info.loadLabel(manager);
                application.setActivity(new ComponentName(
                                info.activityInfo.applicationInfo.packageName,
                                info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);
                //application.icon = getResources().getDrawable(R.drawable.icon_reco);
                // Log.e("bill", info.activityInfo.name);

                if(hasNewVersion)
                {
                    if("com.amlapp.update.otaupgrade".equals(info.activityInfo.applicationInfo.packageName))
                    {
                        application.icon = getResources().getDrawable(R.drawable.update_new);
                    }
                }
                if("cm.aptoidetv.pt.miroir_apps".equals(info.activityInfo.applicationInfo.packageName))
                {
                    //application.icon = getResources().getDrawable(R.drawable.appcenter);
                }
                if (!("com.telstar.launcher1".equals(info.activityInfo.applicationInfo.packageName)
                        || "com.telstar.launcher.Main".equals(info.activityInfo.name)
                        || "com.telstar.launcher.MainActivity".equals(info.activityInfo.name)
                        || "com.google.android.gms.app.settings.GoogleSettingsActivity".equals(info.activityInfo.name)
                        //|| "com.amlapp.update.otaupgrade.MainActivity".equals(info.activityInfo.name)
                        || "com.mbx.settingsmbox.SettingsMboxActivity".equals(info.activityInfo.name)
                        || "com.android.vending.AssetBrowserActivity".equals(info.activityInfo.name)
                        || "com.kehdev.kehdevmanger.KehdevMainActivty".equals(info.activityInfo.name)
                        || "com.android.music".equals(info.activityInfo.applicationInfo.packageName)
                        || "com.android.browser".equals(info.activityInfo.applicationInfo.packageName)
                        || "com.android.gallery".equals(info.activityInfo.applicationInfo.packageName)
                        || "com.projector.settingsmbox.SettingsMboxActivity".equals(info.activityInfo.name)
                        || "com.telstar.settingsmbox.SettingsMboxActivity".equals(info.activityInfo.name))) {
                    if( !("com.meson.videoplayer".equals(info.activityInfo.applicationInfo.packageName) && Build.MODEL.equals("mp125"))) {
                        applist.add(application);
                    }
                }

            }
        }


        return applist;

    }


    private BroadcastReceiver HdmiInOutReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            if (MyApplication.ACTION_HDMI_IN.equals(action)) {
                TelstarLOG.D(TAG, "A HDMI is in");
                if (null != mMenuView) {
                    mMenuView.notifyDataSetChanged();
                }
            } else if (MyApplication.ACTION_HDMI_OUT.equals(action)) {
                TelstarLOG.D(TAG, "A HDMI is out");
                // mHdmiCheckHandler.sendEmptyMessageDelayed(MSG_HDMI_OUT_KILL_APP, 2000);
                if (null != mMenuView) {
                    // 通知数据改变
                    mMenuView.notifyDataSetChanged();
                }
            }
        }
    };


    private void registerHdmiInRecevier() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.ACTION_HDMI_IN);
        filter.addAction(MyApplication.ACTION_HDMI_OUT);
        registerReceiver(HdmiInOutReceiver, filter);
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     */
    @Override
    protected void onResume() {
        super.onResume();
        displayDate();
        updateWifiLevel();
        updateBluetooth();
        mHdmiCheckHandler.sendEmptyMessageDelayed(MSG_WIFI_CHECK, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Destroy all fragments and loaders.
     */
    @Override
    protected void onDestroy() {
        TelstarLOG.D(TAG, " Launcher  MainActivity onDestroy invoked");
        unregisterReceiver(appReceiver);
        unregisterReceiver(netReceiver);
        if (HdmiInOutReceiver != null) {
            unregisterReceiver(HdmiInOutReceiver);
        }
        if(checkTimer != null)
        {
            checkTimer.cancel();
            checkTimer = null ;
        }
        super.onDestroy();
    }

    public boolean isUsbExists() {
        File dir = new File(USB_PATH);
        if (dir.exists() && dir.isDirectory()) {
            if (dir.listFiles() != null) {
                if (dir.listFiles().length > 0) {
                    for (File file : dir.listFiles()) {
                        String path = file.getAbsolutePath();
                        if (path.startsWith(USB_PATH + "/sd") && !path.equals(SD_PATH)) {
                            //	if (path.startsWith("/mnt/sd[a-z]")){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isSdcardExists() {
       /* if(Environment.getExternalStorage2State().startsWith(Environment.MEDIA_MOUNTED)) {
            File dir = new File(SD_PATH);
            if (dir.exists() && dir.isDirectory()) {
                return true;
            }
        }*/
        return false;
    }

    private void updateWifiLevel() {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (mWifi.isConnected()) {
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi_rssi = mWifiInfo.getRssi();

            if (wifi_rssi <= -100) {
                //Log.d("bill", "wifi_rssi <= -100 ");
                wifi_level = -2;
            } else {
                wifi_level = WifiManager.calculateSignalLevel(wifi_rssi, 4);
                //Log.d("bill", "---wifi_level:" + wifi_level);
            }
        } else {
            //Log.d("bill", "---isConnected  no ");
            if (mWifiManager.isWifiEnabled()) {
                wifi_level = -1;
            } else {
                wifi_level = -2;
            }

        }


        //Log.d("bill", "-------------wifi_level:" + wifi_level);
        img_wifi.setVisibility(View.VISIBLE);

        switch (wifi_level) {
            case -2:
                img_wifi.setVisibility(View.GONE);
                break;
            case -1:
                img_wifi.setVisibility(View.VISIBLE);
                img_wifi.setImageResource(R.drawable.wifi_not_connected);
                break;
            case 0:
                img_wifi.setVisibility(View.VISIBLE);
                img_wifi.setImageResource(R.drawable.wifi_level_01);
                break;
            case 1:
                img_wifi.setVisibility(View.VISIBLE);
                img_wifi.setImageResource(R.drawable.wifi_level_02);
                break;
            case 2:
                img_wifi.setVisibility(View.VISIBLE);
                img_wifi.setImageResource(R.drawable.wifi_level_03);
                break;
            case 3:
                img_wifi.setVisibility(View.VISIBLE);
                img_wifi.setImageResource(R.drawable.wifi_level_04);
                break;
            default:
                break;
        }
    }

    private boolean isEthernetOn() {
       /* ConnectivityManager connectivity = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        if (info.isConnected()){
            return true;
        } else {
            return false;
        }*/
        return false;
    }


    private BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            final String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                updateAppList();
            }

        }
    };


    private boolean hasBattery() {
        File file = new File(BATTERY_PRESENT_PATH);
        return file.exists();
    }


    private void updateAppList() {
        Log.e("myitm", "---------------------update app list ");
       /* TvTabHost tth_container = (TvTabHost) findViewById(R.id.tth_container);
        FragmentA fragmentA = (FragmentA)tth_container.fragList.get(0);
        fragmentA.LoadAll();*/
        LoadAll();
  /*      if(adapter != null) {
            adapter.flush(LoadAllApp());
            adapter.notifyDataSetChanged();
        }*/
    }

/*    private void loadFrag() {

        *//**
     * 添加页面
     *//*
        TvTabHost tth_container = (TvTabHost) findViewById(R.id.tth_container);
        tth_container.addPage(getSupportFragmentManager(), new FragmentA(),
                getResources().getString(R.string.class_apps), getResources().getDrawable(R.drawable.application));
       *//* tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Videos");
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Music");
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Sports");
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                "Games");*//*
        tth_container.addPage(getSupportFragmentManager(), new FragmentB(),
                getResources().getString(R.string.class_settings), getResources().getDrawable(R.drawable.setting));
        tth_container.addPage(getSupportFragmentManager(), new FragmentC(),
                getResources().getString(R.string.class_helps), getResources().getDrawable(R.drawable.help));
        tth_container.buildLayout();

        *//**
     * 设监听
     *//*
        tth_container.setOnPageChangeListener(new TvTabHost.ScrollPageChangerListener() {

            @Override
            public void onPageSelected(int pageCurrent) {

                Log.i(TAG, "第 " + (pageCurrent + 1) + " 页");

            }
        });
        *//**
     * 页面跳转
     *//*
        tth_container.setCurrentPage(0);
    }*/

    private void displayDate() {

        is24hFormart = DateFormat.is24HourFormat(this);
        TextClock time = (TextClock) findViewById(R.id.txtTime);
        TextView date = (TextView) findViewById(R.id.txtDate);
        //time.setText(getTime());
        //time.setTypeface(Typeface.DEFAULT);
        date.setText(getDate());
    }

    public String getTime() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        is24hFormart = DateFormat.is24HourFormat(this);
        if (!is24hFormart && hour >= 12) {
            hour = hour - 12;
        }

        String time = "";
        if (hour >= 10) {
            time += Integer.toString(hour);
        } else {
            time += "0" + Integer.toString(hour);
        }
        time += ":";

        if (minute >= 10) {
            time += Integer.toString(minute);
        } else {
            time += "0" + Integer.toString(minute);
        }

        return time;
    }

    private String getDate() {
        final Calendar c = Calendar.getInstance();
        int int_Month = c.get(Calendar.MONTH);
        String mDay = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        int int_Week = c.get(Calendar.DAY_OF_WEEK) - 1;
        String str_week = this.getResources().getStringArray(R.array.week)[int_Week];
        String mMonth = this.getResources().getStringArray(R.array.month)[int_Month];

        String date;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            date = str_week + ", " + mMonth + " " + mDay + this.getResources().getString(R.string.str_day);
        } else {
            date = str_week + ", " + mMonth + " " + mDay;
        }

        //Log.d(TAG, "@@@@@@@@@@@@@@@@@@@ "+ date  + "week = " +int_Week);
        return date;
    }

    public static String GetBtInputName()
    {
        return SystemProperties.get("ro.btremote.name", "btremote1");
    }

    private void updateBluetooth() {
        //Log.e("bill", "bluetooth--------");
        if (mBluetoothState.enabled) {
            img_bluetooth.setVisibility(View.GONE);
            if (mBluetoothState.connected) {
                img_bluetooth.setImageResource(R.drawable.ic_qs_bluetooth_on);
            } else {
                img_bluetooth.setImageResource(R.drawable.ic_qs_bluetooth_not_connected);
            }
        } else {
            img_bluetooth.setVisibility(View.GONE);
        }
    }


    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            // Log.d(TAG, "netReceiver         action = " + action);
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                displayDate();
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)
                    || Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                Log.d("bill","-------------- dddddddddd");
                updateAppList();
            } /*else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                mBluetoothState.enabled = (state == BluetoothAdapter.STATE_ON);
                updateBluetooth();
            } else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                int status = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
                        BluetoothAdapter.STATE_DISCONNECTED);
                Log.d(TAG,"---bt  change");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null)
                {
                    if(!device.getName().equals(GetBtInputName()))
                    {
                        Log.d(TAG,"---bt  change ， name:"+device.getName()+ "  status:"+status);
                        mBluetoothState.connected = (status == BluetoothAdapter.STATE_CONNECTED);
                    }
                }
                updateBluetooth();
            }*/
            /*else if(action.equals(BluetoothInputDevice.ACTION_CONNECTION_STATE_CHANGED))
            {
                try {
                Log.d(TAG,"---bt remote change ");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int status = intent.getIntExtra(BluetoothInputDevice.EXTRA_STATE,BluetoothProfile.STATE_DISCONNECTED);
                if(device != null )
                {
                    Log.d(TAG,"-----device name:"+ device.getName() + " status: "+status);
                    if(null == device.getName())
                    {
                        Log.d(TAG,"-----device name is null");
                        String mac = device.getAddress();
                        Log.d(TAG,"-----device mac:"+ device.getAddress());
                        String btremote_mac = Settings.Global.getString(getContentResolver(), "btremote_mac");
                        Log.d(TAG,"-----get bt remote from datebase :"+ btremote_mac);
                        if(btremote_mac.equals(mac) && status == BluetoothProfile.STATE_CONNECTED ) {
                                Log.d(TAG,"bt remote connect ");
                                img_btremote.setImageResource(R.drawable.btremote_connect);
                            } else {
                                Log.d(TAG,"bt remote  disconnect ");
                                img_btremote.setImageResource(R.drawable.btremote_disconnect);
                            }
                    }
                    else
                    {
                        Log.d(TAG,"-----device name is not  null");
                        if(device.getName() !=  null) {
                            if (device.getName().equals(GetBtInputName()) && status == BluetoothProfile.STATE_CONNECTED) {
                                Log.d(TAG,"bt remote connect ");
                                img_btremote.setImageResource(R.drawable.btremote_connect);
                            } else {
                                Log.d(TAG,"bt remote  disconnect ");
                                img_btremote.setImageResource(R.drawable.btremote_disconnect);
                            }
                        }
                    }
                }}
                catch (Exception e)
                {
                    Log.d(TAG,"get exception:"+ e.toString());
                }
            }*/
           /* else if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
            {
                Log.d(TAG,"---ACTION_ACL_CONNECTED");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null)
                {
                    if(!device.getName().equals(GetBtInputName()))
                    {
                        Log.d(TAG,"---bt  change ， name:"+device.getName());
                        mBluetoothState.connected = true;
                    }
                }
                updateBluetooth();
            }
            else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
            {
                Log.d(TAG,"---ACTION_ACL_DISCONNECTED");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null)
                {
                    if(!device.getName().equals(GetBtInputName()))
                    {
                        Log.d(TAG,"---bt  change ， name:"+device.getName());
                        mBluetoothState.connected = false;
                    }
                }
                updateBluetooth();
            }*/
            else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                updateWifiLevel();
                //sendBroadcast(new Intent(CHECK_NEW_VERSION_ACTION));
            }
            else if( FIND_NEW_VERSION_ACTION.equals(action))
            {
                Log.d(TAG,"--- find new version action");
                hasNewVersion = true ;
                updateAppList();
            }
            else {
                updateWifiLevel();
            }
        }
    };

    private void initHdmiState(final int hdmiNum) {
        TelstarLOG.D(TAG, "init Hdmi State ------------");
        MyApplication mApplication = (MyApplication) this.getApplicationContext();
        List<InputBase> mInputBases = mApplication.getInputSources();
        int state = Integer.valueOf(mSW.readSysfs(HDMI_STATE_PATH), 16);
        if(hdmiNum == 1) {
            if (mInputBases != null && mInputBases.size() > 1) {
                switch (state) {
                    case 0xF: // 有信号
                        mInputBases.get(0).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    default:
                        break;
                }
            }
        } else if(hdmiNum > 1) {  // 多于一个HDMI输入口
            if (mInputBases != null && mInputBases.size() > 3) {
                switch (state) {
                    case 0xF: // 三个口都有接入HDMI信号
                        mInputBases.get(0).setConnectState(InputBase.STATE_CONNECT);
                        mInputBases.get(1).setConnectState(InputBase.STATE_CONNECT);
                        mInputBases.get(2).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    case 0xE:
                        mInputBases.get(1).setConnectState(InputBase.STATE_CONNECT);
                        mInputBases.get(2).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    case 0xD:
                        mInputBases.get(0).setConnectState(InputBase.STATE_CONNECT);
                        mInputBases.get(1).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    case 0xB:
                        mInputBases.get(0).setConnectState(InputBase.STATE_CONNECT);
                        mInputBases.get(2).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    case 0xA:
                        mInputBases.get(2).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    case 0xC:
                        mInputBases.get(1).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    case 0x9:
                        mInputBases.get(0).setConnectState(InputBase.STATE_CONNECT);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    private void CheckHdmiState() {
        int hdmiInPortsNum = mSW.getPropertyInt("mbx.hdmiin.hdmiportsnum", 1);
        String state = mSW.readSysfs(HDMI_STATE_PATH);
        // f 代表有信号输入， hdmi输入口只有一个
        if("f".equals(state) && hdmiInPortsNum == 1) {
            // Kill 与HDMI冲突的Twitch,再打开HDMI
            if(mInputApplication != null && mInputApplication.isAppInstalled("tv.twitch.android.app"))
            {
                TelstarLOG.D(TAG, "Twitch is installed, killing ------- ");
                mInputApplication.forceStopPackage("tv.twitch.android.app");
            }
            Intent i = new Intent();
            i.setClassName("com.amlogic.osdoverlay", "com.amlogic.osdoverlay.FullActivity");
            i.putExtra("source", PortKeyReceiver.TVIN_PORT_HDMI0);
            i.putExtra("source_type", PortKeyReceiver.INPUT_SOURCE_HDMIIN_INTERNAL);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(i);
        }
    }

    private void forceStopPackage(String packageName) {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        am.forceStopPackage(packageName);
    }

    private void checkWiFiConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if(mWifiManager.isWifiEnabled() && !mWifi.isConnected()) {
            // 主动连接已经配置过的WiFi
            if (mWifiManager.getConfiguredNetworks() != null && mWifiManager.getConfiguredNetworks().size() > 0) {
                TelstarLOG.D(TAG, " Connect WiFi is Connfingured");
                mWifiManager.reconnect();
            }
        }
    }
}

