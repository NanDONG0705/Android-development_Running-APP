package com.example.scynd2.UI.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.scynd2.R;
import com.example.scynd2.Utils.DataUtil;
import com.example.scynd2.Utils.DateUtil;


import java.util.Date;

public class RunResultActivity extends Activity {
    private static final String TAG = "RunResultActivity";
    Button btn_resultFinish;
    TextView tv_resultStartTime, tv_resultEndTime, tv_resultDistance, tv_resultSpeed, tv_resultDuration;

    Date startTime, endTime;
    int duration = 0, distance = 0;
    float avgSpeed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_result);

        //finsh, back to the main
        btn_resultFinish = findViewById(R.id.btn_resultFinish);
        btn_resultFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_resultStartTime = findViewById(R.id.tv_resultStartTime);
        tv_resultEndTime = findViewById(R.id.tv_resultEndTime);
        tv_resultDistance = findViewById(R.id.tv_resultDistance);
        tv_resultDuration = findViewById(R.id.tv_resultDuration);
        tv_resultSpeed = findViewById(R.id.tv_resultSpeed);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        startTime = (Date) bundle.get("startTime");
        endTime = (Date) bundle.get("endTime");
        duration = (int) bundle.get("duration");

        boolean isValid = (boolean) bundle.get("isValid");
        if (isValid) {
            Log.d(TAG, "data is valid");
            distance = (int) bundle.get("distance");
            avgSpeed = (float) bundle.get("avgSpeed");
        }

        Log.d(TAG, startTime + " " + endTime + " " + distance + " " + duration + " " + avgSpeed);

        setData();

    }

    public void setData(){

        tv_resultStartTime.setText(DateUtil.getFormattedTime(startTime));
        tv_resultEndTime.setText(DateUtil.getFormattedTime(endTime));
        tv_resultDistance.setText(""+distance);
        //save 2 number
        tv_resultSpeed.setText(""+(((int)(avgSpeed*100))/100.0));
        tv_resultDuration.setText(DataUtil.getFormattedTime(duration));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
