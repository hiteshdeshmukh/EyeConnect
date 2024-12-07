package org.tensorflow.lite.examples.detection.Note;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    TextView title , message;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    static int layoutclick;
    LinearLayout layout;
    DBHandler dbHandler;
    static String finaldate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        layout = findViewById(R.id.layout);
        dbHandler = new DBHandler(this);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
         finaldate = formatter.format(date);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.9f);
                    textToSpeech.speak("tap on the screen and Tell me the title of the note", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we created this method as we have added Onclick in xml code for reminderActivity
                //so when user tap on the screen we increase layout click count because for 1st case
                // we take date from user again for 2nd click we'll take time then task and so on
                layoutclick++;
                textToSpeech.stop();
                startVoiceInput();
                Toast.makeText(getApplicationContext(), String.valueOf(layoutclick), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String res = result.get(0);
                if(layoutclick==1){
                    title.setText(res);
                    if(!title.getText().toString().isEmpty()){
                        textToSpeech.speak("tell me the note that you would like to add",TextToSpeech.QUEUE_FLUSH,null);
                    }
                }
                 if(layoutclick==2) {
                     message.setText(res);
                     if (!message.getText().toString().isEmpty()) {
                         textToSpeech.speak("Title of note is" + title.getText().toString() + "and note is " + message.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                         textToSpeech.speak("say yes to save the note or no to cancel the note", TextToSpeech.QUEUE_ADD, null);
                     }
                 }
                    if(layoutclick==3&&(res.contains("yes")||res.contains("s"))) {
                        dbHandler.addValues(title.getText().toString(), message.getText().toString(), finaldate);
                        textToSpeech.speak("Note successfully saved", TextToSpeech.QUEUE_FLUSH, null);
                        final Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                            }
                        }, 5000);
                    }
                    else if(layoutclick==3 &&res.contains("no")||res.contains("n")){
                            textToSpeech.speak("Cancelling the note",TextToSpeech.QUEUE_FLUSH,null);
                            final Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                }
                            },5000);

                    }
                }




            }
        }
    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
    }
