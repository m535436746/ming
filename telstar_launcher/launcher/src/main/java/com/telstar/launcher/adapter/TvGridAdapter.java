package com.telstar.launcher.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telstar.launcher.R;
import com.telstar.launcher.entity.AppInfo;

import java.util.List;

import reco.frame.tv.TvHttp;
import reco.frame.tv.view.TvImageView;
import reco.frame.tv.view.ViewHolder;
import reco.frame.tv.view.component.TvBaseAdapter;

public class TvGridAdapter extends TvBaseAdapter {

	
	private List<AppInfo> appList;
	private LayoutInflater inflater;
	private  Context mContext ;
    private TvHttp tvHttp;
    private static final String  AptoideApkUrl = "http://ws2.aptoide.com/api/7/getAppMeta/apk_md5sum/" ;
	
	public TvGridAdapter(Context context,List<AppInfo> appList){
		this.inflater= LayoutInflater.from(context);
		this.appList=appList;
		this.mContext = context;
        tvHttp = new TvHttp(context);
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

            holder.download_img = (ImageView) contentView.findViewById(R.id.tiv_download);

			holder.tv_title=(TextView) contentView.findViewById(R.id.tv_title);
			holder.tv_title.setPadding(10,5,10,5);
			//holder.tv_title.setAlpha(0.01f);
			//holder.tv_title.setVisibility(View.GONE);
			//holder.tv_title.setTag("title");
			//holder.tv_title.setAlpha(0.5f);
			holder.tiv_icon=(TvImageView) contentView.findViewById(R.id.tiv_icon);

			holder.tv_title_bg = (TextView) contentView.findViewById(R.id.tv_title_bg);
			//holder.tv_title_bg.setAlpha(0.2f);
			holder.tv_title_bg.setAlpha(0.01f);
			//holder.tv_title_bg.setVisibility(View.GONE);
			//holder.tv_title_bg.setTag("titile_bg");
			contentView.setTag(holder);
			//Log.e("bill", "----------------" + position);
			//contentView.setBackgroundColor(parseItemBackground(position));
			GradientDrawable drawable1 = new GradientDrawable();
			//drawable1.setColor(parseItemBackground(position));
			drawable1.setCornerRadius(10);
			contentView.setBackground(drawable1);

		}else{
			holder=(ViewHolder) contentView.getTag();
		}
		
		final AppInfo app=appList.get(position);
		holder.tv_title.setText(app.Title);
		holder.tiv_icon.setBackground(app.icon);
        if(app.isFromNetwork())
        {
            holder.download_img.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.download_img.setVisibility(View.GONE);
        }
		//holder.tiv_icon.configImageUrl(app.imageUrl);
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("bill","-----click----");
				if ( app.intent != null){
					mContext.startActivity(app.intent);
				}

                if(app.isFromNetwork())
                {
                    // tvHttp.download()
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AptoideApkUrl+app.apk_md5sum));
                    intent.setPackage("cm.aptoidetv.pt.miroir_apps");
                    mContext.startActivity(intent);
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

                    if(app.isFromNetwork())
                    {
						Intent intent1 = mContext.getPackageManager().getLaunchIntentForPackage("cm.aptoidetv.pt");
						Uri uri = Uri.parse(AptoideApkUrl+app.apk_md5sum);
						intent1.setData(uri);
						mContext.startActivity(intent1);
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

                    if(app.isFromNetwork())
                    {
                        // tvHttp.download()
                       // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse());
                       // intent.setPackage("cm.aptoidetv.pt.miroir_apps");

                        Intent intent1 = mContext.getPackageManager().getLaunchIntentForPackage("cm.aptoidetv.pt.miroir_apps");
                        Uri uri = Uri.parse(AptoideApkUrl+app.apk_md5sum);
                        intent1.setData(uri);
                        mContext.startActivity(intent1);
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

	
/*	static class ViewHolder{
		TextView tv_title;
		TvImageView tiv_icon;
		TextView tv_title_bg ;
	}*/


	private int  parseItemBackground1(int num){
		switch (num % 12){
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
				return Color.rgb(220,174,124);
			case 7:
				return Color.rgb(80,147,73);
			case 8:
				return Color.rgb(153,86,181);
			case 9:
				return Color.rgb(222,109,20);
			case 10:
				return Color.rgb(24,177,147);
			case 11:
				return Color.rgb(203,63,48);
			default:
				return Color.rgb(24,177,147);
		}
	}

    private int  parseItemBackground(int num){
        switch (num % 12){
            case 0:
                return Color.rgb(6,74,176);
            case 1:
                return Color.rgb(3,119,30);
            case 2:
                return Color.rgb(84,3,91);
            case 3:
                return Color.rgb(101,119,3);
            case 4:
                return Color.rgb(2,106,115);
            case 5:
                return Color.rgb(91,2,4);
            case 6:
                return Color.rgb(2,142,81);
            case 7:
                return Color.rgb(124,5,67);
            case 8:
                return Color.rgb(58,73,5);
            case 9:
                return Color.rgb(222,109,20);
            case 10:
                return Color.rgb(2,106,85);
            case 11:
                return Color.rgb(203,63,48);
            default:
                return Color.rgb(24,177,147);
        }
    }
}
