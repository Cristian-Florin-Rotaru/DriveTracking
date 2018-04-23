package com.example.gps.gps_speed;

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

public class HarshLogAsync  extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    String action;


    HarshLogAsync(Context ctx) {
        this.context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];

        String login_url = "https://speedtracker.000webhostapp.com/AggroBrakeLog.php";
        action = "Brake";
        if (type.equals("accel")) {
            login_url = "https://speedtracker.000webhostapp.com/AggroAccelLog.php";
            action = "Acceleration";
    }


            try {
                String userID = params[1];
                String latitude = params[2];
                String longitude = params[3];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8") + "&"
                        + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&"
                        + URLEncoder.encode("long", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");
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
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPreExecute() {


    }

    @Override
    protected void onPostExecute(String result) {
        if(result.contains("yes")) {
            Toast.makeText(context, "Successfully Logged Harsh " + action, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "Could not Log Harsh " + action, Toast.LENGTH_LONG).show();
        }

    }



    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}



