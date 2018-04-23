package com.example.gps.gps_speed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements IBaseGpsListener {

    private String userID;
    private String userName;
    String lat;
    String lon;
    float lastKnownSpeed = 0;
    float speedLimit = 15;
    float nCurrentSpeed = 0;
    boolean saveLastKnownSpeedDelay = false;    //Used to delay the updates of lastKnownSpeed
    boolean overSpeedDelay = false;             //Used to delay the requests to log into database (no point to have many logs about the same area)
    TextView user;
    TextView speed;
    TextView latitude;
    TextView longitude;
    TextView spdLimit;
    TextView lastKnownSpdLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = this.getSharedPreferences("Login_Preference", MODE_PRIVATE);
        userID=  Integer.toString(pref.getInt("UserID", 0));   // get UserID of the user that logged in as an Integer but convert it to String
        userName=pref.getString("UserName", null);   // get the UserName of the user that logged in as a String

        user                = findViewById(R.id.userTxtView);
        speed               = findViewById(R.id.speedTxtView);
        latitude            = findViewById(R.id.latTxtView);
        longitude           = findViewById(R.id.longTxtView);
        spdLimit            = findViewById(R.id.spdLimitTxtView);
        lastKnownSpdLimit   = findViewById(R.id.lastSpdTxtView);

        user.setText("User: " + userName);
        speed.setText("Speed: Not Available");
        latitude.setText("Latitude: Not Available");
        longitude.setText("Longitude: Not Available");
        spdLimit.setText("Speed Limit: " + speedLimit);
        lastKnownSpdLimit.setText("Last Speed Limit:" + speedLimit);


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);


    }



    public void finish()
    {
        super.finish();
        System.exit(0);
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        //float nCurrentSpeed = 0;

        if(location != null)
        {
            lat =Double.toString(location.getLatitude());
            lon =Double.toString(location.getLongitude());
        }


        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.UK, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');



        String strUnits = "miles/h";     //sets Units string to miles per hour
        speed.setText("Speed: " + strCurrentSpeed + strUnits);

        latitude.setText("Latitude: " + lat);
        longitude.setText("Longitude: " + lon);

        /*if (lastKnownSpeed - nCurrentSpeed > 12.0)
            logHarshAction("b" ,lat, lon);

        if (lastKnownSpeed - nCurrentSpeed < 12.0)
            logHarshAction("b" ,lat, lon);*/



    }



    @Override
    public void onLocationChanged(Location location) {

        if(location != null)
        {
            CLocation myLocation = new CLocation(location);
            nCurrentSpeed = myLocation.getSpeed();
            this.updateSpeed(myLocation);


            if (lastKnownSpeed - nCurrentSpeed > 10.0) {
                Toast.makeText(this, "Starting to log HARSH BRAKE", Toast.LENGTH_SHORT).show();
                logHarshAction("b");

            }
            if (nCurrentSpeed - lastKnownSpeed > 10.0) {
                Toast.makeText(this, "Starting to log HARSH ACCELERATION", Toast.LENGTH_SHORT).show();
                logHarshAction("a");
            }

            if (!saveLastKnownSpeedDelay) {
                lastKnownSpeed = nCurrentSpeed;
                saveLastKnownSpeedDelay = true;
                Log.d("lastSpeed", "true");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        saveLastKnownSpeedDelay = false;
                        Log.d("lastSpeed", "false");
                    }

                }, 1000); // 1second delay

            }

        }


        if (nCurrentSpeed > speedLimit && !overSpeedDelay) {
            Toast.makeText(this, "Starting to log SURPASSED LIMIT", Toast.LENGTH_SHORT).show();
            logOverLimit(Float.toString(nCurrentSpeed), Float.toString(speedLimit));
            overSpeedDelay = true;
            Log.d("overspeed", "true");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    overSpeedDelay = false;
                    Log.d("overspeed", "false");
                }

            }, 30000); // 30seconds delay

        }

    }



    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
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

    //Creates the three dot menu in top right corner, that has the option to log out
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logoutButton){

            SharedPreferences pref = this.getSharedPreferences("Login_Preference", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("UserID");
            editor.remove("UserName");
            editor.commit();
            this.startActivity(new Intent(this, LoginActivity.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    public void logHarshAction(String type) {
        HarshLogAsync harshLogAsync = new HarshLogAsync(this);
        harshLogAsync.execute("type", userID, lat, lon);
    }

    public void logOverLimit(String speed, String spdLimit) {
        OverLimitLog overLimitLog = new OverLimitLog(this);
        overLimitLog.execute(userID, speed, spdLimit, lat, lon);
    }

}
