package com.example.maptest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

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

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private MapView fragMapView;
    private BaiduMap fragBaiduMap;
    private boolean fragIsFirstLoc = true;
    private LocationClient fragLocationClient;

    private ImageButton fragIbLocation;//重定位按钮

    private Button fragBtnShowCameraLocation;//显示相机布点按钮
    private BitmapDescriptor bitmap;//标点的图标
    private double markerLatitude;//标点的纬度
    private double markerLongitude;//标点经度
    private Marker mMarker;//标点
    private static boolean isFirstAdd=true;

    private DrawerLayout mDrawerLayout;//布局
    private ImageButton showLocationDrawer;//弹出地点列表抽屉

    private ListView LocationListView;//抽屉中的地点列表
    private Button drawerBackBtn;//抽屉布局中的返回按钮
    private ImageButton drawerRefreshLocation;//抽屉布局中刷新地点按钮

//    ArrayList<LatLng> locationList ;
    public ArrayList<String> locationName = new ArrayList<>();//从数据库中获取的地点名字
    public ArrayList<String> locationLat = new ArrayList<>();//从数据库中获取的纬度信息
    public ArrayList<String> locationLng = new ArrayList<>();//从数据库中获取的经度信息
    public Bundle locationBundle = new Bundle();

    private static Toast toast;







    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        initView(view);
        initLocation();
        mapOnClick();

//        setLocationInfo();
//        setMaker();


//        if (isFirstAdd) {
//            isFirstAdd = false;
//            addCameraLocation(30.75032, 103.93059, fragBaiduMap);
//            addCameraLocation(30.75132, 103.93159, fragBaiduMap);
//            addCameraLocation(30.75232, 103.93259, fragBaiduMap);
//            addCameraLocation(30.75332, 103.93356, fragBaiduMap);
//        }

        return view;
    }

    private void mapOnClick() {
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        fragBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            //
            public void onMapClick(LatLng latLng) {
                markerLatitude = latLng.latitude;
                markerLongitude = latLng.longitude;
                //先清除图层
                fragBaiduMap.clear();
                if (mMarker != null) {
                    mMarker.remove();
                }
                LatLng point = new LatLng(markerLatitude, markerLongitude);
                MarkerOptions options = new MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                        .scaleX((float) 0.2)
                        .scaleY((float) 0.2);
                mMarker = (Marker) fragBaiduMap.addOverlay(options);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", "纬度:" + markerLatitude + "经度:" + markerLongitude);
                mMarker.setExtraInfo(bundle);
                showToastCenter(getActivity(),"经度：" + markerLongitude + "纬度：" + markerLatitude);
//                Toast.makeText(getActivity(), "经度：" + markerLongitude + "纬度：" + markerLatitude, Toast.LENGTH_SHORT).show();
                //点击地图之后重新定位
//                initLocation();
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
    }

    private void initLocation() {
        fragBaiduMap.setMyLocationEnabled(true);
        fragLocationClient = new LocationClient(getActivity().getApplicationContext());
        MyLocationListener myListener = new MyLocationListener();
        fragLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//开启gps
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        fragLocationClient.setLocOption(option);
        fragLocationClient.start();
    }

    private void initView(final View view) {
        fragMapView = view.findViewById(R.id.mapFragment);
        fragIbLocation = view.findViewById(R.id.ib_frag_location);
        fragMapView.showScaleControl(true);
        fragBaiduMap = fragMapView.getMap();
        fragBtnShowCameraLocation = view.findViewById(R.id.btn_show_camera_location);

        mDrawerLayout = view.findViewById(R.id.mapFragmentDrawer);
        showLocationDrawer = view.findViewById(R.id.imbtn_show_location_drawer);
        LocationListView = view.findViewById(R.id.location_listview);
        drawerBackBtn = view.findViewById(R.id.back_button);
        drawerRefreshLocation = view.findViewById(R.id.drawerRefreshLocation);

        fragBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                final String info = (String) marker.getExtraInfo().get("info");
                Bundle extraInfo = marker.getExtraInfo();
                String title = extraInfo.getString("title");
                double lat = extraInfo.getDouble("lat");
                double lng = extraInfo.getDouble("lng");
                showToastCenter(getActivity(),title+"--"+lat+"--"+lng);
//                Toast.makeText(getActivity(), title+"--"+lat+"--"+lng, Toast.LENGTH_SHORT).show();
                toCameraInfo(view, extraInfo);
//                Intent intent = new Intent(getActivity(), CameraInfoActivity.class);
//                startActivity(intent);
                return true;
            }
        });
        fragIbLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markerLatitude = 0;
                initLocation();
                if (mMarker != null) {
                    mMarker.remove();
                }
            }
        });

        fragBtnShowCameraLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragBaiduMap.clear();
                setMarker();//标点函数
            }
        });

        showLocationDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshLocation();// 点开抽屉列表时刷新地点
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        drawerBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        drawerRefreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshLocation();

            }
        });



