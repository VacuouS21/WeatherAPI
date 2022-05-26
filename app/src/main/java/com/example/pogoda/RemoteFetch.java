package com.example.pogoda;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    public static JSONObject getJSON(String link){
        try {

            URL url = new URL(link);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
           /* connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));*/

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            System.out.println("HIIIIIIIIIIIII");
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            System.out.println("json"+json);

            JSONObject data = new JSONObject(json.toString());
            System.out.println("data"+data);
            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
