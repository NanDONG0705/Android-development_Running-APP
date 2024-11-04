package com.example.scynd2.UI.Activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.scynd2.R;


public class CountDownActivity extends Activity {

    int count = 3;
    TextView tv_countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
        tv_countDown = findViewById(R.id.tv_countDown);
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    //count down 3 2 1
    private int getCount() {
        count--;
        if (count == 0) {
            Intent intent = new Intent(this, GoogleMapActivity.class);
            startActivity(intent);
            finish();
        }
        return count;
    }

    //Perform a message processing
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                int num = getCount();
                if (num == 0 || num < 0){
                    tv_countDown.setText("GO!");
                }else{
                    tv_countDown.setText(num + "");
                }
                handler.sendEmptyMessageDelayed(0, 1000);
            }

        }
    };
}