//        locationList = new ArrayList<>();
//        locationList.add(new LatLng(30.742, 104.151));//熊猫基地坐标
//        locationList.add(new LatLng(30.742, 104.151));
//        locationList.add(new LatLng(30.742, 104.151));
//        locationList.add(new LatLng(29.66194, 102.64869));//大相岭坐标
//        locationList.add(new LatLng(29.66195, 102.64879));
//        locationList.add(new LatLng(30.753, 103.934));//电子科技大学坐标
//        locationList.add(new LatLng(30.753, 103.934));
        LocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                double lat =Double.valueOf(locationLat.get(i));
                double lng = Double.valueOf(locationLng.get(i));
                LatLng latLng = new LatLng(lat, lng);
                    MapStatus mMapStatus = new MapStatus.Builder()
                            .target(latLng).zoom(18).build();
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    fragBaiduMap.setMapStatus(mMapStatusUpdate);
            }

        });


    }

    private void refreshLocation() {
        GetLocationThread refreshLocationThread = new GetLocationThread();
        refreshLocationThread.start();
    }

    private void addCameraLocation(double Lat,double Lng,BaiduMap map) {
        LatLng cameraGPS = new LatLng(Lat, Lng);

        BitmapDescriptor cameraIcon = BitmapDescriptorFactory
                .fromResource(R.drawable.camera_lightblue);
        OverlayOptions option=new MarkerOptions()
                .position(cameraGPS)
                .icon(cameraIcon)
                .draggable(false)
                .flat(true)
                .scaleX((float) 0.15)
                .scaleY((float) 0.15);
        map.addOverlay(option);
    }

    private void setMarker() {
        List<LatLng> latLngList = new ArrayList<>();
        List<OverlayOptions> optionsList = new ArrayList<>();
        latLngList.add(new LatLng(30.75032, 103.93059));
        latLngList.add(new LatLng(30.75132, 103.93159));
        latLngList.add(new LatLng(30.75332, 103.93356));
        latLngList.add(new LatLng(30.75421, 103.93256));
        latLngList.add(new LatLng(30.75300, 103.93204)); //一般都是接口请求的数据 我这边自己填充的数据
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.camera_lightblue);
        for (int i = 0; i < 5; i++) {
            Bundle mBundle = new Bundle();//用于区别不同的Marker
            mBundle.putString("title","第"+i+"号相机");
            mBundle.putDouble("lat",latLngList.get(i).latitude);
            mBundle.putDouble("lng",latLngList.get(i).longitude);
            OverlayOptions option=new MarkerOptions()
                    .extraInfo(mBundle)
                    .position(latLngList.get(i))
                    .icon(bitmap)
                    .scaleX((float) 0.15).scaleY((float) 0.15);
            optionsList.add(option);
        }
        for (OverlayOptions mOption : optionsList) {
            fragBaiduMap.addOverlay(mOption);
        }
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(latLngList.get(3)).zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        fragBaiduMap.setMapStatus(mMapStatusUpdate);

    }

    /**
     * 监听SDK定位
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || fragMapView == null) {
                return;
            }
            double resultLatitude;
            double resultLongitude;
            if (markerLatitude == 0) {
                resultLatitude = location.getLatitude();
                resultLongitude = location.getLongitude();
                fragIbLocation.setVisibility(View.VISIBLE);
            } else {
                resultLatitude = location.getLatitude();
                resultLongitude = location.getLongitude();
                fragIbLocation.setVisibility(View.VISIBLE);
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
            fragBaiduMap.setMyLocationData(locData);

//            isFirstLoc = false;
            fragBaiduMap.setMyLocationData(locData);
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng latLng = new LatLng(resultLatitude, resultLongitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(18);
            fragBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragLocationClient.stop();
        fragBaiduMap.setMyLocationEnabled(false);
        fragMapView.onDestroy();
    }

    public static void showToastCenter(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT); //如果有居中显示需求
        toast.setText(msg);
        toast.show();
    }

    private void toCameraInfo(View view,Bundle bundle) {
        Intent intent = new Intent(getActivity(), CameraInfoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setLocationInfo(Bundle locationBundle) {
        ArrayList<String> locationInfo ;
//        locationInfo.add("成都-熊猫基地1");
//        locationInfo.add("成都-熊猫基地2");
//        locationInfo.add("成都-熊猫基地3");
//        locationInfo.add("雅安-大相岭1");
//        locationInfo.add("雅安-大相岭2");
//        locationInfo.add("成都-电子科技大学1");
//        locationInfo.add("成都-电子科技大学2");
        locationInfo = locationBundle.getStringArrayList("locationName");
        if (locationInfo != null) {
            ArrayAdapter locationAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, locationInfo);
            LocationListView.setAdapter(locationAdapter);
        }
    }

    public class GetLocationThread extends Thread {

        @Override
        public void run() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Log.d("MainActivity", "加载JDBC驱动成功！");
            } catch (ClassNotFoundException e) {
                Log.d("MainActivity", "加载JDBC驱动失败！");
                e.printStackTrace();
                return;
            }
            String url = "jdbc:mysql://192.168.50.39:3512/location?useSSL=false&serverTimezone=Hongkong&useUnicode=true&characterEncoding=utf-8 ";
            // 构建连接mysql的字符串
            String user = "root"; //自己的用户名
            String password = "root"; //自己的密码
            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                Log.d("MainActivity", "连接数据库成功");
                String sql="SELECT * FROM locationdata";
                try {
                    Statement statement = connection.createStatement();
                    ResultSet res = statement.executeQuery(sql);
                    locationName.clear();
                    locationLat.clear();
                    locationLng.clear();
                    Log.d("MainActivity", "地点信息");
                    while (res.next()) {
                        Log.d("MainActivity",res.getInt(1) + "\t"
                                + res.getString(2) + "\t"
                                + res.getDouble(3) + "\t"
                                + res.getDouble(4));
                        locationName.add(res.getString(2));
                        locationLat.add(res.getString(3));
                        locationLng.add(res.getString(4));
                    }

                    locationBundle.putStringArrayList("locationName", locationName);
                    locationBundle.putStringArrayList("locationLat", locationLat);
                    locationBundle.putStringArrayList("locationLng", locationLng);
//                    Message locationMessage = new Message();
//                    locationMessage.setData(locationBundle);


                } catch (SQLException e) {
                    Log.e("MainActivity", "查询错误");
                    e.printStackTrace();
                }
                try {
                    connection.close();
                    Log.d("MainActivity", "关闭链接成功");
                } catch (SQLException e) {
                    Log.d("MainActivity", "关闭连接失败。");
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                Log.d("MainActivity", "连接数据库失败!");
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLocationInfo(locationBundle);
                }
            });
        }
    }
}
