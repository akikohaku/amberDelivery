package com.example.amberdelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView _textview=null;
    Global global=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().setTitle("");

        global = (Global) getApplication();
        global.setMainActivity(this);

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        global.setUserId(pref.getString("uuid",""));
        global.setUserName(pref.getString("username",""));
        global.setPhone(pref.getString("phone",""));
        global.setType(pref.getInt("type",0));
        global.setToken(pref.getString("token",""));

        if(global.getType()==0){
            //用户
            toNormal();
        }

        if(global.getType()==1){
            //快递员
            toCourier();
        }

        if(global.getType()==2){
            //管理员
            toAdmin();
        }


        if(global.getUserId().equals("")){
            Intent i=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        global.setMainActivity(this);
        if(global.getUserId().equals("")){
            Intent i=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        }else
        if(global.getType()==0){
            //用户
            toNormal();
        }else

        if(global.getType()==1){
            //快递员
            toCourier();
        }else

        if(global.getType()==2){
            //管理员
            toAdmin();
        }
    }

    public void check() {
        JSONObject param = new JSONObject();
        try {
            param.put("id", "1234");
//            param.put("imei", Config.getImei(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/test",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, param.toString());
    }

    public void toCourier(){
        Intent i=new Intent(MainActivity.this,CourierActivity.class);
        startActivity(i);
    }
    public void toNormal(){
        Intent i=new Intent(MainActivity.this,NormalActivity.class);
        startActivity(i);
    }
    public void toAdmin(){
        Intent i=new Intent(MainActivity.this,AdminActivity.class);
        startActivity(i);
    }

}

