package com.example.scynd2.Utils;

import android.content.Context;

import com.example.scynd2.DB.DBOpenHelper;
import com.example.scynd2.UI.Activity.MainActivity;

public class GlobalUtil {

    private static final String TAG = "GlobalUtil";
    private static GlobalUtil instance;

    //db
    public DBOpenHelper databaseHelper;

    private Context context;
    public MainActivity mainActivity;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        databaseHelper = new DBOpenHelper(context, DBOpenHelper.TABLE_NAME, null, 1);
    }

    public static GlobalUtil getInstance(){
        if (instance==null){
            instance = new GlobalUtil();
        }
        return instance;
    }
}
