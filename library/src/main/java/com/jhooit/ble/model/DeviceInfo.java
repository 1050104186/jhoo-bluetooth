package com.jhooit.ble.model;

public class DeviceInfo {

    //蓝牙名称
    private String name;
    //蓝牙地址
    private String address;
    //返回数据长度
    private int length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
