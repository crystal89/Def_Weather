package com.hq.crystalworld.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hq.crystalworld.R;
import com.hq.crystalworld.util.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Crystal on 2017/8/7.
 */

public class LocationFragment extends Fragment {

    private static final String TAG = "LOCATION_FRAGMENT";

    private LinearLayout location_items_layout;
    private ImageButton add_city_button;

    private ListView location_list_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_fragment, container, false);

        add_city_button = (ImageButton) view.findViewById(R.id.add_city_button);
        location_items_layout = (LinearLayout) view.findViewById(R.id.location_items_layout);

        //location_list_view = (ListView) view.findViewById(R.id.location_list_view);

        /*
        //do test
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("浦东新区/35\r\n静安区/32\r\n");
        saveToFile(stringBuilder);*/

        //获取文件中所有关注的城市信息
        HashMap<String, String> hashMap = loadFromFile();
        if (!hashMap.isEmpty()) {
            //更新对应的温度
            addLocationItems(hashMap);
            ConcernedCity.hashMap_city.putAll(hashMap);
        }

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
        if (ConcernedCity.hashMap_city.isEmpty())
            return;
        saveToFile(ConcernedCity.hashMap_city);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "resume");

        //新增关注的城市
        if (ConcernedCity.add_city_name.isEmpty())
            return;

        if (!ConcernedCity.hashMap_city.containsKey(ConcernedCity.add_city_name)) {
            ConcernedCity.hashMap_city.put(ConcernedCity.add_city_name, ConcernedCity.add_city_temp);
            //添加item
            initLocationItemContext(ConcernedCity.add_city_name, ConcernedCity.add_city_temp);
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
            initLocationItemContext(keys_it.next(), valuse_it.next());
        }
    }

    private void initLocationItemContext(String add_name, String add_temp) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.location_item, location_items_layout, false);

        TextView city = (TextView) itemView.findViewById(R.id.location_city);
        TextView temp = (TextView) itemView.findViewById(R.id.location_temp);
        city.setText(add_name);
        temp.setText(add_temp);

        location_items_layout.addView(itemView);
    }

    private void saveToFile(HashMap<String, String> in_content) {
        LogUtil.i("save to file", in_content.toString());
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
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
