package com.jhooit.ble;

import android.content.Context;
import android.util.Log;

import com.jhooit.ble.model.DeviceInfo;
import com.jhooit.ble.service.BleSppHelper;


public class BleSppHandler {

    private static String TAG = "BleSppHandler";
    private static BleSppHelper bleSppHelper;

    /**
     * 初始化默认的 蓝牙 连接
     */
    public static BleSppHelper init(Context context, DeviceInfo deviceInfo) {
        if (bleSppHelper == null) {
            synchronized (BleSppHandler.class) {
                if (bleSppHelper == null) {
                    bleSppHelper = new BleSppHelper(context, deviceInfo);
                }
            }
        } else {
            Log.e(TAG, "Default BleSpp exists!do not start again!");
        }
        return bleSppHelper;
    }

    public static BleSppHelper getDefault() {
        return bleSppHelper;
    }
}
