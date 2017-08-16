package com.hq.crystalworld.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by Crystal on 2017/7/25.
 */

public class GobalApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext() {
        return context;
    }
}
