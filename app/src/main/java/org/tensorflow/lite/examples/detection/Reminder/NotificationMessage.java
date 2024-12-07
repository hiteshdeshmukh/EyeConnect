package org.tensorflow.lite.examples.detection.Reminder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.util.Locale;

//this class creates the Reminder Notification Message

public class NotificationMessage extends AppCompatActivity implements TextToSpeech.OnUtteranceCompletedListener {
    TextView textView;
    private TextToSpeech textToSpeech;
    DBHandler dbHandler;

    @SuppressLint({"InvalidWakeLockTag", "Range"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);
        textView = findViewById(R.id.tv_message);
        textView.setText(AlarmService.message+ ". at time "+AlarmService.time);
        dbHandler = new DBHandler(this);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.speak("you have a reminder, and reminder is. " + textView.getText().toString() + "  .returning to main menu", TextToSpeech.QUEUE_FLUSH, null, "complete");
                }

            }
        });
    }

    @Override
    public void onUtteranceCompleted(String s) {
        finish();
        Intent i = new Intent(NotificationMessage.this, Home.class);
        startActivity(i);


    }
}