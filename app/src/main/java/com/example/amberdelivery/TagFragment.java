package com.example.amberdelivery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagFragment extends Fragment {
    MListAdapter adapter=null;
    MListAdapter2 adapter2=null;
    public static TagFragment newInstance(String label,String uuid,String token) {
        Bundle args = new Bundle();
        args.putString("label", label);
        args.putString("uuid",uuid);
        args.putString("token",token);
        TagFragment fragment = new TagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public
    View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adminlist, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getInfo();
    }

    public void getInfo(){
        if(getArguments().getString("label").equals("快递单")){
            getAllPost();
        }
        if(getArguments().getString("label").equals("快递员")){
            getDeliveryUser();
        }
        if(getArguments().getString("label").equals("用户")){
            getNormalUser();
        }
    }

    public void getAllPost() {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", getArguments().getString("uuid"));
            param.put("token", getArguments().getString("token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/getallpost",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res= JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            JSONArray family = res.getJSONArray("post");

                            List<post> toshow=new ArrayList<>();

                            for (int i = 0; i < family.size(); i++) {
                                String s1 = family.get(i).toString();
                                toshow.add(new post(s1));
                            }
                            showList(toshow);

                        }

                    }
                }, new HttpConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, param.toString());
    }

    public void getDeliveryUser() {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", getArguments().getString("uuid"));
            param.put("token", getArguments().getString("token"));
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
    public void getNormalUser() {
        JSONObject param = new JSONObject();
        try {
            param.put("uuid", getArguments().getString("uuid"));
            param.put("token", getArguments().getString("token"));
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
                                if(temp.level>=10){
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
        ListView _adminList= getView().findViewById(R.id.adminlist);
        adapter2=new MListAdapter2(getActivity(),R.layout.userlistitem,toshow);
        _adminList.setAdapter(adapter2);
    }

    public void showList(List<post> toshow){
        ListView _adminList= getView().findViewById(R.id.adminlist);
        adapter=new MListAdapter(getActivity(),R.layout.postitem,toshow);
        _adminList.setAdapter(adapter);
        _adminList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            //list点击事件
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
            {


                Intent intent=new Intent(getActivity(),PostActivity.class);
                intent.putExtra("info",adapter.getItem(p3).content);
                startActivity(intent);

            }


        });
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
        public View getView(int position, View convertView, ViewGroup parent){
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
    class MListAdapter2 extends ArrayAdapter<User> {
        private int resourceId;
        List<User> object=null;
        public MListAdapter2(Context context, int textViewResourceId, List<User> objects){
            super(context,textViewResourceId,objects);
            resourceId=textViewResourceId;
            this.object=objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            User pos=getItem(position);   //获取当前项的实例
            View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            view.setTag(pos.content);
            TextView _clist1=(TextView) view.findViewById(R.id.c1);
            TextView _clist2=(TextView) view.findViewById(R.id.c2);
            TextView _clist3=(TextView) view.findViewById(R.id.c3);
            _clist1.setText(pos.id);
            _clist2.setText(pos.Phone);
            _clist3.setText(pos.Name);
            return view;
        }
        @Override
        public User getItem(int position){
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
