package com.hq.crystalworld.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Crystal on 2017/7/26.
 * 市
 */

public class City extends DataSupport {
    private int id;
    private String city_name;
    private int city_code;
    private int province_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public int getCity_code() {
        return city_code;
    }

    public void setCity_code(int city_code) {
        this.city_code = city_code;
    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }
}
