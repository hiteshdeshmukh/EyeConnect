package org.tensorflow.lite.examples.detection.Moneytransfer;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class phonetransfer extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView status;
    private TextView To,Subject,To2;
    private int numberOfClicks;
    static String to2,phone;
    float x1,x2;
     private boolean IsInitialVoiceFinshed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_transfer);
        IsInitialVoiceFinshed = false ;
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    tts.speak("Welcome to Phone transfer. tap on the screen , Tell me the Name of person to whom you want to transfer money?",TextToSpeech.QUEUE_FLUSH,null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IsInitialVoiceFinshed=true;


                         }
                    }, 8500);
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        status = (TextView)findViewById(R.id.status);
        To = (TextView) findViewById(R.id.to);
        Subject  = findViewById(R.id.subject);
        To2 = findViewById(R.id.to2);
        numberOfClicks = 0;
    }



    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    public void layoutClicked(View view)
    {
        if(IsInitialVoiceFinshed) {
            numberOfClicks++;
            listen();
        }
    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(phonetransfer.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100&& IsInitialVoiceFinshed){
            IsInitialVoiceFinshed = false;
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(result.get(0).contains("cancel"))
                {
                    tts.speak("Transaction Cancelled!",TextToSpeech.QUEUE_FLUSH,null);

                }
                else {


                    switch (numberOfClicks) {
                        case 1:
                                to2 = result.get(0);
                                To2.setText(to2);
                                tts.speak("Tap on the screen & say the phone number",TextToSpeech.QUEUE_FLUSH,null);
                            if(To2.getText().toString().isEmpty()) {
                                numberOfClicks--;
                            }
                            break;


                        case 2:
                                phone = result.get(0).replaceAll("[^\\d.]", "");
                                To.setText(phone);
                            if(To.getText().toString().isEmpty()) {
                                numberOfClicks--;
                            }
                            tts.speak("tap on the screen & say, how much money you want to transfer",TextToSpeech.QUEUE_FLUSH,null);

                         break;

                        case 3:
                                String amount;
                                amount = result.get(0).replaceAll("[^\\d.]", "");
                                Subject.setText(amount);
                            if(Subject.getText().toString().isEmpty()) {
                                numberOfClicks--;
                            }
                                status.setText("confirm");
                                tts.speak("Please Confirm the details, Name of account holder is: " + To2.getText().toString() + ",Phone number is "+ Arrays.toString(To.getText().toString().split("(?!^)"))+". And Money that you want to transfer is ,: " + Subject.getText().toString()+"rupees" ,TextToSpeech.QUEUE_FLUSH,null);
                                tts.speak(",swipe left to listen again, or say Yes to confirm or no to cancel the transaction",TextToSpeech.QUEUE_ADD,null);
                                break;

                        default:

                            if(result.get(0).equals("yes")) {
                                if (To.getText().toString().equals("")) {
                                    if (Subject.getText().toString().equals("")) {
                                        tts.speak("Details may be incorrect or incomplete, canceling the transaction",TextToSpeech.QUEUE_FLUSH,null);
                                        final Handler h = new Handler(Looper.getMainLooper());
                                        h.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent i = new Intent(phonetransfer.this, Home.class);
                                                startActivity(i);

                                            }
                                        },8000);
                                    }
                                } else {
                                    status.setText("transferring money ");
                                    tts.speak("transferring money please wait",TextToSpeech.QUEUE_FLUSH,null);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            status.setText("Amount transferred successfully.");
                                            tts.speak("Amount transferred successfully.",TextToSpeech.QUEUE_FLUSH,null);
                                        }
                                    }, 6000);
                                    final Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                             finish();
                                            Intent i = new Intent(phonetransfer.this, Home.class);
                                            startActivity(i);
                                        }
                                    }, 9000);


                                }
                            }

                            else if(result.get(0).contains("no")){
                                tts.speak("transaction cancelled. restarting please wait",TextToSpeech.QUEUE_FLUSH,null);
                                To.setText("");
                                Subject.setText("");
                                IsInitialVoiceFinshed=true;
                                final Handler handler1 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        Intent intent = new Intent(phonetransfer.this,Home.class);
                                        startActivity(intent);

                                    }
                                },3000);

                            }

                    }

                }
            }
            else {
                switch (numberOfClicks) {
                    case 1:
                        tts.speak("Tap on the screen & say the phone number",TextToSpeech.QUEUE_FLUSH,null);

                        break;
                    case 2:
                        tts.speak("tap on the screen & say, how much money you want to transfer",TextToSpeech.QUEUE_FLUSH,null);

                        break;
                    case 3:
                        tts.speak("Please Confirm the details, Name of account holder is: " + To2.getText().toString() + ",Phone number is "+ Arrays.toString(To.getText().toString().replace("0","zero").split("(?!^)"))+". And Money that you want to transfer is ,: " + Subject.getText().toString() + ",swipe left to listen again, or say Yes to confirm or no to cancel the transaction",TextToSpeech.QUEUE_FLUSH,null);

                        break;

                    default:
                     tts.speak("say yes to proceed the transaction or no to cancel the transaction",TextToSpeech.QUEUE_FLUSH,null);
                        break;
                }
                numberOfClicks--;
            }
        }
        IsInitialVoiceFinshed=true;
    }


    public boolean onTouchEvent(MotionEvent touchEvent) {
         switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                 break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                 if (x1 < x2) {
                     tts.speak("Please Confirm the details, Name of account holder is: " + To2.getText().toString() + ",Phone number is "+ Arrays.toString(To.getText().toString().replace("0","zero").split("(?!^)"))+". And Money that you want to transfer is ,: " + Subject.getText().toString() ,TextToSpeech.QUEUE_FLUSH,null);
                     tts.speak(",swipe left to listen again, and say Yes to confirm or no to cancel the transaction",TextToSpeech.QUEUE_ADD,null);
                     break;
                 }
                if (x1 > x2) {

                    break;
                }

                break;
        }

        return false;
    }

    public void onPause() {
        if (tts != null) {
            tts.stop();
        }
        super.onPause();

    }


}