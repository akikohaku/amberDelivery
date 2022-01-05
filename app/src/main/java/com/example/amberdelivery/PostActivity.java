package com.example.amberdelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.amberdelivery.data.Global;

public class PostActivity extends AppCompatActivity {

    Global global = null;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postinfo);
        this.getSupportActionBar().setTitle("快递单详情");

        global = (Global) getApplication();
    }
    String id="";
    @Override

    protected void onResume() {

        super.onResume();
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        System.out.println(info);
        JSONObject res= JSON.parseObject(info);
        TextView _id=findViewById(R.id.postId);
        _id.setText(res.getString("id"));
        id=res.getString("id");

        TextView _sender=findViewById(R.id.postSender);
        _sender.setText(res.getString("senderName"));

        TextView _senderPhone=findViewById(R.id.postSenderPhone);
        _senderPhone.setText(res.getString("senderPhone"));

        TextView _senderAddress=findViewById(R.id.postSenderAddress);
        _senderAddress.setText(res.getString("senderAddress"));

        TextView _rcv=findViewById(R.id.postRcv);
        _rcv.setText(res.getString("rcvName"));

        TextView _rcvPhone=findViewById(R.id.postRcvPhone);
        _rcvPhone.setText(res.getString("rcvPhone"));

        TextView _rcvAddress=findViewById(R.id.postRcvAddress);
        _rcvAddress.setText(res.getString("rcvAddress"));

        TextView _content=findViewById(R.id.postContent);
        _content.setText(res.getString("content"));

        TextView _tips=findViewById(R.id.postTips);
        _tips.setText(res.getString("tips"));

        Button _postQR=findViewById(R.id.postGetQR);
        _postQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostActivity.this,ShowQRActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        CardView _postAdmin=findViewById(R.id.postAdmin);
        if(global.getType()==2){
            _postAdmin.setVisibility(View.VISIBLE);
        }else{
            _postAdmin.setVisibility(View.INVISIBLE);
        }

        _postAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PostActivity.this,AccessActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);

            }
        });

    }
}
