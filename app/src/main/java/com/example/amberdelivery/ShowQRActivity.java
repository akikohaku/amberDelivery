package com.example.amberdelivery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.amberdelivery.data.Global;
import com.king.zxing.util.CodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowQRActivity extends AppCompatActivity {
    Global global = null;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showqr);
        this.getSupportActionBar().setTitle("二维码");

        global = (Global) getApplication();

    }
    Bitmap qrCode=null;
    String pid="0";
    @Override
    public void onResume() {

        super.onResume();
        Intent intent=getIntent();
        pid=intent.getStringExtra("id");

        ImageView _showImage=findViewById(R.id.showImage);
        qrCode = CodeUtils.createQRCode(pid, 600, null);
        _showImage.setImageBitmap(qrCode);

        Button _showButton=findViewById(R.id.showButton);
        _showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToLocal(qrCode,pid);
            }
        });

        Button _showCancel=findViewById(R.id.showCancel);
        _showCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ShowQRActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void checkNeedPermissions(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            //6.0以上需要动态申请权限
            if ( ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
            ) {
                //权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, 1);
            }
        }
    }

    private void saveToLocal(Bitmap bitmap, String bitName) {
        checkNeedPermissions();
        String dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(dir +"/"+ bitName + ".jpg");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
                //保存图片后发送广播通知更新数据库
                // Uri uri = Uri.fromFile(file);
                // sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                try {
                    MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file.getAbsolutePath(), bitName+"jpg", null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "保存成功",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}