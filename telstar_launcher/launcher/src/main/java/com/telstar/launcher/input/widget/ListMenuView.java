package com.telstar.launcher.input.widget;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.telstar.launcher.MyApplication;
import com.telstar.launcher.R;
import com.telstar.launcher.input.bean.InputBase;
import com.telstar.launcher.input.util.AnimationFactory;
import com.telstar.launcher.input.util.TelstarLOG;

import java.util.List;


public class ListMenuView implements IListMenuView, OnItemSelectedListener, OnItemClickListener, OnKeyListener {
	
	private static final String TAG = ListMenuView.class.getSimpleName();

	private Activity mActivity;
	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private LayoutParams mWmParams;
	private MyApplication mApplication;
	
	private OnMenuListener mListMenuListener;
	
//	private RelativeLayout mMenuRootLayout;
	private FrameLayout mFrameLayout;
	private PopListView mInputList;
	private ImageView mBackImageView;
	private TextView mInfoTextView;
	private View mLastSelectedView;
	private Handler mHandler;
	
	// 输入源数据
	private PopListAdapter mAdapter = null;
	private List<InputBase> mInputSources = null;
	
	private Boolean isRemoveMenuView = true;
	private boolean isInAnimation = false;
	
	public ListMenuView(Activity activity) {
		// TODO Auto-generated constructor stub
		mActivity = activity;
		if(mActivity == null)
			throw new AssertionError("Context can't be null");
		mInflater = mActivity.getLayoutInflater();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) mActivity.getWindowManager();
		mHandler = new Handler();
		
