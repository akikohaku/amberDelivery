package com.example.amberdelivery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import java.util.ArrayList;
import java.util.List;

public class AddDeliverActivity extends AppCompatActivity {
    Global global = null;
    private MainActivity mainActivity;
    String postid="0";
    MListAdapter2 adapter2=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpostdeliver);
        this.getSupportActionBar().setTitle("添加权限");

        global = (Global) getApplication();
    }

    @Override
    public void onResume() {

        super.onResume();

        Intent intent=getIntent();
        String id=intent.getStringExtra("id");
        getDeliveryUser(id);
        postid=id;
    }

    public void showUserDelivery(List<User> toshow){
        ListView _adminList= findViewById(R.id.addPostList);
        adapter2=new MListAdapter2(AddDeliverActivity.this,R.layout.deliverylist,toshow);
        _adminList.setAdapter(adapter2);
    }


    class MListAdapter2 extends ArrayAdapter<User> {
        private int resourceId;
        List<User> object=null;
        public MListAdapter2(Context context, int textViewResourceId, List<User> objects){
            super(context,textViewResourceId,objects);
            resourceId=textViewResourceId;
            this.object=objects;
        }
        String id="0";
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            User pos=getItem(position);   //获取当前项的实例
            View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            view.setTag(pos.content);
            id=pos.id;
            TextView _clist1=(TextView) view.findViewById(R.id.c1);
            TextView _clist2=(TextView) view.findViewById(R.id.c2);
            TextView _clist3=(TextView) view.findViewById(R.id.c3);
            Button _deviler=(Button) view.findViewById(R.id.button2);
            _clist1.setText(pos.id);
            _clist2.setText(pos.Phone);
            _clist3.setText(pos.Name);
            _deviler.setText("添加");
            _deviler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddPostDeliver(pos.id,postid);
                }
            });

            return view;
        }
        @Override
        public User getItem(int position){
            return object.get(position)  ;
        }
    }

    public void getDeliveryUser(String id) {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/getalluser",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res= JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            JSONArray family = res.getJSONArray("users");

                            List<User> toshow=new ArrayList<>();

                            for (int i = 0; i < family.size(); i++) {
                                String s1 = family.get(i).toString();
                                User temp=new User(s1);
                                if(temp.level>=5&&temp.level<10){
                                    toshow.add(new User(s1));
                                }

                            }
                            showUserDelivery(toshow);

                        }

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, param.toString());
    }


    public void AddPostDeliver(String uid,String target) {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("toid", uid);
            param.put("topost", target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/addpostdeliver",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res= JSON.parseObject(result);
                        if(res.getString("status").equals("0")) {
                            Intent intent = new Intent(AddDeliverActivity.this, AccessActivity.class);
                            intent.putExtra("id", postid);
                            startActivity(intent);
                            finish();
                        }else if(res.getString("status").equals("4")){
                            Toast.makeText(getApplicationContext(), "已经添加过了",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, param.toString());
    }

    class User{
        public String content="";
        public String id="";
        public String Name="";
        public String Phone="";
        public int level=10;
        public  User(String in){
            JSONObject res= JSON.parseObject(in);
            content=in;
            id=res.getString("uuid");
            Phone=res.getString("phone");
            Name=res.getString("username");
            level=Integer.valueOf(res.getString("level"));
        }

    }
}