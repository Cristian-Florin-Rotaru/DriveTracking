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
 * Works in background, it sends into database the information about going over
 * the speed limit
 * Connects to database using SpeedLimitLog.php located on
 * https://speedtracker.000webhostapp.com/ (Used free web hosting service)
 */
public class OverLimitLog extends AsyncTask<String, Void, String> {
    Context context;
    String action;


    OverLimitLog(Context ctx) {
        this.context = ctx;
    }

    /**
     * @param params holds the information required to be sent to database (userID, speed, speedLimit,
     *               latitude, longitude)
     * @return the result (yes/no) from the php script
     */
    @Override
    protected String doInBackground(String... params) {
        String userID = params[0];
        String speed = params[1];
        String speedLimit = params[2];
        String latitude = params[3];
        String longitude = params[4];

        String logUrl = "https://speedtracker.000webhostapp.com/SpeedLimitLog.php";
        action = "Over speed limit";


        try {
            URL url = new URL(logUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8") + "&"
                    + URLEncoder.encode("speed", "UTF-8") + "=" + URLEncoder.encode(speed, "UTF-8") + "&"
                    + URLEncoder.encode("limit", "UTF-8") + "=" + URLEncoder.encode(speedLimit, "UTF-8") + "&"
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
        if (result.contains("yes")) {
            Toast.makeText(context, "Successfully Logged " + action, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Could not Log " + action, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}





