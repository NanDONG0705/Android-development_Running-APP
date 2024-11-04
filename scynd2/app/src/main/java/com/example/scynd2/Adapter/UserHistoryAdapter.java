package com.example.scynd2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scynd2.Bean.RunRecord;
import com.example.scynd2.R;
import com.example.scynd2.Utils.DataUtil;
import com.example.scynd2.Utils.DateUtil;


import java.util.List;

public class UserHistoryAdapter extends RecyclerView.Adapter<UserHistoryAdapter.RecordHolder> {

    //record
    List<RunRecord> records;


    //Single record ViewHolder
    class RecordHolder extends RecyclerView.ViewHolder {
        //get elements in log_item
        TextView tv_recordDistance, tv_recordUnit, tv_recordStartTime, tv_recordAvgSpeed, tv_recordDuration;


        public RecordHolder(@NonNull View itemView) {
            super(itemView);

            tv_recordDistance = itemView.findViewById(R.id.tv_recordDistance);
            tv_recordUnit = itemView.findViewById(R.id.tv_recordUnit);
            tv_recordStartTime = itemView.findViewById(R.id.tv_recordStartTime);
            tv_recordAvgSpeed = itemView.findViewById(R.id.tv_recordAvgSpeed);
            tv_recordDuration = itemView.findViewById(R.id.tv_recordDuration);
        }
    }

    public UserHistoryAdapter(List<RunRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.history_item, viewGroup, false);
        RecordHolder recordHolder = new RecordHolder(view);
        return recordHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordHolder recordHolder, int i) {
        RunRecord record = records.get(i);
        int distance = (int)record.getDistance();

        //if bigger than 1000 m，change m
        double distanceKM = 0;
        boolean unitIsKM = false;
        if (distance > 1000) {
            distanceKM = distance / 1000.0;
            unitIsKM = true;
        }

        if (unitIsKM){
            recordHolder.tv_recordDistance.setText(String.valueOf(String.valueOf(distanceKM)));
            recordHolder.tv_recordUnit.setText("KM");
        }else{
            recordHolder.tv_recordDistance.setText(String.valueOf(distance));
            recordHolder.tv_recordUnit.setText("m");
        }

        recordHolder.tv_recordStartTime.setText(DateUtil.getFormattedTime(record.getStartTime()));
        recordHolder.tv_recordAvgSpeed.setText(String.valueOf(((int)(record.getAvgSpeed()*100)/100.0)));
        recordHolder.tv_recordDuration.setText(DataUtil.getFormattedTime(record.getDuration()));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
