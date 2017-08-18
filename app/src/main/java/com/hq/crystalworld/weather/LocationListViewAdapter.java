package com.hq.crystalworld.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hq.crystalworld.R;
import com.hq.crystalworld.util.LogUtil;

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

    public final class ListViewItem {
        public TextView city_text;
        public TextView temp_text;
        public Button delete_button;
    }

    private ListViewItem _listViewItem;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        _listViewItem = new ListViewItem();
        convertView = LayoutInflater.from(_context).inflate(R.layout.location_list_item, null);
        //实例化组件
        _listViewItem.city_text = (TextView) convertView.findViewById(R.id.location_city);
        _listViewItem.temp_text = (TextView) convertView.findViewById(R.id.location_temp);
        _listViewItem.delete_button = (Button) convertView.findViewById(R.id.delete_button);
        convertView.setTag(_listViewItem);
        _listViewItem.city_text.setText((String) _data.get(position).get("city_name"));
        _listViewItem.temp_text.setText((String) _data.get(position).get("city_temp"));
        return convertView;
    }
}
