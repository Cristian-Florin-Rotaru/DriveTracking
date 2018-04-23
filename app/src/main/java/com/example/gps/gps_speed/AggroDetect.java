package com.example.gps.gps_speed;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AggroDetect implements IBaseGpsListener{
    float currentSpeed = -1;
    float lastSpeed = -1;

    public void harshBrakeOrAccel (final Location location) {


        //Handlers that will record when an event is occurring (Either harsh brakes or harsh acceleration)
        Handler harshBrake = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //Here will be the code to record harsh brakes
            }
        };

        Handler harshAccel = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //Here will be the code to record harsh acceleration
            }
        };

        Runnable checkTwoSec = new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                while (true){
                    if (currentSpeed < 0 && lastSpeed < 0){
                        lastSpeed = location.getSpeed();
                        waitTwoSec();
                        currentSpeed = location.getSpeed();
                        waitTwoSec();
                    }
                    if (lastSpeed - currentSpeed > (float)4.0)

                    lastSpeed = currentSpeed;
                    currentSpeed = location.getSpeed();


                }

            }
        };

        }
        public void waitTwoSec (){

            try {
                wait(2000);

            }catch (Exception e){

            }

        }

    private float updateSpeed(Location location) {





        if(location != null)
        return location.getSpeed();
        else return 0;

       /* Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.UK, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "KM/H";        //sets Units string to miles per hour*/
        /*if(this.useMetricUnits())
        {
            strUnits = "km/h";          //sets Units string to kilometres per hour
        }*/

     /*   TextView txtCurrentSpeed = (TextView) this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setTextColor(Color.BLACK);

        //if (this.useMetricUnits())
        if (Double.parseDouble(strCurrentSpeed) > 5)
            txtCurrentSpeed.setTextColor(Color.RED);
        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);*/
        }

  /*  private boolean useMetricUnits() {

        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        return chkUseMetricUnits.isChecked();
    }*/

    @Override
    public void onLocationChanged(Location location) {

        if(location != null)
        {
            Location myLocation = new Location(location);
            this.updateSpeed(myLocation);
        }
    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}
