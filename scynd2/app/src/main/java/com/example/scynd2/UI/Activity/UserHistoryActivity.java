package com.example.scynd2.UI.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.VelocityTracker;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scynd2.Adapter.UserHistoryAdapter;
import com.example.scynd2.Bean.RunRecord;
import com.example.scynd2.R;
import com.example.scynd2.Utils.GlobalUtil;


import java.util.List;

public class UserHistoryActivity extends Activity {

    RecyclerView rv_log;
    UserHistoryAdapter logAdapter;

    LinearLayout ll_noData;

    LinearLayout ll_logActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        rv_log = findViewById(R.id.rv_log);

        ll_logActivity = findViewById(R.id.ll_logActivity);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserHistoryActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_log.setLayoutManager(linearLayoutManager);

        ll_noData = findViewById(R.id.ll_noData);

        DividerItemDecoration decoration = new DividerItemDecoration(UserHistoryActivity.this, DividerItemDecoration.VERTICAL);
        rv_log.addItemDecoration(decoration);

        //get all data
        List<RunRecord> records = GlobalUtil.getInstance().databaseHelper.queryRecord();

        if (records.size() != 0) {
            ll_noData.setVisibility(LinearLayout.INVISIBLE);

            logAdapter = new UserHistoryAdapter(records);
            rv_log.setAdapter(logAdapter);
        }
    }


}
