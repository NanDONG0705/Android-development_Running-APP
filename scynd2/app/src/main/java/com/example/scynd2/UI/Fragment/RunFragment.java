package com.example.scynd2.UI.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.scynd2.Bean.RunRecord;
import com.example.scynd2.R;
import com.example.scynd2.UI.Activity.CountDownActivity;
import com.example.scynd2.UI.Activity.UserGuidesActivity;
import com.example.scynd2.Utils.DateUtil;
import com.example.scynd2.Utils.GlobalUtil;
import com.example.scynd2.Utils.GoalsUtil;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RunFragment extends Fragment {

    public static final String TAG = "RunFragment";

    TickerView tv_todayDistance;
    ImageButton btn_run;
    Button btn_guide;
    Context context;
    TextView tv_unit;
    TextView selectedOptionTextView;
    NiceSpinner locSpinner,timeSpinner;
    TextView selectedTimeTextView;


    public RunFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_run, container, false);
        btn_guide = view.findViewById(R.id.btn_guide);
        //set TickerView
        tv_todayDistance = view.findViewById(R.id.tv_todayDistance);
        tv_todayDistance.setCharacterLists(TickerUtils.provideNumberList());
        tv_todayDistance.setAnimationDuration(2000);
        selectedOptionTextView = view.findViewById(R.id.selected_option_textview);
        selectedTimeTextView = view.findViewById(R.id.selected_time_textview);

        locSpinner = (NiceSpinner) view.findViewById(R.id.loc_spinner);
        timeSpinner = (NiceSpinner) view.findViewById(R.id.time_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("1", "2", "3", "4", "5"));
        locSpinner.attachDataSource(dataset);
        timeSpinner.attachDataSource(dataset);


        locSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any
                selectedOptionTextView.setText(locSpinner.getSelectedItem().toString()+"  km");
            }
        });

        timeSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                // This example uses String, but your type can be any
                selectedTimeTextView.setText(timeSpinner.getSelectedItem().toString()+"  min");
            }
        });

        //add OnClickListener
        btn_run = view.findViewById(R.id.btn_run);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoalsUtil.getInstance().setMyloc(locSpinner.getSelectedItem().toString());
                GoalsUtil.getInstance().setMytime(timeSpinner.getSelectedItem().toString());
                Intent intent = new Intent(context, CountDownActivity.class);
                context.startActivity(intent);
            }
        });
        btn_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserGuidesActivity.class);
                context.startActivity(intent);
            }
        });
        tv_unit = view.findViewById(R.id.tv_unit);


        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        String todayDate = DateUtil.getFormattedDate();
        List<RunRecord> results = GlobalUtil.getInstance().databaseHelper.queryRecord(todayDate);

        //today total move distance
        int todayTotalDistance = 0;
        for (RunRecord record :
                results) {
            todayTotalDistance += record.getDistance();
        }

        //if bigger than 1000m show km
        if (todayTotalDistance < 1000) {
            tv_todayDistance.setText(todayTotalDistance + "");
        }else{
            double disKM = todayTotalDistance/1000.0;
            tv_todayDistance.setText(disKM + "");
            tv_unit.setText("KM");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
