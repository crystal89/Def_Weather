package com.hq.crystalworld.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hq.crystalworld.R;
import com.hq.crystalworld.gson.Weather;
import com.hq.crystalworld.util.HttpUtil;
import com.hq.crystalworld.util.LogUtil;
import com.hq.crystalworld.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Crystal on 2017/8/8.
 * http://guolin.tech/api/weather?cityid=%E6%B5%A6%E4%B8%9C%E6%96%B0%E5%8C%BA&key=bc0418b57b2d4918819d3974ac1285d9
 */

public class SearchCityActivity extends AppCompatActivity {

    private static final String TAG = "SEARCH_CITY";

    private static final int Add_CITY_ITEM = 1;

    private EditText search_city_text;
    private Button search_city_button;
    final boolean isSearchSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_city);

        search_city_text = (EditText) findViewById(R.id.search_city_text);
        search_city_button = (Button) findViewById(R.id.search_city_button);
        search_city_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_city_text.getText() == null)
                    return;
                searchCity(search_city_text.getText().toString());
                LogUtil.i(TAG, "do search");
            }
        });
    }

    public boolean searchCity(String cityName) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + cityName + "&key=bc0418b57b2d4918819d3974ac1285d9";
        //LogUtil.i(TAG, weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchCityActivity.this, "无法查找到当前城市！", Toast.LENGTH_SHORT).show();
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
                            //通知LocationFragment新增一个item内容
                            ConcernedCity.add_city_name = weather.basic.cityName;
                            ConcernedCity.add_city_temp = weather.now.temperature;
                            finish();
                        } else {
                            Toast.makeText(SearchCityActivity.this, "无法查找到当前城市！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return isSearchSuccess;
    }
}
