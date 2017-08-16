package com.hq.crystalworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hq.crystalworld.util.LogUtil;
import com.hq.crystalworld.weather.ChooseAreaFragment;
import com.hq.crystalworld.weather.WeatherActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private String curUser;

    private DrawerLayout drawerLayout;

    private Fragment chooseAreaFragment;
    private Fragment aboutFragment;

    private Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* //将背景图片与状态栏融合
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/

        chooseAreaFragment = new ChooseAreaFragment();
        aboutFragment = new AboutFragment();

        //设置默认显示天气状况
        changeFragment(chooseAreaFragment);

        try {
            prefs = getSharedPreferences("data", MODE_PRIVATE);
            if (prefs != null) ;
            curUser = prefs.getString("u_account", "");
            if (curUser.isEmpty()) {
                curUser = "游客";
            }

            SharedPreferences weather_prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (weather_prefs.getString("weather", null) != null) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                changeFragment(chooseAreaFragment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle(curUser);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_weather);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_personalInfo:
                        LogUtil.d("personal fragment", "change");
                        break;
                    case R.id.nav_weather:
                        LogUtil.d("weather fragment", "change");
                        /*if (curFragment != chooseAreaFragment)
                            changeFragment(chooseAreaFragment);*/
                        break;
                    case R.id.nav_album:
                        LogUtil.d("album fragment", "change");
                        break;
                    case R.id.nav_setting:
                        break;
                    case R.id.nav_about:
                        LogUtil.d("aboutFragment fragment", "change");
                        if (curFragment != aboutFragment)
                            changeFragment(aboutFragment);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    public void changeFragment(Fragment _fragment) {
        curFragment = _fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout_fragment, _fragment);
        //back键返回到上一个fragment
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
