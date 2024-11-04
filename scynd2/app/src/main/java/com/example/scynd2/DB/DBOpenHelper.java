package com.example.scynd2.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.scynd2.Bean.RunRecord;
import com.example.scynd2.Utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBOpenHelper extends SQLiteOpenHelper {
    public final String TAG = "DBOpenHelper";
    public static final String TABLE_NAME = "DBOpenHelper";

    public static final String CREATE_DB = "create table DBOpenHelper(" +
            "id integer primary key autoincrement, " +
            "uuid text, " +
            "date date, " +
            "distance double, " +
            "duration int, " +
            "avgSpeed double, " +
            "startTime date, " +
            "endTime date);";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    /**
     * add record to Database
     *
     * @param runRecord the record you want to add to database
     * @return if added successfully, then return true. Otherwise, return false.
     */
    public boolean addRecord(RunRecord runRecord) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("uuid", runRecord.getUuid());
        values.put("date", runRecord.getDate());
        values.put("distance", runRecord.getDistance());
        values.put("duration", runRecord.getDuration());
        values.put("avgSpeed", runRecord.getAvgSpeed());
        values.put("startTime", DateUtil.getFormattedTime(runRecord.getStartTime()));
        values.put("endTIme", DateUtil.getFormattedTime(runRecord.getEndTime()));
        long result = db.insert(TABLE_NAME, null, values);
        values.clear();

        if (result == -1) {
            Log.d(TAG, runRecord.getUuid() + " failed to add");
            return false;
        } else {
            Log.d(TAG, runRecord.getUuid() + " added successfully");
            return true;
        }

    }


    /**
     * Query Date
     *
     * @param queryDate the date you want to query
     * @return a list of RunRecords
     */
    public List<RunRecord> queryRecord(String queryDate) {
        List<RunRecord> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;

        //if not enter data return all data
        if (queryDate == null) {
            String sql = "select * from " + TABLE_NAME + " order by startTime desc";
            cursor = db.rawQuery(sql, null);
        } else {
            String sql = "select * from " + TABLE_NAME + " where date = ? order by startTime desc";
            cursor = db.rawQuery(sql, new String[]{queryDate});
        }

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                //get data
                String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                int duration = cursor.getInt(cursor.getColumnIndex("duration"));
                double avgSpeed = cursor.getDouble(cursor.getColumnIndex("avgSpeed"));
                Date startTime = DateUtil.strToTime(cursor.getString(cursor.getColumnIndex("startTime")));
                Date endTime = DateUtil.strToTime(cursor.getString(cursor.getColumnIndex("endTime")));

                //init
                RunRecord runRecord = new RunRecord();
                runRecord.setUuid(uuid);
                runRecord.setDate(date);
                runRecord.setDistance(distance);
                runRecord.setDuration(duration);
                runRecord.setAvgSpeed(avgSpeed);
                runRecord.setStartTime(startTime);
                runRecord.setEndTime(endTime);

                //add to results
                results.add(runRecord);
            }
        }

        return results;

    }

    /**
     * return all date
     * use queryRecord(null)
     *
     * @return a list of all RunRecords
     */
    public List<RunRecord> queryRecord() {

        List<RunRecord> results = queryRecord(null);

        return results;
    }




}
