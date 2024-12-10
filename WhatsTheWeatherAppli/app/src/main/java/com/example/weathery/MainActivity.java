package com.example.weathery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main_weather";

    private ArrayList<ForecastModel> forecastModelArrayList;
    private CyclerViewAdapter cyclerViewAdapter;
    private ArrayList<ForecastDaysModel> forecastDaysModelArrayList;
    private CyclerViewDaysAdapter cyclerViewDaysAdapter;
    FusedLocationProviderClient fusedLocationProviderClient; //For getting current location

    private TextView weatherStatus;
    private TextView currentTempValue;
    private TextView currentTempValueF;
    private TextView currentTempValueCMin;
    private TextView currentTempValueCMax;
    private TextView currentTempValueFMin;
    private TextView currentTempValueFMax;
    private TextView currentWindValue;
    private TextView currentWindDirection;
    private TextView currentHumidityValue;
    private TextView currentPressureValue;
    private TextView currentPollutionValue;
    private TextView currentSo2level;
    private TextView currentNo2level;
    private ImageView currentWeatherIcon;
    private TextView sublocation;
    private Button add_button;

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewDays;

    String iconName = ""; //For forecastweatherIcon method : updateForecastWeather
    String iconDaysName = "";
    int forecastTemp;
    int forecastDaysTemp;
    long forecastTime;
    long forecastHoursDay;
    String forecastTimeStr = "";
    String forecastDaysTimeStr = "";
    String forecastTempStr = "";
    String forecastDaysTempStr = "";
    String forecastHoursDayStr = "";

    String myCity = ""; //Paris
    double longitude; //2.333333
    double lattitude; //48.866667

    public ArrayList<String> savedLocations = new ArrayList<>();

    public String sublocationsearch = "";
    public String sublocationPref = "";
    public String location_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate fired.");

        final Intent intent = getIntent();
        String mCity = intent.getStringExtra("mCity");
        if (mCity != null) {
            getSearchResult(mCity);
        }

        SharedPreferences sh = getSharedPreferences("location", MODE_PRIVATE);
        myCity = sh.getString("locationName", "");
        longitude = sh.getFloat("locationLong", 0);
        lattitude = sh.getFloat("locationLat", 0);

        updateLocationWeather(longitude, lattitude);
        updatePollution(longitude, lattitude);

        updateForecastWeather(longitude, lattitude);

        //5 days forecast
        updateForecastFiveDaysWeather(longitude, lattitude);

        sublocationPref = myCity;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); // locationrequest

        recyclerView = findViewById(R.id.rcv_today_weather_list);
        recyclerViewDays = findViewById(R.id.rcv_weather_days_list);
        RelativeLayout relativeLayout = findViewById(R.id.search_view_btn);
        RelativeLayout locationLayout = findViewById(R.id.location_btn); // For getting current location weather
        add_button = findViewById(R.id.add_btn);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("Saved_locations", savedLocations);
                startActivity(intent);
            }
        });

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocationSettingsRequest();
            }
        });


        add_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                location_text = sublocation.getText().toString();
                if (savedLocations.contains(location_text))
                    Toast.makeText(getApplicationContext(), "cityAlreadySaved", Toast.LENGTH_SHORT).show();
                else {
                    savedLocations.add(location_text);
                    Toast.makeText(getApplicationContext(), location_text + " savedWithSuccess", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forecastModelArrayList = new ArrayList<>();
        forecastDaysModelArrayList = new ArrayList<>();

        //Initializing adapter for RecyclerView and Pass Arraylist to ForecastModel and ForecastDaysModel
        cyclerViewAdapter = new CyclerViewAdapter(this, forecastModelArrayList);
        cyclerViewDaysAdapter = new CyclerViewDaysAdapter(this, forecastDaysModelArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cyclerViewAdapter);

        recyclerViewDays.setLayoutManager(layoutManager2);
        recyclerViewDays.setAdapter(cyclerViewDaysAdapter);

        weatherStatus = findViewById(R.id.weather_status);
        currentTempValue = findViewById(R.id.current_temp_value);
        currentTempValueF = findViewById(R.id.current_temp_value_f);
        currentTempValueCMin = findViewById(R.id.current_temp_value_c_min);
        currentTempValueCMax = findViewById(R.id.current_temp_value_c_max);
        currentTempValueFMin = findViewById(R.id.current_temp_value_f_min);
        currentTempValueFMax = findViewById(R.id.current_temp_value_f_max);
        currentHumidityValue = findViewById(R.id.humidity_value);
        currentWindValue = findViewById(R.id.wind_value);
        currentWindDirection = findViewById(R.id.wind_direction);
        currentPressureValue = findViewById(R.id.pressure_value);
        currentPollutionValue = findViewById(R.id.pollution_value);
        currentNo2level = findViewById(R.id.no2_value);
        currentSo2level = findViewById(R.id.so2_value);
        sublocation = findViewById(R.id.sublocation_txt);
        currentWeatherIcon = findViewById(R.id.current_weather_icon);

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }


    private void getSearchResult(String requiredCity) {

        if (requiredCity != null) {
            Toast.makeText(this, requiredCity, Toast.LENGTH_SHORT).show();
            if (Geocoder.isPresent()) {
                try {
                    //String requiredCity = mCity;
                    Geocoder geocoder = new Geocoder(this);
                    List<Address> requiredAddress = geocoder.getFromLocationName(requiredCity, 5);

                    List<Double> ll = new ArrayList<>(requiredAddress.size());
                    for (Address a : requiredAddress) {
                        if (a.hasLatitude() && a.hasLongitude()) {
                            ll.add(a.getLatitude());
                            ll.add(a.getLongitude());
                        }
                    }

                    double searchLat = ll.get(0);
                    double searchLon = ll.get(1);

                    //Typecasting
                    float num1 = (float) searchLat;
                    float num2 = (float) searchLon;

                    SharedPreferences sharedPreferences = getSharedPreferences("location", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("locationName", requiredCity);
                    editor.putFloat("locationLong", num2);
                    editor.putFloat("locationLat", num1);
                    editor.apply();

                    sublocationsearch = requiredCity;
                    updateLocationWeather(searchLon, searchLat);
                    updatePollution(searchLon, searchLat);

                    updateForecastWeather(searchLon, searchLat);

                    //5 days forecast
                    updateForecastFiveDaysWeather(searchLon, searchLat);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void displayLocationSettingsRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Toast.makeText(getApplicationContext(), "addOnSuccessListener", Toast.LENGTH_SHORT).show();
                getLocation();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "addOnFailureListener", Toast.LENGTH_SHORT).show();
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                2001);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //getLocation();

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled;//To check if gps is turned on or not
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);//To check if location settings are enabled or not

            Log.d(TAG, "Before request...");
            //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            Log.d(TAG, "After request...");
            Log.d(TAG, "GPS Enabled" + gps_enabled);

            if (!gps_enabled) {
                displayLocationSettingsRequest();
            } else {

                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        Log.d(TAG, "Location : " + location);

                        if (location != null) {
                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this,
                                        Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1);

                                //Set longitude and lattitude
                                lattitude = addresses.get(0).getLatitude();
                                Log.d(TAG, "Lattitude : " + lattitude);
                                longitude = addresses.get(0).getLongitude();
                                Log.d(TAG, "Longitude : " + longitude);
                                myCity = addresses.get(0).getLocality();
                                String wantedCity = addresses.get(0).getLocality();
                                Log.d(TAG, "City : " + myCity);

                                //To clear the arraylist to prevent data duplication
                                if (forecastModelArrayList != null && forecastModelArrayList.size() > 0) {
                                    forecastModelArrayList.clear();
                                }
                                if (forecastDaysModelArrayList != null && forecastDaysModelArrayList.size() > 0) {
                                    forecastDaysModelArrayList.clear();
                                }

                                //Call methods
                                sublocationsearch = wantedCity;
                                updateLocationWeather(longitude, lattitude);//updateWeather(myCity);
                                updatePollution(longitude, lattitude);

                                updateForecastWeather(longitude, lattitude);

                                //5 days forecast
                                updateForecastFiveDaysWeather(longitude, lattitude);


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //getLastKnownLocation();
                            Log.d(TAG, "Nothing happened.");
                            Toast.makeText(MainActivity.this, "Try search...Can't get location right now.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }



    public void updateLocationWeather(double longitude, double lattitude) {

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lattitude + "&lon=" + longitude + "&appid=45eaf3e68ca19150c02f5e2d0df61081&units=metric";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Log.d(TAG,"Before response value");
                Log.d(TAG, "Weather : " + response);

                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    JSONObject wind_object = response.getJSONObject("wind");

                    int temp = main_object.getInt("temp");
                    int temp_min = main_object.getInt("temp_min");
                    int temp_max = main_object.getInt("temp_max");
                    int fahreneitTemp = (int) (Math.round(temp * 1.8 + 32));
                    int fahreneitTempMin = (int) (Math.round(temp_min * 1.8 + 32));
                    int fahreneitTempMax = (int) (Math.round(temp_max * 1.8 + 32));
                    int humidity = main_object.getInt("humidity");
                    int pressure = main_object.getInt("pressure");
                    String city_name = response.getString("name");
                    String status = object.getString("description");
                    Double speed = wind_object.getDouble("speed");
                    int degree = wind_object.getInt("deg");
                    long dt = response.getLong("dt");
                    String date = "";

                    Calendar calendar = Calendar.getInstance(Locale.FRENCH);
                    calendar.setTimeInMillis(dt * 1000);
                    date = DateFormat.format("hh a", calendar).toString();

                    //Log.d(TAG, "DATE : " + date);
                    String hours = date.substring(0, 2);
                    String dOrN = date.substring(3, 5);
                    //Log.d(TAG, "HOURS : " + hours);
                    //Log.d(TAG, "dOrN: " + dOrN);
                    boolean isNight;
                    if ((Integer.parseInt(hours) >= 8 && dOrN.contains("PM")) || (Integer.parseInt(hours) < 6 && dOrN.contains("AM")))
                        isNight = true;
                    else
                        isNight = false;


                    //int temp_int = Integer.parseInt(temp);

                    currentTempValue.setText(String.valueOf(temp));
                    currentTempValueF.setText(String.valueOf(fahreneitTemp));
                    currentTempValueCMin.setText(String.valueOf(temp_min));
                    currentTempValueCMax.setText(String.valueOf(temp_max));
                    currentTempValueFMin.setText(String.valueOf(fahreneitTempMin));
                    currentTempValueFMax.setText(String.valueOf(fahreneitTempMax));
                    currentHumidityValue.setText(String.valueOf(humidity));
                    currentPressureValue.setText(String.valueOf(pressure));
                    currentWindValue.setText(String.valueOf(speed));
                    currentWindDirection.setText(WindDirection(degree));
                    String upperCaseStatus = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
                    weatherStatus.setText(upperCaseStatus);
                    String upperCaseCity = city_name.substring(0, 1).toUpperCase() + city_name.substring(1).toLowerCase();
                    sublocation.setText(upperCaseCity);

                    //https://openweathermap.org/weather-conditions

                    String[] clear = {"clear sky"};
                    String[] clouds = {"few clouds", "scattered clouds", "broken clouds", "overcast clouds"};
                    String[] atmo = {"mist", "smoke", "haze", "sand/dust whirls", "fog", "sand", "dust", "volcanic ash", "squalls", "tornado"};
                    String[] snow = {"light snow", "snow", "heavy snow", "sleet", "light shower sleet", "shower sleet", "light rain and snow", "rain and snow", "light shower snow", "shower snow", "heavy shower snow"};
                    String[] rain = {"shower rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "heavy intensity shower rain", "ragged shower rain"};
                    String[] drizzle = {"drizzle", "light intensity drizzle", "heavy intensity drizzle", "light intensity drizzle rain", "drizzle rain", "heavy intensity drizzle rain", "shower rain and drizzle", "heavy shower rain and drizzle", "shower drizzle"};
                    String[] thunder = {"thunderstorm", "thunderstorm with light rain", "thunderstorm with rain", "thunderstorm with heavy rain", "light thunderstorm", "heavy thunderstorm", "ragged thunderstorm", "thunderstorm with light drizzle", "thunderstorm with drizzle", "thunderstorm with heavy drizzle"};

                    //To check the condition

                    List one = Arrays.asList(clouds);
                    List two = Arrays.asList(atmo);
                    List three = Arrays.asList(snow);
                    List four = Arrays.asList(rain);
                    List five = Arrays.asList(drizzle);
                    List six = Arrays.asList(thunder);
                    List seven = Arrays.asList(clear);

                    String iconUrl = "";

                    if (one.contains(status)) {
                        if(isNight)
                            iconUrl = "https://openweathermap.org/img/wn/02n@4x.png";
                        else
                            iconUrl = "https://openweathermap.org/img/wn/02d@4x.png";
                        Picasso.get().load(iconUrl).into(currentWeatherIcon);
                    }
                    if (two.contains(status)) {
                        if(isNight)
                            iconUrl = "https://openweathermap.org/img/wn/50n@4x.png";
                        else
                            iconUrl = "https://openweathermap.org/img/wn/50d@4x.png";
                        Picasso.get().load(iconUrl).into(currentWeatherIcon);
                    }
                    if (three.contains(status)) {
                        if(isNight)
                            iconUrl = "https://openweathermap.org/img/wn/13n@4x.png";
                        else
                            iconUrl = "https://openweathermap.org/img/wn/13d@4x.png";
                        Picasso.get().load(iconUrl).into(currentWeatherIcon);
                    }
                    if (four.contains(status)) {
                        if(isNight)
                            iconUrl = "https://openweathermap.org/img/wn/10n@4x.png";
                        else
                            iconUrl = "https://openweathermap.org/img/wn/10d@4x.png";
                        Picasso.get().load(iconUrl).into(currentWeatherIcon);
                    }
                    if (five.contains(status)) {
                        if(isNight)
                            iconUrl = "https://openweathermap.org/img/wn/09n@4x.png";
                        else
                            iconUrl = "https://openweathermap.org/img/wn/09d@4x.png";
                        Picasso.get().load(iconUrl).into(currentWeatherIcon);
                    }
                    if (six.contains(status)) {
                        if(isNight)
                            iconUrl = "https://openweathermap.org/img/wn/11n@4x.png";
                        else
                            iconUrl = "https://openweathermap.org/img/wn/11d@4x.png";
                        Picasso.get().load(iconUrl).into(currentWeatherIcon);
                    }
                    if (seven.contains(status)) {
                        if(isNight)
                            iconUrl = "https://openweathermap.org/img/wn/01n@4x.png";
                        else
                            iconUrl = "https://openweathermap.org/img/wn/01d@4x.png";
                        Picasso.get().load(iconUrl).into(currentWeatherIcon);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    public String WindDirection(int degree) {
        double result = (double) degree/45;
        String windDirection = "Empty";
        if (result >= 0 && result <= 0.5)
            windDirection = "North";
        if (result > 0.5 && result <= 1.5)
            windDirection = "North-East";
        if (result > 1.5 && result <= 2.5)
            windDirection = "East";
        if (result > 2.5 && result <= 3.5)
            windDirection = "South-East";
        if (result > 3.5 && result <= 4.5)
            windDirection = "South";
        if (result > 4.5 && result <= 5.5)
            windDirection = "South-West";
        if (result > 5.5 && result <= 6.5)
            windDirection = "West";
        if (result > 6.5 && result <= 7.5)
            windDirection = "North-West";
        if (result > 7.5 && result <= 8)
            windDirection = "North";
        return windDirection;
    }

    public void updatePollution(double longitude, double lattitude) {

        //String key = "6091d7651a1cc6ab4b6e742d900dfb3d";
        String url = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" + lattitude + "&lon=" + longitude + "&appid=45eaf3e68ca19150c02f5e2d0df61081";

        //Log.d(TAG,"Before JSON request");

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Log.d(TAG,"Pollution:");
                Log.d(TAG, "Pollution : " + response);

                try {
                    //JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("list");
                    JSONObject object = array.getJSONObject(0);
                    JSONObject airQuality_object = object.getJSONObject("main");
                    JSONObject components = object.getJSONObject("components");


                    int airQuality = airQuality_object.getInt("aqi");
                    double no2level = components.getDouble("no2");
                    double so2level = components.getDouble("so2");

                    currentNo2level.setText("NO2 : " + String.valueOf(no2level));
                    currentSo2level.setText("SO2 : " + String.valueOf(so2level));

                    //int temp_int = Integer.parseInt(temp);

                    switch (airQuality) {
                        case 1:
                            String value = "Good";
                            currentPollutionValue.setText(value);
                            //Log.d(TAG,"Case 1");
                            break;
                        case 2:
                            value = "Fair";
                            currentPollutionValue.setText(value);
                            //Log.d(TAG,"Case 2");
                            break;
                        case 3:
                            value = "Moderate";
                            currentPollutionValue.setText(value);
                            //Log.d(TAG,"Case 3");
                            break;
                        case 4:
                            value = "Poor";
                            currentPollutionValue.setText(value);
                            //Log.d(TAG,"Case 4");
                            break;
                        case 5:
                            value = "Very Poor";
                            currentPollutionValue.setText(value);
                            //Log.d(TAG,"Case 5");
                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }


    public void updateForecastWeather(double longitude, double lattitude) {
        //String key = "6091d7651a1cc6ab4b6e742d900dfb3d";
        String url = "https://api.openweathermap.org/data/3.0/onecall?lat=" + lattitude + "&lon=" + longitude + "&exclude=minutely,daily&appid=45eaf3e68ca19150c02f5e2d0df61081&units=metric";
        //Log.d(TAG,"Before JSON request");

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Log.d(TAG,"Forecast : ");
                Log.d(TAG, "Forecast : " + response);

                try {
                    for (int i = 0; i <= 23; i++) {
                        //JSONObject main_object = response.getJSONObject("main");
                        JSONArray array = response.getJSONArray("hourly");
                        JSONObject object = array.getJSONObject(i);
                        forecastTime = object.getLong("dt");
                        forecastTemp = object.getInt("temp");

                        JSONArray iconArray = object.getJSONArray("weather");
                        JSONObject iconArrayJSONObject = iconArray.getJSONObject(0);
                        iconName = iconArrayJSONObject.getString("icon");

                        //Typecasting
                        forecastTempStr = String.valueOf(forecastTemp);

                        Calendar calendar = Calendar.getInstance(Locale.FRENCH);
                        calendar.setTimeInMillis(forecastTime * 1000);
                        forecastTimeStr = DateFormat.format("hh:mm a", calendar).toString();

                        forecastModelArrayList.add(new ForecastModel(forecastTempStr, iconName, forecastTimeStr));//Add data to arraylist

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }


    public void updateForecastFiveDaysWeather(double longitude, double lattitude) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lattitude + "&lon=" + longitude + "&appid=45eaf3e68ca19150c02f5e2d0df61081&units=metric";
        //Log.d(TAG,"Before JSON request");

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "Forecast 5 days : " + response);

                try {
                    JSONArray list = response.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject object = list.getJSONObject(i);
                        JSONObject main_object = object.getJSONObject("main");
                        forecastDaysTemp = main_object.getInt("temp");

                        forecastHoursDay = object.getLong("dt");
                        Calendar calendar = Calendar.getInstance(Locale.FRENCH);
                        calendar.setTimeInMillis(forecastHoursDay * 1000);
                        forecastHoursDayStr = DateFormat.format("hh:mm a", calendar).toString();

                        forecastDaysTimeStr = object.getString("dt_txt").substring(5, 10);
                        if(forecastDaysTimeStr.substring(0,3).contains("01-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("01-", "January ");
                        if(forecastDaysTimeStr.substring(0,3).contains("02-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("02-", "February ");
                        if(forecastDaysTimeStr.substring(0,3).contains("03-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("03-", "March ");
                        if(forecastDaysTimeStr.substring(0,3).contains("04-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("04-", "April ");
                        if(forecastDaysTimeStr.substring(0,3).contains("05-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("05-", "May ");
                        if(forecastDaysTimeStr.substring(0,3).contains("06-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("06-", "June ");
                        if(forecastDaysTimeStr.substring(0,3).contains("07-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("07-", "July ");
                        if(forecastDaysTimeStr.substring(0,3).contains("08-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("08-", "August ");
                        if(forecastDaysTimeStr.substring(0,3).contains("09-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("09-", "September ");
                        if(forecastDaysTimeStr.substring(0,3).contains("10-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("10-", "October ");
                        if(forecastDaysTimeStr.substring(0,3).contains("11-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("11-", "November ");
                        if(forecastDaysTimeStr.substring(0,3).contains("12-"))
                            forecastDaysTimeStr = forecastDaysTimeStr.replace("12-", "December ");


                        JSONArray iconArray = object.getJSONArray("weather");
                        JSONObject iconArrayJSONObject = iconArray.getJSONObject(0);
                        iconDaysName = iconArrayJSONObject.getString("icon");

                        //Typecasting
                        forecastDaysTempStr = String.valueOf(forecastDaysTemp);

                        forecastDaysModelArrayList.add(new ForecastDaysModel(forecastDaysTempStr, iconDaysName, forecastDaysTimeStr, forecastHoursDayStr));//Add data to arraylist
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }
}


