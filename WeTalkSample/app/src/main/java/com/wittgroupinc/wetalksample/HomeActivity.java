package com.wittgroupinc.wetalksample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wittgroupinc.wetalksdk.WeTalkCall;
import com.wittgroupinc.wetalksdk.WeTalkManager;
import com.wittgroupinc.wetalksdk.WeTalkSessionCallBack;

import static com.wittgroupinc.wetalksample.R.id.accept_btn;
import static com.wittgroupinc.wetalksample.R.id.call_btn;
import static com.wittgroupinc.wetalksample.R.id.reject_btn;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, WeTalkSessionCallBack {

    private Button acceptBtn, rejectBtn, callBtn;
    private EditText userIdEt;
    private WeTalkCall currentCall;
    private RelativeLayout makeCallSection;
    private LinearLayout receiveCallSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        com.wittgroupinc.wetalksdk.WeTalkManager.getInstance().addSessionListener(this);
    }


    private void initViews() {
        acceptBtn = (Button) findViewById(R.id.accept_btn);
        rejectBtn = (Button) findViewById(reject_btn);
        callBtn = (Button) findViewById(call_btn);
        userIdEt = (EditText) findViewById(R.id.opponent_id_et);
        callBtn.setOnClickListener(this);
        acceptBtn.setOnClickListener(this);

        rejectBtn.setOnClickListener(this);
        receiveCallSection = (LinearLayout) findViewById(R.id.incoming_call_layout);
        makeCallSection = (RelativeLayout) findViewById(R.id.make_call_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case call_btn:
                com.wittgroupinc.wetalksdk.WeTalkManager.getInstance().makeCall(userIdEt.getText().toString());
                break;
            case accept_btn:
                WeTalkManager.getInstance().acceptCall(currentCall);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        acceptBtn.setVisibility(View.GONE);
                        rejectBtn.setText("Hang Up");
                    }
                });
                break;
            case reject_btn:
                com.wittgroupinc.wetalksdk.WeTalkManager.getInstance().rejectCall(currentCall);
                resetUI();
                break;
        }
    }


    @Override
    public void callDisconnected(int code, WeTalkCall call) {
        makeStatusToast("Disconnected......");
        resetUI();

    }

    @Override
    public void callAccepted(int code, WeTalkCall call) {
        makeStatusToast("Accepted....");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receiveCallSection.setVisibility(View.VISIBLE);
                makeCallSection.setVisibility(View.GONE);
                acceptBtn.setVisibility(View.GONE);
                rejectBtn.setText("Hang Up");
            }
        });

    }

    @Override
    public void callHangUp(int code, WeTalkCall call) {
        makeStatusToast("Hangup....");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetUI();
            }
        });
    }

    @Override
    public void callRinging(int code, WeTalkCall call) {
        makeStatusToast("Ringing.....");
    }

    @Override
    public void callConnected(int code, WeTalkCall call) {
        makeStatusToast("Connected....");

    }

    @Override
    public void incoming(int code, WeTalkCall call) {
        makeStatusToast("Incoming....");
        currentCall = call;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeCallSection.setVisibility(View.GONE);
                receiveCallSection.setVisibility(View.VISIBLE);

            }
        });

    }

    private void makeStatusToast(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HomeActivity.this, status, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void resetUI(){
        makeCallSection.setVisibility(View.VISIBLE);
        receiveCallSection.setVisibility(View.GONE);
        rejectBtn.setText("Reject");
    }
}
