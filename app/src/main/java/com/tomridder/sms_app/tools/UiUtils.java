package com.tomridder.sms_app.tools;

import android.content.Context;
import android.os.Process;
import android.widget.Toast;

public class UiUtils
{
    private static Toast toast;

    public static Context getContext()
    {
        return MyApplication.instance;
    }

    public static android.os.Handler getMainThreadHandler() {
        return MyApplication.instance.handler;
    }
    public static int getMainThreadId()
    {
        return MyApplication.instance.mainThreadId;
    }

    public static void showToast(Context ctx,String msg)
    {
        if(toast==null)
        {
            toast=Toast.makeText(ctx,msg,Toast.LENGTH_SHORT);
        }else
        {
            toast.setText(msg);
        }
        toast.show();
    }
    public static  boolean isRunOnUiThread()
    {
        int currentThreadId= Process.myTid();
        int mainThreadId=getMainThreadId();
        return currentThreadId==mainThreadId;
    }




}
