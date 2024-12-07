package org.tensorflow.lite.examples.detection.Note;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Locale;

public class Notes extends AppCompatActivity {
     RecyclerView mRecyclerview;
    ArrayList<Model> dataholder = new ArrayList<>();
    static String query1;
     TextView textView,txt;
    public static ArrayList<String> time=new ArrayList<>();
    float x1, x2;
    myAdapter adapter;
    private TextToSpeech textToSpeech;
    DBHandler dbHandler;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notedisplay);
//        title = findViewById(R.id.title);
//        message = findViewById(R.id.message);
        dbHandler = new DBHandler(this);
        textView = findViewById(R.id.textView);
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        txt = findViewById(R.id.textView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.8f);
                    textToSpeech.speak("Welcome to Notes. Swipe right to add the note and swipe left to read the notes. Press long to return in main menu.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        final GestureDetector gd = new GestureDetector(Notes.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //when user press long on the screen it return to main menu
                super.onLongPress(e);
                Intent i = new Intent(Notes.this,Home.class);
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

         textView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
             public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        if (x1>x2){
                            textToSpeech.stop();
                            finish();
                            Intent i = new Intent(Notes.this,NoteActivity.class);
                             startActivity(i);
                            return false;

                        }

                        if (x1 < x2) {
                            if(time.toString().contains("[]")){
                                txt.setText("There are no notes. swipe right, to add the note");
                                textToSpeech.speak("There are no notes. swipe right, to add the note", TextToSpeech.QUEUE_FLUSH,null);
                            }
                            else {
                                textToSpeech.speak(time.toString(),TextToSpeech.QUEUE_FLUSH,null);
                                textToSpeech.speak("swipe left to read once again", TextToSpeech.QUEUE_ADD,null);

                            }
                            return false;

                        }


                        break;
                }


                return gd.onTouchEvent(event);
            }
        });

        Cursor cursor = dbHandler.readallnotes();                  //Cursor To Load data From the database
        while (cursor.moveToNext()) {
            Model model = new Model(cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(0));
            dataholder.add(model);
        }


        if (cursor.moveToFirst()) {
            do {
                     time.add("You have a note, at"+cursor.getString(3)+",and title is, "+cursor.getString(1)+ ",and note is "+ cursor.getString(2));
            }
            while (cursor.moveToNext());
            // moving our cursor to next.
        }

        adapter = new myAdapter(dataholder);
        if(time.size()>0){
            txt.setText("You have a previous notes. swipe left to read the notes or swipe right to create note.");
            query1 = "You have a previous notes. swipe left to read the note or swipe right to create note.";
            Toast.makeText(getApplicationContext(), time.toString(), Toast.LENGTH_LONG).show();
        }
        else {
            query1 = "you have no notes. swipe right to create a note.";
            txt.setText(query1);
        }

        mRecyclerview.setAdapter(adapter);



    }
    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }





}



