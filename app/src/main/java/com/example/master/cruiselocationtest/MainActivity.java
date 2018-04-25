package com.example.master.cruiselocationtest;

import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.*;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
        //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
        //原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明



    LocationClientOption option = new LocationClientOption();//全局参数，SDK设置对象


    public String getCurrentSystemTime(){

        String CH_day="";
        Calendar calendar_now=Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar_now.get(Calendar.YEAR);
        //月
        int month = calendar_now.get(Calendar.MONTH)+1;
        //日
        int day = calendar_now.get(Calendar.DAY_OF_MONTH);
        //获取系统时间
        //小时
        int hour = calendar_now.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar_now.get(Calendar.MINUTE);
        //秒
        int second = calendar_now.get(Calendar.SECOND);
        //星期
        int day_in_week =calendar_now.get(Calendar.DAY_OF_WEEK);
        switch (day_in_week){
            case 1:CH_day="星期一";break;
            case 2:CH_day="星期二";break;
            case 3:CH_day="星期三";break;
            case 4:CH_day="星期四";break;
            case 5:CH_day="星期五";break;
            case 6:CH_day="星期六";break;
            default:CH_day="星期日";break;

        }
        return year+"年"+month+"月"+day+"日"+hour+"时"+minute+"分"+second+"秒"+" "+CH_day;
    }


    public void LocationSetFunc(){
        option.setLocationMode(LocationMode.Device_Sensors);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");//使用百度经纬度坐标，待后续程序开发使用
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }
    TextView text_longitude;
    TextView text_latitude;
    TextView text_radius;
    TextView text_coorType;
    TextView text_errorType;
    TextView text_Time;
    TextView text_errorcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_longitude=(TextView) findViewById(R.id.text_longitude);
        text_latitude=(TextView) findViewById(R.id.text_latitude);
        text_radius=(TextView) findViewById(R.id.text_radius);
        text_coorType=(TextView) findViewById(R.id.text_coorType);
        text_errorType=(TextView) findViewById(R.id.text_GPSState);
        text_errorcode=(TextView) findViewById(R.id.text_errorcode);
        text_Time=(TextView) findViewById(R.id.text_time);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationSetFunc();
        mLocationClient.start();
        new para_showing().start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mLocationClient.restart();
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
        /*start()：启动定位SDK；stop()：关闭定位SDK。调用start()之后只需要等待定位结果自动回调即可。
        开发者定位场景如果是单次定位的场景，在收到定位结果之后直接调用stop()函数即可。
        如果stop()之后仍然想进行定位，可以再次start()等待定位结果回调即可。
        自v7.2版本起，新增LocationClient.reStart()方法，用于在某些特定的异常环境下重启定位。
        如果开发者想按照自己逻辑请求定位，可以在start()之后按照自己的逻辑请求LocationClient.requestLocation()函数，
        会主动触发定位SDK内部定位逻辑，等待定位回调即可。*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    private class para_showing extends Thread {//用来计时1秒的子线程
        @Override
        public void run() {
            while (true)
            {
                //super.run();
                try {
                    Thread.sleep(100);
                    Message msg = new Message();
                    msg.what = 1;
                    mhandler.sendMessage(msg);
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

        }
    }

    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1)
            {
                text_Time.setText(getCurrentSystemTime());
                text_latitude.setText("纬度："+myListener.get_latitude);
                text_longitude.setText("经度："+myListener.get_longitude);
                text_coorType.setText("定位类型："+myListener.get_coorType);
                text_radius.setText("定位精度："+myListener.get_radius+"        GPS锁定卫星数："+myListener.GPS_Num);
                text_errorcode.setText("错误代码："+myListener.get_errorCode);
                switch (myListener.GPS_State)
                {
                    case 0:text_errorType.setText("GPS状态：未知");break;
                    case 1:text_errorType.setText("GPS状态：好");break;
                    case 2:text_errorType.setText("GPS状态：中");break;
                    case 3:text_errorType.setText("GPS状态：差");break;
                    default:text_errorType.setText(""+null);break;
                }
            }
        }
    };

}
