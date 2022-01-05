package com.example.amberdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import org.apache.commons.codec.digest.DigestUtils;

import com.example.amberdelivery.utils.HttpConnection;

import com.alibaba.fastjson.*;

public class RegisterActivity extends AppCompatActivity {
    int level=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        this.getSupportActionBar().setTitle("注册");

        Button _cancelButton=findViewById(R.id.regcancel);

        _cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        Button _regButton=findViewById(R.id.regRegButton);
        _regButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                register();
            }
        });

        RadioButton _radioNormal=findViewById(R.id.regNormal);
        RadioButton _radioDeliver=findViewById(R.id.regDeliver);
        _radioNormal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                level=10;
                _radioDeliver.setChecked(false);

            }
        });
        _radioDeliver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                level=5;
                _radioNormal.setChecked(false);

            }
        });

    }

    public void register() {
        JSONObject param = new JSONObject();
        EditText _userName=findViewById(R.id.regName);
        EditText _userPhone=findViewById(R.id.regPhone);
        EditText _userPass=findViewById(R.id.regPass);
        EditText _userRePass=findViewById(R.id.regRePass);

        if(_userName.length()==0){
            new  AlertDialog.Builder(this)
                    .setTitle("用户名不能为空" )
                    .setMessage("请重新输入" )
                    .setPositiveButton("确定" ,  null )
                    .show();
            return;
        }
        if(_userPhone.length()==0){
            new  AlertDialog.Builder(this)
                    .setTitle("手机号不能为空" )
                    .setMessage("请重新输入" )
                    .setPositiveButton("确定" ,  null )
                    .show();
            return;
        }
        if(_userPass.length()==0){
            new  AlertDialog.Builder(this)
                    .setTitle("密码不能为空" )
                    .setMessage("请重新输入" )
                    .setPositiveButton("确定" ,  null )
                    .show();
            return;
        }

        if(!_userPass.getText().toString().equals(_userRePass.getText().toString())){
            new  AlertDialog.Builder(this)
                    .setTitle("两次输入的密码不一致" )
                    .setMessage("请重新输入" )
                    .setPositiveButton("确定" ,  null )
                    .show();
            return;
        }
        RegisterActivity that=this;
        try {
            param.put("username", _userName.getText().toString());
            param.put("phone", _userPhone.getText().toString());
            param.put("password", DigestUtils.md5Hex(_userPass.getText().toString()));
            param.put("level", level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new HttpConnection("https://wechat.api.kohaku.xin:11731/register",
                new HttpConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject res=JSON.parseObject(result);
                        if(res.getString("status").equals("1")) {
                            new  AlertDialog.Builder(that)
                                    .setTitle("用户名已被占用" )
                                    .setMessage("" )
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        }
                        if(res.getString("status").equals("2")) {
                            new  AlertDialog.Builder(that)
                                    .setTitle("手机号已被注册" )
                                    .setMessage("" )
                                    .setPositiveButton("确定" ,  null )
                                    .show();
                        }
                        if(res.getString("status").equals("0")){

                            ToastUtils.showToast(that, "注册成功");
                            Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(i);
                            return;
                        }
                        if(res.getString("status").equals("-1")) {
                            new  AlertDialog.Builder(that)
                                    .setTitle("未知系统错误" )
                                    .setMessage("" )
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
}

class ToastUtils {

    private static Toast toast;
    /**
     * Android原生Toast的显示
     */
    public static void showToast(Context context, String content){

        if (toast == null){
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}