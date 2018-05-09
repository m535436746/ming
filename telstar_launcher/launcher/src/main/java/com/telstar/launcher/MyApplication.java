package com.telstar.launcher;

import android.app.ActivityManager;
import android.app.Application;
import android.app.SystemWriteManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.SystemProperties;

import com.telstar.launcher.input.bean.DlnaInputEntity;
import com.telstar.launcher.input.bean.HdmiInputEntity;
import com.telstar.launcher.input.bean.InputBase;
import com.telstar.launcher.input.bean.MiracastInputEntity;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bill on 18-5-2.
 */

public class MyApplication extends Application {

    private static final String mSharedPreferencesName = "cn.net.telstar.input.prefs";
    private static final String Key_Last_Select_Index = "lastSelectIndex";

    public static final String ACTION_HDMI_IN = "telstar.intent.action.HDMI_IN";
    public static final String ACTION_HDMI_OUT = "telstar.intent.action.HDMI_OUT";
    public static final String HDMIIN_RESET_PORT = "com.amlogic.osdoverlay.RESET_PORT";
    public static final String HDMIIN_CLOSE = "com.amlogic.osdoverlay.CLOSE";

    private int mHdmiInputNum = 1;

    private Context mContext;
    private List<InputBase> mInputSources = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xutils3
        x.Ext.init(this);
        //设置ｄｅｂｕｇ模式
        x.Ext.setDebug(false);
        mContext = this;
        initInputSources();
        Resources res = getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        config.fontScale = 1.3f;
        getResources().updateConfiguration(config, res.getDisplayMetrics());
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Resources res = getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        config.fontScale = 1.3f;
        getResources().updateConfiguration(config, res.getDisplayMetrics());
        super.onConfigurationChanged(newConfig);
    }

    private void initInputSources() {
        if (mInputSources == null)
            mInputSources = new ArrayList<InputBase>();
        mInputSources.clear();
        InputBase miracast, dlna, hdmi;
        SystemWriteManager swm = (SystemWriteManager) mContext.getSystemService("system_write");
        int hdmiInPortsNum = swm.getPropertyInt("mbx.hdmiin.hdmiportsnum", 1);
        mHdmiInputNum = hdmiInPortsNum;
        if (hdmiInPortsNum == 1) {
            hdmi = new HdmiInputEntity("HDMI", InputBase.InputType.HDMI, InputBase.STATE_DISCONNECT,
                    HdmiInputEntity.SOURCE_HDMI_IN[0], HdmiInputEntity.INPUT_SOURCE_HDMIIN_INTERNAL);
            mInputSources.add(hdmi);
        } else {
            int i;
            for (i = 0; i < hdmiInPortsNum; i++) {
                hdmi = new HdmiInputEntity("HDMI" + (i + 1), InputBase.InputType.HDMI, InputBase.STATE_DISCONNECT,
                        HdmiInputEntity.SOURCE_HDMI_IN[i], HdmiInputEntity.INPUT_SOURCE_HDMIIN_INTERNAL);
                mInputSources.add(hdmi);
            }
        }


        // if(!(Build.DEVICE.equals("telstarU5_cn")) && !(Build.DEVICE.equals("mp230_cn")) && !(Build.DEVICE.equals("mp125_cn")) ) {
        String display_dlna_miracast = SystemProperties.get("ro.display.dlna_miracast", "true");
        if ("true".equals(display_dlna_miracast)) {
            dlna = new DlnaInputEntity("DLNA", InputBase.InputType.DLAN, InputBase.STATE_NONE);
            mInputSources.add(dlna);
            miracast = new MiracastInputEntity("Screen Mirroring", InputBase.InputType.MIRACAST, InputBase.STATE_NONE);
            mInputSources.add(miracast);
        }
    }

    public int getHdmiNum() {
        return mHdmiInputNum;
    }
    /**
     * 判断Hdmi是否在前台运行
     * @return
     */
    public boolean isHdmiRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
        String name = cn.getClassName();
        return "com.amlogic.osdoverlay.FullActivity".equalsIgnoreCase(name);
    }

    public int getLastSelectedInput() {
        SharedPreferences sp = getSharedPreferences(mSharedPreferencesName, MODE_PRIVATE);
        return sp.getInt(Key_Last_Select_Index, 0);
    }

    public void saveLastSelectedInput(int select) {
        SharedPreferences.Editor editor = getSharedPreferences(mSharedPreferencesName, MODE_PRIVATE).edit();
        editor.putInt(Key_Last_Select_Index, select);
        editor.commit();
    }

    public List<InputBase> getInputSources() {
        return mInputSources;
    }

    public List<InputBase> getHdmiInputs() {
        return mInputSources == null ? null : mInputSources.subList(0, mHdmiInputNum);
    }

    public boolean isAppInstalled(String packageName) {
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

    public void forceStopPackage(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        am.forceStopPackage(packageName);
    }
}
