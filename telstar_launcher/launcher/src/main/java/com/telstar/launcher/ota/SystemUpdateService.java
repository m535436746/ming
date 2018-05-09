package com.telstar.launcher.ota;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by bill on 2016/5/30.
 */
public class SystemUpdateService extends Service {

    private static final String TAG="update";
    private Timer timer;
    private CheckTask checkTask;
    private  int NetworkChangeNum = 0 ;
    private SharedPreferences mPreferences;
    private Editor editor;
    private String hdinfo=null;
    private String deviceID=null;//"MID1000001";
    private String clientVersion=null;//"V1.3";
    private String clientIP=null;//"192.168.5.153";
    private String isAutoRequest="1";
    private String md5="";
    private String version="";
    private String url="";
    private String checkstatus=null;
    private int    versionid = 0;
    private String deviceNo= Utils.getDeviceNo();
    private String clientCode=null;
    private String typeName=null;
    private String versionCode=null;
    private String androidVersion=null;
    private String hardwareInfo=null;
    private String ext=null;

    private byte[] checkLock = new byte[0];
    private byte[] statusLock = new byte[0];
    private byte[] posLock = new byte[0];
    private int status=-1;
    private long fileLen=-1;
    private long position=-1;


    private boolean isTaskRunning=false;
    private boolean isTaskRunning()
    {
        synchronized(checkLock){
            return isTaskRunning;
        }
    }

    private void setTaskRunning(boolean run)
    {
        synchronized(checkLock){
            isTaskRunning=run;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter=new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Utils.CHECK_NOW);
        Log.d(TAG, "-------------ota service create ------");
        registerReceiver(mIntentReceiver, filter);
        timer=new Timer();
        checkTask=new CheckTask();
        timer.schedule(checkTask, 2000, 1000*60);//*30*2
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            Log.d(TAG, "-------------"+action);

            if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                Message msg4=mHandler.obtainMessage(Utils.CHECK_UPDATE_FILE);
                mHandler.sendMessageDelayed(msg4,30000);
            }

