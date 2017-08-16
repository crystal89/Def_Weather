package com.hq.crystalworld.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Crystal on 2017/7/28.
 */

public class AQI {

    @SerializedName("city")
    public AQICity aqiCity;

    public class AQICity {
        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm25;

        @SerializedName("qlty")
        public String qlty;
    }
}
