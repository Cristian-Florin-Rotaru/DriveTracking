package com.example.gps.gps_speed;

import android.content.Context;
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

/**
 * Created by ProgrammingKnowledge on 1/5/2016.
 * http://www.codebind.com/android-tutorials-and-examples/android-mysql-database-tutorial-android-login-php-mysql/
 * Modified by Cristian-Florin Rotaru 2018
 * Works in background, it sends into database the information about harsh brakes
 * or aggressive accelerations.
 * Connects to database using AggroBrakeLog.php and AggroAccelLog.php located on
 * https://speedtracker.000webhostapp.com/ (Used free web hosting service)
 */
public class HarshLogAsync  extends AsyncTask<String,Void,String> {
    Context context;
    String action;


    HarshLogAsync(Context ctx) {
        this.context = ctx;
    }

    /**
     * @param params holds the information required to be sent to database
     *               (type, userID, latitude, longitude) where type is used to
     *               select what incident has to be logged (either acceleration or brake)
     * @return the result (yes/no) from the php script
     */
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];

        String logUrl = "https://speedtracker.000webhostapp.com/AggroBrakeLog.php";
        action = "Brake";
        if (type.contains("accel")) {
            logUrl = "https://speedtracker.000webhostapp.com/AggroAccelLog.php";
            action = "Acceleration";
        }


        try {
            String userID = params[1];
            String latitude = params[2];
            String longitude = params[3];
            URL url = new URL(logUrl);
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
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String result) {
        if(result.contains("yes")) {
            Toast.makeText(context, "Successfully Logged Harsh " + action, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Could not Log Harsh " + action, Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}



