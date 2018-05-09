package com.telstar.launcher;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bill on 2016/6/2.
 */
public class SettingData {
    private static final String TAG = "SettingData";
    private static final String SHARED_PREFERENCES_NAME = "telstar_settings";
    private static final String LAUNCHER_MODE = "launcher_mode";

    public  SettingData()
    {
    }
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //1  android
    //2  apple
   public  static String  getLauncherMode(Context context) {
        return getSharedPreferences(context).getString(LAUNCHER_MODE,"1");
    }
   public static void setLauncherMode(Context context,String mode)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LAUNCHER_MODE, mode);
        editor.apply();
    }
}
