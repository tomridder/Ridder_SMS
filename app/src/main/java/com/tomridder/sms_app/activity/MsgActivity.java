package com.tomridder.sms_app.activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomridder.sms_app.R;
import com.tomridder.sms_app.adapter.MsgAdapter;
import com.tomridder.sms_app.bean.MessageBean;

import java.util.ArrayList;

public class MsgActivity extends AppCompatActivity
{

    private static ArrayList<MessageBean> messageBeanList=new ArrayList<>();
    private static EditText inputText;
    private ImageView send;
    private TextView person;
    private ImageView back;
    private static RecyclerView msgRecyclerView;
    private  static  MsgAdapter adapter;
    private static Uri SMS=Uri.parse("content://sms/");
    private static Uri SMS_INBOX=Uri.parse("content://sms/");
    public static String receiverNum;
    public static long threadId;
    public static   String person1;
    private  static   ArrayList<MessageBean> messageBeanArrayList1;
    private  static Context mContext;
    public static final int NEW_MESSAGE_REQUEST = 0x33;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        inputText=(EditText)findViewById(R.id.input_text);
        send=(ImageView)findViewById(R.id.send);
        person=(TextView)findViewById(R.id.tv_person);
        back=(ImageView)findViewById(R.id.iv_back);
        msgRecyclerView=(RecyclerView)findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        mContext=this;
        final Intent intent=getIntent();
        messageBeanArrayList1=new ArrayList<>();
         threadId=intent.getLongExtra("SMS",0);
        receiverNum=intent.getStringExtra("SMS_NUMBER");
         person1=intent.getStringExtra("SMS_PERSON");
        if(person1!=null)
        {
            person.setText(person1);
        }else
        {
            person.setText(receiverNum);
        }

        if(threadId!=0)
        {

           ArrayList<MessageBean> messageBeanArrayList2=new ArrayList<MessageBean>();
           messageBeanArrayList2.clear();
            messageBeanArrayList2=getSmsContent(String.valueOf(threadId));
            adapter=new MsgAdapter(messageBeanArrayList2);
            msgRecyclerView.setAdapter(adapter);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content=inputText.getText().toString();
                receiverNum=intent.getStringExtra("SMS_NUMBER");
                if(!"".equals(content))
                {
                    SMSMethod.getInstance(MsgActivity.this).SendMessage(receiverNum,content);
                    ContentResolver resolver=getContentResolver();
                    Uri uri=Uri.parse("content://sms/");
                    ContentValues values=new ContentValues();
                    values.put("address",receiverNum);
                    values.put("type",2);
                    values.put("body",content);
                    values.put("date",System.currentTimeMillis());
                    values.put("thread_id",threadId);
                    resolver.insert(uri,values);
                    messageBeanArrayList1.clear();
                    messageBeanArrayList1=getSmsContent(String.valueOf(threadId));
                    adapter=new MsgAdapter(messageBeanArrayList1);
                 //    adapter.notifyDataSetChanged();
                    msgRecyclerView.scrollToPosition(messageBeanArrayList1.size()-1);
                    inputText.setText("");
                }
            }
        });

    }




    private static ArrayList<MessageBean> getSmsContent(String threadId)
    {
        messageBeanList.clear();
        ContentResolver cr=mContext.getContentResolver();
        String[] projection=new String[]{"_id","address","person","date","read","status","type","body" };
        Cursor cur=cr.query(SMS_INBOX,projection,"thread_id=?",new String[]{threadId},"date asc");
        if(null!=cur)
        {
            while(cur.moveToNext())
            {
                int id = cur.getInt(cur.getColumnIndex("_id"));
                String addressNumber=cur.getString(cur.getColumnIndex("address"));
                String person=cur.getString(cur.getColumnIndex("person"));
                long date=cur.getLong(cur.getColumnIndex("date"));
                int read=cur.getInt(cur.getColumnIndex("read"));
                int status=cur.getInt(cur.getColumnIndex("status"));
                int type=cur.getInt(cur.getColumnIndex("type"));
                String body=cur.getString(cur.getColumnIndex("body"));

                Log.i("tag","id : " + id + ",address: " + addressNumber + " person : " + person + " date : " + date + " type : "
                        + type + " read : " + read + " threadid: " + 0+"body"+body);
                 MessageBean messageBean=new MessageBean(id,addressNumber,person,date,read,type,0,body);
                messageBeanList.add(messageBean);
            }
            cur.close();
            return messageBeanList;
        }else
        {
            MessageBean no_message=new MessageBean(1,"","",0,0,0,0,"No message");
            messageBeanList.add(no_message);
            cur.close();
            return messageBeanList;
        }

    }
    public static  class MessageReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            Object[] pdus=(Object[])bundle.get("pdus");
            SmsMessage[] messages=new SmsMessage[pdus.length];
            for(int i=0;i<messages.length;i++)
            {
                messages[i]=SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            String address=messages[0].getOriginatingAddress();
            String fullMessage="";
            for(SmsMessage message:messages)
            {
                fullMessage=fullMessage+message.getMessageBody();
            }
            Log.i("MsgActivity_tag","address+"+address +"content"+fullMessage);
            //写入 到 短信
//            Uri uri=Uri.parse("content://sms/");
//            ContentResolver resolver=context.getContentResolver();
//            ContentValues values=new ContentValues();
//            values.put("address",address);
//            values.put("type",1);
//            values.put("body",fullMessage);
//            values.put("date",System.currentTimeMillis());
//            if(address==receiverNum)
//            {
//                values.put("thread_id",threadId);
//            }
//            resolver.insert(uri,values);
            if(messageBeanArrayList1!=null)
            {
                messageBeanArrayList1.clear();
                messageBeanArrayList1=getSmsContent(String.valueOf(threadId));
                adapter=new MsgAdapter(messageBeanArrayList1);
                //    adapter.notifyDataSetChanged();s
                msgRecyclerView.scrollToPosition(messageBeanArrayList1.size()-1);
                inputText.setText("");
            }


        }



    }


}