		mApplication = (MyApplication) activity.getApplication();
		initMenuWindow();
	}
	
	private void initMenuWindow() {
		mWmParams = new LayoutParams();
		// 设置window type
		mWmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		mWmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		// FLAG_NOT_TOUCH_MODAL 不阻塞事件传递到后面的窗口
		mWmParams.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM; // |
																			// WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
																			// //LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗显示的停靠位置为右侧置顶
		mWmParams.gravity = Gravity.RIGHT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
//		mWmParams.x = 0;
//		mWmParams.y = 0;
		// 设置悬浮窗口长宽数据
		mWmParams.width = 420;
		mWmParams.height = LayoutParams.MATCH_PARENT;
		// 获取浮动窗口视图所在布局
		// 添加mFloatLayout
//		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mInputList.getLayoutParams();
//		params.addRule(RelativeLayout.CENTER_VERTICAL, 1);
//		mInputList.setLayoutParams(params);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400, 400);
//		params.addRule(RelativeLayout.CENTER_VERTICAL, 1);
//		mMenuRootLayout.addView(mInputList, params);
//		mInputSources = Arrays.asList(mActivity.getResources().getStringArray(R.array.input_sources));
//		
//		PopListAdapter mAdapter = new PopListAdapter(mActivity, mInputSources);
//		mInputList.setAdapter(mAdapter);
//		mInputList.setOnKeyListener(this);
//		mInputList.setOnItemClickListener(this);
//		mInputList.setOnItemSelectedListener(this);
//		mInputList.requestFocus();
//		addMenuView();
	}
	
	public void toggleMenu() {
		if(isInAnimation)
			return;
		synchronized (isRemoveMenuView) {
			if(isRemoveMenuView) {
				addMenuView();
			} else {
				removeMenuLyaout();
			}
		}
	}
	
	public void closeMenu() {
		synchronized (isRemoveMenuView) {
			if(!isRemoveMenuView)
				removeMenuLyaout();
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		int action = event.getAction();
		if (action == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_MENU:
				toggleMenu();
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT: // 防止菜单往右边跑到其它地方.
			case KeyEvent.KEYCODE_DPAD_UP: // 防止菜单往上面跑到其它地方.
			case KeyEvent.KEYCODE_DPAD_DOWN: // 防止菜单往下面跑到其它地方.
				v.onKeyDown(keyCode, event);
				return true;
			default:
				break;
			}
		}
		return false;
	}
	
	/**
	 * 移除悬浮窗口的布局.
	 */
	public void removeMenuLyaout() {
//		if (mMenuRootLayout != null) {
//			synchronized (isRemoveMenuView) {
//				mMenuRootLayout.removeAllViews();
//				mWindowManager.removeView(mMenuRootLayout);
//				mMenuRootLayout = null;
//				isRemoveMenuView = true;
//			}
//		}
		if(isInAnimation)
			return;
		Animation animation = AnimationFactory.getLtRAnimation();
		Animation animation2 = AnimationFactory.getLtRAnimation();
		animation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				isInAnimation = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				TelstarLOG.D(TAG, " onAnimationEnd ");
				if(mFrameLayout != null) {
					synchronized (isRemoveMenuView) {
						mFrameLayout.removeAllViews();
						mBackImageView = null;
						mInputList = null;
						mWindowManager.removeView(mFrameLayout);
						mFrameLayout = null;
						isRemoveMenuView = true;
						isInAnimation = false;
					}
				}
			}
		});
		if(mInputList != null)
			mInputList.startAnimation(animation);
		if(mBackImageView != null)
			mBackImageView.startAnimation(animation2);

	}
	
	/**
	 * 给MenuView添加ListView及数据
	 */
	public void addMenuView() {
		synchronized (isRemoveMenuView) {
			// 先添加Layout
//			mMenuRootLayout = (RelativeLayout) mInflater.inflate(R.layout.menu_fragment, null);
			mFrameLayout = (FrameLayout) mInflater.inflate(R.layout.menu_fragment, null);
//			mWindowManager.addView(mMenuRootLayout, mWmParams);
			mWindowManager.addView(mFrameLayout, mWmParams);
			isRemoveMenuView = false;
			mBackImageView = new ImageView(mActivity);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
			mBackImageView.setBackgroundResource(R.mipmap.menu_bg);
			mFrameLayout.addView(mBackImageView, params);

			mInputList = (PopListView) mInflater.inflate(R.layout.menu_list, null);
			params = new FrameLayout.LayoutParams(440, FrameLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			mFrameLayout.addView(mInputList, params);

			mInfoTextView = (TextView)mInflater.inflate(R.layout.info_textview, null);
			params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			if(mApplication.getHdmiNum() == 1) {
				params.bottomMargin = 120;
			} else {
				params.bottomMargin = 60;
			}
			params.rightMargin = 20;
			params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			mFrameLayout.addView(mInfoTextView, params);

			mFrameLayout.setLayoutAnimation(AnimationFactory.loadAnimation());
//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400,
//					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
//			params.addRule(RelativeLayout.CENTER_VERTICAL, 1);
//			mMenuRootLayout.addView(mInputList, params);
			// mMenuRootLayout.setBackgroundResource(R.drawable.shadow);
//			mMenuRootLayout.setLayoutAnimation(AnimationFactory.loadAnimation());

//			mInputSources = Arrays.asList(mActivity.getResources().getStringArray(R.array.input_sources));
			mInputSources = mApplication.getInputSources();

			mAdapter = new PopListAdapter(mActivity, mInputSources);
			mInputList.setAdapter(mAdapter);
			/**
			 * 在这里还需要对默认选项进行选中（如果HDMI数为3个)
			 * 第一次，默认是HDMI1
			 * 其余的是选中是从Application中读取， 并写入Shared Preferences保存，以便下次再用
			 */
			if(mApplication.getHdmiNum() == 3) {
				int lastSelectNum = mApplication.getLastSelectedInput();
				int finalSelectNum = -1;
				// 获取所有的HDMI输入
				List<InputBase> hdmiInputs = mApplication.getHdmiInputs();
				for(int i=0; i<hdmiInputs.size(); i++) {
					InputBase input = hdmiInputs.get(i);
					if(input.getConnectState() == InputBase.STATE_CONNECT) {
						if(i == lastSelectNum) {
							// 上一次选中的有 CONNECT 优先选择并退出循环
							finalSelectNum = i;
							break;
						} else if(finalSelectNum > -1) {
							// 在前面已经有一个更小的选择了 不作处理
							continue;
						} else {
							finalSelectNum = i;
						}
					}
				}
				// 最后都没有连接的， 默认上一次选中的
				if(finalSelectNum == -1)
					finalSelectNum = lastSelectNum;
				mInputList.setSelection(finalSelectNum);
			}
			mInputList.setOnKeyListener(this);
			mInputList.setOnItemClickListener(this);
			mInputList.setOnItemSelectedListener(this);
			mInputList.requestFocus();
		}
		// View selectView = mInputList.getChildAt(0);
		// selectView.setAnimation(AnimationFactory.getZoomInAnimationSet());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(mInfoTextView != null)
			mInfoTextView.setVisibility(View.GONE);
		if(mListMenuListener != null) {
			mListMenuListener.onMenuItemClick(arg0, arg1, arg2, arg3);
			return;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(mInfoTextView != null) {
			mInfoTextView.setVisibility(View.VISIBLE);
			InputBase input = mInputSources.get(arg2);
			if(input != null) {
				if (input.getType() == InputBase.InputType.HDMI)
					mInfoTextView.setText("");
				else if(input.getType() == InputBase.InputType.MIRACAST)
					mInfoTextView.setText(mActivity.getString(R.string.info_mirror));
				else if(input.getType() == InputBase.InputType.DLAN)
					if("Brookstone".equalsIgnoreCase(Build.BRAND) || "Miroir".equalsIgnoreCase(Build.BRAND)) {
						mInfoTextView.setText(Build.BRAND + mActivity.getString(R.string.info_dlna));
					}
					else
					{
						mInfoTextView.setText("");
					}
			}
		}
		if(mListMenuListener != null) {
			mListMenuListener.onMenuItemSelected(arg0, arg1, arg2, arg3);
			return;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public IListMenuView setMenuData(List<InputBase> datas) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IListMenuView setOnMenuListener(OnMenuListener cb) {
		// TODO Auto-generated method stub
		mListMenuListener = cb;
		return this;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		if(mAdapter != null) {
			mAdapter.setDatas(mApplication.getInputSources());
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new PopListAdapter(mActivity, mApplication.getInputSources());
			if(mInputList != null) {
				mInputList.setAdapter(mAdapter);
				mInputList.postInvalidate();
			}
		}
	}

}
