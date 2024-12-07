package org.tensorflow.lite.examples.detection.Reminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Reminder extends AppCompatActivity {

    private static final int NOTIFICATION_REQUEST_CODE = 1;
    RecyclerView mRecyclerview;
    TextView textView;
    ArrayList<Model> dataholder = new ArrayList<>();                                               //Array list to add reminders and display in recyclerview
    static float x1, x2;
    public static ArrayList<String> time = new ArrayList<>();
    TextView txt;
    myAdapter adapter;
    private DBHandler dbHandler;
    static String query1;
    private static TextToSpeech textToSpeech;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        dbHandler = new DBHandler(this);
        textView = findViewById(R.id.textView);
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        txt = findViewById(R.id.textView);
        manageNotificationPermission();
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final GestureDetector gd = new GestureDetector(Reminder.this, new GestureDetector.SimpleOnGestureListener() {


            //here is the method for double tap


            @Override
            public boolean onDoubleTap(MotionEvent e) {

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //when user press long on the screen it return to main menu
                super.onLongPress(e);
                Intent i = new Intent(Reminder.this, Home.class);
                startActivity(i);
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }


        });

//here yourView is the View on which you want to set the double tap action

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        if (x1 > x2) {
                            textToSpeech.stop();
                            finish();
                            Intent i = new Intent(Reminder.this, ReminderActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            return false;

                        }

                        if (x1 < x2) {
                            if (time.toString().contains("[]")) {
                                txt.setText("There are no reminders for today. swipe right, to add the reminder");
                                textToSpeech.speak("There are no reminders for today. swipe right, to add the reminder", TextToSpeech.QUEUE_FLUSH, null);
                            } else {
                                textToSpeech.speak(time.toString(), TextToSpeech.QUEUE_FLUSH, null);
                                textToSpeech.speak("swipe left to read once again", TextToSpeech.QUEUE_ADD, null);

                            }
                            return false;

                        }


                        break;
                }


                return gd.onTouchEvent(event);
            }
        });

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.speak(query1, TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.speak("or press long on the screen to return in main menu", TextToSpeech.QUEUE_ADD, null);
                }
            }


        });


        Boolean ISDELETED = dbHandler.deleteAll();

        if (ISDELETED) {
            Toast.makeText(getApplicationContext(), "All reminders deleted ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        Cursor cursor = dbHandler.readallreminders();                  //Cursor To Load data From the database
        while (cursor.moveToNext()) {
            Model model = new Model(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            dataholder.add(model);
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(2).equals(formattedDate)) {
                    time.add("You have a reminder at," + cursor.getString(3) + ",and reminder is " + cursor.getString(1) + ".");

                }
            }
            while (cursor.moveToNext());
            // moving our cursor to next.
        }

        adapter = new myAdapter(dataholder);
        if (time.size() > 0) {
            txt.setText("You have a new reminder for today. swipe left to read the reminder or swipe right to create reminder.");
            query1 = "You have a new reminder for today. swipe left to read the reminder or swipe right to create reminder.";
            Toast.makeText(getApplicationContext(), time.toString(), Toast.LENGTH_LONG).show();
        } else {
            query1 = "you have no reminder for today. swipe right to create a reminder.";
            txt.setText(query1);
        }

        mRecyclerview.setAdapter(adapter);

        //Binds the adapter with recyclerview


    }

    private void manageNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        (Activity) getApplicationContext(),
                        new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                        NOTIFICATION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        time.clear();
        //Makes the user to exit from the app
        super.onBackPressed();
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }


}
