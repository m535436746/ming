package com.telstar.launcher.input.receiver;

import android.app.SystemWriteManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;

import com.telstar.launcher.MyApplication;
import com.telstar.launcher.input.bean.InputBase;
import com.telstar.launcher.input.util.TelstarLOG;

import java.io.File;
import java.util.List;


public class PortKeyReceiver extends BroadcastReceiver {

    private static final String TAG = "PortKeyReceiver";

    private Context mContext;
    public static final String SII9233A_PATH = "/sys/class/sii9233a/enable";
    public static final String SII9293_PATH = "/sys/class/sii9293/enable";
    public static final int TVIN_PORT_HDMI0 = 0x00004000;
    public static final int TVIN_PORT_HDMI1 = 0x00004001;
    public static final int TVIN_PORT_HDMI2 = 0x00004002;
    public static final int TVIN_PORT_HDMI3 = 0x00004003;
    public static final int TVIN_PORT_HDMI4 = 0x00004004;
    public static final int TVIN_PORT_HDMI5 = 0x00004005;
    public static final int TVIN_PORT_HDMI6 = 0x00004006;
    public static final int TVIN_PORT_HDMI7 = 0x00004007;

    // According to SourceType defined in frameworks/base/services/jni/com_android_server_OverlayViewService.cpp
    public static final int INPUT_SOURCE_HDMIIN_SII9293 = 0;
    public static final int INPUT_SOURCE_HDMIIN_SII9233 = 1;
    public static final int INPUT_SOURCE_HDMIIN_INTERNAL = 2;
    public static final int INPUT_SOURCE_VGA = 3;
    public static final int INPUT_SOURCE_CVBS = 4;

    public static final int HDMIIN_SII9293_PORTS_NUM = 1;
    public static final int HDMIIN_SII9233_PORTS_NUM = 4;
    public static final int HDMIIN_INTERNAL_PORTS_NUM = 2;
    public static final int VGA_PORTS_NUM = 0;
    public static final int CVBS_PORTS_NUM = 0;

    public static final String PORTKEY_ACTION = "com.amlogic.hdmiin.portkey";
    public static final int KEYCODE_PORT0 = KeyEvent.KEYCODE_1;
    public static final int KEYCODE_PORT1 = KeyEvent.KEYCODE_2;
    public static final int KEYCODE_PORT2 = KeyEvent.KEYCODE_3;
    public static final int KEYCODE_PORT3 = KeyEvent.KEYCODE_4;
    public static final int[] KEYCODE_PORTS = {KEYCODE_PORT0, KEYCODE_PORT1, KEYCODE_PORT2, KEYCODE_PORT3};
    public static final String PORT = "port";
    private static final int DEFAULT_PORT = 0;
    private int mHdmiInSourceType = -1;
    private SystemWriteManager mSw = null;

    private static final String UEVENT_ACTION = "com.amlogic.hdmiin.uevent";
    private MyApplication mApplication;

    private void setInputSource() {
        if (new File(SII9293_PATH).exists()) {
            mHdmiInSourceType = INPUT_SOURCE_HDMIIN_SII9293;
        } else if (new File(SII9233A_PATH).exists()) {
            mHdmiInSourceType = INPUT_SOURCE_HDMIIN_SII9233;
        } else {
            mHdmiInSourceType = INPUT_SOURCE_HDMIIN_INTERNAL;
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        mApplication = (MyApplication) context.getApplicationContext();
        mContext = context;
        String action = intent.getAction();
        TelstarLOG.D(TAG, "context: " + context + ", intent: " + intent + ", action: " + action);
        int keyCodePort = -1;
        if (mSw == null)
            mSw = (SystemWriteManager) context.getSystemService("system_write");
        final boolean portKey = mSw.getPropertyBoolean("mbx.hdmiin.portkey", true);  // true
        if (intent != null) {
            if (action.equals(UEVENT_ACTION)) {

                String uevent = intent.getStringExtra("uevent");
                int ueventNum = Integer.parseInt(uevent);
                // 获取输入源
                List<InputBase> mInputBases = mApplication.getInputSources();
                boolean plugged = (ueventNum > 0) ? true : false;
                TelstarLOG.D(TAG, "Get Uevent Num: " + ueventNum + " isPlug: " + plugged);    // 1
                int port = 0;
                // TV IN 的端口
                int selectPortIndex = -1;
                switch (Math.abs(ueventNum)) {
                    case 1:
                        // 接收到HDMI1接口
                        port = TVIN_PORT_HDMI0;
                        selectPortIndex = 0;
                        break;
                    case 2:
                        port = TVIN_PORT_HDMI1;
                        if("telstarU5".equals(Build.MODEL)) {
                            selectPortIndex = 2;
                        } else {
                            selectPortIndex = 1;
                        }
                        break;
                    case 4:
                        port = TVIN_PORT_HDMI2;
                        if("telstarU5".equals(Build.MODEL)) {
                            selectPortIndex = 1;
                        } else {
                            selectPortIndex = 2;
                        }
                        break;
                    default:
                        break;
                }

                if (port == 0)
                    return;

                setInputSource();
                // 将InputSources 中修改连接状态
                if (mInputBases != null && mInputBases.size() > 0 && selectPortIndex >= 0) {
                    if(plugged) {
                        mInputBases.get(selectPortIndex).setConnectState(InputBase.STATE_CONNECT);
                        context.sendBroadcast(new Intent(MyApplication.ACTION_HDMI_IN));
                    } else {
                        mInputBases.get(selectPortIndex).setConnectState(InputBase.STATE_DISCONNECT);
                        // 通知HDMI信号拔出
                        context.sendBroadcast(new Intent(MyApplication.ACTION_HDMI_OUT));
                    }
                }

                if (!plugged) {
                    final boolean isProjecor = mSw.getPropertyBoolean("mbx.hdmiin.projector", false);
                    if (isProjecor && mHdmiInSourceType == INPUT_SOURCE_HDMIIN_INTERNAL && mApplication.isHdmiRunning()) {
                        // 发送HDMI关闭的广播
                        TelstarLOG.D(TAG, " Send com.amlogic.osdoverlay.CLOSE Broadcast");
                        Intent i = new Intent(mApplication.HDMIIN_CLOSE);
                        i.putExtra("source", port);
                        i.putExtra("source_type", mHdmiInSourceType);
                        context.sendBroadcast(i);
                    }
                    return;
                }
                if (mHdmiInSourceType != INPUT_SOURCE_HDMIIN_INTERNAL)
                    return;
                // 当只有一个Hdmi输入口的时候，直接打开
                if (!mApplication.isHdmiRunning() && mApplication.getHdmiNum() == 1) {
                    // Kill 与HDMI冲突的Twitch,再打开HDMI
                    if(mApplication != null && mApplication.isAppInstalled("tv.twitch.android.app"))
                    {
                        TelstarLOG.D(TAG, "Twitch is installed, killing ------- ");
                        mApplication.forceStopPackage("tv.twitch.android.app");
                    }
                    final int finalPort = port;
                    final int hdmiInSourceType = mHdmiInSourceType;
                    Intent i = new Intent();
                    i.setClassName("com.amlogic.osdoverlay", "com.amlogic.osdoverlay.FullActivity");
                    i.putExtra("source", finalPort);
                    i.putExtra("source_type", hdmiInSourceType);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        }

    }
}
