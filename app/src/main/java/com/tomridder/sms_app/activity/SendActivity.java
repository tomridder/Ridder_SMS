package com.tomridder.sms_app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tomridder.sms_app.R;

public class SendActivity extends AppCompatActivity
{

    EditText etReceiver;
    EditText etContent;
    ImageView ivBack;
    ImageView ivSend;
    private  String receiver=null;
    private  String content=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();

        ivSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                receiver=etReceiver.getText().toString().trim();
                content=etContent.getText().toString();
                Log.i("tag","receiver "+receiver+"content "+content);
                SMSMethod.getInstance(SendActivity.this).SendMessage2(receiver,content);
                Log.i("tag","clicked");
            }
        });
    }

    private void initView()
    {
        etReceiver=(EditText)findViewById(R.id.et_receiver);
        etContent=(EditText)findViewById(R.id.et_content);
        ivBack=(ImageView)findViewById(R.id.iv_back);
        ivSend=(ImageView)findViewById(R.id.iv_send);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
