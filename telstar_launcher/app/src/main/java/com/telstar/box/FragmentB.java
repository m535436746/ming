package com.telstar.box;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class FragmentB extends Fragment {


	RelativeLayout  projector_setting;
	RelativeLayout  about_system ;
	RelativeLayout  more_setting ;
	RelativeLayout  system_update ;
	//RelativeLayout  file_manage ;
	RelativeLayout  network_manage ;
	RelativeLayout  miracast_setting ;
	RelativeLayout  media_center ;


	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.activity_relativelayoutasgroup, container, false);


		projector_setting = (RelativeLayout) parent.findViewById(R.id.projector_setting);
		projector_setting.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER )) {

					Intent intent = new Intent();
					intent.setClassName("com.projector.settingsmbox", "com.projector.settingsmbox.SettingsMboxActivity");
					getActivity().startActivity(intent);
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
				getActivity().startActivity(intent);
			}
		});



		network_manage = (RelativeLayout) parent.findViewById(R.id.network_setting);
		network_manage.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER )) {

					Intent intent = new Intent();
					intent.setClassName("com.telstar.settingsmbox", "com.telstar.settingsmbox.SettingsMboxActivity");
					getActivity().startActivity(intent);
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
				getActivity().startActivity(intent);
			}
		});

		about_system = (RelativeLayout) parent.findViewById(R.id.about_system);
		about_system.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER)) {

					Intent intent = new Intent();
					intent.setClass(getContext(), AboutSystem.class);
					getActivity().startActivity(intent);
					return true;
				}
				return false;
			}
		});

		about_system.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getContext(), AboutSystem.class);
				getActivity().startActivity(intent);
			}
		});

		more_setting = (RelativeLayout) parent.findViewById(R.id.more_setting);
		more_setting.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER)) {
					Intent intent = new Intent();
					intent.setClassName("com.android.settings", "com.android.settings.Settings");
					getActivity().startActivity(intent);
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
				getActivity().startActivity(intent);
			}
		});


		system_update = (RelativeLayout) parent.findViewById(R.id.system_update);
		system_update.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER)) {
					Intent intent = new Intent();
					intent.setClassName("com.amlapp.update.otaupgrade", "com.amlapp.update.otaupgrade.MainActivity");
					getActivity().startActivity(intent);
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
				getActivity().startActivity(intent);
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


		miracast_setting = (RelativeLayout) parent.findViewById(R.id.miracast_setting);
		miracast_setting.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER)) {
					Intent intent = new Intent();
					intent.setClassName("com.amlogic.miracast", "com.amlogic.miracast.WiFiDirectMainActivity");
					getActivity().startActivity(intent);
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
				getActivity().startActivity(intent);
			}
		});


		media_center = (RelativeLayout) parent.findViewById(R.id.media_center);
		media_center.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER )) {
					Intent intent = new Intent();
					intent.setClassName("com.amlogic.mediacenter", "com.amlogic.mediacenter.MediaCenterActivity");
					getActivity().startActivity(intent);
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
				getActivity().startActivity(intent);
			}
		});


		return parent;

	}
	

	
}
