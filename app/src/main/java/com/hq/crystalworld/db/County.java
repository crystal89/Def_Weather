package com.hq.crystalworld.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Crystal on 2017/7/26.
 * 县
 */

public class County extends DataSupport {
    private int id;
    private String country_name;
    private String weather_id;
    private int city_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }
}
