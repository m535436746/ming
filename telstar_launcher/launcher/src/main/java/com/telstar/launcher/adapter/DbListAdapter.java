package com.telstar.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.telstar.launcher.R;
import com.telstar.launcher.entity.AppInfo;

public class DbListAdapter extends BaseAdapter {

	
	private List<AppInfo> appList;
	private LayoutInflater inflater;
	
	public DbListAdapter(Context context,List<AppInfo> appList){
		this.inflater= LayoutInflater.from(context);
		this.appList=appList;
	}
	
	@Override
	public int getCount() {
		return appList.size();
	}

	@Override
	public Object getItem(int position) {
		return appList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View contentView, ViewGroup parent) {
		ViewHolder holder=null;
		if (contentView==null) {
			contentView=inflater.inflate(R.layout.item_db, null);
			holder=new ViewHolder();
			holder.tv_title=(TextView) contentView.findViewById(R.id.tv_title);
			holder.tv_imageUrl=(TextView) contentView.findViewById(R.id.tv_imageUrl);
			contentView.setTag(holder);
		}else{
			holder=(ViewHolder) contentView.getTag();
		}
		AppInfo app=appList.get(position);
		
		holder.tv_title.setText("id="+app.id+"--"+app.title+"");
		holder.tv_imageUrl.setText(app.title);
		
		return contentView;
	}

	
	static class ViewHolder{
		TextView tv_title;
		TextView tv_imageUrl;
	}
}
