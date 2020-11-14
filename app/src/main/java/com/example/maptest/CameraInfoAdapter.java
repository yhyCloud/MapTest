package com.example.maptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CameraInfoAdapter extends ArrayAdapter {
    private int resourceId;
    Context mContext;

    public CameraInfoAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<CameraInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CameraInfo cameraInfo = (CameraInfo) getItem(position);
        View view;
//        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        } else {
            view = convertView;
        }
        //获取子控件布局并添加内容
//        ImageView cameraImage = view.findViewById(R.id.camera_image);
        TextView cameraId = view.findViewById(R.id.camera_info_id);
        TextView cameraIp = view.findViewById(R.id.camera_info_ip);
        TextView cameraGPS = view.findViewById(R.id.camera_info_gps);

        cameraId.setText("相机id："+cameraInfo.getId());
        cameraIp.setText("相机ip："+cameraInfo.getIP());
        cameraGPS.setText("维度：" + cameraInfo.getLatitude() + " 经度" + cameraInfo.getLongitude());
        return view;
    }
}
