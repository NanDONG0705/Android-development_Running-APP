package com.example.scynd2.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps.AMapUtils;
import com.example.scynd2.Bean.RunRecord;
import com.example.scynd2.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.scynd2.Utils.DataUtil;
import com.example.scynd2.Utils.GlobalUtil;
import com.example.scynd2.Utils.GoalsUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private List<LatLng> trackPoints;
    private Polyline trackPolyline;
    private Location lastLocation;

    //Flag for determining whether to display positioning information only once and user relocation
    private boolean isFirstLoc = true;
    //Previous positioning position and current positioning position for distance calculation
    LatLng lastLatLng, nowLatLng;
    //total movement distance
    int distanceThisTime = 0;
    //avg speed
    float avgSpeed = 0;
    //start time
    Date startTime;

    //Wait for two changes of position for a more accurate position.
    int count = 2;

    //show data
    TextView tv_mapSpeed, tv_mapDuration, tv_mapUnit;
    TickerView tv_mapDistance;
    ImageView reach,noreach;
    TextView my_goal;

    //stop button
    FloatingActionButton btn_stop;
    private boolean reachflag = false;
    private int locgoal;
    private int timegoal;
    private boolean isshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_map);
        // Getting a reference to the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // init FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // init the track point list
        trackPoints = new ArrayList<>();

        //init start time
        startTime = new Date();

        //init data display component
        tv_mapDuration = findViewById(R.id.tv_mapDuration);
        tv_mapSpeed = findViewById(R.id.tv_mapSpeed);
        tv_mapUnit = findViewById(R.id.tv_mapUnit);

        tv_mapDistance = findViewById(R.id.tv_mapDistance);
        tv_mapDistance.setCharacterLists(TickerUtils.provideNumberList());
        tv_mapDistance.setAnimationDuration(500);

        reach = findViewById(R.id.isreach);
        noreach = findViewById(R.id.noreach);

        my_goal = findViewById(R.id.mygoal);

        my_goal.setText("My Target:\n"+GoalsUtil.getInstance().getMyloc()+"km"+"\n"
                +GoalsUtil.getInstance().getMytime()+"min");

        locgoal = Integer.parseInt(GoalsUtil.getInstance().getMyloc()) * 1000;
        timegoal = Integer.parseInt(GoalsUtil.getInstance().getMytime()) * 60;

        //stop button
        btn_stop = findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GoogleMapActivity.this, RunResultActivity.class);

                //
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", new Date());
                intent.putExtra("duration", (int) ((new Date().getTime() - startTime.getTime()) / 1000));

                //If the distance is 0, it is not stored in the database
                if (distanceThisTime < 10) {
                    Toast.makeText(getApplicationContext(), "Distance too short，Record Invalid", Toast.LENGTH_SHORT).show();
                    //Indicate that this time it is not valid
                    intent.putExtra("isValid", false);
                    startActivity(intent);
                    finish();
                    return;
                }

                //Deposit of this data in the database
                RunRecord runRecord = new RunRecord();
                runRecord.setEndTime(new Date());
                runRecord.setDistance(distanceThisTime);
                runRecord.setDuration((int) ((new Date().getTime() - startTime.getTime()) / 1000));
                runRecord.setAvgSpeed(avgSpeed);
                if (GlobalUtil.getInstance().databaseHelper.addRecord(runRecord)) {
                    Toast.makeText(getApplicationContext(), "Data Recorded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data Record Failed", Toast.LENGTH_SHORT).show();
                }

                //Indicates that this time is valid
                intent.putExtra("isValid", true);
                intent.putExtra("distance", distanceThisTime);
                intent.putExtra("avgSpeed", avgSpeed);
                startActivity(intent);
                finish();
            }
        });

        // Creating a Location Request
        locationRequest = new LocationRequest()
                .setInterval(200) //Set the interval between position updates in milliseconds
                .setFastestInterval(200) // Set the fastest position update interval in milliseconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Setting the priority of location requests

        // Setting the priority of a location request
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    // Processing new location updates
                    handleNewLocation(location);
                }
            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                startLocationUpdates();
            }
        }
    }
    private void startLocationUpdates() {
        // check location quest
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        float nowSpeed = location.getSpeed();//get speed

        //Calculate the average speed, i.e. (current speed + current average speed)/2
        //If it is the first positioning, the current speed is the average speed
        if (isFirstLoc) {
            avgSpeed = nowSpeed;
        } else {
            avgSpeed = (avgSpeed + nowSpeed) / 2;
        }

        //retain two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedSpeed = decimalFormat.format(avgSpeed);
        String formattednowSpeed = decimalFormat.format(nowSpeed);
        avgSpeed = Float.parseFloat(formattedSpeed);
        nowSpeed = Float.parseFloat(formattednowSpeed);

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        if (!isFirstLoc) {
            int tempDistance = (int) lastLocation.distanceTo(location);
            if (tempDistance > 300) {
                lastLocation = location;
                return;
            }

        //current time
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(location.getTime());
        String locTime = df.format(date);//locationtime

            // Calculate total distance
            distanceThisTime += tempDistance;

            // add track point
            trackPoints.add(latLng);

            // draw line
            if (trackPolyline != null) {
                trackPolyline.remove();
            }
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.RED)
                    .width(8)
                    .addAll(trackPoints);
            trackPolyline = mMap.addPolyline(polylineOptions);

            //Gain duration seconds
            int duration = (int) (new Date().getTime() - startTime.getTime()) / 1000;
            //Converts duration seconds to HH:mm:ss and displays it to the control
            tv_mapDuration.setText(DataUtil.getFormattedTime(duration));
            //Display the total distance to the control
            if (distanceThisTime > 1000) {
                //If greater than 1000 metres, kilometres are displayed
                double showDisKM = distanceThisTime / 1000.0;
                tv_mapDistance.setText(showDisKM + "");
                tv_mapUnit.setText("KM");
            } else {
                tv_mapDistance.setText(distanceThisTime + "");
            }
            //show speed
            if (nowSpeed == 0) {
                tv_mapSpeed.setText("--.--");
            } else {
                tv_mapSpeed.setText(nowSpeed + "");
            }

            if (distanceThisTime > locgoal && duration < timegoal){
                reachflag = true;
            }

            //Signs indicating compliance or non-compliance
            if (reachflag){
                //It's already popped up once.
                if(!isshow) {
                    Toast.makeText(this, "Target Completion!", Toast.LENGTH_SHORT).show();
                    isshow = true;
                }
                reach.setVisibility(View.VISIBLE);
                noreach.setVisibility(View.INVISIBLE);
            }

            if (duration > timegoal){
                if (!reachflag) {
                    noreach.setVisibility(View.VISIBLE);
                    reach.setVisibility(View.INVISIBLE);
                }
            }
        }
        //  Move the map view to the current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        if (isFirstLoc) {
            isFirstLoc = false;
        }
        lastLocation = location;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop position update
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Execute mMapView.onSaveInstanceState (outState) when activity executes onSaveInstanceState to save the current state of the map
    }


    //Used to detect whether two consecutive presses
    private long time = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - time > 1000)) {
                Toast.makeText(this, "Press again to return to the main screen", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        // Checking Positioning Privileges
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            startLocationUpdates();
        } else {
            // Request Location Permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}

