package com.example.amberdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import com.alibaba.fastjson.*;
import com.example.amberdelivery.utils.PageUtil;

import org.apache.commons.codec.digest.DigestUtils;

public class LoginActivity extends AppCompatActivity {
    Global global=null;
    private MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.getSupportActionBar().setTitle("登录");

        Button _registerButton=findViewById(R.id.register);
        global = (Global) getApplication();

        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        Button _loginButton=findViewById(R.id.loginloginbt);
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    public void login() {
        JSONObject param = new JSONObject();
        EditText _userName=findViewById(R.id.loginName);
        EditText _userPass=findViewById(R.id.loginPass);
        if(_userName.length()==0){
            new  AlertDialog.Builder(this)
                    .setTitle("用户名不能为空" )
                    .setMessage("请重新输入" )
                    .setPositiveButton("确定" ,  null )
                    .show();
            return;
        }
        if(_userPass.length()==0){
            new  AlertDialog.Builder(this)
                    .setTitle("密码不能为空" )
                    .setMessage("请重新输入" )
                    .setPositiveButton("确定" ,  null )
                    .show();
            return;
        }
        try {
            param.put("username", _userName.getText().toString());
            param.put("password", DigestUtils.md5Hex(_userPass.getText().toString()));
//            param.put("imei", Config.getImei(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoginActivity that=this;
        new HttpConnection("https://wechat.api.kohaku.xin:11731/login",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res=JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            global.setUserId(res.getString("uuid"));
                            global.setToken(res.getString("token"));
                            getProfile();
                        }else{
                            new  AlertDialog.Builder(that)
                                    .setTitle("账号或密码错误" )
                                    .setMessage("请重新输入" )
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        }

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, param.toString());
    }
    public void getProfile() {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("target", global.getUserId());
//            param.put("imei", Config.getImei(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoginActivity that=this;
        new HttpConnection("https://wechat.api.kohaku.xin:11731/getprofile",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res=JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            global.setUserName(res.getString("username"));
                            global.setPhone(res.getString("phone"));
                            if(Integer.valueOf(res.getString("level").toString())>=10){
                                global.setType(0);
                                global.getMainActivity().toNormal();

                            }else if(Integer.valueOf(res.getString("level").toString())>=5){
                                global.setType(1);
                                global.getMainActivity().toCourier();
                            }else{
                                global.setType(2);
                                global.getMainActivity().toAdmin();
                            }
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("uuid",global.getUserId());
                            editor.putString("username",global.getUserName());
                            editor.putString("phone",global.getPhone());
                            editor.putString("token",global.getToken());
                            editor.putInt("type",global.getType());
                            editor.commit();

                        }

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {
                new  AlertDialog.Builder(that)
                        .setTitle("未知系统错误" )
                        .setMessage("请重新输入" )
                        .setPositiveButton("确定" ,  null )
                        .show();
            }
        }, param.toString());
    }
}
