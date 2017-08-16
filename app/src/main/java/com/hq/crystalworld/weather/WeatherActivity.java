package com.hq.crystalworld.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hq.crystalworld.R;
import com.hq.crystalworld.gson.Forecast;
import com.hq.crystalworld.gson.Weather;
import com.hq.crystalworld.service.AutoUpdateService;
import com.hq.crystalworld.util.HttpUtil;
import com.hq.crystalworld.util.LogUtil;
import com.hq.crystalworld.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Crystal on 2017/7/27.
 */

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WEATHER_FRAGMENT";
    /**
     * 获取bing每日一图
     */
    private ImageView bing_pic;

    public SwipeRefreshLayout weather_swipe_refresh_layout;
    private String mWeatherId;

    private ScrollView weather_Layout;
    /**
     * 选中的城市
     */
    private TextView title_city;

    /**
     * 当前时间下的天气状况
     */
    private TextView title_update_time;
    private TextView weather_cur_degree_text;
    private TextView weather_cur_info_text;

    /**
     * 天气预报
     */
    private LinearLayout weather_forecast_layout;

    /**
     * 空气质量
     */
    private TextView weather_aqi_text;
    private TextView weather_pm25_text;
    private TextView weather_qlty_text;

    /**
     * 生活建议
     */
    private TextView weather_suggestion_comf_text;
    private TextView weather_suggestion_cw_text;
    private TextView weather_suggestion_sport_text;

    /**
     * 缓存信息
     */
    private SharedPreferences prefs;

    public DrawerLayout weather_drawer_layout;
    private Button nav_choose_area_button;

    private ImageButton nav_location_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);

        bing_pic = (ImageView) findViewById(R.id.bing_pic);

        weather_swipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.weather_swipe_refresh_layout);
        weather_swipe_refresh_layout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        weather_Layout = (ScrollView) findViewById(R.id.weather_layout);

        title_city = (TextView) findViewById(R.id.title_city);
        title_update_time = (TextView) findViewById(R.id.title_update_time);

        weather_cur_degree_text = (TextView) findViewById(R.id.weather_cur_degree_text);
        weather_cur_info_text = (TextView) findViewById(R.id.weather_cur_info_text);

        weather_forecast_layout = (LinearLayout) findViewById(R.id.weather_forecast_layout);

        weather_aqi_text = (TextView) findViewById(R.id.weather_aqi_text);
        weather_pm25_text = (TextView) findViewById(R.id.weather_pm25_text);
        weather_qlty_text = (TextView) findViewById(R.id.weather_qlty_text);

        weather_suggestion_comf_text = (TextView) findViewById(R.id.weather_suggestion_comf_text);
        weather_suggestion_cw_text = (TextView) findViewById(R.id.weather_suggestion_cw_text);
        weather_suggestion_sport_text = (TextView) findViewById(R.id.weather_suggestion_drsg_text);

        weather_drawer_layout = (DrawerLayout) findViewById(R.id.weather_drawer_layout);
        nav_choose_area_button = (Button) findViewById(R.id.nav_choose_area_button);
        nav_choose_area_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weather_drawer_layout.openDrawer(GravityCompat.START);
            }
        });

        nav_location_button = (ImageButton) findViewById(R.id.nav_location_button);
        nav_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weather_drawer_layout.isDrawerOpen(GravityCompat.END))
                    weather_drawer_layout.closeDrawer(GravityCompat.END);
                else {
                    weather_drawer_layout.openDrawer(GravityCompat.END);
                }
            }
        });

        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);

            String bingPic = prefs.getString("bing_pic", null);
            if (bingPic != null) {
                Glide.with(WeatherActivity.this).load(bingPic).into(bing_pic);
            } else {
                LoadBingPic();
            }

            String weatherStr = prefs.getString("weather", null);
            if (weatherStr != null) {
                //有缓存时直接解析天气数据
                Weather weather = Utility.handleWeatherResponse(weatherStr);
                mWeatherId = weather.basic.weatherId;
                showWeatherInfo(weather);
            } else {
                mWeatherId = getIntent().getStringExtra("weather_id");
                if (mWeatherId == null) {
                    //第一次进入app时默认显示北京天气
                    mWeatherId = "CN101010100";
                }
                //无缓存时去服务器查询天气
                weather_Layout.setVisibility(View.INVISIBLE);
                requestWeather(mWeatherId);
            }
            weather_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestWeather(mWeatherId);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather, container, false);
        bing_pic = (ImageView) view.findViewById(R.id.bing_pic);

        weather_swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.weather_swipe_refresh_layout);
        weather_swipe_refresh_layout.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimary));

        weather_Layout = (ScrollView) view.findViewById(R.id.weather_layout);

        title_city = (TextView) view.findViewById(R.id.title_city);
        title_update_time = (TextView) view.findViewById(R.id.title_update_time);

        weather_cur_degree_text = (TextView) view.findViewById(R.id.weather_cur_degree_text);
        weather_cur_info_text = (TextView) view.findViewById(R.id.weather_cur_info_text);

        weather_forecast_layout = (LinearLayout) view.findViewById(R.id.weather_forecast_layout);

        weather_aqi_text = (TextView) view.findViewById(R.id.weather_aqi_text);
        weather_pm25_text = (TextView) view.findViewById(R.id.weather_pm25_text);
        weather_qlty_text = (TextView) view.findViewById(R.id.weather_qlty_text);

        weather_suggestion_comf_text = (TextView) view.findViewById(R.id.weather_suggestion_comf_text);
        weather_suggestion_cw_text = (TextView) view.findViewById(R.id.weather_suggestion_cw_text);
        weather_suggestion_sport_text = (TextView) view.findViewById(R.id.weather_suggestion_drsg_text);

        weather_drawer_layout = (DrawerLayout) view.findViewById(R.id.weather_drawer_layout);
        nav_choose_area_button = (Button) view.findViewById(R.id.nav_choose_area_button);
        nav_choose_area_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weather_drawer_layout.openDrawer(GravityCompat.START);
            }
        });

        nav_location_button = (ImageButton) view.findViewById(R.id.nav_location_button);
        nav_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }*/

   /* @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            String bingPic = prefs.getString("bing_pic", null);
            if (bingPic != null) {
                Glide.with(getActivity()).load(bingPic).into(bing_pic);
            } else {
                LoadBingPic();
            }

            String weatherStr = prefs.getString("weather", null);

            if (weatherStr != null) {
                //有缓存时直接解析天气数据
                Weather weather = Utility.handleWeatherResponse(weatherStr);
                mWeatherId = weather.basic.weatherId;
                showWeatherInfo(weather);
            } else {
                mWeatherId = getActivity().getIntent().getStringExtra("weather_id");
                if (mWeatherId == null) {
                    //第一次进入app时默认显示北京天气
                    mWeatherId = "CN101010100";
                }
                //无缓存时去服务器查询天气
                weather_Layout.setVisibility(View.INVISIBLE);
                requestWeather(mWeatherId);
            }
            weather_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestWeather(mWeatherId);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 加载bing每日一图
     */
    private void LoadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bing_pic);
                    }
                });
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        LogUtil.i(TAG, weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        weather_swipe_refresh_layout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        }
                        weather_swipe_refresh_layout.setRefreshing(false);
                    }
                });
            }
        });
        LoadBingPic();
    }

    /**
     * 处理并展示weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            LogUtil.i(TAG, cityName);

            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String curDegree = weather.now.temperature + "°";
            String curWeatherInfo = weather.now.more.info;
            String curWeatherWindDir = weather.now.wind.dir;
            String curWeatherWindSc = weather.now.wind.sc;
            String curWeatherWindSpeed = weather.now.wind.speed;

            title_city.setText(cityName);
            title_update_time.setText(updateTime);
            weather_cur_degree_text.setText(curDegree);
            weather_cur_info_text.setText(curWeatherInfo + "  风向：" + curWeatherWindDir + "，" + curWeatherWindSc + "  风速：" + curWeatherWindSpeed);

            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, weather_forecast_layout, false);

                TextView weather_date_text = (TextView) view.findViewById(R.id.weather_date_text);
                TextView weather_info_text = (TextView) view.findViewById(R.id.weather_info_text);
                TextView weather_tmp_max_text = (TextView) view.findViewById(R.id.weather_tmp_max_text);
                TextView weather_tmp_min_text = (TextView) view.findViewById(R.id.weather_tmp_min_text);
                weather_date_text.setText(forecast.date);
                weather_info_text.setText(forecast.more.info);
                weather_tmp_max_text.setText(forecast.temperature.max + "°");
                weather_tmp_min_text.setText(forecast.temperature.min + "°");

                weather_forecast_layout.addView(view);
            }

            if (weather.aqi != null) {
                weather_aqi_text.setText(weather.aqi.aqiCity.aqi);
                weather_pm25_text.setText(weather.aqi.aqiCity.pm25);
                weather_qlty_text.setText(weather.aqi.aqiCity.qlty);
            }

            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String drsg = "运动建议：" + weather.suggestion.sport.info;
//        String uv = "UV：" + weather.suggestion.uv.info;
            weather_suggestion_comf_text.setText(comfort);
            weather_suggestion_cw_text.setText(carWash);
            weather_suggestion_sport_text.setText(drsg);

            weather_Layout.setVisibility(View.VISIBLE);

            //每隔8小时自动更新天气状况
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
        }
    }
}
