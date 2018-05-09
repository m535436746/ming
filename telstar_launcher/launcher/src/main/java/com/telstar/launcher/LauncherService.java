package com.telstar.launcher;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by bill on 2016/6/2.
 */
public class LauncherService extends Service {
    private static final String TAG = "main";
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("change");
        registerReceiver(mBroadcastReceiver,intentFilter);
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("change"))
            {
                String str  = SettingData.getLauncherMode(getApplicationContext());
                Log.d(TAG,"launcher mode :" + str);
                if("1".equals(str))
                {
                    SettingData.setLauncherMode(getApplicationContext(),"2");
                    Intent intent1 = new Intent();
                    intent1.setClass(getApplicationContext(),Main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    SettingData.setLauncherMode(getApplicationContext(),"1");
                    Intent intent1 = new Intent();
                    intent1.setClass(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    };



    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
