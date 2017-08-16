package com.hq.crystalworld.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Crystal on 2017/7/28.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    @SerializedName("wind")
    public Wind wind;

    public class More {
        @SerializedName("txt")
        public String info;
    }

    public class Wind {
        @SerializedName("dir")
        public String dir;
        @SerializedName("sc")
        public String sc;
        @SerializedName("spd")
        public String speed;
    }
}
