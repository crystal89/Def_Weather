package com.hq.crystalworld.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Crystal on 2017/7/26.
 * 省份
 */

public class Province extends DataSupport {
    private int id;
    private String province_name;
    private int province_code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public int getProvince_code() {
        return province_code;
    }

    public void setProvince_code(int province_code) {
        this.province_code = province_code;
    }
}
