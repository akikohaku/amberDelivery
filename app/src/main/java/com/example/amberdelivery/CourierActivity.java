package com.example.amberdelivery;
import androidx.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;
import com.king.zxing.CaptureActivity;

import static com.king.zxing.CaptureFragment.KEY_RESULT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourierActivity extends AppCompatActivity{

    Global global=null;
    private MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier);
        this.getSupportActionBar().setTitle("快递员");

        global= (Global) getApplication();
        View _scan=findViewById(R.id.courierScan);
        _scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });

        View _user=findViewById(R.id.courierUser);
        _user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(CourierActivity.this,QRcodeActivity.class);
                startActivity(i);
            }
        });

        View _courierProfile=findViewById(R.id.courierProfile);
        _courierProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(CourierActivity.this,UserActivity.class);
                startActivity(i);
            }
        });

    }
    MListAdapter adapter=null;
    @Override
    protected  void onResume() {

        super.onResume();

        TextView _courierName=findViewById(R.id.courierName);
        _courierName.setText(global.getUserName());

        TextView _courierId=findViewById(R.id.courierId);
        _courierId.setText(global.getUserId());
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        Set<String> items = pref.getStringSet("post", new HashSet<>());
        List<post> toshow=new ArrayList<>();
        for(String item:items){
            toshow.add(new post(item));
        }
        ListView _courierList=findViewById(R.id.courierList);

        adapter=new MListAdapter(CourierActivity.this,R.layout.postitem,toshow);
        _courierList.setAdapter(adapter);
        _courierList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            //list点击事件
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
            {


                Intent intent=new Intent(CourierActivity.this,PostActivity.class);
                intent.putExtra("info",adapter.getItem(p3).content);
                startActivity(intent);

            }


        });

    }

    public void scan(){
            //跳转到扫描界面
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String result = data.getStringExtra(KEY_RESULT);
                System.out.println(result);
                getPost(result);
            }
        }
    }

    public void getPost(String id) {
        com.alibaba.fastjson.JSONObject param = new com.alibaba.fastjson.JSONObject();
        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("target", id);
//            param.put("imei", Config.getImei(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        CourierActivity that=this;
        new HttpConnection("https://wechat.api.kohaku.xin:11731/getpostinfo",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res= JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                            Set<String> item = pref.getStringSet("post", new HashSet<>());
                            item.add(result);
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putStringSet("post",item);
                            editor.commit();
                            Intent intent=new Intent(CourierActivity.this,PostActivity.class);
                            intent.putExtra("info",result);
                            startActivity(intent);
                        }else if(res.getString("status").equals("1")){
                            new  AlertDialog.Builder(that)
                                    .setTitle("权限不足！" )
                                    .setMessage("" )
                                    .setPositiveButton("确定" ,  null )
                                    .show();

                        }else if(res.getString("status").equals("2")){
                            new  AlertDialog.Builder(that)
                                    .setTitle("您不是邮递员！" )
                                    .setMessage("" )
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        }else{
                            new  AlertDialog.Builder(that)
                                    .setTitle("查询失败！" )
                                    .setMessage("错误的二维码" )
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        }

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {
                new  AlertDialog.Builder(that)
                        .setTitle("未知系统错误" )
                        .setMessage("" )
                        .setPositiveButton("确定" ,  null )
                        .show();
            }
        }, param.toString());
    }
    class MListAdapter extends ArrayAdapter<post> {
        private int resourceId;
        List<post> object=null;
        public MListAdapter(Context context, int textViewResourceId, List<post> objects){
            super(context,textViewResourceId,objects);
            resourceId=textViewResourceId;
            this.object=objects;
        }
        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            post pos=getItem(position);   //获取当前项的实例
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
        public post getItem(int position){
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



