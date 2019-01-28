package com.tomridder.sms_app.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tomridder.sms_app.R;
import com.tomridder.sms_app.bean.MessageBean;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewMessageAdapter extends BaseQuickAdapter<MessageBean,BaseViewHolder>
{
    private ArrayList<Long> threadIdList=new ArrayList<>();

    public RecyclerViewMessageAdapter(int layoutResId, @Nullable List<MessageBean> data)
    {
        super(layoutResId, data);
    }

    @Override
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
        ;

    }
}
