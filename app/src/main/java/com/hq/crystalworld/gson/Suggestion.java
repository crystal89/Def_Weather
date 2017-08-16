package com.hq.crystalworld.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Crystal on 2017/7/28.
 */

public class Suggestion {

    @SerializedName("air")
    public Air air;

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("sport")
    public Sport sport;

    @SerializedName("uv")
    public UV uv;

    public class Air {
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;
    }

    public class Comfort {
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;
    }

    public class Drsg {
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;
    }

    public class UV {
        @SerializedName("brf")
        public String brf;

        @SerializedName("txt")
        public String info;
    }
}
