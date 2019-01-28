package com.tomridder.sms_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tomridder.sms_app.R;
import com.tomridder.sms_app.adapter.ViewPagerAdapter;
import com.tomridder.sms_app.fragment.CallFragment;
import com.tomridder.sms_app.fragment.MessageFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout linearLayoutContact;
    private LinearLayout linearLayoutSMS;
    private ViewPager myViewPager;
    private List<Fragment> list;
    private ViewPagerAdapter adapter;
    private ImageView iv_send;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

        //AskForPermission();
        initView();
        linearLayoutContact.setOnClickListener(this);
        linearLayoutSMS.setOnClickListener(this);
        myViewPager.setOnPageChangeListener(new MyPageChangerListener());
        iv_send.setOnClickListener(this);
        list=new ArrayList<>();
        list.add(new CallFragment());
        list.add(new MessageFragment());
        adapter=new ViewPagerAdapter(getSupportFragmentManager(),list);
        myViewPager.setAdapter(adapter);
        myViewPager.setCurrentItem(0);
    }





    private void initView()
    {
        linearLayoutContact=(LinearLayout)findViewById(R.id.ll_contact);
        linearLayoutSMS=(LinearLayout)findViewById(R.id.ll_SMS);
        myViewPager=(ViewPager)findViewById(R.id.viewpager);
        iv_send=(ImageView)findViewById(R.id.ma_iv_send);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_contact:
                myViewPager.setCurrentItem(0);
                linearLayoutContact.setBackgroundColor(getResources().getColor(R.color.orangered));
                linearLayoutSMS.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                break;
            case R.id.ll_SMS:
                myViewPager.setCurrentItem(1);
                linearLayoutContact.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                linearLayoutSMS.setBackgroundColor(getResources().getColor(R.color.orangered));
                break;
            case R.id.ma_iv_send:
                Intent intent=new Intent(MainActivity.this,SendActivity.class);
                startActivity(intent);
                break;


        }
    }

    public class MyPageChangerListener implements ViewPager.OnPageChangeListener
    {
        public MyPageChangerListener() {
            super();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            switch(position)
            {
                case 0:
                    linearLayoutContact.setBackgroundColor(getResources().getColor(R.color.orangered));
                    linearLayoutSMS.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                    break;
                case 1:
                    linearLayoutContact.setBackgroundColor(getResources().getColor(R.color.dodgerblue));
                    linearLayoutSMS.setBackgroundColor(getResources().getColor(R.color.orangered));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
