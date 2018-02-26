package com.example.gps.gps_speed;


import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import java.util.Timer;
import java.util.TimerTask;

public class AggroDetect{
    float currentSpeed = -1;
    float lastSpeed = -1;

    public void harshBrakeOrAccel (Location location, float speed) {

        final CLocation loc = new CLocation(location, false);

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
                        lastSpeed = loc.getSpeed();
                        waitTwoSec();
                        currentSpeed = loc.getSpeed();
                        waitTwoSec();
                    }
                    if (lastSpeed - currentSpeed > (float)4.0)

                    lastSpeed = currentSpeed;
                    currentSpeed = loc.getSpeed();


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


}
