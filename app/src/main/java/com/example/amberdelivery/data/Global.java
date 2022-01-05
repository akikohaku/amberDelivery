package com.example.amberdelivery.data;
import android.app.Application;

import com.example.amberdelivery.MainActivity;

public class Global extends Application{
    private String userName="";
    private String userId="";
    private String token="";
    private String phone="";
    private int level=10;
    private int type=2;
    private MainActivity mainActivity=null;

    @Override
    public void onCreate(){
        super.onCreate();
    }
    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String c){
        this.userName= c;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
