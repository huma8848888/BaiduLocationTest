package com.example.master.cruiselocationtest;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.*;


public class MyLocationListener extends BDAbstractLocationListener {

    public double get_latitude;//获取纬度
    public double get_longitude;//获取经度
    public float get_radius;//获取定位精度
    public String get_coorType;//获取经纬度坐标类型
    public int get_errorCode;//获取定位类型
    public int GPS_State;
    public int GPS_Num;
    @Override
    public void onReceiveLocation(BDLocation location){
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明



        get_latitude=latitude;
        get_longitude=longitude;
        get_radius=radius;
        get_coorType=coorType;
        get_errorCode=errorCode;
        GPS_State=location.getGpsAccuracyStatus();
        GPS_Num=location.getSatelliteNumber();//获取锁定的卫星数
    }
}
