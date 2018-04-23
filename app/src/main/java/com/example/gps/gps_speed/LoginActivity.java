package com.example.gps.gps_speed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Login activity has a simple layout with 2 edit text fields and a button
 * that handles the inputs "username" and "password" then connects to database
 * to check if the inputs correspond to an account
 */
public class LoginActivity extends AppCompatActivity {

    EditText UsernameEt, PasswordEt;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Checks for Location access permission and asks for it if it was not granted
        PermissionRequest permReq = new PermissionRequest(this);
        permReq.locPermissionCheck();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        UsernameEt = findViewById(R.id.usernameTxtField);
        PasswordEt = findViewById(R.id.passwordField);

        //If it finds an UserID saved in shared preferences, that means there is also an username
        //so it takes the username, and
        SharedPreferences pref = this.getSharedPreferences("Login_Preference", MODE_PRIVATE);
        userName = pref.getString("UserName", null);                // get userName of the user that logged in, as a String
        //If the username found is not null, it just goes directly to the MainActivity
        //so no more user details required (user has to log out to delete the information
        //from shared preferences
        if (userName != null) {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    /**
     * Checks the Username and password
     * and changes the activity to "MainActivity"
     * if the detail introduced are correspond to
     * an account
     */
    public void onLogin(View view) {
        String username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();
        String type = "login";
        LoginAsync loginAsync = new LoginAsync(this);
        loginAsync.execute(type, username, password);
    }
}