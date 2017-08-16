package com.hq.crystalworld.weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hq.crystalworld.MainActivity;
import com.hq.crystalworld.R;
import com.hq.crystalworld.db.City;
import com.hq.crystalworld.db.County;
import com.hq.crystalworld.db.Province;
import com.hq.crystalworld.util.HttpUtil;
import com.hq.crystalworld.util.LogUtil;
import com.hq.crystalworld.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Crystal on 2017/7/26.
 */

public class ChooseAreaFragment extends Fragment {

    public static final String TAG = "CHOOSE_AREA_FRAGMENT";

    /**
     * 级别
     */
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView areaTitle;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> areaAdapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省级列表
     */
    private List<Province> provinceList;
    /**
     * 市级列表
     */
    private List<City> cityList;
    /**
     * 县级列表
     */
    private List<County> countyList;
    /**
     * 被选中的省
     */
    private Province selectedProvince;
    /**
     * 被选中的市
     */
    private City selectedCity;
    /**
     * 被选中的县
     */
    private County selectedCounty;

    /**
     * 当前选中的级别
     */
    private int curLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //动态加载
        View view = inflater.inflate(R.layout.choose_area_fragment, container, false);
        areaTitle = (TextView) view.findViewById(R.id.area_title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        areaAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(areaAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (curLevel == LEVEL_PROVINCE) {
                        selectedProvince = provinceList.get(position);
                        queryCities();
                    } else if (curLevel == LEVEL_CITY) {
                        selectedCity = cityList.get(position);
                        queryCounties();
                    } else if (curLevel == LEVEL_COUNTY) {
                        String weatherId = countyList.get(position).getWeather_id();
                        LogUtil.i(TAG, weatherId);

                        //判断当前fragment是在mainactivity中还是weatheractivity中
                        if (getActivity() instanceof MainActivity) {
                            //设置为已经缓存的城市的天气
                            Intent intent = new Intent(getActivity(), WeatherActivity.class);
                            intent.putExtra("weather_id", weatherId);
                            LogUtil.i(TAG, getActivity().toString());
                            startActivity(intent);
                            getActivity().finish();
                        } else if (getActivity() instanceof WeatherActivity) {
                            //根据weatherid切换城市天气
                            WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                            weatherActivity.weather_drawer_layout.closeDrawers();
                            weatherActivity.weather_swipe_refresh_layout.setRefreshing(true);
                            weatherActivity.requestWeather(weatherId);
                        }
           /*             SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String weatherStr = prefs.getString("weather", null);
                        if (weatherStr != null) {
                            Weather weather = Utility.handleWeatherResponse(weatherStr);
                            if (weather.basic.weatherId != weatherId) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear();
                                editor.commit();
                                getActivity().getIntent().putExtra("weather_id", weatherId);
                            } else {
                                weatherId = weather.basic.weatherId;
                            }
                        } else {
                            getActivity().getIntent().putExtra("weather_id", weatherId);
                        }
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.framelayout_fragment, new WeatherActivity());
                        //back键返回到上一个fragment
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();*/
                    }
                }
            });
        }

        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (curLevel == LEVEL_COUNTY) {
                        queryCities();
                    } else if (curLevel == LEVEL_CITY)
                        queryProvinces();
                }
            });
        }
        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，没有再从服务器查询
     */
    private void queryProvinces() {
        areaTitle.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvince_name());
            }
            areaAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryAreaFromServer(address, "province");
        }
    }

    /**
     * 查询选中省份的所有城市，优先从数据库查询，没有再从服务器查询
     */
    private void queryCities() {
        areaTitle.setText(selectedProvince.getProvince_name());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("province_id = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCity_name());
            }
            areaAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvince_code();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryAreaFromServer(address, "city");
        }
    }

    /**
     * 查询选中城市的所有县，优先从数据库查询，没有再从服务器查询
     */
    private void queryCounties() {
        areaTitle.setText(selectedCity.getCity_name());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("city_id=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountry_name());
            }
            areaAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvince_code();
            int cityCode = selectedCity.getCity_code();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryAreaFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询area数据
     */
    private void queryAreaFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG, "load failure");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                LogUtil.i(TAG, responseStr);
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseStr);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseStr, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseStr, selectedCity.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
