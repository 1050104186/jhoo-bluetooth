package com.jhooit.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.jhooit.ble.model.DeviceInfo;
import com.jhooit.ble.service.BleSppHelper;


/**
 * 蓝牙连接控制器
 * author: created by 闹闹 on 2023/2/2
 * version: v1.0.0
 */
public class BleSppController {

    private static Context mContext;
    private static String mDeviceName;
    private static String mDeviceAddress;
    private static int mDataLength;
    private static BluetoothAdapter mBluetoothAdapter;

    /**
     * 初始化
     *
     * @param context
     * @param devName    蓝牙名称
     * @param devAddress 蓝牙地址
     * @param length     返回值的长度
     */
    public static void initBleControl(Context context, String devName, String devAddress, int length) {
        mContext = context;
        mDeviceName = devName;
        mDeviceAddress = devAddress;
        mDataLength = length;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        //搜索
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {

            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.d("BleSppController", "搜索到的" + device.getName());
                if (device.getName() != null) {
                    String name = device.getName();
                    String address = device.getAddress();
                    if (name.equals(mDeviceName) && address.equals(mDeviceAddress)) {
                        DeviceInfo deviceInfo = new DeviceInfo();
                        deviceInfo.setName(device.getName());
                        deviceInfo.setAddress(device.getAddress());
                        deviceInfo.setLength(mDataLength);
                        //初始化
                        BleSppHelper init = BleSppHandler.init(mContext, deviceInfo);
                        //构造连接
                        init.createConnect();
                        init.registerReceiver();
                        mBluetoothAdapter.stopLeScan(this);
                    }
                }
            }
        });
    }
}
