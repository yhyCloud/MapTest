package com.example.maptest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

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

    private ImageView mImageView;

    private ExpandableListView mExpandableListView;

    //item数据测试用
    private ArrayList<String> mGroupList;
    private ArrayList<ArrayList<String>> mItemSet;
    static int count = 0;
    ArrayList<Integer> photo = new ArrayList();

    private Button showPresentBtn;//显示实时画面按钮
    private Button cameraSettingBtn;//跳转相机设置界面按钮



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
                mImageView.setImageResource(R.drawable.presentvideo);
                break;
            case R.id.camera_activity_cameraSettingBtn://相机设置按钮
                Intent intent = new Intent(CameraInfoActivity.this, MainActivity.class);
                intent.putExtra("id", 3);
                CameraInfoActivity.this.startActivity(intent);

            default:
                break;
        }
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

}