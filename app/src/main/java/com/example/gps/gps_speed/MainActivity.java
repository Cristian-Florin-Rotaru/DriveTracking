package com.example.gps.gps_speed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;


public class MainActivity extends AppCompatActivity implements IBaseGpsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Checks for Location permission and asks for it if it was not granted
        PermissionRequest permReq = new PermissionRequest(this);
        permReq.locPermissionCheck();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);





       /* CheckBox chkUseMetricUntis = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        chkUseMetricUntis.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.this.updateSpeed(null);

            }
        });*/


    }



    public void finish()
    {
        super.finish();
        System.exit(0);
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if(location != null)
        {
            location.setUseMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.UK, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "mph";        //sets Units string to miles per hour
        if(this.useMetricUnits())
        {
            strUnits = "km/h";          //sets Units string to kilometres per hour
        }

        TextView txtCurrentSpeed = (TextView) this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setTextColor(Color.BLACK);

        if (this.useMetricUnits())
        if (Double.parseDouble(strCurrentSpeed) > 5)
            txtCurrentSpeed.setTextColor(Color.RED);
        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    private boolean useMetricUnits() {

        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        return chkUseMetricUnits.isChecked();
    }

//    @Override
    public void onLocationChanged(Location location) {

        if(location != null)
        {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

//    @Override
    public void onProviderDisabled(String provider) {

    }

//    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

//    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

//    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    }



}
