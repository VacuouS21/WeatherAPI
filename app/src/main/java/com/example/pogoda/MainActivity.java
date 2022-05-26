package com.example.pogoda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;

import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;

import android.widget.TextView;

import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String CITY;

    View swipe;
    ArrayList<String> users = new ArrayList<String>();

    String API = "6daa5b79270dd11c0213880e14a1a570";
    int i=0;

    Button search;

    EditText etCity;

    Typeface weatherFont;

    TextView city, country, time, temp, forecast, humidity, min_temp, max_temp, sunrises, sunsets;
    ImageView image;


    private LocationManager locationManager;


    private static final String TAG = "Debug";
    private Boolean flag = false;

    @SuppressLint("MissingPermission")
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if(getIntent().getExtras()!=null) {
            Bundle arguments = getIntent().getExtras();
            users = arguments.getStringArrayList("hello");
        }
        weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                123);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        CITY = showLocation(location);
        new weatherTask().execute();

        if(users.size()<=0) Collections.addAll(users, CITY,"Moscow","New York","Chita");

        {

            swipe=findViewById(R.id.swipe);
            etCity = findViewById(R.id.Your_city);

            search = findViewById(R.id.search);

// CALL ALL ANSWERS :

            city = findViewById(R.id.city);

            country = findViewById(R.id.country);

            time = findViewById(R.id.time);

            temp = findViewById(R.id.temp);

            forecast = findViewById(R.id.forecast);

            humidity = findViewById(R.id.humidity);

            min_temp = findViewById(R.id.min_temp);

            max_temp = findViewById(R.id.max_temp);

            sunrises = findViewById(R.id.sunrises);

            sunsets = findViewById(R.id.sunsets);

            image=findViewById(R.id.image);
            //image.setTypeface(weatherFont);
           // city.setText(messageText);

// CLICK ON SEARCH BUTTON :

            image.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
                public void onSwipeTop() {
                    Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                }
                public void onSwipeRight() {
                    if(i>0) {i--;
                        CITY=users.get(i);
                        new weatherTask().execute();
                        Toast.makeText(MainActivity.this, "Свайп<-", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();

                }
                public void onSwipeLeft() {
                    if(i+1<users.size()) {i++;
                        CITY=users.get(i);
                        new weatherTask().execute();
                        Toast.makeText(MainActivity.this, "Свайп->", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();
                }
                public void onSwipeBottom() {
                    Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                }

            });

            search.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {
                    //setText("https://api.openweathermap.org/data/2.5/weather?q=" + "Moscow"+ "&units=metric&appid=" + API);
                    CITY = etCity.getText().toString();
                    users.add(CITY);
                    new weatherTask().execute();

                }

            });

        }

    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }



    class weatherTask extends AsyncTask<String, Void, String>{

        @Override

        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected String doInBackground(String... args) {

            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY+ "&units=metric&appid=" + API);

            return response;

        }

        @Override

        protected void onPostExecute(String result) {

            try {

                JSONObject jsonObj = new JSONObject(result);
               /* city.setText(json.getString("name").toUpperCase(Locale.US) +
                        ", " +
                        json.getJSONObject("sys").getString("country"));*/
                JSONObject main = jsonObj.getJSONObject("main");

                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                JSONObject sys = jsonObj.getJSONObject("sys");

// CALL VALUE IN API :

                String city_name = jsonObj.getString("name");

                String countryname = sys.getString("country");

                Long updatedAt = jsonObj.getLong("dt");

                String updatedAtText = "Last Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.UK).format(new Date(updatedAt * 1000));

                String temperature = main.getString("temp");

                String cast = weather.getString("description");

                String icon=weather.getString("icon");

                String urlIcon="http://openweathermap.org/img/wn/"+icon+"@2x.png";

                System.out.println(urlIcon);

                Toast.makeText(MainActivity.this, urlIcon, Toast.LENGTH_SHORT).show();
                String humi_dity = main.getString("humidity");

                String temp_min = main.getString("temp_min");

                String temp_max = main.getString("temp_max");

                Long rise = sys.getLong("sunrise");

                String sunrise = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(rise * 1000));

                Long set = sys.getLong("sunset");

                String sunset = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(set * 1000));
// SET ALL VALUES IN TEXTBOX :

                city.setText(city_name);

                country.setText(countryname);

                time.setText(updatedAtText);

                temp.setText(temperature + "°C");

                forecast.setText(cast);

                humidity.setText(humi_dity);

                min_temp.setText(temp_min);

                max_temp.setText(temp_max);

                sunrises.setText(sunrise);

                sunsets.setText(sunset);

                new DownloadImageTask((ImageView) findViewById(R.id.image))
                        .execute(urlIcon);
               /* URL newurl = new URL(urlIcon);
                Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                image.setImageBitmap(mIcon_val);*/
                /*setWeatherIcon(weather.getInt("id"),jsonObj.getJSONObject("sys").getLong("sunrise") * 1000,
                        jsonObj.getJSONObject("sys").getLong("sunset") * 1000);*/

            } catch (Exception e) {

                Toast.makeText(MainActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();

            }

        }

    }



    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = this.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = this.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = this.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
       // image.setText(icon);
    }



    private String showLocation(Location loc) {
        String longitude = "Longitude: " + loc.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v(TAG, latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.ENGLISH);
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
               // System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return cityName;
    }

    public void getList(View view){
        Intent intent = new Intent(this,list.class);
        intent.putStringArrayListExtra("hello",users);
        System.out.println(users.get(0).toString());
        startActivity(intent);
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}