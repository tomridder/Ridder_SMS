package com.tomridder.sms_app.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.tomridder.sms_app.R;
import com.tomridder.sms_app.activity.MsgActivity;
import com.tomridder.sms_app.utils.CursorUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Telephony.Sms.Intents.SMS_DELIVER_ACTION;
import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;

/**
 * Created by DX on 18/10/19
 * Describe :
 */
public class SmsSReceiver extends BroadcastReceiver{


    private NotificationManager mNotificationManager;
    public final static String ACTION_RECEIVE_SMS = "action_receive_sms";
    public final static String EXTRA_THREAD_ID = "thread_id";
    public final static String EXTRA_PHONE_NUMBER = "phone_number";
    public final static String EXTRA_SMS_CONTENT = "sms_content";

    private final static int NOTIFY_ID = 0x123;
    private final static String CHANNEL_NAME = "channel_sms";
    private final static String CHANNEL_ID = "channel_sms_01";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action=intent.getAction();
        if(action.equals(SMS_RECEIVED_ACTION)|| action.equals(SMS_DELIVER_ACTION) )
        {
            Bundle bundle=intent.getExtras();
            if(bundle!=null)
            {
                Object[] pdus=(Object[])bundle.get("pdus");
                String format=intent.getStringExtra("format");
                SmsMessage[] smsMessages=new SmsMessage[pdus.length];
                int i=0;
                for(Object pdu:pdus)
                {
                  byte[] pduMessage=(byte[])pdu;
                  SmsMessage smsMessage;
                    if(Build.VERSION.SDK_INT < 23){
                        smsMessage = SmsMessage.createFromPdu(pduMessage) ;
                    }else{
                        smsMessage = SmsMessage.createFromPdu(pduMessage,format) ;
                    }
                  smsMessages[i++]=smsMessage;
                }
                int error =intent.getIntExtra("errorCode",0);
                onReceiveSms(context,smsMessages,error);
            }
        }

    }

    private void onReceiveSms(Context context,SmsMessage[] smsMessages,int error)
    {
        Uri uri=storeMessage(context,smsMessages,error);
        Cursor cursor=context.getContentResolver().query(uri,new String[]{"thread_id","address"},null,null,null,null);
        long threadId=-1;
        String address="unknow";
        Log.i("tag",cursor.getCount()+","+cursor.getColumnNames());
        cursor.moveToFirst();
        if(cursor.getCount()!=0)
        {
            threadId=cursor.getLong(cursor.getColumnIndex("thread_id"));
            address=cursor.getString(cursor.getColumnIndex("address"));
        }
        cursor.close();
        StringBuilder body=new StringBuilder();
        for(int i=0;i<smsMessages.length;i++)
        {
            SmsMessage sms=smsMessages[i];
            body.append(sms.getDisplayMessageBody());
        }
        showNotification(context,body.toString(),address,getContactName(context,address),threadId);
        Intent intent = new Intent();
        intent.setAction(ACTION_RECEIVE_SMS);
        intent.putExtra(EXTRA_PHONE_NUMBER, address);
        intent.putExtra(EXTRA_SMS_CONTENT, body.toString());
        intent.putExtra(EXTRA_THREAD_ID, threadId+"");
        context.sendBroadcast(intent);
   }

    private String getContactName(Context context, String address) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{address}, null);
        String contactName = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                contactName = CursorUtils.getColumnStringValue(cursor, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            }
        }
        cursor.close();
        return contactName;
    }

