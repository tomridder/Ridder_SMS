package com.tomridder.sms_app.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.tomridder.sms_app.receiver.SMSReceiver;

import java.util.ArrayList;
import java.util.List;

public class SMSMethod {
    private static SMSMethod mSMSmsMethod;
    public static String SMS_SEND_ACTION="SMS_SEND_ACTION";
    public static String SMS_DELIVERED_ACTION="SMS_DELIVERED_ACTION";

    private SMSReceiver mSendSMSReceiver,mDeliveredSMSReceiver;

    private Context mContext;

    private SMSMethod(Context context)
    {
        mContext=context;
        registerReceiver();
    }

    public static SMSMethod getInstance(Context context)
    {
        if(mSMSmsMethod==null)
        {
            synchronized (SMSMethod.class)
            {
                if(mSMSmsMethod==null)
                {
                    mSMSmsMethod=new SMSMethod(context);
                }
            }
        }
        return mSMSmsMethod;
    }

    public void registerReceiver()
    {
        IntentFilter mFilter01;
        mFilter01=new IntentFilter(SMS_SEND_ACTION);
        mSendSMSReceiver=new SMSReceiver();
        mContext.registerReceiver(mSendSMSReceiver,mFilter01);

        mFilter01=new IntentFilter(SMS_DELIVERED_ACTION);
        mDeliveredSMSReceiver=new SMSReceiver();
        mContext.registerReceiver(mDeliveredSMSReceiver,mFilter01);
    }

    public void unregisterReceiver()
    {
        if(mSendSMSReceiver!=null)
        {
            mContext.unregisterReceiver(mSendSMSReceiver);
        }
        if(mDeliveredSMSReceiver!=null)
        {
            mContext.unregisterReceiver(mDeliveredSMSReceiver);
        }
    }

    public void SendMessage(String strDestAddress,String strMessage)
    {
        SmsManager smsManager=SmsManager.getDefault();
        try
        {
            Intent itSend=new Intent(SMS_SEND_ACTION);
            Intent itDeliver=new Intent(SMS_DELIVERED_ACTION);

            PendingIntent mSendPI=PendingIntent.getBroadcast(mContext,0,itSend,0);

            PendingIntent mDeliverPI=PendingIntent.getBroadcast(mContext,0,itDeliver,0);

            List<String> divideContents=smsManager.divideMessage(strMessage);
            for(String text:divideContents)
            {
                smsManager.sendTextMessage(strDestAddress,null,text,mSendPI,mDeliverPI);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void SendMessage2(String strDestAddress,String strMessage)
    {
        ArrayList<PendingIntent> sentPendingIntents=new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents=new ArrayList<PendingIntent>();
        SmsManager smsManager=SmsManager.getDefault();
        try
        {
            Intent itSend=new Intent(SMS_SEND_ACTION);
            Intent itDeliver=new Intent(SMS_DELIVERED_ACTION);

            PendingIntent mSendPI=PendingIntent.getBroadcast(mContext,0,itSend,0);
            PendingIntent mDeliverPI=PendingIntent.getBroadcast(mContext,0,itDeliver,0);
            ArrayList<String> mSMSMessage=smsManager.divideMessage(strMessage);
            for(int i=0; i<mSMSMessage.size();i++)
            {
                sentPendingIntents.add(i,mSendPI);
                deliveredPendingIntents.add(i,mDeliverPI);
            }
            smsManager.sendMultipartTextMessage(strDestAddress,null,mSMSMessage,sentPendingIntents,deliveredPendingIntents);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
