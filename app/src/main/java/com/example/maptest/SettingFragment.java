package com.example.maptest;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.getSystemService;

public class SettingFragment extends Fragment  implements AdapterView.OnItemClickListener {
    private WifiManager wifiManager;
    private ListView settingListView;
    private ArrayList<Map<String, String>> settingListData1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        settingListView = view.findViewById(R.id.setting_listView);
        settingListView.setAdapter(getSimpleAdapter());
        settingListView.setAdapter(getSimpleAdapter());
        settingListView.setOnItemClickListener(this);
        setListViewHeightBasedOnChildren(settingListView);
    }

    private SimpleAdapter getSimpleAdapter() {
        settingListData1 = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("text", "网络设置");
        settingListData1.add(map);

        map = new HashMap<String, String>();
        map.put("text", "网络设备");
        settingListData1.add(map);

        map = new HashMap<String, String>();
        map.put("text", "相机设置");
        settingListData1.add(map);

        map = new HashMap<String, String>();
        map.put("text", "相机设备");
        settingListData1.add(map);

        return new SimpleAdapter(getActivity().getApplicationContext(), settingListData1,
                R.layout.list_item, new String[] { "text" },
                new int[] { R.id.tv_settingitem });
    }
    /***
     * 动态设置listview的高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }





    private void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<ScanResult> list;
        public MyAdapter(Context context, List<ScanResult> list) {
            // TODO Auto-generated constructor stub
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = null;
            view = inflater.inflate(R.layout.item_wifi_list, null);
            ScanResult scanResult = list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.ssid_textView);
            textView.setText(scanResult.SSID);
            TextView signalStrenth = (TextView) view.findViewById(R.id.signal_strenth);
            signalStrenth.setText(String.valueOf(Math.abs(scanResult.level)));
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            //判断信号强度，显示对应的指示图标
            if (Math.abs(scanResult.level) > 100) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wifi));
            } else if (Math.abs(scanResult.level) > 80) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wifi));
            } else if (Math.abs(scanResult.level) > 70) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wifi));
            } else if (Math.abs(scanResult.level) > 60) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wifi));
            } else if (Math.abs(scanResult.level) > 50) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wifi));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wifi));
            }
            return view;
        }

    }
}

