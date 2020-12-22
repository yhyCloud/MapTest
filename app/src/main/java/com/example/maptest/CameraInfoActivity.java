package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.maptest.vlc.VlcModel;

import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class CameraInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private MapView mMapView;

    private LocationClient mLocationClient;
    private BaiduMap mBaiduMap;
    boolean isFirstLoc = true;//首次定位

    private BitmapDescriptor bitmap;//标点的图标
    private double markerLatitude=0;//标点维度
    private double markerLongitude=0;//标点经度
    private ImageButton ibLocation;//重置定位按钮
    private Marker mMarker;//标点

    private TextView cameraId;
    private TextView cameraLat;//相机纬度
    private TextView cameraLng;//相机经度

    private ScrollView mScrollView;//滑动布局栏

    private ImageView mImageView;//显示图像文件imageview控件
    private ExpandableListView mExpandableListView;//显示文件列表控件

    //item数据测试用
    private ArrayList<String> mGroupList;
    private ArrayList<ArrayList<String>> mItemSet;
    static int count = 0;
    ArrayList<Integer> photo = new ArrayList();

    private Button showPresentBtn;//显示实时画面按钮
    private Button cameraSettingBtn;//跳转相机设置界面按钮
    private VideoView mVideoView;//显示实时画面控件
    private SurfaceView mSurfaceView;//显示实时画面的控件
    private Button RtspRefreshButton;//播放
    private Button RtspStopButton;//暂停

    private VlcModel mVlcModel;
    private Handler mHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
//        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_camera_info);
        initView();
        initPhotoData();//初始化测试用图片数据
//        initLocation();
//        mapOnClick();
        mapOnTouch();
        listOnTouch();
        Intent mapIntent = getIntent();//获取从mapFragment传来的Intent
        Bundle bundle = mapIntent.getExtras();//获取Intent中的bundle数据
        if (bundle != null) {
            setCameraInfo(bundle);
            setMarkerLocation(bundle);
        }
        initTestData();

        //Vlc相关线程
//        mVlcModel = new VlcModel("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", (SurfaceView) findViewById(R.id.surfaceV), mEventListener);
        mVlcModel = new VlcModel("rtsp://192.168.50.60:554/test.264", (SurfaceView) findViewById(R.id.surfaceV), mEventListener);
        HandlerThread handlerThread = new HandlerThread("");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        //Android6.0以下不能隐藏状态栏
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
            mVlcModel.setLandscapeDisplayHeight(mVlcModel.getLandscapeDisplayHeight() - getStatusBarHeight(this));
        }
        mVlcModel.updateVideoSurfaces();


        FileInfoAdapter adapter = new FileInfoAdapter(this, mGroupList, mItemSet);
        mExpandableListView.setAdapter(adapter);
        mExpandableListView.expandGroup(0);
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if (count == 10) {
                    count = 0;
                }
                mImageView.setImageResource(photo.get(count));
                count++;
                return true;
            }
        });
        showPresentBtn.setOnClickListener(this);
        cameraSettingBtn.setOnClickListener(this);
        RtspRefreshButton.setOnClickListener(this);
