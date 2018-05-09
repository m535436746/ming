package com.telstar.launcher.input.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListView;

public class PopListView extends ListView implements OnKeyListener{

	private static final String TAG = PopListView.class.getSimpleName();

	public PopListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PopListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PopListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	private void cleanAnimation() {
		int count = getChildCount();
		int i;
		for (i = 0; i < count; i++) {
			View view = getChildAt(i);
			view.clearAnimation();
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
