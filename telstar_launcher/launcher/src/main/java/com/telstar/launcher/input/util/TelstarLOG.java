package com.telstar.launcher.input.util;

import android.util.Log;

public class TelstarLOG {
	private static final boolean IS_DEBUG = true;
	private static final String SEARCH_KEYWORK = "Telstar--- ";

	public static void D(String tag, String msg) {
		if (IS_DEBUG)
			Log.d(tag, SEARCH_KEYWORK + msg);
	}
	
	public static void E(String tag, String msg) {
		if (IS_DEBUG)
			Log.e(tag, SEARCH_KEYWORK + msg);
	}
	
}
