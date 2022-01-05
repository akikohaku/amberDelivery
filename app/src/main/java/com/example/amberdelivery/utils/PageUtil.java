package com.example.amberdelivery.utils;

import android.app.Activity;
import android.content.Context;

import com.example.amberdelivery.MainActivity;

public class PageUtil {
    private Context contxt;
    private MainActivity activity;

    public Context getContxt() {
        return contxt;
    }

    public void setContxt(Context contxt) {
        this.contxt = (MainActivity) contxt;
    }

    public Activity getActivity() {
        return (MainActivity) activity;
    }

    public void setActivity(Activity activity) {
        this.activity = (MainActivity) activity;
    }

    public PageUtil(Context context, MainActivity activity) {
        this.setContxt(context);
        this.setActivity(activity);
    }
    private void toCourier(){
        activity.toCourier();
    }
}