            if(action.equals(Utils.CHECK_NOW))
            {
                Log.e(TAG,"---------------receiver check_now ");
                new Thread(checkNowTask).start();
            }
        }
    };

    Runnable checkNowTask=new Runnable()
    {
        public void run() {

            Log.d(TAG, "------------->CheckNow begin");
            if(isTaskRunning())
            {
                Log.d(TAG, "-------checkNowTask------>task is running");
                mHandler.sendEmptyMessage(Utils.SERVICE_HANDLER_NO_NEW_VERSION);
                setTaskRunning(false);
                return;
            }
            try{
                if(!Utils.isNetworkOpen(SystemUpdateService.this))
                {
                    mHandler.sendEmptyMessage(Utils.SERVICE_HANDLER_NETWORK_ERROR);
                    setTaskRunning(false);
                    return ;
                }
                setTaskRunning(true);

                deviceID=Utils.getDevice();
                clientVersion=Utils.getVersion();
                hdinfo=Utils.getHWINFO();
                Log.d(TAG, "------------->deviceID:"+deviceID);
                Log.d(TAG, "------------->clientVersion:"+clientVersion);
                Log.d(TAG, "------------->hdinfo:"+hdinfo);
                clientCode=Utils.getClinetCode();
                typeName=Utils.getTypeName();
                versionCode=Utils.getVersion();
                androidVersion=Utils.getAndroidVersion();
                hardwareInfo=Utils.getHWINFO();
                ext="";
                Log.d(TAG, "------------->deviceNo:"+deviceNo +" clientCode:"+clientCode+ " typeName:" +typeName + " versionCode:"+versionCode + "  androidVersion:"+ androidVersion + "  hardwareInfo:"+hardwareInfo + "  ext:"+ext);
                Bundle rs=Utils.getResponseString(deviceNo,clientCode,typeName,versionCode,androidVersion,hardwareInfo,ext);
                checkstatus=rs.getString("status");
                if(checkstatus==null)
                    checkstatus="";
                md5=rs.getString("md5");
                if(md5==null)
                    md5="";
                url=rs.getString("url");
                if(url==null)
                    url="";
                version=rs.getString("version");
                if(version==null)
                    version="";
                editor=mPreferences.edit();
                if(checkstatus.equalsIgnoreCase("1")&&(!version.equals(""))){
                    versionid=Integer.parseInt(rs.getString("versionid"));
                    editor.putString("version", version);
                    editor.putString("md5", md5);
                    editor.putString("url", url);
                    editor.putInt("versionid",versionid);
                    Intent intent = new Intent(Utils.CHECK_END);
                    intent.putExtra("status","1");
                    intent.putExtra("version",version);
                    intent.putExtra("md5",md5);
                    intent.putExtra("url",url);
                    sendBroadcast(intent);
                    setTaskRunning(false);
                }
                if(checkstatus.equalsIgnoreCase("2")){
                    Intent intent = new Intent(Utils.CHECK_END);
                    intent.putExtra("status","2");
                    sendBroadcast(intent);
                    mHandler.sendEmptyMessage(Utils.SERVICE_HANDLER_NO_NEW_VERSION);
                }
                if(checkstatus.equalsIgnoreCase("-1")||checkstatus.equalsIgnoreCase("0")||checkstatus.equalsIgnoreCase("3")){
                    Intent intent = new Intent(Utils.CHECK_END);
                    intent.putExtra("status","-1");
                    sendBroadcast(intent);
                }
                editor.commit();

            }
            catch(Exception e)
            {
                Log.d(TAG, "--------------->exception:"+e);
                mHandler.sendEmptyMessage(Utils.SERVICE_HANDLER_UNKOWN_ERROR);
            }
            setTaskRunning(false);
        }

    };



    private class CheckTask extends TimerTask
    {
        @Override
        public void run() {
            Log.d(TAG, "------------->CheckTask begin");

            if(isTaskRunning())
            {
                Log.d(TAG, "--------checktask----->task is running");
                return;
            }

            try{
                Thread.sleep(1000);
                if(!Utils.isNetworkOpen(SystemUpdateService.this))
                {
                    // mHandler.sendEmptyMessage(Utils.SERVICE_HANDLER_NETWORK_ERROR);
                    setTaskRunning(false);
                    return;
                }
                setTaskRunning(true);
                mPreferences=getSharedPreferences("setting_ota", 0);
                deviceID=Utils.getDevice();
                clientVersion=Utils.getVersion();
                hdinfo=Utils.getHWINFO();
                Log.d(TAG, "------------->deviceID:"+deviceID);
                Log.d(TAG, "------------->clientVersion:"+clientVersion);
                Log.d(TAG, "------------->hdinfo:"+hdinfo);
                clientCode=Utils.getClinetCode();
                typeName=Utils.getTypeName();
                //versionCode=Utils.getVersion();
                versionCode="V1.0.0";
                androidVersion="4.1.1";
                hardwareInfo=Utils.getHWINFO();
                ext="";
                Log.d(TAG, "------------->deviceNo:"+deviceNo +" clientCode:"+clientCode+ " typeName:" +typeName + " versionCode:"+versionCode + "  androidVersion:"+ androidVersion + "  hardwareInfo:"+hardwareInfo + "  ext:"+ext);
                Bundle rs=Utils.getResponseString(deviceNo,"telstar",typeName,versionCode,androidVersion,hardwareInfo,ext);
                //Bundle rs=Utils.getResponseString(deviceNo,"YF","M10S","V1.1.2",androidVersion,hardwareInfo,ext);
                //Bundle rs=Utils.getResponseString(deviceID,clientVersion,sn,hdinfo,"",isAutoRequest);
                checkstatus=rs.getString("status");
                if(checkstatus==null)
                    checkstatus="";
                md5=rs.getString("md5");
                if(md5==null)
                    md5="";
                url=rs.getString("url");
                if(url==null)
                    url="";
                version=rs.getString("version");
                if(version==null)
                    version="";

                editor=mPreferences.edit();
                if(checkstatus.equalsIgnoreCase("1")&&(!version.equals(""))){
                    versionid=Integer.parseInt(rs.getString("versionid"));
                    editor.putString("version", version);
                    editor.putString("md5", md5);
                    editor.putString("url", url);
                    editor.putInt("versionid",versionid);
                   // mHandler.sendEmptyMessage(Utils.SERVICE_HANDLER_SHOW_UPDATEUI);
                    setTaskRunning(false);
                }
                editor.commit();

            }catch(Exception e)
            {

                Log.d(TAG, "--------------->  CheckTask  exception:"+e);
            }
            setTaskRunning(false);
        }

    }


    private Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what)
            {
                case Utils.SERVICE_HANDLER_SHOW_UPDATEUI:
                    sendBroadcast(new Intent(Utils.CHECK_END));
                    break;
                case Utils.SERVICE_HANDLER_NETWORK_ERROR:
                    sendBroadcast(new Intent(Utils.CHECK_END));
                    Intent localIntent1 = new Intent();
                    localIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    localIntent1.putExtra("msg", "1");
                    ComponentName localComponentName1 = new ComponentName("com.android.inhuasoft.ota", "com.android.inhuasoft.ota.ui.UIForUpdateCustomAlert");
                    localIntent1.setComponent(localComponentName1);
                    //startActivity(localIntent1);
                    break;
                case Utils.SERVICE_HANDLER_NO_NEW_VERSION:
                    sendBroadcast(new Intent(Utils.CHECK_END));
                    Intent localIntent2 = new Intent();
                    localIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    localIntent2.putExtra("msg", "2");
                    ComponentName localComponentName2 = new ComponentName("com.android.inhuasoft.ota", "com.android.inhuasoft.ota.ui.UIForUpdateCustomAlert");
                    localIntent2.setComponent(localComponentName2);
                   // startActivity(localIntent2);
                    break;
                case Utils.SERVICE_HANDLER_UNKOWN_ERROR:
                    sendBroadcast(new Intent(Utils.CHECK_END));
                    Intent localIntent3 = new Intent();
                    localIntent3.putExtra("msg", "3");
                    localIntent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName localComponentName3 = new ComponentName("com.android.inhuasoft.ota", "com.android.inhuasoft.ota.ui.UIForUpdateCustomAlert");
                    localIntent3.setComponent(localComponentName3);
                    //startActivity(localIntent3);
                    break;
                case Utils.SERVICE_HANDLER_UPDATE_ISRUNING:
                    sendBroadcast(new Intent(Utils.CHECK_END));
                    Intent localIntent4 = new Intent();
                    localIntent4.putExtra("msg", "4");
                    localIntent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName localComponentName4 = new ComponentName("com.android.inhuasoft.ota", "com.android.inhuasoft.ota.ui.UIForUpdateCustomAlert");
                    localIntent4.setComponent(localComponentName4);
                    //startActivity(localIntent4);
                    break;
                case Utils.SERVICE_HANDLER_DOWNLOAD:
                    sendBroadcast(new Intent(Utils.CHECK_END));

                    break;
                case Utils.UPDATE_HANDLER_CHECKFILE_FAILED:
                    sendBroadcast(new Intent(Utils.CHECK_END));
                    Intent updateintent = new Intent();
                    updateintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName updateComponentName = new ComponentName("com.android.inhuasoft.ota", "com.android.inhuasoft.ota.ui.UIForUpdateAlert");
                    updateintent.setComponent(updateComponentName);
                    startActivity(updateintent);
                    break;
                case Utils.SERVICE_HANDLER_STORAGE_NOENOUGH:
                    sendBroadcast(new Intent(Utils.CHECK_END));
                    Intent nointent = new Intent();
                    nointent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName noComponentName = new ComponentName("com.android.inhuasoft.ota", "com.android.inhuasoft.ota.ui.UIForUpdateRunningAlert");
                    nointent.setComponent(noComponentName);
                    startActivity(nointent);
                    break;
                case Utils.CHECK_UPDATE_FILE:
                    break;
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
