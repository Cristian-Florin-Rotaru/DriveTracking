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
    private String lat;
    private String lon;
    private float lastKnownSpeed = 0;
    private float speedLimit = 25;
    private float harshTrigger = 10;
    private float nCurrentSpeed = 0;
    private boolean saveLastKnownSpeedDelay = false;    //Used to delay the updates of lastKnownSpeed
    private boolean overSpeedDelay = false;             //Used to delay the requests to log into database (no point to have many logs about the same area)
    private boolean harshAccelDelay = false;            //Used to delay the requests to log into database (no point to have many logs about the same area)
    private boolean harshBrakeDelay = false;            //Used to delay the requests to log into database (no point to have many logs about the same area)
    private TextView user;
    private TextView speed;
    private TextView latitude;
    private TextView longitude;
    private TextView spdLimit;
    private TextView lastKnownSpdLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        retrieveSharedPrefs();
        setTextViews();

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateLocation(null);


    }

    /**
     * sets the default text views when MainActivity is loaded (until the information from location is received)
     */
    private void setTextViews() {
        user = findViewById(R.id.userTxtView);
        speed = findViewById(R.id.speedTxtView);
        latitude = findViewById(R.id.latTxtView);
        longitude = findViewById(R.id.longTxtView);
        spdLimit = findViewById(R.id.spdLimitTxtView);
        lastKnownSpdLimit = findViewById(R.id.lastSpdTxtView);

        user.setText("User: " + userName);
        speed.setText("Speed: Not Available");
        latitude.setText("Latitude: Not Available");
        longitude.setText("Longitude: Not Available");
        spdLimit.setText("Speed Limit: " + speedLimit);
        lastKnownSpdLimit.setText("Last Speed Limit:" + speedLimit);
    }

    private void retrieveSharedPrefs() {
        SharedPreferences pref = this.getSharedPreferences("Login_Preference", MODE_PRIVATE);
        userID = Integer.toString(pref.getInt("UserID", 0));   // get UserID of the user that logged in as an Integer but convert it to String
        userName = pref.getString("UserName", null);   // get the UserName of the user that logged in as a String
    }

    public void finish() {
        super.finish();
        System.exit(0);
    }

    /**
     * Updates the text in MainActivity for Latitude, Longitude and Speed
     *
     * @param location
     */
    private void updateLocation(Location location) {


        //saves the coordinates in lat and lon variables
        if (location != null) {
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());
        }


        //Formats the strCurrentSpeed to have a format like this: "000.0"
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.UK, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');


        String strUnits = "miles/h";     //sets Units string to miles per hour
        speed.setText("Speed: " + strCurrentSpeed + strUnits);

        latitude.setText("Latitude: " + lat);
        longitude.setText("Longitude: " + lon);


    }

    /**
     * Whenever the location is changed, retrieves the speed as mph
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            //Converts m/s to mph
            nCurrentSpeed = location.getSpeed() * 2.2369362920544f;
            this.updateLocation(location);
            checkAggressiveAction();

        }

        //lastKnownSpeed gets the current speed and then it is set a delay of 1 sec
        //until it can be updated again (it helps the app to compare the current speed value
        //with the one that that was registered a second before
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


        //logs if the speed was exceeded and it gives a delay of 30 seconds so
        //it will not log into database too many over speed limit records
        //checks if speedLimit > 0 (has a value)
        if (nCurrentSpeed > speedLimit && !overSpeedDelay && speedLimit > 0) {
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

    /**
     * Creates the 3 dot menu that has the log out button
     * If the log out button is pressed, deletes the information from shared preferences,
     * kills the MainActivity and starts the Login Activity
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logoutButton) {

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

    /**
     * Logs when Aggressive Acceleration or Harsh Brake is detected
     *
     * @param type tells the method what type of log it is (acceleration or brake)
     */
    private void logAggressiveAction(String type) {
        HarshLogAsync harshLogAsync = new HarshLogAsync(this);
        harshLogAsync.execute(type, userID, lat, lon);
    }

    /**
     * checks for HarshBrakes or Aggressive Acceleration and triggers logAggressiveAction if any was detected
     */
    private void checkAggressiveAction() {
        //Checks if the speed dropped below the "harshTrigger", if so, it logs into database
        //and starts a delay of 3 seconds until the next
        if (lastKnownSpeed - nCurrentSpeed > harshTrigger && !harshBrakeDelay) {
            Toast.makeText(this, "Starting to log HARSH BRAKE", Toast.LENGTH_SHORT).show();
            logAggressiveAction("b");
            harshBrakeDelay = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    harshBrakeDelay = false;
                    Log.d("harshAccel", "false");
                }

            }, 3000); // 3 seconds delay
        }
        if (nCurrentSpeed - lastKnownSpeed > harshTrigger && !harshAccelDelay) {
            Toast.makeText(this, "Starting to log HARSH ACCELERATION", Toast.LENGTH_SHORT).show();
            logAggressiveAction("accel");
            harshAccelDelay = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    harshAccelDelay = false;
                    Log.d("harshAccel", "false");
                }

            }, 3000); // 3 seconds delay
        }
    }


    /**
     * Logs when over speed limit was detected
     *
     * @param speed    current speed
     * @param spdLimit speed limit
     */
    private void logOverLimit(String speed, String spdLimit) {
        OverLimitLogAsync overLimitLogAsync = new OverLimitLogAsync(this);
        overLimitLogAsync.execute(userID, speed, spdLimit, lat, lon);
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

    //Creates the three dot menu in top right corner, that has the option to log out
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
