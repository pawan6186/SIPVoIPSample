package com.wittgroupinc.wetalksample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wittgroupinc.wetalksdk.WeTalkAccount;

import org.pjsip.pjsua2.BuddyConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Button registerBtn;
    private Context context;

    public static WeTalkAccount account = null;

    ArrayList<Map<String, String>> buddyList;







    private HashMap<String, String> putData(String uri, String status)
    {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("uri", uri);
        item.put("status", status);
        return item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        registerBtn = (Button) findViewById(R.id.registration_btn);
        registerBtn.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registration_btn :
                startActivity(new Intent(context, RegistrationActivity.class));
                break;
        }
    }


    private void addBuddy(BuddyConfig initial)
    {
        final BuddyConfig cfg = new BuddyConfig();
        final BuddyConfig old_cfg = initial;
        final boolean is_add = initial == null;
        cfg.setUri("aerer");
        cfg.setSubscribe(true);
        account.addBuddy(cfg);
        buddyList.add(putData(cfg.getUri(), ""));


    }



}
