package com.example.scynd2.UI.Activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.scynd2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.scynd2.UI.Fragment.RecordFragment;
import com.example.scynd2.UI.Fragment.RunFragment;
import com.example.scynd2.Utils.GlobalUtil;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final  String TAG = "MainActivity";
    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    private FragmentManager fm;
    private RecordFragment frgLog;
    private RunFragment frgRun;
    String isFirstUse = null;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_log:
                    if(frgLog == null){
                        frgLog = new RecordFragment();
                    }
                    switchFragment(frgLog);
                    break;
                case R.id.navigation_run:
                    if (frgRun == null){
                        frgRun = new RunFragment();
                    }
                    switchFragment(frgRun);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //apply WRITE_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//customisable code
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //apply WRITE_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//customisable code
        }

        fm = getSupportFragmentManager();

        //BottomNavigationBar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_run);


        //set Context
        GlobalUtil.getInstance().setContext(getApplicationContext());
        GlobalUtil.getInstance().mainActivity = this;

        InputStream inputStream = null;

        try {
            inputStream = openFileInput("config");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            isFirstUse = bufferedReader.readLine();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * change fragment
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {
        List<Fragment> fragments = fm.getFragments();
        for (Fragment f : fragments) {
            if (!f.equals(fragment) && !f.isHidden()) {
                fm.beginTransaction().hide(f).commit();
            }
        }
        if (fragment.isAdded()) {
            fm.beginTransaction().show(fragment).commit();
        } else {
            fm.beginTransaction().add(R.id.fl_main, fragment).commit();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }
}
