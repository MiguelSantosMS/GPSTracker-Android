package com.example.miguel.gpstracker;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by miguel on 22/11/2016.
 */

public class AsyncJSON extends AsyncTask<String,Void,String> {

    MapsActivity parent;
    public AsyncJSON(MapsActivity parent){
        this.parent = parent;
    }
    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {
            String json = params[0];
            URL url = new URL("https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyCFWdBh83ML-6r6X6_vX9Cc5LCmugWR6f8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Set some headers to inform server about the type of the content
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-type", "application/json");
            conn.connect();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(json);
            writer.flush();
            writer.close();
            os.close();

            // read the response
            String response = "";

            int responseCode=conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }

            JSONObject responsejson = new JSONObject(response);
            JSONObject location = responsejson.getJSONObject("location");
            String lat = location.getString("lat");
            String lng = location.getString("lng");
            Log.v("Latitude", " "+lat);
            Log.v("Longitude", " "+lng);
            result = lat+"/"+lng;
            Log.v("Result", " "+result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        // Call an external function to receive the result
        parent.CreateMarker(result);
    }
}
