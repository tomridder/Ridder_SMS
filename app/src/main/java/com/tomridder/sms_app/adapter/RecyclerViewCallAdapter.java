package com.tomridder.sms_app.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tomridder.sms_app.R;
import com.tomridder.sms_app.bean.ContactBean;

import java.util.List;

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
