package com.example.gps.gps_speed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ProgrammingKnowledge on 1/5/2016.
 * http://www.codebind.com/android-tutorials-and-examples/android-mysql-database-tutorial-android-login-php-mysql/
 * Modified by Cristian-Florin Rotaru 2018
 * LoginAsync
 * Checks the username and password comparing with the accounts available on the database
 * Uses the LoginPHP.php in order to connect to database
 * Works in background
 */
public class LoginAsync extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;


    LoginAsync(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String login_url = "https://speedtracker.000webhostapp.com/LoginPHP.php";
        if (type.equals("login")) {
            try {
                String user_name = params[1];
                String password = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result + "," + user_name;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");

    }

    @Override
    protected void onPostExecute(String result) {
        if(result.contains("Reject")) {
            alertDialog.setMessage("Wrong Username or Password");
            alertDialog.show();
            Toast.makeText(context, "Access Denied", Toast.LENGTH_LONG).show();
        }
        else {
            String res = result.replaceAll("[ \n\r]","");
            String[] user = res.split(",");

            alertDialog.setMessage("Welcome " + user[1]);
            alertDialog.show();
            Toast.makeText(context, "Access Allowed", Toast.LENGTH_SHORT).show();
            //Saves the UserID and UserName in shared preferences, helps main activity to know
            //wich user is logged on and also helps LoginActivity to go directly to MainActivity
            //if the user did not log out before closing
            SharedPreferences pref = context.getSharedPreferences("Login_Preference", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("UserID", Integer.parseInt(user[0]));
            editor.putString("UserName", user[1]);
            editor.apply();
            context.startActivity(new Intent(context, MainActivity.class));     //starts MainActivity
            ((Activity) context).finish();                                       //Closes LoginActivity

        }

    }



    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}