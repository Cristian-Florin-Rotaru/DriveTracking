package com.example.gps.gps_speed;

import android.location.Location;

/**
 * Code from:
 * http://mycodingworld1.blogspot.co.uk/2015/12/calculate-speed-from-gps-location.html
 */
public class CLocation extends Location {

    private boolean bUseMetricUnits = true;

    public CLocation(Location location){
        this(location, true);
    }

    public CLocation(Location location, boolean bUseMetricUnits) {

        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }


    public boolean getUseMetricUnits() {
        return this.bUseMetricUnits;
    }

    public void setUseMetricUnits(boolean bUseMetricUnits) {
        this.bUseMetricUnits = bUseMetricUnits;
    }

    @Override
    public float distanceTo(Location dest) {
        // TODO Auto-generated method stub
        float nDistance = super.distanceTo(dest);
        if(!this.getUseMetricUnits()) {
            //Convert metres to feet
            nDistance = nDistance * 3.28083989501312f;
        }
        return nDistance;
    }

    @Override
    public float getAccuracy() {
        // TODO Auto-generated method stub
        float nAccuracy = super.getAccuracy();
        if(!this.getUseMetricUnits()) {
            //Convert metres to feet
            nAccuracy = nAccuracy * 3.28083989501312f;
        }
        return nAccuracy;
    }

    @Override
    public double getAltitude() {
        // TODO Auto-generated method stub
        double nAltitude = super.getAltitude();
        if(!this.getUseMetricUnits()) {
            //Convert metres to feet
            nAltitude = nAltitude * 3.28083989501312d;
        }
        return nAltitude;
    }

    @Override
    public float getSpeed() {

        //convert from metres/second to km/hour
        float nSpeed = super.getSpeed();
        if(!this.getUseMetricUnits()) {
            //Convert metres/second to miles/hour
            nSpeed = nSpeed * 2.2369362920544f;
        }
        return nSpeed;
    }


}