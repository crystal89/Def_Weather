package com.hq.crystalworld.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Crystal on 2017/7/28.
 * 根据api返回的json数据格式定义
 * * @SerializedName以注解的方式与Json中的数据对应
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
