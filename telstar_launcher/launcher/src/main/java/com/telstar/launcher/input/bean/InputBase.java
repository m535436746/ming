package com.telstar.launcher.input.bean;

import java.io.Serializable;

public abstract class InputBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7018231762060563726L;

	public enum InputType {
		MIRACAST, DLAN, HDMI
	}

	public static final int STATE_NONE = 0;
	public static final int STATE_DISCONNECT = 1;
	public static final int STATE_CONNECT = 2;

	private InputType mType;
	private int mConnectState;
	private String mName;
	
	public InputBase(String name, InputType type, int state) {
		// TODO Auto-generated constructor stub
		mType = type;
		mConnectState = state;
		mName = name;
	}

	public int getConnectState() {
		return mConnectState;
	}

	public void setConnectState(int mConnectState) {
		this.mConnectState = mConnectState;
	}

	public InputType getType() {
		return mType;
	}

	public void setType(InputType mType) {
		this.mType = mType;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

}
