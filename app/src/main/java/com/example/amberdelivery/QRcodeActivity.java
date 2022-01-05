package com.example.amberdelivery;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashSet;
import java.util.Set;

public class QRcodeActivity extends AppCompatActivity {
    Global global = null;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createqr);
        this.getSupportActionBar().setTitle("创建快递单");
        this.getSupportActionBar().setHomeButtonEnabled(true);

        global = (Global) getApplication();

        Button _qrButton=findViewById(R.id.qrButton);
        _qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });
    }
    JSONObject out=new JSONObject();
    public void uploadPost() {
        JSONObject param = new JSONObject();
        EditText _sender=findViewById(R.id.qrSender);
        EditText _senderPhone=findViewById(R.id.qrPhone);
        EditText _senderAddress=findViewById(R.id.qrSenderAddress);

        EditText _rcv=findViewById(R.id.qrRcv);
        EditText _rcvPhone=findViewById(R.id.qrRcvPhone);
        EditText _rcvAddress=findViewById(R.id.qrRcvAddress);

        EditText _content=findViewById(R.id.qrContent);
        EditText _tips=findViewById(R.id.qrTips);
        QRcodeActivity that=this;
        if(_sender.length()==0){
            ToastUtils.showToast(that, "寄件人不能为空");
            return;
        }
        if(_senderPhone.length()==0){
            ToastUtils.showToast(that, "寄件电话不能为空");
            return;
        }
        if(_senderAddress.length()==0){
            ToastUtils.showToast(that, "寄件地址不能为空");
            return;
        }

        if(_rcv.length()==0){
            ToastUtils.showToast(that, "收件人不能为空");
            return;
        }
        if(_rcvPhone.length()==0){
            ToastUtils.showToast(that, "收件电话不能为空");
            return;
        }
        if(_rcvAddress.length()==0){
            ToastUtils.showToast(that, "收件地址不能为空");
            return;
        }



        try {
            param.put("uuid", global.getUserId());
            param.put("token", global.getToken());
            param.put("sender", _sender.getText().toString());
            param.put("senderPhone", _senderPhone.getText().toString());
            param.put("senderAddress", _senderAddress.getText().toString());
            param.put("rcv", _rcv.getText().toString());
            param.put("rcvPhone", _rcvPhone.getText().toString());
            param.put("rcvAddress", _rcvAddress.getText().toString());
            param.put("content", _content.getText().toString());
            param.put("tips", _tips.getText().toString());
            out.put("senderName", _sender.getText().toString());
            out.put("senderPhone", _senderPhone.getText().toString());
            out.put("senderAddress", _senderAddress.getText().toString());
            out.put("rcvName", _rcv.getText().toString());
            out.put("rcvPhone", _rcvPhone.getText().toString());
            out.put("rcvAddress", _rcvAddress.getText().toString());
            out.put("content", _content.getText().toString());
            out.put("tips", _tips.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/createpost",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res= JSON.parseObject(result);
                        if(res.getString("status").equals("0")){
                            String id=res.getString("id");
                            out.put("id",id);
                            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                            Set<String> item = pref.getStringSet("post", new HashSet<>());
                            item.add(out.toJSONString());
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putStringSet("post",item);
                            editor.commit();

                            Intent intent=new Intent(QRcodeActivity.this,ShowQRActivity.class);
                            intent.putExtra("id",id);
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
}