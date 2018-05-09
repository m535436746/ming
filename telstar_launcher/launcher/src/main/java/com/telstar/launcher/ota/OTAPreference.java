package com.telstar.launcher.ota;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.telstar.launcher.R;


public class OTAPreference extends PreferenceActivity{
	private static final String TAG="update";
    /** Called when the activity is first created. */
	private CheckBoxPreference autoupdate_screen;
    private SharedPreferences share_preference;
    private Editor editor;
    private static final String AUTOUPDATE_TAG="ota_autoupdate_screen_checkbox_key";
    private static final String TIPS_TAG="ota_tips_key";
    private boolean isrunning=false;
    private Preference tips_screen, check_now ;
    private  ProgressDialog dialog ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "----------------------onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ota_preference);
        
        check_now = (Preference) findPreference("ota_checknow");
        share_preference=getSharedPreferences("setting_ota", 0);
        {
        autoupdate_screen=(CheckBoxPreference)findPreference(AUTOUPDATE_TAG);
        autoupdate_screen.setPersistent(false);
        autoupdate_screen.setChecked(share_preference.getBoolean("autoupdate", true));
        }
        {
        	tips_screen=(Preference)findPreference(TIPS_TAG);
        }
        if(autoupdate_screen.isChecked())
        {
        	tips_screen.setSummary(R.string.ota_task_running_tips_start_content);
        }else
        {
        	tips_screen.setSummary(R.string.ota_task_running_tips_stop_content);
        }
        IntentFilter filter=new IntentFilter();
		filter.addAction(Utils.CHECK_END);
		registerReceiver(mIntentReceiver, filter);
    }
    
    
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "receiver --------------------------------"+action);
			if(action.equals(Utils.CHECK_END))
			{
				if(dialog != null)
				{
					dialog.dismiss();
				}
			}
		}
    };
    
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mIntentReceiver);
		super.onDestroy();
	}



	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		Log.d(TAG, "----------------onPreferenceTreeClick");
		// TODO Auto-generated method stub
		if(preference==autoupdate_screen)
		{

		}
		if(preference == check_now)
		{
	         Intent intent = new Intent(Utils.CHECK_NOW);
			 sendBroadcast(intent);
		}
		return false;
	}
	
	private static final int RUNNING_DAILOG=56;


}
