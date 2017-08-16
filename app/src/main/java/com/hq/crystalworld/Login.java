package com.hq.crystalworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Crystal on 2017/7/25.
 */

public class Login extends AppCompatActivity {

    private Button pass_button;
    private EditText login_account;
    private EditText login_password;
    private CheckBox save_password;
    private Button login_button;
    private Button register_account;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String account;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login_account = (EditText) findViewById(R.id.login_account);
        login_password = (EditText) findViewById(R.id.login_password);

        save_password = (CheckBox) findViewById(R.id.save_password);

        try {
            prefs = getSharedPreferences("data", MODE_PRIVATE);
            if (prefs != null) {
                boolean isRemember = prefs.getBoolean("save_password", false);
                if (isRemember) {
                    login_account.setText(prefs.getString("u_account", ""));
                    login_password.setText(prefs.getString("u_password", ""));
                    save_password.setChecked(isRemember);
                }
            }
            editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = login_account.getText().toString();
                password = login_password.getText().toString();
                if (account.equals("crystal") && password.equals("123456")) {
                    if (save_password.isChecked()) {
                        editor.putString("u_account", account);
                        editor.putString("u_password", password);
                        editor.putBoolean("save_password", true);
                    } else {
                        editor.putString("u_account", account);
                        editor.putString("u_password", password);
                        editor.putBoolean("save_password", false);
                    }
                    editor.commit();
                    changeToMainActivity();
                } else {
                    Toast.makeText(getBaseContext(), "请输入正确的用户名和密码或注册新账号！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        pass_button = (Button) findViewById(R.id.pass_button);
        pass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = "";
                password = "";
                editor.putString("u_account", account);
                editor.putString("u_password", password);
                editor.putBoolean("save_password", false);
                editor.commit();
                changeToMainActivity();
                Toast.makeText(getBaseContext(), "当前正在以游客身份进行访问！", Toast.LENGTH_SHORT).show();
            }
        });

        register_account = (Button) findViewById(R.id.register_button);
        register_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取账号
                account = login_account.getText().toString();
                //获取密码
                password = login_password.getText().toString();

                String u_account = prefs.getString("u_account", "");
                //判断账号是否已经存在或为空,账号和密码均填写正确，进行注册
                if (!account.isEmpty() && !account.equals(u_account)) {
                    editor.putString("u_account", account);
                    editor.putString("u_password", password);
                    if (save_password.isChecked())
                        editor.putBoolean("save_password", true);
                    else
                        editor.putBoolean("save_password", false);
                    editor.commit();
                    changeToMainActivity();
                } else {
                    Toast.makeText(getBaseContext(), "注册的用户名不能为空或已存在！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
