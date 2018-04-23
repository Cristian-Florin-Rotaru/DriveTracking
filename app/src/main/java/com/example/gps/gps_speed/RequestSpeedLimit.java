package com.example.gps.gps_speed;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RequestSpeedLimit extends AsyncTask<String, Void, String> {
    Context context;
    String action;


    RequestSpeedLimit(Context ctx) {
        this.context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String lat1 = params[0];
        String long1 = params[1];
        String lat2 = params[2];
        String long2 = params[3];
        //lat2>lat1

        if (Double.parseDouble(lat1) > Double.parseDouble(lat2)) {

            String aux = lat2;
            lat2 = lat1;
            lat1 = aux;

            aux = long2;
            long2 = long1;
            long1 = aux;
        }


        String requestUrl = "http://www.overpass-api.de/api/xapi?*[maxspeed=*][bbox=long1,lat1,long2,lat2]";
        requestUrl = requestUrl.replace("lat1", lat1).replace("long1", long1).replace("lat2", lat2).replace("long2", long2);


        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();

            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
               /* if (line.contains("<tag k=\"highway\" v=\""))
                    if (result.equals(null))
                        result = line.replace("<tag k=\"highway\" v=\"", "").replace("\"/>", "");
                if (line.contains("<tag k=\"maxspeed\" v="))
                result = line.replace("<tag k=\"maxspeed\" v=", "").replace("\"/>", "");*/
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
        if (result != null) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Could not retrieve speed limit ", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}





