package com.example.amberdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import com.alibaba.fastjson.*;
import com.example.amberdelivery.utils.PageUtil;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

public class AccessActivity extends AppCompatActivity {
    Global global = null;
    private MainActivity mainActivity;
    MListAdapter2 adapter2=null;
    String postid="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access);
        this.getSupportActionBar().setTitle("管理");

        global = (Global) getApplication();

        ListView _accessList=findViewById(R.id.accessList);

    }

    @Override
    public void onResume() {

        super.onResume();

        Intent intent=getIntent();
        String id=intent.getStringExtra("id");
        getDeliveryUser(id);
        postid=id;

        TextView _accessId=findViewById(R.id.accessId);
        _accessId.setText(id);

        Button _addDeliver=findViewById(R.id.accessAdd);
        _addDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AccessActivity.this,AddDeliverActivity.class);
                intent.putExtra("id",postid);
                startActivity(intent);
                finish();
            }
        });

        Button _accessDelete=findViewById(R.id.accessDelete);
        _accessDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost();
            }
        });
    }

    public void deletePost(){
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("target", postid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/deletepost",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res= JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            Toast.makeText(getApplicationContext(), "删除成功",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(AccessActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, param.toString());
    }

    public void getDeliveryUser(String id) {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("target", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/getpostdeliver",
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

    public void showUserDelivery(List<User> toshow){
        ListView _adminList= findViewById(R.id.accessList);
        adapter2=new MListAdapter2(AccessActivity.this,R.layout.deliverylist,toshow);
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
            _deviler.setText("删除");
            _deviler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePostDeliver(pos.id,postid);
                }
            });

            return view;
        }
        @Override
        public User getItem(int position){
            return object.get(position)  ;
        }
    }

    public void deletePostDeliver(String id,String target) {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("toid", id);
            param.put("topost", target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/deletepostdeliver",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {

                        getDeliveryUser(postid);
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