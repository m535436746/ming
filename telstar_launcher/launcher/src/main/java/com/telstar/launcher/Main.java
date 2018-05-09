package com.telstar.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by bill on 2016/6/2.
 */
public class Main extends Activity {
    private static final String TAG = "main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ComponentName componentName = new ComponentName(Main.this, LauncherService.class);
        Intent intent1 = new Intent();
        intent1.setComponent( componentName);
        startService(intent1);

         String str  = SettingData.getLauncherMode(this);
        Log.d(TAG,"launcher mode :" + str);
        if("2".equals(str)) {
            setContentView(R.layout.activity_main);
        }
        else
        {
            Intent intent = new Intent();
            intent.setClass(Main.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK  || keyCode == KeyEvent.KEYCODE_HOME) {
            Log.e("bill","keycode_back");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
