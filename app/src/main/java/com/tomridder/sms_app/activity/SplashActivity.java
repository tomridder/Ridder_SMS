package com.tomridder.sms_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;

import com.tomridder.sms_app.R;

public class SplashActivity extends AppCompatActivity {

    private  String myPackageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AskForPermission();
    }
    private void AskForPermission()
    {
        myPackageName=getPackageName();
        if(!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName))
        {
            Intent intent=new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,myPackageName);
            startActivityForResult(intent,122);
        }else
        {
            startMainActivity();
        }
    }

    private void startMainActivity()
    {
        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==122)
        {
            if(!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName))
            { Intent intent=new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,myPackageName);
                startActivityForResult(intent,122);
            }else
            {
                startMainActivity();
            }
        }
    }


}
