package org.tensorflow.lite.examples.detection.QRProduct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.Toast;

import org.tensorflow.lite.examples.detection.Battery;
import org.tensorflow.lite.examples.detection.Calculator;
import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.DateAndTime;
import org.tensorflow.lite.examples.detection.Location.LocationActivity;
import org.tensorflow.lite.examples.detection.Moneytransfer.Banktransfer;
import org.tensorflow.lite.examples.detection.Moneytransfer.phonetransfer;
import org.tensorflow.lite.examples.detection.OCRReader;
import org.tensorflow.lite.examples.detection.ObjectDetection.MainActivity;
import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.Reminder.Reminder;
import org.tensorflow.lite.examples.detection.Weather;

import java.util.ArrayList;
import java.util.Locale;

public class QRresult extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextToSpeech textToSpeech;
    private ArrayList<Prodlist> courseModalArrayList;
    private DBHandler dbHandler;
    private Prodadapter Prodadapter;
    private RecyclerView prodlist;
    static String sum1;
     double sum=0;
    float x1,x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrresult);


         courseModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(QRresult.this);

        // getting our course array
        // list from db handler class.
        courseModalArrayList = dbHandler.readCourses();

        // on below line passing our array lost to our adapter class.
        Prodadapter = new Prodadapter(courseModalArrayList, QRresult.this);
        prodlist = findViewById(R.id.idRVCourses);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(QRresult.this, RecyclerView.VERTICAL, false);
        prodlist.setLayoutManager(linearLayoutManager);

        // setting our adapter to recycler view.
        prodlist.setAdapter(Prodadapter);



        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.speak("product name is,"+QRactivity.GetQRResult()+".cost is, "+QRactivity.getNum()+"rupees",TextToSpeech.QUEUE_FLUSH,null);
                   textToSpeech.speak("swipe right and say add product to add one more product in list. or say no, to know the total cost of products",TextToSpeech.QUEUE_ADD,null);

                }
            }

        });
    }





        public boolean onTouchEvent(MotionEvent touchEvent) {

            switch (touchEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    x1 = touchEvent.getX();
                     break;
                case MotionEvent.ACTION_UP:
                    x2 = touchEvent.getX();
                     if (x1 < x2) {
                         textToSpeech.speak("Name of the products are,"+DBHandler.arrayList+""+".Total cost is"+sum1+"rupees", TextToSpeech.QUEUE_FLUSH, null);
                         textToSpeech.speak("swipe left to listen again and swipe right and say what you want", TextToSpeech.QUEUE_ADD, null);

                    }
                    if (x1 > x2) {
                        startVoiceInput();
                        break;
                    }

                    break;
            }

            return false;
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
                    if (result.get(0).contains("exit")) {
                        finish();
                        finishAffinity();
                        System.exit(0);
                    }
                    if (result.get(0).contains("add product")) {
                        Intent intent = new Intent(getApplicationContext(), QRactivity.class);
                        startActivity(intent);

                    }
                    if (result.get(0).contains("no")) {
                        for (int i = 0; i < DBHandler.arraysum.size(); i++) {
                            sum = sum + DBHandler.arraysum.get(i);
                            sum1 = Double.toString(sum).replaceAll("\\.0*$", "");

                        }
                        textToSpeech.speak("Name of the products are," + DBHandler.arrayList + "" + ".Total cost is," + sum1, TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak("swipe left to listen again or swipe right and say what you want", TextToSpeech.QUEUE_ADD, null);
                        Toast.makeText(getApplicationContext(), DBHandler.arrayList + "" + DBHandler.arraysum, Toast.LENGTH_SHORT).show();


                    }


                    if (result.get(0).contains("read")) {
                        finish();
                        dbHandler.deleteData();
                        Intent intent = new Intent(getApplicationContext(), OCRReader.class);
                        startActivity(intent);

                    }
                    if (result.get(0).contains("calculator")) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(), Calculator.class);
                        startActivity(intent);

                    }
                    if (result.get(0).contains("time and date")) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(), DateAndTime.class);
                        startActivity(intent);

                    }
                    if (result.get(0).contains("weather")) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(), Weather.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

                    if (result.get(0).contains("battery")) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(), Battery.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (result.get(0).contains("location")) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    if (result.get(0).contains("bank transfer")) {
                        finish();
                        Intent i = new Intent(QRresult.this, Banktransfer.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } else if (result.get(0).contains("phone transfer")) {
                        finish();
                        Intent i = new Intent(QRresult.this, phonetransfer.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                    if (result.get(0).contains("object detection")) {
                        finish();
                        Intent i = new Intent(QRresult.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                    if (result.get(0).contains("scan the QR") || result.get(0).contains("QR")) {
                        Intent i = new Intent(QRresult.this, QRactivity.class);
                        startActivity(i);
                    }
                    if (result.get(0).contains("exit")) {
                        finish();
                        finishAffinity();
                    }
                    if (result.get(0).contains("create new list")) {
                        finish();
                        prodlist.setAdapter(null);
                        DBHandler.arrayList.clear();
                        DBHandler.arraysum.clear();
                        Boolean isDeleted = dbHandler.deleteData();
                        if (isDeleted) {

                           textToSpeech.speak( "New list has been created", TextToSpeech.QUEUE_FLUSH,null);

                        } else {
                            Toast.makeText(QRresult.this, "New list not created", Toast.LENGTH_SHORT).show();
                        }

                        Handler h = new Handler(Looper.getMainLooper());
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), QRactivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }, 3000);

                    }
                    if (result.get(0).contains("reminder")) {
                        Intent i = new Intent(QRresult.this, Reminder.class);
                        startActivity(i);
                    }


                    if (result.get(0).contains("add product")) {

                        Intent intent = new Intent(getApplicationContext(), QRactivity.class);
                        startActivity(intent);

                    }

                }
            }

        }


        public void onPause() {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            super.onPause();

        }
    }
