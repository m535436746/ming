package com.telstar.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class SettingActivity extends Activity {

    RelativeLayout projector_setting;
    RelativeLayout  about_system ;
    RelativeLayout  more_setting ;
    RelativeLayout  system_update ;
    //RelativeLayout  file_manage ;
    RelativeLayout  network_manage ;
    RelativeLayout  miracast_setting ;
    RelativeLayout  media_center ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relativelayoutasgroup);

        projector_setting = (RelativeLayout)findViewById(R.id.projector_setting);
        projector_setting.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                        keyCode == KeyEvent.KEYCODE_ENTER )) {

                    Intent intent = new Intent();
                    intent.setClassName("com.projector.settingsmbox", "com.projector.settingsmbox.SettingsMboxActivity");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        projector_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("bill", "-----click ----");
                Intent intent = new Intent();
                intent.setClassName("com.projector.settingsmbox", "com.projector.settingsmbox.SettingsMboxActivity");
                startActivity(intent);
            }
        });



        network_manage = (RelativeLayout) findViewById(R.id.network_setting);
        network_manage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                        keyCode == KeyEvent.KEYCODE_ENTER )) {

                    Intent intent = new Intent();
                    intent.setClassName("com.telstar.settingsmbox", "com.telstar.settingsmbox.SettingsMboxActivity");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        network_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("bill", "-----click ----");
                Intent intent = new Intent();
                intent.setClassName("com.telstar.settingsmbox", "com.telstar.settingsmbox.SettingsMboxActivity");
                startActivity(intent);
            }
        });

        about_system = (RelativeLayout) findViewById(R.id.about_system);
        about_system.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                        keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), AboutSystem.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        about_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AboutSystem.class);
                startActivity(intent);
            }
        });

        more_setting = (RelativeLayout) findViewById(R.id.more_setting);
        more_setting.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                        keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Intent intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.Settings");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        more_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.Settings");
                startActivity(intent);
            }
        });


        system_update = (RelativeLayout)findViewById(R.id.system_update);
        system_update.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                        keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Intent intent = new Intent();
                    intent.setClassName("com.amlapp.update.otaupgrade", "com.amlapp.update.otaupgrade.MainActivity");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        system_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.amlapp.update.otaupgrade", "com.amlapp.update.otaupgrade.MainActivity");
                startActivity(intent);
            }
        });


	/*	file_manage = (RelativeLayout) parent.findViewById(R.id.file_manage);
		file_manage.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER)) {
					Intent intent = new Intent();
					intent.setClassName("com.fb.FileBrower", "com.fb.FileBrower.FileBrower");
					getActivity().startActivity(intent);
					return true;
				}
				return false;
			}
		});
		file_manage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName("com.fb.FileBrower", "com.fb.FileBrower.FileBrower");
				getActivity().startActivity(intent);
			}
		});*/


        miracast_setting = (RelativeLayout) findViewById(R.id.miracast_setting);
        miracast_setting.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                        keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Intent intent = new Intent();
                    intent.setClassName("com.amlogic.miracast", "com.amlogic.miracast.WiFiDirectMainActivity");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        miracast_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.amlogic.miracast", "com.amlogic.miracast.WiFiDirectMainActivity");
                startActivity(intent);
            }
        });


        media_center = (RelativeLayout)findViewById(R.id.media_center);
        media_center.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                        keyCode == KeyEvent.KEYCODE_ENTER )) {
                    Intent intent = new Intent();
                    intent.setClassName("com.amlogic.mediacenter", "com.amlogic.mediacenter.MediaCenterActivity");
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        media_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.amlogic.mediacenter", "com.amlogic.mediacenter.MediaCenterActivity");
                startActivity(intent);
            }
        });


    }
}