//    private String getContactName(Context context, String address) {
//        Cursor cursor = context.getContentResolver().query(MessageDataSource.PHONE_URI,
//                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                        ContactsContract.CommonDataKinds.Phone.NUMBER},
//                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
//                new String[]{address}, null);
//        String contactName = null;
//        if (cursor != null) {
//            cursor.moveToFirst();
//            if (!cursor.isAfterLast()) {
//                contactName = CursorUtils.getColumnStringValue(cursor, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//            }
//        }
//        cursor.close();
//        return contactName;
//    }
    private Uri storeMessage(Context context,SmsMessage[] msgs,int error)
    {
        SmsMessage sms=msgs[0];
        ContentValues values=extractContentValues(sms);
        values.put(Telephony.Sms.ERROR_CODE, error);
        int pduCount = msgs.length;
        if (pduCount == 1) {
            values.put(Telephony.Sms.Inbox.BODY, replaceFormFeeds(sms.getDisplayMessageBody()));
        } else {
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < pduCount; i++) {
                sms = msgs[i];
                body.append(sms.getDisplayMessageBody());
            }
            values.put(Telephony.Sms.Inbox.BODY, replaceFormFeeds(body.toString()));
        }

        Long threadId = values.getAsLong(Telephony.Sms.THREAD_ID);
        String address = sms.getOriginatingAddress();
        values.put(Telephony.Sms.ADDRESS, address);
        if (((threadId == null) || (threadId == 0)) && (address != null)) {
            threadId = getOrCreateThreadId(context, address);
            values.put(Telephony.Sms.THREAD_ID, threadId);
        }

        ContentResolver resolver = context.getContentResolver();
        Uri insertedUri = resolver.insert(Uri.parse("content://sms"), values);

        return insertedUri;
    }

    public static String replaceFormFeeds(String s) {
        return s == null ? "" : s.replace('\f', '\n');
    }

    private Long getOrCreateThreadId(Context context, String address) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/"), new String[]{"thread_id", "address"}, "address=?", new String[]{address}, null);
        cursor.moveToFirst();
        long threadId = 0;
        if(cursor.getCount() > 0) {
            threadId = cursor.getLong(cursor.getColumnIndex("thread_id"));
        } else if(cursor.getCount() == 0) {
            Cursor cursor2 = contentResolver.query(Uri.parse("content://sms/"), new String[]{"max(thread_id) as max"}, null, null, null);
            if(cursor2.getCount() == 1) {
                cursor2.moveToFirst();
                threadId = cursor2.getLong(0)+1;
            }
            cursor2.close();
        }

        cursor.close();
        return threadId;
    }

    private ContentValues extractContentValues(SmsMessage sms)
    {
        ContentValues values=new ContentValues();
        values.put(Telephony.Sms.Inbox.ADDRESS, sms.getDisplayOriginatingAddress());
        Calendar buildDate = new GregorianCalendar(2011, 8, 18);    // 18 Sep 2011
        Calendar nowDate = new GregorianCalendar();
        long now = System.currentTimeMillis();
        nowDate.setTimeInMillis(now);

        if (nowDate.before(buildDate)) {
            now = sms.getTimestampMillis();
        }
        values.put(Telephony.Sms.Inbox.DATE, new Long(now));
        values.put(Telephony.Sms.Inbox.DATE_SENT, Long.valueOf(sms.getTimestampMillis()));
        values.put(Telephony.Sms.Inbox.PROTOCOL, sms.getProtocolIdentifier());
        values.put(Telephony.Sms.Inbox.READ, 0);
        values.put(Telephony.Sms.Inbox.SEEN, 0);
        if (sms.getPseudoSubject().length() > 0) {
            values.put(Telephony.Sms.Inbox.SUBJECT, sms.getPseudoSubject());
        }
        values.put(Telephony.Sms.Inbox.REPLY_PATH_PRESENT, sms.isReplyPathPresent() ? 1 : 0);
        values.put(Telephony.Sms.Inbox.SERVICE_CENTER, sms.getServiceCenterAddress());
        return values;
    }

    private void initNotification(Context context) {
        if(mNotificationManager == null) {
            mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(CHANNEL_ID);
            if(channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(true);
                mNotificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context,String sms,String sender,String contactName,long threadId)
    {
        Log.i("tag","sender"+ sender+"sms"+sms);
        initNotification(context);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(context,CHANNEL_ID);
        Intent intent=new Intent(context, MsgActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("SMS",threadId);
        intent.putExtra("SMS_PERSON",contactName);
        intent.putExtra("SMS_NUMBER",sender);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,MsgActivity.NEW_MESSAGE_REQUEST,intent,PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contactName==null? sender :contactName)
                .setContentText(sms)
                .setColor(Color.RED)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent);
        Notification notification=notificationBuilder.build();
        notification.flags=NotificationCompat.FLAG_AUTO_CANCEL;

        if(mNotificationManager!=null)
        {
            mNotificationManager.notify(NOTIFY_ID,notification);
        }
    }





}
