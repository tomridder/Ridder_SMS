package com.tomridder.sms_app.fragment;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tomridder.sms_app.R;
import com.tomridder.sms_app.activity.MsgActivity;
import com.tomridder.sms_app.adapter.RecyclerViewMessageAdapter;
import com.tomridder.sms_app.bean.MessageBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MessageFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener
{

    @BindView(R.id.messgaeRecycler)
    RecyclerView messageRecycler;
    Unbinder unbinder;
    private ArrayList<MessageBean> mDatalist;
    private RecyclerViewMessageAdapter recyclerViewMessageAdapter;
    private Uri SMS= Uri.parse("content://sms/");
    private  Context context;
    private IntentFilter intentFilter;
    private  ArrayList<MessageBean> smsContent;
    private MFMessageReceiver mfMessageReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate=inflater.inflate(R.layout.fragment_message,container,false);
        unbinder=ButterKnife.bind(this,inflate);
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mfMessageReceiver=new MFMessageReceiver();
        getActivity().registerReceiver(mfMessageReceiver,intentFilter);
        return inflate;

    }


    public  class MFMessageReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
//            Bundle bundle=intent.getExtras();
//            Object[] pdus=(Object[])bundle.get("pdus");
//            SmsMessage[] messages=new SmsMessage[pdus.length];
//            for(int i=0;i<messages.length;i++)
//            {
//                messages[i]=SmsMessage.createFromPdu((byte[]) pdus[i]);
//            }
//            String address=messages[0].getOriginatingAddress();
//            String fullMessage="";
//            for(SmsMessage message:messages)
//            {
//                fullMessage=fullMessage+message.getMessageBody();
//            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mDatalist=new ArrayList<>();
            ArrayList<MessageBean> smsContent1=getSmsContent();
            ArrayList<MessageBean> messgeBeans1=ContentRemove(smsContent1);
            recyclerViewMessageAdapter=new RecyclerViewMessageAdapter(R.layout.message_item,messgeBeans1);
            recyclerViewMessageAdapter.setOnItemChildClickListener(MessageFragment.this);
            messageRecycler.setAdapter(recyclerViewMessageAdapter);
                Log.i("tag","received");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatalist=new ArrayList<>();
         smsContent=getSmsContent();
        ArrayList<MessageBean> messgeBeans=ContentRemove(smsContent);
        recyclerViewMessageAdapter=new RecyclerViewMessageAdapter(R.layout.message_item,messgeBeans);
        recyclerViewMessageAdapter.setOnItemChildClickListener(this);
        messageRecycler.setAdapter(recyclerViewMessageAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mfMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatalist=new ArrayList<>();
        ArrayList<MessageBean> smsContent1=getSmsContent();
        ArrayList<MessageBean> messgeBeans1=ContentRemove(smsContent1);
        recyclerViewMessageAdapter=new RecyclerViewMessageAdapter(R.layout.message_item,messgeBeans1);
        recyclerViewMessageAdapter.setOnItemChildClickListener(this);
        messageRecycler.setAdapter(recyclerViewMessageAdapter);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position)
    {
        int id=view.getId();
        Intent intent;
        long threadId;
        String address;
        String person;
        MessageBean bean;
        switch (id)
        {
            case R.id.tv_delete:

                ContentResolver cr=getContext().getContentResolver();
                Uri uriSms=Uri.parse("content://sms/inbox");
                bean=(MessageBean)adapter.getData().get(position);
                threadId=bean.getThreadId();
                int result=cr.delete(Uri.parse("content://sms/conversations/"+threadId),null,null);
                Log.d("tagdelete","threadId"+threadId+"result"+result);
             //   mDatalist=getSmsContent();
                if(result>0)
                {
                    recyclerViewMessageAdapter.remove(position);
                }
                break;
            case R.id.tv_thumb:

                break;
            case R.id.mi_rl_content:
                bean=(MessageBean)adapter.getData().get(position);
                intent=new Intent(getContext(), MsgActivity.class);
                threadId=bean.getThreadId();
                address=bean.getAddress();
                person=bean.getPerson();
                intent.putExtra("SMS",threadId);
                intent.putExtra("SMS_NUMBER",address);
                intent.putExtra("SMS_PERSON",person);
                startActivityForResult(intent,111);
                break;

        }

    }




    public ArrayList<MessageBean> getSmsContent()
    {
        ContentResolver cr= getContext().getContentResolver();
        String[] projection=new String[]{"_id","thread_id","address","person","date","read","status","type","body"};
        Cursor cur = cr.query(SMS, projection, null, null, "date desc");
        if(cur!=null)
        {
            while (cur.moveToNext())
            {
                int id=cur.getInt(cur.getColumnIndex("_id"));
                long threadId=cur.getLong(cur.getColumnIndex("thread_id"));
                String addressNumber=cur.getString(cur.getColumnIndex("address"));
                String person=cur.getString(cur.getColumnIndex("person"));
                long date=cur.getLong(cur.getColumnIndex("date"));
                int read=cur.getInt(cur.getColumnIndex("read"));
                int status=cur.getInt(cur.getColumnIndex("status"));
                int type=cur.getInt(cur.getColumnIndex("type"));
                String body=cur.getString(cur.getColumnIndex("body"));
                if(type== 4)
                {
                    Log.i("tag","id : " + id + ",address: " + addressNumber + " person : " + person + " date : " + date +" type : "
                            + type+ " read : " + read + " threadid: " + threadId+"body"+body);
                }

                MessageBean messageBean=new MessageBean(id,addressNumber,person,date,read,type,threadId,body);
                mDatalist.add(messageBean);
            }
            cur.close();
            return  mDatalist;
        }else
        {
            MessageBean no_message=new MessageBean(1,"","",0,0,0,0,"No Message");
            mDatalist.add(no_message);
            cur.close();
            return mDatalist;
        }
    }

    private ArrayList<MessageBean> ContentRemove(ArrayList<MessageBean> smsContent)
    {
        ArrayList<Long> addressList =new ArrayList<>();
        ArrayList<MessageBean> contentRemoveBeanList=new ArrayList<>();
        for(MessageBean messageBean :smsContent)
        {
            long threadId=messageBean.getThreadId();
            boolean contains=addressList.contains(threadId);
            if(!contains)
            {
                addressList.add(threadId);
                contentRemoveBeanList.add(messageBean);
            }
        }
        return  contentRemoveBeanList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
