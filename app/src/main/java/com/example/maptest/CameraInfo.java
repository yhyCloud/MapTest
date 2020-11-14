package com.example.maptest;

public class CameraInfo {
    int id;//相机id
    String IP;//相机ip
    String longitude;//经度
    String latitude;//纬度

    public CameraInfo(int id, String IP, String latitude, String longitude) {
        this.id = id;
        this.IP = IP;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public String getIP() {
        return IP;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
