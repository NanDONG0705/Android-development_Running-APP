package com.example.scynd2.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.scynd2.R;


public class LocationService extends Service {

    public AMapLocationClient locationClient;
    //define mLocationOption object
    public AMapLocationClientOption locationOption = null;

    //android 8.0 Backend Location Permissions
    private static final String NOTIFICATION_CHANNEL_NAME = "Location";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    private Intent locationIntent = new Intent();
    public LocationService() {
    }

    @Override
    public void onCreate() {
        initLocation();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyLocation();

    }


    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * If AMLocationClient is instantiated in the current Activity
             * the onDestroy of AMLocationClient must be executed in the onDestroy of the Activity.
             */
            locationClient.disableBackgroundLocation(true);
            locationClient.stopLocation();
            locationClient.unRegisterLocationListener(locationListener);
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }


    private void initLocation() {
        //inti client
        try {
            locationClient = new AMapLocationClient(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationOption = getDefaultOption();
        //set location
        locationClient.setLocationOption(locationOption);
        //set listener
        locationClient.setLocationListener(locationListener);
        //set once?
        locationOption.setOnceLocation(false);
        //address information?
        locationOption.setNeedAddress(true);
        //open cache
        locationOption.setLocationCacheEnable(true);
        //dont konw yet
        locationClient.enableBackgroundLocation(2004, buildNotification());
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

    }

    private void startLocation() {
        // start location
        locationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//Optional, set the positioning mode, the optional modes are high accuracy, device only, network only. Default is high accuracy mode
        mOption.setGpsFirst(true);//Optional, set whether gps priority, only valid in high precision mode. Default off
        mOption.setHttpTimeOut(30000);//Optional, set the network request timeout. The default is 30 seconds. Not valid in device-only mode
        mOption.setInterval(1000);//Optional, set the positioning interval. Default is 2 seconds
        mOption.setNeedAddress(true);//Optional, set whether to return inverse geographic address information. Default is true
        mOption.setOnceLocation(false);//Optional, sets whether to single-position or not. Default is false
        mOption.setOnceLocationLatest(false);//Optional, set whether to wait for wifi refresh, default is false. If set to true, it will automatically change to single positioning, don't use it for continuous positioning.
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//Optional, set the protocol for network requests. HTTP or HTTPS, default is HTTP.
        mOption.setSensorEnable(false);
        mOption.setWifiScan(true); //Optional, set whether to enable wifi scanning. Default is true, if it is set to false, it will stop active refreshing at the same time, after it stops, it totally depends on system refreshing, the positioning position may have error.
        mOption.setLocationCacheEnable(true);
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//Optional, set the language of inverse geographic information, the default value is the default language (choose the language according to the region)
        return mOption;
    }

    /**
     * location listener
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {

            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode=0 location
                if (location.getErrorCode() == 0) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    locationIntent.setAction("com.swq.mcsrefine.activity.mylocation");
                    locationIntent.putExtra("location", location);
                    sendBroadcast(locationIntent);
                } else {
                    //location failed
                    sb.append("Location Error" + "\n");
                    sb.append("Error Code:" + location.getErrorCode() + "\n");
                    sb.append("Error Message:" + location.getErrorInfo() + "\n");
                    sb.append("Error Description:" + location.getLocationDetail() + "\n");
                }

            } else {
            }
        }
    };




    private Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Notification has been modified on Android O. If you set targetSDKVersion>=26, it is recommended to use this method to create a notification bar.
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName()+"001";
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//Whether or not to show the dot in the top right corner of the desktop icon
                notificationChannel.setLightColor(Color.BLUE); //color of the dot
                notificationChannel.setShowBadge(true); //Whether to show the notification of this channel when the desktop icon is pressed for a long time
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Location")
                .setContentText("")
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }


}