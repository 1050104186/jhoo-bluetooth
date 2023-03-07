package com.jhooit.ble.model;

public class BConstants {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String ACTION_WRITE_SUCCESSFUL = "com.example.bluetooth.le.WRITE_SUCCESSFUL";
    public final static String ACTION_GATT_SERVICES_NO_DISCOVERED = "com.example.bluetooth.le.GATT_SERVICES_NO_DISCOVERED";
    //位数（长度）
    public static final int DIGIT = 14;
    public static final String HY_SEND = "com.jhooit.blue.send";
    public static final String TYPE_STATUS = "status";
    public static final String TYPE_DATA = "data";
    public static final String TYPE_DEV = "type_dev";
    public static final String TYPE = "type";
    public static final String DEVICE = "device";
    public static final String MESSAGE = "message";
    public static final String FAIL = "fail";
    public static final String SUCCESS = "success";

}
