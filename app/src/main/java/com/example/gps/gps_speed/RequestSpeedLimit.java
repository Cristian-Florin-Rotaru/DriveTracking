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

/**
 * Did not manage to successfully implement this class, it was a try to replace the Google Roads api
 * which is not free to use
 */
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

        /**
         * http://www.overpass-api.de/ requires the second latitude parameter to be bigger than the first latitude parameter
         */
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


        /**
         * Tries to make a connection with the http://www.overpass-api.de/ web service
         * and read the "xapi" file that is returned
         * (It works just to send the request but not able to get an process the information)
         * (Tested separately in browser with the link build in the earlier stage, it worked,
         * so the problem is in the Reader part of code)
         */
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

    /**
     * used for debugging, if the code was functional to retrieve the information,
     * here would be the code for sending to MainActivity the speed limit found
     *
     * @param result should have been the speed limit found between the specified coordinates
     *               or null if no information was found
     */
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





