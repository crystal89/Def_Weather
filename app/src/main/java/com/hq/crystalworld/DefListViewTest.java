package com.hq.crystalworld;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Crystal on 2017/8/16.
 */

public class DefListViewTest extends AppCompatActivity {

    private ListView def_list_view;
    private List<Map<String, Object>> mapList;
    private Map<String, Object> map;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deflistview);

        def_list_view = (ListView) findViewById(R.id.def_list_view);

        mapList = new ArrayList<Map<String, Object>>();
        map = new HashMap<String, Object>();

        map.put("city", "");
        map.put("temp", "");
        map.put("remove_button", "");
        mapList.add(map);


        adapter = new ListViewAdapter(this, mapList);
        def_list_view.setAdapter(adapter);
    }

    public class ListViewAdapter extends BaseAdapter {
        private Context _context;
        private List<Map<String, Object>> _mapList;

        public final class ListItemView {
            public TextView city_text;
            public TextView temp_text;
            public Button remove_button;
        }

        private ListItemView _listItemView;

        public ListViewAdapter(Context context, List<Map<String, Object>> mapList) {
            _context = context;
            _mapList = mapList;
        }

        @Override
        public int getCount() {
            return _mapList.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
