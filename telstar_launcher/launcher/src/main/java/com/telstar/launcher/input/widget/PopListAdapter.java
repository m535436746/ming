package com.telstar.launcher.input.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.telstar.launcher.R;
import com.telstar.launcher.input.bean.InputBase;

import java.util.List;



public class PopListAdapter extends BaseAdapter {

	private static final String TAG = PopListAdapter.class.getSimpleName();
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<InputBase> mInputs;

	private class ViewHolder {
		ImageView mInputSourceState;
		TextView mInputSourceName;
	}

	public PopListAdapter(Context context, List<InputBase> inputs) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mInputs = inputs;
	}

	public void setDatas(List<InputBase> inputs) {
		mInputs = inputs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mInputs != null ? mInputs.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mInputs != null ? mInputs.get(position) : 0;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (position < 0 || mInputs.size() <= 0) {
			return null;
		}
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.menu_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mInputSourceState = (ImageView) convertView.findViewById(R.id.item_iv_status);
			viewHolder.mInputSourceName = (TextView) convertView.findViewById(R.id.item_tx_input);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		InputBase input = mInputs.get(position);
		
		viewHolder.mInputSourceName.setText(input.getName());
		// 检测HDMI线是否已经接入  接入则显示为绿色
		if(input.getType() == InputBase.InputType.HDMI && input.getConnectState() == InputBase.STATE_CONNECT) {
			viewHolder.mInputSourceName.setTextColor(Color.GREEN);
		} else {
			viewHolder.mInputSourceName.setTextColor(mContext.getResources().getColor(R.color.snow));
		}

		if(input.getType() == InputBase.InputType.MIRACAST) {
			viewHolder.mInputSourceName.setTextSize(25.0f);
		}
		return convertView;
	}

}