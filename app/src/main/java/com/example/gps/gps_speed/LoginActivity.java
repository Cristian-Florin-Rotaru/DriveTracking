package com.example.gps.gps_speed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText UsernameEt, PasswordEt;
    private int UserID;
    private String UserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Checks for Location and Internet access permission and asks for it if it was not granted
        PermissionRequest permReq = new PermissionRequest(this);
        permReq.locPermissionCheck();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        UsernameEt = findViewById(R.id.usernameTxtField);
        PasswordEt = findViewById(R.id.passwordField);

        SharedPreferences pref = this.getSharedPreferences("Login_Preference", MODE_PRIVATE);
        UserID=pref.getInt("UserID", 0);                // get UserID of the user that logged in as an Integer
        if (UserID > 0) {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void onLogin(View view) {
        String username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();
        String type = "login";
        LoginAsync loginAsync = new LoginAsync(this);
        loginAsync.execute(type, username, password);
    }
}