package com.telstar.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telstar.launcher.adapter.TvGridAdapter;
import com.telstar.launcher.entity.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import reco.frame.tv.view.TvGridView;

public class FragmentA extends Fragment {
	public TvGridView tgv_imagelist;
	public TvGridAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View parent = inflater.inflate(R.layout.frag_a, container, false);
		tgv_imagelist = (TvGridView) parent.findViewById(R.id.tgv_imagelist);
		
		/*List<AppInfo> appList = new ArrayList<AppInfo>();
		for (int i = 0; i < 45; i++) {
			AppInfo app = new AppInfo();
			app.title = "全家盒框架" + i;
			appList.add(app);

		}*/

		//LoadAll();
		return parent;
	}


	public  void LoadAll()
	{
		adapter = new TvGridAdapter(getActivity(), LoadAllApp());
		//tgv_imagelist.removeAllViews();
		tgv_imagelist.setAdapter(adapter);
		//tgv_imagelist.initGridView();
	}

	public List<AppInfo> LoadAllApp()
	{
		List<AppInfo> applist = new ArrayList<AppInfo>();
		PackageManager manager = getContext().getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		if(apps != null)
		{
			final int count = apps.size();
			//Log.e("bill"," count :  "+ count);
		    for (int i = 0; i < count; i++) {
			    AppInfo application = new AppInfo();
			    ResolveInfo info = apps.get(i);
				application.Title = info.loadLabel(manager);
				application.setActivity(new ComponentName(
								info.activityInfo.applicationInfo.packageName,
								info.activityInfo.name),
						Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				application.icon = info.activityInfo.loadIcon(manager);
				//application.icon = getResources().getDrawable(R.drawable.icon_reco);
				//Log.e("bill", info.activityInfo.name);
				if( !  ("com.telstar.box".equals(info.activityInfo.applicationInfo.packageName)
						|| "com.google.android.gms.app.settings.GoogleSettingsActivity".equals(info.activityInfo.name)
						|| "com.amlapp.update.otaupgrade.MainActivity".equals(info.activityInfo.name)
						|| "com.mbx.settingsmbox.SettingsMboxActivity".equals(info.activityInfo.name)
						|| "com.projector.settingsmbox.SettingsMboxActivity".equals(info.activityInfo.name)
						|| "com.telstar.settingsmbox.SettingsMboxActivity".equals(info.activityInfo.name))   )
				    {
				      	applist.add(application);
			    	}

		  }
		}
		return  applist;

	}

}
