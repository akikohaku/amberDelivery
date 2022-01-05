package com.example.amberdelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.amberdelivery.data.Global;
import com.example.amberdelivery.utils.HttpConnection;

import com.alibaba.fastjson.*;
import com.example.amberdelivery.utils.PageUtil;
import com.google.android.material.tabs.TabLayout;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    Global global = null;
    private MainActivity mainActivity;
    private List<TagFragment> tabFragmentList = new ArrayList<>();
    private String[] tabs = {"快递单", "快递员", "用户"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        this.getSupportActionBar().setTitle("管理员");

        global = (Global) getApplication();

        TextView _adminName=findViewById(R.id.adminName);
        TextView _adminId=findViewById(R.id.adminId);

        _adminId.setText(global.getUserId());
        _adminName.setText(global.getUserName());

        View _adminProfile=findViewById(R.id.adminProfile);

        _adminProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(AdminActivity.this,UserActivity.class);
                startActivity(i);
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.adminViewPage);
        //添加tab
        for (int i = 0; i < tabs.length; i++) {
            tabFragmentList.add(TagFragment.newInstance(tabs[i],global.getUserId(),global.getToken()));
        }

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return tabFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        });

        //设置TabLayout和ViewPager联动
        tabLayout.setupWithViewPager(viewPager,false);
    }
}