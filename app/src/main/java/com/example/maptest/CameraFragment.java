package com.example.maptest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CameraFragment extends Fragment {
    private List<CameraInfo> mCameraInfoList = new ArrayList<CameraInfo>();
    private ListView cameraInfoListView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.camera_fragment, container, false);

        initCameraInfo();//初始化相机数据
        CameraInfoAdapter cameraInfoAdapter = new CameraInfoAdapter(getActivity().getApplicationContext(), R.layout.camerainfo_item_view, mCameraInfoList);
        cameraInfoListView = view.findViewById(R.id.camera_info_listview);
        cameraInfoListView.setAdapter(cameraInfoAdapter);
        cameraInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CameraInfoActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void initCameraInfo() {
        for (int i = 0; i < 2; i++) {
            CameraInfo cameraInfo_1 = new CameraInfo(147, "192.168.31.12", "30.032", "103.045");
            CameraInfo cameraInfo_2 = new CameraInfo(245, "192.168.31.5", "30.152", "103.348");
            CameraInfo cameraInfo_3 = new CameraInfo(364, "192.168.31.41", "30.532", "103.345");
            CameraInfo cameraInfo_4 = new CameraInfo(445, "192.168.31.21", "30.732", "103.475");
            CameraInfo cameraInfo_5 = new CameraInfo(573, "192.168.31.75", "30.582", "103.645");
            CameraInfo cameraInfo_6 = new CameraInfo(642, "192.168.31.41", "30.532", "103.345");
            CameraInfo cameraInfo_7 = new CameraInfo(742, "192.168.31.41", "30.532", "103.345");
            CameraInfo cameraInfo_8 = new CameraInfo(821, "192.168.31.41", "30.532", "103.345");
            CameraInfo cameraInfo_9 = new CameraInfo(923, "192.168.31.41", "30.532", "103.345");
            mCameraInfoList.add(cameraInfo_1);
            mCameraInfoList.add(cameraInfo_2);
            mCameraInfoList.add(cameraInfo_3);
            mCameraInfoList.add(cameraInfo_4);
            mCameraInfoList.add(cameraInfo_5);
            mCameraInfoList.add(cameraInfo_6);
            mCameraInfoList.add(cameraInfo_7);
            mCameraInfoList.add(cameraInfo_8);
            mCameraInfoList.add(cameraInfo_9);
        }

    }


}
