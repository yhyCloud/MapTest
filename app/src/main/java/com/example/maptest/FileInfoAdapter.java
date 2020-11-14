package com.example.maptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FileInfoAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<String> mGroup;
    private ArrayList<ArrayList<String>> mItemList;
    private final LayoutInflater mInflater;

    public FileInfoAdapter(Context context, ArrayList<String> group, ArrayList<ArrayList<String>> itemList) {
        this.mContext = context;
        this.mGroup = group;
        this.mItemList = itemList;
        mInflater = LayoutInflater.from(mContext);
    }



    @Override
    public int getGroupCount() {
        return mGroup.size();//父项的个数
    }

    @Override
    public int getChildrenCount(int i) {
        return mItemList.get(i).size();//某个父项子项的个数
    }

    @Override
    public Object getGroup(int i) {
        return mGroup.get(i);//获得某个父项
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mItemList.get(groupPosition).get(childPosition);//获得某个子项
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;//父项的id
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //获取父项的view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.expendlistview_parentitem, parent, false);
        }
        String group = mGroup.get(groupPosition);
        TextView tvGroup = convertView.findViewById(R.id.expendlistview_main_tv);
        tvGroup.setText(group);
        return convertView;
    }

    //获取子项的id
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String child = mItemList.get(groupPosition).get(childPosition);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.expendlistview_childitem,parent,false);
        }
        TextView tvChild = (TextView)convertView.findViewById(R.id.file_name);
//        tvChild.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,child,Toast.LENGTH_SHORT).show();
//            }
//        });
        tvChild.setText(child);
        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
