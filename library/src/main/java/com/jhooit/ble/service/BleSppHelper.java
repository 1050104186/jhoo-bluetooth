package com.jhooit.ble.service;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import com.jhooit.ble.R;
import com.jhooit.ble.model.BConstants;
import com.jhooit.ble.model.DeviceInfo;
import com.jhooit.ble.utils.StringUtils;
import com.jhooit.ble.utils.HexUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 幫助
 */
public class BleSppHelper {

    private String TAG = "BleHelper";
    private long recvBytes = 0;
    private long lastSecondBytes = 0;
    private long sendBytes;
    private StringBuilder mData;

    int sendIndex = 0;
    int sendDataLen = 0;
    byte[] sendBuf;

    //测速
    private Timer timer;
    private TimerTask task;

    static long recv_cnt = 0;

    private int mDataLength;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private Context mContext;
    private boolean isAscii = false;

    public BleSppHelper(Context context, DeviceInfo deviceInfo) {
        this.mContext = context;
        this.mDeviceName = deviceInfo.getName();
        this.mDataLength = deviceInfo.getLength();
        this.mDeviceAddress = deviceInfo.getAddress();
    }

    public void createConnect() {
        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);
    }

    public void onConnect() {
        mBluetoothLeService.connect(mDeviceAddress);
    }

    public void disConnect() {
        mBluetoothLeService.disconnect();
    }

    public void registerReceiver() {
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(mGattUpdateReceiver);
    }

    public void unbindService() {
        mContext.unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    public void send(String txt) {
        Log.d(TAG, "发送 txt：" + txt);
        getSendBuf(txt);
        onSendBtnClicked();
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
//                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BConstants.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BConstants.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BConstants.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //特征值找到才代表连接成功
                mConnected = true;
                updateConnectionState(R.string.connected);
            } else if (BConstants.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)) {
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BConstants.ACTION_DATA_AVAILABLE.equals(action)) {
                //接收到数据
                byte[] data = intent.getByteArrayExtra(BConstants.EXTRA_DATA);
                displayData(data);
            } else if (BConstants.ACTION_WRITE_SUCCESSFUL.equals(action)) {
                Log.e(TAG, "发送字节：" + sendBytes);
                if (sendDataLen > 0) {
                    Log.e(TAG, "Write OK,Send again");
                    onSendBtnClicked();
                } else {
                    Log.e(TAG, "Write Finish");
                }
            }
        }
    };

    /**
     * 发送
     *
     * @param txt
     */
    private void getSendBuf(String txt) {
        sendIndex = 0;
        if (isAscii) {
            sendBuf = txt.getBytes();
        } else {
            sendBuf = HexUtils.fromHexString(txt);
            Log.e(TAG, "发送：" + HexUtils.toHexString(sendBuf));
        }
        sendDataLen = sendBuf.length;
    }

    private void onSendBtnClicked() {
        if (sendDataLen > 20) {
            sendBytes = 20;
            final byte[] buf = new byte[20];
            // System.arraycopy(buffer, 0, tmpBuf, 0, writeLength);
            for (int i = 0; i < 20; i++) {
                buf[i] = sendBuf[sendIndex + i];
            }
            sendIndex += 20;
            mBluetoothLeService.writeData(buf);
            sendDataLen -= 20;
        } else {
            sendBytes = sendDataLen;
            final byte[] buf = new byte[sendDataLen];
            for (int i = 0; i < sendDataLen; i++) {
                buf[i] = sendBuf[sendIndex + i];
            }
            mBluetoothLeService.writeData(buf);
            sendDataLen = 0;
            sendIndex = 0;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BConstants.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BConstants.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BConstants.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BConstants.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BConstants.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BConstants.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    /**
     * 更新连接状态
     *
     * @param resourceId
     */
    private void updateConnectionState(final int resourceId) {
        String string = mContext.getString(resourceId);
        Log.e(TAG, "连接状态" + string);
        Intent intent = new Intent(BConstants.HY_SEND);
        intent.putExtra(BConstants.TYPE, BConstants.TYPE_STATUS);
        intent.putExtra(BConstants.MESSAGE, string);
        mContext.sendBroadcast(intent);

//        if (string.equals("Connected")) {
//            try {
//                Thread.sleep(2000);
//                send("CC000000000000000000AA0000DD");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 接收到数据
     *
     * @param buf
     */
    private void displayData(byte[] buf) {
        int recvBytes = buf.length;
        String string = StringUtils.bytesToString(buf);
        Log.e(TAG, "收到数据：" + string + " -- 长度：" + recvBytes);
        if (recvBytes == mDataLength) {
            Intent intent = new Intent(BConstants.HY_SEND);
            Log.e(TAG, "接收到数据：" + string);
            intent.putExtra(BConstants.TYPE, BConstants.TYPE_DATA);
            intent.putExtra(BConstants.MESSAGE, string);
            mContext.sendBroadcast(intent);
        }
    }
}
