package com.hq.crystalworld.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hq.crystalworld.R;
import com.hq.crystalworld.util.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Crystal on 2017/8/7.
 */

public class LocationFragment extends Fragment {

    private static final String TAG = "LOCATION_FRAGMENT";

    private LinearLayout location_items_layout;
    private ImageButton add_city_button;

    private ListView list_view;
    private List<HashMap<String, Object>> hashMapList;
    private HashMap<String, Object> map;
    private LocationListViewAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_fragment, container, false);

        add_city_button = (ImageButton) view.findViewById(R.id.add_city_button);
        location_items_layout = (LinearLayout) view.findViewById(R.id.location_items_layout);

      /*
        //do test
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("浦东新区/35\r\n静安区/32\r\n");
        saveToFile(stringBuilder);*/

        list_view = (ListView) view.findViewById(R.id.list_view);
        hashMapList = new ArrayList<HashMap<String, Object>>();
        map = new HashMap<String, Object>();

        //获取文件中所有关注的城市信息
        final HashMap<String, String> hashMap = loadFromFile();
        if (!hashMap.isEmpty()) {
            //更新对应的温度
            addLocationItems(hashMap);
            ConcernedCity.hashMap_city.putAll(hashMap);
        }

        listAdapter = new LocationListViewAdapter(getContext(), hashMapList);
        list_view.setAdapter(listAdapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //getFragmentManager().popBackStackImmediate();
                Toast.makeText(getContext(), "返回到" + parent.getItemAtPosition(position).toString() + "天气展示界面！", Toast.LENGTH_SHORT).show();
            }
        });

        //listview中删除按钮的点击事件
        listAdapter.setmOnItemDeleteListener(new LocationListViewAdapter.onItemDeleteListener() {
            @Override
            public void onItemDeleteClick(int i) {
                //删除concernedCity中hashmap的信息
                String city_name = hashMapList.get(i).get("city_name").toString();
                if (ConcernedCity.hashMap_city.containsKey(city_name)) {
                    ConcernedCity.hashMap_city.remove(city_name);
                }
                hashMapList.remove(i);
                listAdapter.notifyDataSetChanged();
                LogUtil.i(TAG, "remove item");
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //添加新的城市的天气
        add_city_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入城市查找页面
                Intent intent = new Intent(getActivity(), SearchCityActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "pause");

        //保存关注的城市
        saveToFile(ConcernedCity.hashMap_city);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "resume");

        //新增关注的城市
        if (ConcernedCity.add_city_name == null)
            return;

        if (!ConcernedCity.hashMap_city.containsKey(ConcernedCity.add_city_name)) {
            ConcernedCity.hashMap_city.put(ConcernedCity.add_city_name, ConcernedCity.add_city_temp);
            //添加item
            //initLocationItemContext(ConcernedCity.add_city_name, ConcernedCity.add_city_temp);
            map.put("city_name", ConcernedCity.add_city_name);
            map.put("city_temp", ConcernedCity.add_city_temp);
            hashMapList.add(map);
            LogUtil.i(TAG, map.toString());
            //刷新listview
            listAdapter.notifyDataSetChanged();
        }

        //清空新增关注的城市
        ConcernedCity.add_city_name = null;
        ConcernedCity.add_city_temp = null;
    }

    //新增关注的城市
    private void addLocationItems(HashMap<String, String> hashMap_city) {
        Set<String> keys = hashMap_city.keySet();
        Iterator<String> keys_it = keys.iterator();
        Collection<String> values = hashMap_city.values();
        Iterator<String> valuse_it = values.iterator();

        while (keys_it.hasNext()) {
            String city_name = keys_it.next();
            String city_temp = valuse_it.next();
            //initLocationItemContext(city_name, city_temp);

            map.put("city_name", city_name);
            map.put("city_temp", city_temp);
            hashMapList.add(map);
        }
    }

    private void initLocationItemContext(String add_name, String add_temp) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.location_list_item, location_items_layout, false);
        TextView city = (TextView) itemView.findViewById(R.id.location_city);
        TextView temp = (TextView) itemView.findViewById(R.id.location_temp);
        city.setText(add_name);
        temp.setText(add_temp + "°");

        location_items_layout.addView(itemView);
    }

    /**
     * 将关注的城市信息保存到文件中
     */
    private void saveToFile(HashMap<String, String> in_content) {
        LogUtil.i("save to file", in_content.toString());
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            //全覆盖方式保存
            out = getContext().openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));

            Set<String> keys = in_content.keySet();
            Iterator<String> keys_it = keys.iterator();
            Collection<String> values = in_content.values();
            Iterator<String> valuse_it = values.iterator();

            StringBuilder stringBuilder = new StringBuilder();
            while (keys_it.hasNext()) {
                stringBuilder.append(keys_it.next() + "/" + valuse_it.next() + "\r\n");
            }
            writer.write(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    //刷新文件流的缓冲
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件中加载已经保存的关注的城市信息
     */
    private HashMap<String, String> loadFromFile() {
        FileInputStream in = null;
        BufferedReader reader = null;
        HashMap<String, String> out_content = new HashMap<String, String>();
        try {
            in = getContext().openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                LogUtil.i("load from file", line);
                out_content.put(line.split("/")[0], line.split("/")[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out_content;
    }
}
