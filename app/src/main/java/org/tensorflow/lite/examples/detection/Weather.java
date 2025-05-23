package org.tensorflow.lite.examples.detection;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.Location.LocationActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Weather extends AppCompatActivity {

    EditText cityInput;
    final int VOICE_CODE = 100;
    TextToSpeech textToSpeech;
    Button cityBtn;
    ImageView voice_btn;
    TextView cityTextView, timeTextView, dateTextView, weatherStatusText, temperatureText;
    ImageView weatherStatusImg;
    String currentTime, dateOutput;
    String cityEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        cityTextView = findViewById(R.id.city_text_view);


        cityTextView.setText("");
        timeTextView = findViewById(R.id.time_text_view);
        timeTextView.setText("");
        dateTextView = findViewById(R.id.date_text_view);
        dateTextView.setText("");
        weatherStatusImg = findViewById(R.id.weather_img);
        temperatureText = findViewById(R.id.temperature_text);
        temperatureText.setText("");
        weatherStatusText = findViewById(R.id.weather_status_text);
        weatherStatusText.setText("");
        cityInput = findViewById(R.id.city_txt_input);
        cityBtn = findViewById(R.id.city_btn);
        cityInput = (EditText) findViewById(R.id.city_txt_input);
        voice_btn = (ImageView) findViewById(R.id.weather_img);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    Toast.makeText(Weather.this, "tap on the screen and say the name of city", Toast.LENGTH_SHORT).show();
                    textToSpeech.speak("say the name of city.", TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.setSpeechRate(1f);
                }
            }
        });


        voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voice_to_text();
            }
        });
    }

    private void voice_to_text() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something!!");
        try {
            startActivityForResult(intent, VOICE_CODE);
        } catch (ActivityNotFoundException e) {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VOICE_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    cityInput.setText(result.get(0));
                    if (cityInput.getText().toString().contains("read")) {
                        Intent intent = new Intent(getApplicationContext(), OCRReader.class);
                        startActivity(intent);
                    } else if (cityInput.getText().toString().contains("main menu")) {
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                    } else if (cityInput.getText().toString().contains("location")) {
                        Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                        startActivity(intent);
                        cityInput.setText(null);
                    } else if (cityInput.getText().toString().contains("battery")) {
                        Intent intent = new Intent(getApplicationContext(), Battery.class);
                        startActivity(intent);
                        cityInput.setText(null);
                    } else if (cityInput.getText().toString().contains("object detection")) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("org.tensorflow.lite.examples.detection", "ObjectDetection"));
                        startActivity(intent);
                    } else if (cityInput.getText().toString().contains("back")) {
                        cityInput.setText(null);
                        startActivity(new Intent(this, Home.class));
                    } else if (cityInput.getText().toString().contains("time and date")) {
                        onDestroy();
                        Intent intent = new Intent(getApplicationContext(), DateAndTime.class);
                        startActivity(intent);
                        cityInput.setText(null);
                    } else if (cityInput.getText().toString().contains("calculator")) {
                        Intent intent = new Intent(getApplicationContext(), Calculator.class);
                        startActivity(intent);
                    } else if (cityInput.getText().toString().contains("back")) {
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                    } else if (cityInput.getText().toString().contains("exit")) {
                        onPause();
                        finishAffinity();
                    } else {
                        api_url(cityInput.getText().toString());

                    }
                }
            }
        }

        // user will have to say city name
        //To ensure editText is not empty
        if (cityInput.getText().toString().contains(""))
            Toast.makeText(Weather.this, "Enter city name", Toast.LENGTH_SHORT).show();

        else {
            cityEntered = cityInput.getText().toString();
            api_url(cityEntered); //passing user inputy
        }
    }

    public void api_url(String citySearch) {
        // creating url as per user input
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + citySearch + "&appid=b761b4cfe64507fdd7579ab7daf29996&units=metric";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONObject("main");
                    JSONArray weatherArray = response.getJSONArray("weather");
                    JSONObject description = weatherArray.getJSONObject(0);
                    JSONObject icon = weatherArray.getJSONObject(0);
                    String iconId = icon.getString("icon");
                    String temp = (Math.round(mainObject.getDouble("temp"))) + "°C";
                    String desc = description.getString("main");
                    updateUI(temp, desc); // method for setting values to the textViews
                    SetIcon(iconId); // setting icon as per response from api
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    textToSpeech.speak("Not getting weather details. Tap on the screen and say the name of city", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    public void updateUI(String temperature, String description) {
        cityEntered = cityInput.getText().toString().replaceAll("location", "");
        cityEntered = cityInput.getText().toString().replaceAll("calculator", "");
        cityEntered = cityInput.getText().toString().replaceAll("read", "");
        // showing output
        temperatureText.setText(temperature);

        temperatureText.getText().toString();
        cityInput.setText(cityEntered);
        cityInput.getText().toString();
        if (temperature == null && temperature.isEmpty()) {
            textToSpeech.speak("Not getting weather details. Tap on the screen and say the name of city", TextToSpeech.QUEUE_FLUSH, null);
        }
        textToSpeech.speak("Temperature in the city" + cityEntered.replaceAll("read", "") + "is" + temperature, TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak("Tap on the screen and say the name of city or say what you want", TextToSpeech.QUEUE_ADD, null);
        weatherStatusText.setText(description);
        timeTextView.setText(currentTime);
        dateTextView.setText(dateOutput);
        cityTextView.setText(cityEntered);
    }

    public void SetIcon(String id) {
        switch (id) {
            case "01d":
                weatherStatusImg.setImageResource(R.drawable.clear_skyd);
                break;
            case "01n":
                weatherStatusImg.setImageResource(R.drawable.clear_skyn);
                break;
            case "02d":
                weatherStatusImg.setImageResource(R.drawable.few_cloudsd);
                break;
            case "02n":
                weatherStatusImg.setImageResource(R.drawable.few_cloudn);
                break;
            case "03d":
                weatherStatusImg.setImageResource(R.drawable.few_cloudsd);
                break;
            case "03n":
                weatherStatusImg.setImageResource(R.drawable.few_cloudn);
                break;
            case "04d":
                weatherStatusImg.setImageResource(R.drawable.few_cloudsd);
                break;
            case "04n":
                weatherStatusImg.setImageResource(R.drawable.few_cloudn);
                break;
            case "09d":
                weatherStatusImg.setImageResource(R.drawable.rain);
                break;
            case "09n":
                weatherStatusImg.setImageResource(R.drawable.rain);
                break;
            case "10d":
                weatherStatusImg.setImageResource(R.drawable.rain);
                break;
            case "10n":
                weatherStatusImg.setImageResource(R.drawable.rain);
                break;
            case "11d":
                weatherStatusImg.setImageResource(R.drawable.storm);
                break;
            case "11n":
                weatherStatusImg.setImageResource(R.drawable.storm);
                break;
            case "13d":
                weatherStatusImg.setImageResource(R.drawable.snow);
                break;
            case "13n":
                weatherStatusImg.setImageResource(R.drawable.snow);
                break;
            case "50d":

                weatherStatusImg.setImageResource(R.drawable.mist);
                break;
            case "50n":
                weatherStatusImg.setImageResource(R.drawable.mistn);
                break;

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }
}