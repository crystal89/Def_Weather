package com.hq.crystalworld.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hq.crystalworld.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Crystal on 2017/8/16.
 */

/**
 * 创建list view中的adapter
 */
public class LocationListViewAdapter extends BaseAdapter {

    private Context _context;
    private List<HashMap<String, Object>> _data;

    public final class ViewHolder {
        public TextView city_text;
        public TextView temp_text;
        public Button delete_button;
    }

    public LocationListViewAdapter(Context context, List<HashMap<String, Object>> data) {
        _context = context;
        _data = data;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return _data.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(_context).inflate(R.layout.location_list_item, null);
            //实例化组件
            viewHolder.city_text = (TextView) convertView.findViewById(R.id.location_city);
            viewHolder.temp_text = (TextView) convertView.findViewById(R.id.location_temp);
            viewHolder.delete_button = (Button) convertView.findViewById(R.id.delete_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.city_text.setText((String) _data.get(position).get("city_name"));
        viewHolder.temp_text.setText((String) _data.get(position).get("city_temp"));
        //删除按钮事件
        viewHolder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemDeleteListener.onItemDeleteClick(position);
            }
        });
        return convertView;
    }

    //删除按钮的监听接口
    public interface onItemDeleteListener {
        public void onItemDeleteClick(int i);
    }

    private onItemDeleteListener mOnItemDeleteListener;

    public void setmOnItemDeleteListener(onItemDeleteListener mItemDeleteListener) {
        this.mOnItemDeleteListener = mItemDeleteListener;
    }
}
