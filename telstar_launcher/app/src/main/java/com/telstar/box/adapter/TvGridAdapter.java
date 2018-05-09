package com.telstar.box.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telstar.box.R;
import com.telstar.box.entity.AppInfo;

import java.util.List;

import reco.frame.tv.view.TvImageView;
import reco.frame.tv.view.component.TvBaseAdapter;

public class TvGridAdapter extends TvBaseAdapter {

	
	private List<AppInfo> appList;
	private LayoutInflater inflater;
	private  Context mContext ;
	
	public TvGridAdapter(Context context,List<AppInfo> appList){
		this.inflater= LayoutInflater.from(context);
		this.appList=appList;
		this.mContext = context;
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
			contentView=inflater.inflate(R.layout.item_grid, null);
			holder=new ViewHolder();
			holder.tv_title=(TextView) contentView.findViewById(R.id.tv_title);
			//holder.tv_title.setAlpha(0.5f);
			holder.tiv_icon=(TvImageView) contentView.findViewById(R.id.tiv_icon);

			holder.tv_title_bg = (TextView) contentView.findViewById(R.id.tv_title_bg);
			holder.tv_title_bg.setAlpha(0.2f);
			contentView.setTag(holder);
			//Log.e("bill", "----------------" + position);
			//contentView.setBackgroundColor(parseItemBackground(position));
			GradientDrawable drawable1 = new GradientDrawable();
			drawable1.setColor(parseItemBackground(position));
			drawable1.setCornerRadius(10);
			contentView.setBackground(drawable1);

		}else{
			holder=(ViewHolder) contentView.getTag();
		}
		
		final AppInfo app=appList.get(position);
		holder.tv_title.setText(app.Title);
		holder.tiv_icon.setBackground(app.icon);
		//holder.tiv_icon.configImageUrl(app.imageUrl);
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("bill","-----click----");
				if ( app.intent != null){
					mContext.startActivity(app.intent);
				}
			}
		});
		contentView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.e("bill", "-----touch down----");
					v.setAlpha(0.7f);
					v.setScaleX(1.1f);
					v.setScaleY(1.1f);
				}
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					Log.e("bill", "-----touch up----");
					v.setAlpha(1.0f);
					v.setScaleX(1.0f);
					v.setScaleY(1.0f);

					if (app.intent != null) {
						mContext.startActivity(app.intent);
					}
				}

				return false;
			}
		});
		contentView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//Log.e("bill", "-----on key listener----");
				if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
						keyCode == KeyEvent.KEYCODE_ENTER )) {

					if (app.intent != null) {
						mContext.startActivity(app.intent);
					}
					return true;
				}
				return false;
			}
		});
		
		return contentView;
	}
	
	public void addItem(AppInfo item) {
		appList.add(item);
	}

	public void clear() {
		appList.clear();
	}

	public void flush(List<AppInfo> appListNew) {
		appList = appListNew;
	}

	
	static class ViewHolder{
		TextView tv_title;
		TvImageView tiv_icon;
		TextView tv_title_bg ;
	}


	private int  parseItemBackground(int num){
		switch (num % 11){
			case 0:
				return Color.rgb(75,168,189);
			case 1:
				return Color.rgb(248,184,58);
			case 2:
				return Color.rgb(208,81,147);
			case 3:
				return Color.rgb(122,188,79);
			case 4:
				return Color.rgb(24,172,143);
			case 5:
				return Color.rgb(51,149,215);
			case 6:
				return Color.rgb(203,63,48);
			case 7:
				return Color.rgb(46,202,112);
			case 8:
				return Color.rgb(153,86,181);
			case 9:
				return Color.rgb(222,109,20);
			case 10:
				return Color.rgb(24,177,147);
			default:
				return Color.rgb(24,177,147);
		}
	}
}
