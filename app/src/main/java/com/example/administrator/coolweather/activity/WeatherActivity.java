package com.example.administrator.coolweather.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.coolweather.R;
import com.example.administrator.coolweather.gson.Forecast;
import com.example.administrator.coolweather.gson.Weather;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivityTest";
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdate;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initUI();

        initWeather();
    }

    private void initWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            Log.d(TAG, "weatherId: " + weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    private void initUI() {
        weatherLayout = findViewById(R.id.sl_weather);
        titleCity = findViewById(R.id.tv_title_city);
        titleUpdate = findViewById(R.id.tv_title_update_time);
        degreeText = findViewById(R.id.tv_now_degree);
        weatherInfoText = findViewById(R.id.tv_now_weather_info);
        forecastLayout = findViewById(R.id.ll_forecast);
        aqiText = findViewById(R.id.tv_aqi);
        pm25Text = findViewById(R.id.tv_pm25);
        comfortText = findViewById(R.id.tv_comfort);
        carWashText = findViewById(R.id.tv_car_wash);
        sportText = findViewById(R.id.tv_sport);

    }

    private void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=c7b2e013d1fd4575a877d6625d7394fe";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d(TAG, "responseText: " + responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            Log.d(TAG, "weather.status:" + weather.status);
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            Log.d(TAG, "responseText: " + responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        if (updateTime != null) {
            titleUpdate.setText(updateTime);
        }
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.tv_date);
            TextView infoText = view.findViewById(R.id.tv_info);
            TextView maxText = view.findViewById(R.id.tv_max);
            TextView minText = view.findViewById(R.id.tv_min);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);
    }
}
