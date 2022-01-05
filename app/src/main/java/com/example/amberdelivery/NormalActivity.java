package com.example.amberdelivery;

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
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NormalActivity extends AppCompatActivity {
    Global global = null;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normaluser);
        this.getSupportActionBar().setTitle("您好");

        global = (Global) getApplication();
    }
    NormalActivity.MListAdapter adapter=null;
    @Override
    protected  void onResume() {

        super.onResume();

        TextView _normalName=findViewById(R.id.normalName);
        TextView _normalId=findViewById(R.id.normalId);

        _normalId.setText(global.getUserId());
        _normalName.setText(global.getUserName());

        View _normalView=findViewById(R.id.normalProfile);
        _normalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(NormalActivity.this,UserActivity.class);
                startActivity(i);
            }
        });

        View _normalQR=findViewById(R.id.normalQR);
        _normalQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(NormalActivity.this,QRcodeActivity.class);
                startActivity(i);
            }
        });
        getMyPost();

        showPost();
    }

    public void showPost(){

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        Set<String> items = pref.getStringSet("post", new HashSet<>());
        List<NormalActivity.post> toshow=new ArrayList<>();
        for(String item:items){
            toshow.add(new NormalActivity.post(item));
        }

        ListView _normalList=findViewById(R.id.normalList);

        adapter=new NormalActivity.MListAdapter(NormalActivity.this,R.layout.postitem,toshow);
        _normalList.setAdapter(adapter);
        _normalList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            //list点击事件
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
            {


                Intent intent=new Intent(NormalActivity.this,PostActivity.class);
                intent.putExtra("info",adapter.getItem(p3).content);
                startActivity(intent);

            }


        });
    }

    public void getMyPost() {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("phone",global.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        NormalActivity that=this;
        new HttpConnection("https://wechat.api.kohaku.xin:11731/getmypost",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res= JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            JSONArray family = res.getJSONArray("post");

                            Set<String> item = new HashSet<>();

                            for (int i = 0; i < family.size(); i++) {
                                String s1 = family.get(i).toString();
                                item.add(s1);
                            }

                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putStringSet("post",item);
                            editor.commit();

                            showPost();
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

    class MListAdapter extends ArrayAdapter<NormalActivity.post> {
        private int resourceId;
        List<NormalActivity.post> object=null;
        public MListAdapter(Context context, int textViewResourceId, List<NormalActivity.post> objects){
            super(context,textViewResourceId,objects);
            resourceId=textViewResourceId;
            this.object=objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            NormalActivity.post pos=getItem(position);   //获取当前项的实例
            View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            view.setTag(pos.content);
            TextView _clist1=(TextView) view.findViewById(R.id.clist1);
            TextView _clist2=(TextView) view.findViewById(R.id.clist2);
            TextView _clist3=(TextView) view.findViewById(R.id.clist3);
            _clist1.setText(pos.rcv);
            _clist2.setText(pos.rcvPhone);
            _clist3.setText(pos.rcvAddress);
            return view;
        }
        @Override
        public NormalActivity.post getItem(int position){
            return object.get(position)  ;
        }
    }
    class post{
        public String content="";
        public String rcv="";
        public String rcvPhone="";
        public String rcvAddress="";
        public  post(String in){
            JSONObject res= JSON.parseObject(in);
            content=in;
            rcv=res.getString("rcvName");
            rcvPhone=res.getString("rcvPhone");
            rcvAddress=res.getString("rcvAddress");
        }

    }
}