package com.tomridder.sms_app.fragment;

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
import com.tomridder.sms_app.adapter.RecyclerViewCallAdapter;
import com.tomridder.sms_app.bean.ContactBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CallFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener
{
    @BindView(R.id.callRecyclerView)
    RecyclerView callRecyclerView;
    Unbinder unbinder;
    private List<ContactBean> mDataList;
    private RecyclerViewCallAdapter recyclerViewCallAdapter;
    private String TAG="theme";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate=inflater.inflate(R.layout.fragment_call,container,false);
        unbinder= ButterKnife.bind(this,inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDataList=new ArrayList<>();
        getContentConfig();
        recyclerViewCallAdapter=new RecyclerViewCallAdapter(R.layout.call_item,mDataList);
        recyclerViewCallAdapter.setOnItemChildClickListener(this);
        callRecyclerView.setAdapter(recyclerViewCallAdapter);
    }

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

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
