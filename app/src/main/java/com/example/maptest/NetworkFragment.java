package com.example.maptest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

public class NetworkFragment extends Fragment {
    private MapView fragNetMapView;
    private BaiduMap fragNetBaiduMap;
    private LocationClient fragLocationClient;

    private ImageButton fragIbNetGroup;//重定位按钮
    private BitmapDescriptor bitmap;//标点的图标
    private double markerLatitude;//标点的纬度
    private double markerLongitude;//标点经度
    private Marker mMarker;//标点

    private static boolean isFirstDraw = false;

    private Button BtnViewNetworkLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.network_fragment, container, false);
        initView(view);
        BtnViewNetworkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLocation();
                if (isFirstDraw == false) {
                    isFirstDraw = true;
                    DrawAP();
                    DrawBridge();
                }
            }
        });
//        initNetWork();
//        mapOnClick();
        return view;
    }

    private void DrawBridge() {
        LatLng bridgePoint = new LatLng(30.75242, 103.92900);
        BitmapDescriptor bridgeIcon= BitmapDescriptorFactory.fromResource(R.drawable.bridge);
        OverlayOptions options = new MarkerOptions()
                .position(bridgePoint)
                .icon(bridgeIcon)
                .scaleX((float) 0.3)
                .scaleY((float) 0.3)
                .flat(true)
                .perspective(true);
        fragNetBaiduMap.addOverlay(options);
    }

    private void DrawAP() {
        LatLng AP1 = new LatLng(30.75032, 103.93059);
        CircleOptions AP1CircleOptions = new CircleOptions().center(AP1)
                .radius(200)
                .fillColor(0xAAfcb75c)
                .stroke(new Stroke(1, R.color.colorAccentTran));
        Overlay AP1Circle = fragNetBaiduMap.addOverlay(AP1CircleOptions);

        LatLng AP2 = new LatLng(30.75542, 103.93649);
        CircleOptions AP2CircleOptions = new CircleOptions().center(AP2)
                .radius(200)
                .fillColor(0xAAfcb75c)
                .stroke(new Stroke(1, R.color.colorAccentTran));
        Overlay AP2Circle = fragNetBaiduMap.addOverlay(AP2CircleOptions);

        LatLng AP3 = new LatLng(30.75242, 103.93349);
        CircleOptions AP3CircleOptions = new CircleOptions().center(AP3)
                .radius(150)
                .fillColor(0xAAfcb75c)
                .stroke(new Stroke(1, R.color.colorAccentTran));
        Overlay AP3Circle = fragNetBaiduMap.addOverlay(AP3CircleOptions);
    }

    private void initView(View view) {
        fragNetMapView = view.findViewById(R.id.NetFragmentMapView);
        fragIbNetGroup = view.findViewById(R.id.ib_netfrag_netgroup);
        fragNetMapView.showScaleControl(false);
        fragNetBaiduMap = fragNetMapView.getMap();
        fragNetBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        BtnViewNetworkLocation = view.findViewById(R.id.btn_viewNetWorkLocation);

    }
    private void initLocation() {
        fragNetBaiduMap.setMyLocationEnabled(true);
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

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || fragNetMapView == null) {
                return;
            }
            double resultLatitude;
            double resultLongitude;
            if (markerLatitude == 0) {
                resultLatitude = location.getLatitude();
                resultLongitude = location.getLongitude();
                BtnViewNetworkLocation.setVisibility(View.VISIBLE);
            } else {
                resultLatitude = location.getLatitude();
                resultLongitude = location.getLongitude();
                BtnViewNetworkLocation.setVisibility(View.VISIBLE);
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
            fragNetBaiduMap.setMyLocationData(locData);

//            isFirstLoc = false;
            fragNetBaiduMap.setMyLocationData(locData);
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng latLng = new LatLng(resultLatitude, resultLongitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(20.0f);
            fragNetBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fragLocationClient != null) {
            fragLocationClient.stop();
        }
        fragNetBaiduMap.setMyLocationEnabled(false);
        fragNetMapView.onDestroy();
        System.out.println("fragNetMapView onDestroyView");
    }
}
