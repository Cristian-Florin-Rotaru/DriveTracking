package com.example.gps.gps_speed;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * A simple class that holds the locPermissionRequest method, that simply checks
 * if the app already has Permission to access location, if the app does not have permissions granted,
 * it asks for them
 */
public class PermissionRequest {
    Context context;

    public PermissionRequest(Context context) {
        this.context = context;
    }

    //locPermissionCheck Checks for Location permission and asks for it if it was not granted
    public void locPermissionCheck (){

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {

            int REQUEST_CODE = 1;
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);
        }

        permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {

            int REQUEST_CODE = 2;
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }

    }
}
