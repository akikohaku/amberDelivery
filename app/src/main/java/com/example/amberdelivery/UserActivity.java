package com.example.amberdelivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amberdelivery.data.Global;

public class UserActivity extends AppCompatActivity {
    Global global = null;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        this.getSupportActionBar().setTitle("个人信息");

        global = (Global) getApplication();

        Button _logout=findViewById(R.id.logout);
        _logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.setUserId("");
                global.setPhone("");
                global.setUserName("");
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                Intent i=new Intent(UserActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();

        TextView _userid=findViewById(R.id.userId);
        TextView _userName=findViewById(R.id.userName);
        TextView _userPhone=findViewById(R.id.userPhone);

        _userid.setText(global.getUserId());
        _userName.setText(global.getUserName());
        _userPhone.setText(global.getPhone());
    }
}