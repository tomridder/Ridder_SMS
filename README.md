# Ridder_SMS
#### A SMS app that can replace the System SMS
#### 一个可以替代系统短信应用的sms app
![Image](https://github.com/tomridder/Ridder_SMS/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png)

### 前言
> 本文的内容主要是解析短信App Ridder_SMS 的制作流程，以及代码的具体实现，若有什么不足之处，还请提出建议，附上这个 APP 的 Github 地址 [Ridder_SMS](https://github.com/tomridder/Ridder_SMS) 欢迎大家  :heart: star 和 fork.


#### 本文的主要内容
- 联系人查询 短信查询 以及 短信收发的效果演示  
- 联系人 查询 短信查询的实现
- 短信接收的实现
- 短信聊天界面的实现
#### 1.联系人查询 短信查询 以及 短信收发的效果演示 ：

![gif](https://github.com/tomridder/Ridder_SMS/blob/master/sms1.gif)

#### 2.联系人 查询 短信查询的实现 :
**(1).联系人 查询 的实现**
```  
   private List<ContactBean> getContentConfig()
    {
        Uri raw_uri=Uri.parse("content://com.android.contacts/raw_contacts");
        Uri data_uri=Uri.parse("content://com.android.contacts/data");
        Cursor raw_cursor=getContext().getContentResolver().query(raw_uri,new String[]{"_id"},null,null,null);
        if(raw_cursor==null)
        {
            return mDataList;
        }
        Log.i(TAG,raw_cursor.getCount()+"");
        while(raw_cursor.moveToNext())
        {
            String id=raw_cursor.getString(0);
            Cursor data_cursor= getContext().getContentResolver().query(data_uri,new String[]{"mimetype","data1"},
                    "raw_contact_id = ?",new String[]{id},null);
            if(data_cursor==null)
            {
                return mDataList;
            }
            Log.i(TAG,"联系人"+id);
            String name =null;
            String number=null;
            while(data_cursor.moveToNext())
            {
                String type=data_cursor.getString(0);
                if(type.equals("vnd.android.cursor.item/name"))
                {
                    Log.i(TAG,"name"+data_cursor.getString(1));
                    name=data_cursor.getString(1);
                }else if(type.equals("vnd.android.cursor.item/phone_v2"))
                {
                    Log.i(TAG,"number"+data_cursor.getString(1));
                    number=data_cursor.getString(1);
                }
            }
            ContactBean contactBean=new ContactBean(name,number);
            mDataList.add(contactBean);
            data_cursor.close();
        }
        raw_cursor.close();
        return  mDataList;
    }
       
```  

1.首先利用contentProvider查询 raw_contacts表，得到id的游标raw_cursor。

2.接着游标raw_cursor一行行下移，得到每一行的id。查询data表中raw_contact_id列等于raw_cursor中id的行，选出其中的mimetype和data1列，得到游标data_cursor。

3.data_cursor一一行行下移，首先查询mimetype的类型，如果是name类型，则将data1列赋值给name变量。如果是phone_v2类型，则将data1列赋值给number变量。

4.利用raw_cursor的每一行中的name变量和number变量 构造 ContactBean类型的List，最终返回List。

```  
public class RecyclerViewCallAdapter extends BaseQuickAdapter<ContactBean,BaseViewHolder>

{
    public RecyclerViewCallAdapter(int layoutResId, @Nullable List<ContactBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ContactBean item) {
        helper.setText(R.id.tv_name,item.getName())
                .setText(R.id.tv_number,item.getCallNum());
    }
}
```  
5.将List 加载到recycleView上。

**(2)短信 查询 的实现**
```  
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
```
短信的查询则简单的多，只涉及到了一张表(Uri.parse("content://sms/"))

1.首先利用contentProvider查询sms表，选出其中的id,addressNumber,person,date,read,type,threadId,body列，按照时间降序排列，得到游标cur。

2.cur一行行下移，用cur的每一行和 id,addressNumber,person,date,read,type,threadId,body 变量，构造MessageBean类型的List。

```
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
```

3.根据threadId去重短信。

```
    protected void convert(BaseViewHolder helper, MessageBean item)
    {
        if(!TextUtils.isEmpty(item.getPerson()))
        {
            helper.setText(R.id.mi_tv_person,item.getPerson());
        }else
        {
            helper.setText(R.id.mi_tv_person,item.getAddress());
        }

        if(item.getDate()==0)
        {
            helper.setText(R.id.tv_date," ");
        }else
        {
            CharSequence format= DateFormat.format("MM-dd hh:mm",item.getDate());
            helper.setText(R.id.tv_date,format.toString());
        }

        if(!TextUtils.isEmpty(item.getMessage()))
        {
            helper.setText(R.id.mi_tv_content,item.getMessage());
        }else
        {
            helper.setText(R.id.mi_tv_content,"++");
        }

        helper.addOnClickListener(R.id.tv_delete)
                .addOnClickListener(R.id.tv_thumb)
                .addOnClickListener(R.id.mi_rl_content);
    }
```
4.最终返回List，加list装到recycleView中。
#### 3.短信接收的实现：


1.首先注册SmsSReceiver，当接收到短信和短信发送成功时，系统会发出两条广播，分别是SMS_RECEIVED_ACTION 和 SMS_DELIVER_ACTION。

```
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
```
2.在onReceive方法中，借助ContentResolver和ContentValues将发出与接收到的短信和写入到sms表中。

```
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mfMessageReceiver=new MFMessageReceiver();
        getActivity().registerReceiver(mfMessageReceiver,intentFilter);
```

```
            mDatalist=new ArrayList<>();
            ArrayList<MessageBean> smsContent1=getSmsContent();
            ArrayList<MessageBean> messgeBeans1=ContentRemove(smsContent1);
            recyclerViewMessageAdapter=new RecyclerViewMessageAdapter(R.layout.message_item,messgeBeans1);
            recyclerViewMessageAdapter.setOnItemChildClickListener(MessageFragment.this);
            messageRecycler.setAdapter(recyclerViewMessageAdapter);
```
3.在MessageFragment中，注册广播，当接收到到android.provider.Telephony.SMS_RECEIVED 广播时，刷新recycleView。


#### 4.短信聊天界面的实现：
```  
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBean messageBean=messageBeanList.get(position);
        if(messageBean.getType()==1)
        {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(messageBean.getMessage());
        }else if(messageBean.getType()==2)
        {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(messageBean.getMessage());
        }
    }      
```  
1.MsgAdapter中有左右两个layout，根据messageBean的type类型，如果是接收，则显示leftLayout，隐藏rightLayout。如果是发出，则隐藏leftLayout，显示rightLayout。
```  
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
```  
2.借助 ContentResolver 和从MainActivity传递过来的threadId，进行短信查询，构造messageBeanList，最终加载到RecycleView上。不要忘记将RecycleVIew定位到最后一行。
``` 
msgRecyclerView.scrollToPosition(messageBeanArrayList1.size()-1);
``` 



### 结语
> 
以上便是我写这个 APP 的具体实现思路，以及踩过的一些坑，记录下来，给大家看看。

最后附上这个 APP 的 Github 地址  [Ridder_SMS](https://github.com/tomridder/Ridder_SMS) 欢迎大家 :heart: star 和 fork。

如果有什么想法或者建议，非常欢迎大家来讨论。
 
-----
