package com.telstar.launcher;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothInputDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothUuid;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bill on 2016/4/29.
 */
public class BtRemoteCheckService extends Service {

    private static final String TAG = "BtRemoteCheckService";
    private static final String BT_INPUT_NAME = "WBT" ;
    private BluetoothAdapter mBtAdapter;
    private Timer timer;
    private CheckTask checkTask;
    static final int BluetoothProfie_Input = 4;
    private IntentFilter mAdapterIntentFilter, mProfileIntentFilter;
    private byte[] checkLock = new byte[0];

    private boolean isTaskRunning = false;

    private boolean isTaskRunning() {
        synchronized (checkLock) {
            return isTaskRunning;
        }
    }

    private void setTaskRunning(boolean run) {
        synchronized (checkLock) {
            isTaskRunning = run;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "----oncreate----");
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapterIntentFilter = new IntentFilter();
        mProfileIntentFilter = new IntentFilter();

        mAdapterIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mAdapterIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mAdapterIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mAdapterIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mAdapterIntentFilter.addAction(BluetoothDevice.ACTION_DISAPPEARED);
        mAdapterIntentFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        mAdapterIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mAdapterIntentFilter.addAction(BluetoothDevice.ACTION_PAIRING_CANCEL);
        mAdapterIntentFilter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        mAdapterIntentFilter.addAction(BluetoothDevice.ACTION_UUID);


        registerReceiver(mBroadcastReceiver, mAdapterIntentFilter);
        if (mBtAdapter != null) {
            if (!mBtAdapter.isEnabled()) {
                Log.d(TAG, "----- bt is close ,try to open bt .....");
                mBtAdapter.enable();
                try {
                    Thread.sleep(2 * 1000);
                } catch (Exception e) {
                    return;
                }
            }


        }
        timer = new Timer();
        checkTask = new CheckTask();
        timer.schedule(checkTask, 500, 1000 * 30);
    }



    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if(action.equals(BluetoothDevice.ACTION_FOUND))
            {
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                Log.d(TAG,"find bt devices : name "+ name );
                if(BT_INPUT_NAME.equals(name))
                {
                    Log.d(TAG,"=======》find bt devices : name "+ name );
                    if(device.getBondState() ==  BluetoothDevice.BOND_NONE)
                    {
                        Log.d(TAG,"=======》 try to bond  "+ name );
                        if (mBtAdapter.isDiscovering()) {
                            mBtAdapter.cancelDiscovery();
                        }
                        device.createBond();
                    }
                    if(device.getBondState() == BluetoothDevice.BOND_BONDED)
                    {

                    }
                }
            }
         }
    };


    private class CheckTask extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "----check task runing ......");
           /* if (!CheckBtInputConnect()) {
                if (mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                mBtAdapter.startDiscovery();
            }*/

            CheckBtInputConnect();
        }

    }


    private boolean CheckBtInputConnect() {
        BluetoothInputDevice hiddevice;
        int input = mBtAdapter.getProfileConnectionState(BluetoothProfie_Input);
        Set<BluetoothDevice> bondedlist = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedlist) {
            if ( BT_INPUT_NAME.equals(device.getName())) {
                ParcelUuid[] uuids = device.getUuids();
                if ((BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Hid) ||
                        BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Hogp))) {
                    if (input == BluetoothProfile.STATE_CONNECTED) {
                        Log.d(TAG, "check true ----");
                        return true;
                    }
                    Log.d(TAG, "check false  1----");
                    return false;
                }
            }
        }
        Log.d(TAG, "check false 2----");
        return false;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
        Log.d(TAG, "----onDestroy----");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "----onBind----");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "----onUnbind----");
        return super.onUnbind(intent);
    }
}
