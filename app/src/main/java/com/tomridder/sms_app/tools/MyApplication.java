package com.tomridder.sms_app.tools;

import android.app.Application;
import android.content.Context;
import android.os.Process;

import com.blankj.utilcode.util.Utils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MyApplication extends Application
{
    public static MyApplication instance;
    public Context context;
    public android.os.Handler handler;
    public int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        context=getApplicationContext();
        handler=new android.os.Handler();
        mainThreadId= Process.myPid();
        Logger.addLogAdapter(new AndroidLogAdapter());
        Utils.init(this);
    }
}
