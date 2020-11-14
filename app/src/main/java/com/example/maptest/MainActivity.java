package com.example.maptest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioButton mapRb, networkRb,cameraRb , settingRb;
    private RadioGroup mRadioGroup;
    private MapFragment mMapFragment;
    private NetworkFragment mNetworkFragment;
    private CameraFragment mCameraFragment;
    private SettingFragment mSettingFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (checkedId) {
            case R.id.rd_map:
                if (mMapFragment == null) {
                    mMapFragment = new MapFragment();
                    transaction.add(R.id.fragment_container, mMapFragment);
                } else {
                    transaction.show(mMapFragment);
                }
                break;
            case R.id.rd_network:
                if (mNetworkFragment == null) {
                    mNetworkFragment = new NetworkFragment();
                    transaction.add(R.id.fragment_container, mNetworkFragment);
                } else {
                    transaction.show(mNetworkFragment);
                }
                break;
            case R.id.rd_camera:
                if (mCameraFragment == null) {
                    mCameraFragment = new CameraFragment();
                    transaction.add(R.id.fragment_container, mCameraFragment);
                } else {
                    transaction.show(mCameraFragment);
                }
                break;
            case R.id.rd_setting:
                if (mSettingFragment == null) {
                    mSettingFragment = new SettingFragment();
                    transaction.add(R.id.fragment_container, mSettingFragment);
                } else {
                    transaction.show(mSettingFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (mMapFragment != null) {
            transaction.hide(mMapFragment);
        }
        if (mNetworkFragment != null) {
            transaction.hide(mNetworkFragment);
        }
        if (mCameraFragment != null) {
            transaction.hide(mCameraFragment);
        }
        if (mSettingFragment != null) {
            transaction.hide(mSettingFragment);
        }
    }

    private void initView() {
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);
        //初始化
        mapRb = findViewById(R.id.rd_map);
        networkRb = findViewById(R.id.rd_network);
        cameraRb = findViewById(R.id.rd_camera);
        settingRb = findViewById(R.id.rd_setting);
        //初始界面定在第一个
        mapRb.setChecked(true);

        //底部导航的时候会发生图片的颜色变化，所以radiobutton中的照片不是一张，而是引用了自定义的选择器照片
        //本来使用的是getResources.getDrawable,不过已经过时，所以使用ContextCompat
        Drawable map = ContextCompat.getDrawable(this, R.drawable.selector_map_drawable);
        //当这个图片被绘制时，给他绑定一个矩形规定这个矩形,参数前两个对应图片相对于左上角的新位置，后两个为图片的长宽
        map.setBounds(0, 0, 80, 80);
         //设置图片在文字的哪个方向,分别对应左，上，右，下
        mapRb.setCompoundDrawables(null, map, null, null);

        //底部导航的时候会发生图片的颜色变化，所以radiobutton中的照片不是一张，而是引用了自定义的选择器照片
        //本来使用的是getResources.getDrawable,不过已经过时，所以使用ContextCompat
        Drawable network = ContextCompat.getDrawable(this, R.drawable.selector_network_drawable);
        //当这个图片被绘制时，给他绑定一个矩形规定这个矩形,参数前两个对应图片相对于左上角的新位置，后两个为图片的长宽
        network.setBounds(0, 0, 80, 80);
        //设置图片在文字的哪个方向,分别对应左，上，右，下
        networkRb.setCompoundDrawables(null, network, null, null);

        //底部导航的时候会发生图片的颜色变化，所以radiobutton中的照片不是一张，而是引用了自定义的选择器照片
        //本来使用的是getResources.getDrawable,不过已经过时，所以使用ContextCompat
        Drawable camera = ContextCompat.getDrawable(this, R.drawable.selector_camera_drawable);
        //当这个图片被绘制时，给他绑定一个矩形规定这个矩形,参数前两个对应图片相对于左上角的新位置，后两个为图片的长宽
        camera.setBounds(0, 0, 80, 80);
        //设置图片在文字的哪个方向,分别对应左，上，右，下
        cameraRb.setCompoundDrawables(null, camera, null, null);

        //底部导航的时候会发生图片的颜色变化，所以radiobutton中的照片不是一张，而是引用了自定义的选择器照片
        //本来使用的是getResources.getDrawable,不过已经过时，所以使用ContextCompat
        Drawable setting = ContextCompat.getDrawable(this, R.drawable.selector_setting_drawable);
        //当这个图片被绘制时，给他绑定一个矩形规定这个矩形,参数前两个对应图片相对于左上角的新位置，后两个为图片的长宽
        setting.setBounds(0, 0, 80, 80);
        //设置图片在文字的哪个方向,分别对应左，上，右，下
        settingRb.setCompoundDrawables(null, setting, null, null);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int FragmentIndex = intent.getIntExtra("id", 3);
        Toast.makeText(this, "获取到FragmentIndex" + FragmentIndex, Toast.LENGTH_SHORT);
    }
}