//        videoPause.setOnClickListener(this);

    }

    private void setMarkerLocation(Bundle bundle) {
        LatLng latLng=new LatLng(bundle.getDouble("lat"), bundle.getDouble("lng"));
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.camera_red);
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap)
                .scaleY((float) 0.15).scaleX((float) 0.15);
        mBaiduMap.addOverlay(option);
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(latLng).zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

    /**
     * 在相机信息栏中设置数据
     * @param bundle
     */
    private void setCameraInfo(Bundle bundle) {
        String title = bundle.getString("title");
        double lat = bundle.getDouble("lat");
        double lng = bundle.getDouble("lng");
        cameraId.setText("相机id："+title);
        cameraLat.setText("纬度" + lat);
        cameraLng.setText("经度" + lng);
    }

    private void initView() {
        mMapView = findViewById(R.id.locationMapView);
        ibLocation = findViewById(R.id.ib_location);
        mMapView.showScaleControl(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                final String info = (String) marker.getExtraInfo().get("info");
                Toast.makeText(CameraInfoActivity.this,"点击布点",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        cameraId = findViewById(R.id.camera_activity_id);
        cameraLng = findViewById(R.id.camera_activity_longitude);
        cameraLat = findViewById(R.id.camera_activity_latitude);

        mScrollView = findViewById(R.id.camerainfoactivity_scrollview);//滑动布局栏
        mExpandableListView = findViewById(R.id.file_listview);
        mImageView = findViewById(R.id.filePhotoShow);
        showPresentBtn = findViewById(R.id.camera_activity_showPresentBtn);
        cameraSettingBtn = findViewById(R.id.camera_activity_cameraSettingBtn);
//        mVideoView = findViewById(R.id.video_view);//视频控件
        mSurfaceView = findViewById(R.id.surfaceV);
//        videoPause = findViewById(R.id.video_pause);//暂停按钮
        RtspRefreshButton = findViewById(R.id.rtsp_refresh);//播放按钮
        RtspStopButton = findViewById(R.id.rtsp_stop);
    }

    private void initLocation() {
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
        MyLocationListener myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//开启gps
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 点击切换到其他标点位置时，重置定位坐标，点击之后回到自动定位
     * @param view
     */
    public void resetLocation(View view) {
        markerLatitude = 0;
        initLocation();
        if (mMarker != null) {
            mMarker.remove();
        }

    }

    private void mapOnClick() {
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            //
            public void onMapClick(LatLng latLng) {
                markerLatitude = latLng.latitude;
                markerLongitude = latLng.longitude;
                //先清除图层
                mBaiduMap.clear();
                LatLng point = new LatLng(markerLatitude, markerLongitude);
                MarkerOptions options = new MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                        .scaleX((float) 0.2).scaleY((float) 0.2);
                mMarker = (Marker) mBaiduMap.addOverlay(options);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", "维度:" + markerLatitude + "经度:" + markerLongitude);
                mMarker.setExtraInfo(bundle);
                //点击地图之后重新定位
                initLocation();
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
    }

    /**
     * 设置地图触摸拦截
     */
    private void mapOnTouch() {
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                }else {
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                }
            }
        });
    }

    /**
     * 设置文件列表listview的触摸拦截
     */
    @SuppressLint("ClickableViewAccessibility")
    private void listOnTouch() {
        mExpandableListView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                mExpandableListView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_activity_showPresentBtn://显示实时画面按钮
//                initPermission();
//                initVideoPath();
//                String rtspUrl="rtsp://10.10.10.117:554/test.264";
//                PlayRtspStream(rtspUrl);
//                showExternalImage();
//                mImageView.setImageResource(R.drawable.presentvideo);
//                initVideoPath();
                showRtsp();
                break;
            case R.id.camera_activity_cameraSettingBtn://相机设置按钮
                Intent intent = new Intent(CameraInfoActivity.this, MainActivity.class);
                intent.putExtra("id", 3);
                CameraInfoActivity.this.startActivity(intent);
                break;
            case R.id.rtsp_refresh:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mVlcModel.release();
                    }
                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mVlcModel.attachViews();
                    }
                });
                break;
            case R.id.rtsp_stop:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mVlcModel.release();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void showRtsp() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mVlcModel.attachViews();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVlcModel.updateVideoSurfaces();
    }

    MediaPlayer.EventListener mEventListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {
            switch (event.type) {
                case MediaPlayer.Event.Opening:
                    break;
                case MediaPlayer.Event.Playing:
                    break;
                case MediaPlayer.Event.Buffering:
                    break;
                case MediaPlayer.Event.Paused:
                    break;
                case MediaPlayer.Event.Stopped:
                    break;
                case MediaPlayer.Event.PositionChanged:
                    break;
                case MediaPlayer.Event.TimeChanged:
                    break;
                case MediaPlayer.Event.SeekableChanged:
                    break;
                case MediaPlayer.Event.PausableChanged:
                    break;
                case MediaPlayer.Event.MediaChanged:
                    break;
                case MediaPlayer.Event.EndReached:
                    break;
                case MediaPlayer.Event.EncounteredError:
                    break;
                default:
                    break;
            }
        }
    };

    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void PlayRtspStream(String rtspUrl) {
        mVideoView.setVideoURI(Uri.parse(rtspUrl));
        mVideoView.requestFocus();
        mVideoView.start();
    }


    private void showExternalImage() {
        String url = "/storage/emulated/0/Pictures/WeiXin/mmexport1605513821590.jpg";
        try {
            FileInputStream fis = new FileInputStream(url);
            Bitmap bitmap= BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
            mImageView.setImageBitmap(bitmap);
            Toast.makeText(this,"显示相机图像",Toast.LENGTH_SHORT).show();
        }
         catch (Exception e) {
             Toast.makeText(this,"未能显示",Toast.LENGTH_SHORT).show();
             e.printStackTrace();
         }


//        Toast.makeText(this,"显示手机内部图片",Toast.LENGTH_SHORT).show();

    }

    private void initVideoPath() {
//        File file = new File(Environment.getExternalStorageDirectory(),
//                "/Pictures/QQ/pandavideo_cut.mp4");
//        System.out.println(file.getPath());
        File file = new File(Environment.getExternalStorageDirectory(),
                "/Pictures/QQ/pandavideo_cut.mp4");
        String videoPath = file.getAbsolutePath();

//        Toast.makeText(this,"文件是否存在"+file.exists(),Toast.LENGTH_LONG).show();
       Toast.makeText(this,"文件路径"+videoPath,Toast.LENGTH_LONG).show();
        mVideoView.setVideoPath(videoPath);
        mVideoView.start();

    }

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //map销毁后不再处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            double resultLatitude;
            double resultLongitude;
            if (markerLatitude == 0) {
                resultLatitude = location.getLatitude();
                resultLongitude = location.getLongitude();
                ibLocation.setVisibility(View.VISIBLE);
            }else {
                resultLatitude = markerLatitude;
                resultLongitude = markerLongitude;
                ibLocation.setVisibility(View.VISIBLE);
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
            mBaiduMap.setMyLocationData(locData);
//            isFirstLoc = false;
            mBaiduMap.setMyLocationData(locData);
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng latLng = new LatLng(resultLatitude, resultLongitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(18);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                mVlcModel.attachViews();
//            }
//        });
//    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mVlcModel != null) {
                    mVlcModel.detachViews();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        if (mLocationClient != null) {
            mLocationClient.stop();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mVlcModel.release();
            }
        });
    }

    private void initTestData() {
        mGroupList = new ArrayList<>();
        mGroupList.add("图像文件");
        mGroupList.add("视频文件");
        mItemSet = new ArrayList<>();
        ArrayList<String> itemList1 = new ArrayList<>();
        itemList1.add("20200211001.jpg");
        itemList1.add("20200211002.jpg");
        itemList1.add("20201211001.jpg");
        itemList1.add("20201211002.jpg");
        itemList1.add("20201211003.jpg");
        itemList1.add("20201211004.jpg");
        itemList1.add("20201211005.jpg");
        ArrayList<String> itemList2 = new ArrayList<>();
        itemList2.add("20201011001.avi");
        itemList2.add("20201011002.avi");
        itemList2.add("20201011003.avi");
        itemList2.add("20201012001.avi");
        itemList2.add("20201012002.avi");
        itemList2.add("20201012003.avi");
        mItemSet.add(itemList1);
        mItemSet.add(itemList2);

    }

    private  void  initPhotoData() {
        photo.add(R.drawable.panda0);
        photo.add(R.drawable.panda1);
        photo.add(R.drawable.panda2);
        photo.add(R.drawable.panda3);
        photo.add(R.drawable.panda4);
        photo.add(R.drawable.panda4);
        photo.add(R.drawable.panda5);
        photo.add(R.drawable.panda6);
        photo.add(R.drawable.panda7);
        photo.add(R.drawable.panda8);
        photo.add(R.drawable.panda9);
    }


    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
            };
    List<String> mPermissionList = new ArrayList<>();
    private final int mRequestCode = 100;//权限请求码

    private void initPermission() {
        mPermissionList.clear();//清空没有通过的权限
        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        }else{
            showExternalImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            if (hasPermissionDismiss) {
                Toast.makeText(this,"拒绝权限将无法访问",Toast.LENGTH_LONG).show();
                finish();
            }else {
                showExternalImage();
            }
        }
    }
}