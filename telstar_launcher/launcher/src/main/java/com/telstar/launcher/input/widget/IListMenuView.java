package com.telstar.launcher.input.widget;

import android.view.View;
import android.widget.AdapterView;

import com.telstar.launcher.input.bean.InputBase;

import java.util.List;


public interface IListMenuView {
	/**
	 * 菜单View回调事件.
	 */
	public interface OnMenuListener {
		public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id);

		public void onMenuItemSelected(AdapterView<?> parent, View view, int position, long id);
	}

	/**
	 * 设置Menu的数据
	 * @param datas
	 * @return
	 */
	public IListMenuView setMenuData(List<InputBase> datas);
	/**
	 * 设置菜单view事件.
	 */
	public IListMenuView setOnMenuListener(OnMenuListener cb);
	
	/**
	 * 通知ListView的数据改变
	 */
	public void notifyDataSetChanged();
}
