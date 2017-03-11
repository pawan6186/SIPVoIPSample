package com.wittgroupinc.wetalksample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wittgroupinc.wetalksdk.WeTalkCallBack;
import com.wittgroupinc.wetalksdk.WeTalk;
import com.wittgroupinc.wetalksdk.WeTalkManager;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    private final Handler handler = new Handler(this);
    private EditText userNameET;
    private EditText passwordET;
    private Button registerBtn;
    private WeTalkManager weTalkManger;

    @Override
    public boolean handleMessage(Message message) {

        if (message.what == WeTalkManager.MSG_TYPE.REG_STATE) {

            String msg = (String) message.obj;
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
            builder.setCancelable(false);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //
                }
            })
                    .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            // Create the AlertDialog object and return it
            builder.create().show();

        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        try{
            weTalkManger = com.wittgroupinc.wetalksdk.WeTalkManager.getInstance();
            weTalkManger.initialize(RegistrationActivity.this);
            initView();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private void initView() {
        userNameET = (EditText) findViewById(R.id.username_et);
        passwordET = (EditText) findViewById(R.id.password_et);
        registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                com.wittgroupinc.wetalksdk.WeTalkManager.getInstance().registerUser(userNameET.getText().toString(), passwordET.getText().toString(), new WeTalkCallBack() {
                    @Override
                    public void onSuccess(int code, Object obj) {
                        if(code == WeTalk.RequestCode.REGISTER){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                }
                            });
//                            Message m = Message.obtain(handler, WeTalkManager.MSG_TYPE.REG_STATE, (String)obj);
//                            m.sendToTarget();
                        }

                    }


                    @Override
                    public void onFailure(int code, Object obj) {
                        if(code == WeTalk.RequestCode.REGISTER){
                            Message m = Message.obtain(handler, com.wittgroupinc.wetalksdk.WeTalkManager.MSG_TYPE.REG_STATE, (String)obj);
                            m.sendToTarget();
                        }

                    }
                });
        }
    }


}
