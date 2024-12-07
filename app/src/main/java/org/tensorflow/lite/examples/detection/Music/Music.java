package org.tensorflow.lite.examples.detection.Music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Music extends AppCompatActivity {

    private static ArrayList<Song> songList;
    MediaPlayer mediaPlayer;
    private static TextToSpeech textToSpeech;
    TextView bstop;
    TextView mtitle, artist, cDuration, tDuration;
    String current, duration;
    SeekBar seekBar;
    Handler seekHandler;
    float x1,x2,y1,y2;

  static  int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.speak("Welcome to music , swipe right to return to main screen or swipe left for next song",TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                initUi();
            }
        },5000);

    }
    public void initUi(){
        Random random = new Random();
        songList = new ArrayList<Song>();
        bstop = (TextView) findViewById(R.id.imageButton);
        mtitle = (TextView) findViewById(R.id.textViewTitle);
        artist = (TextView) findViewById(R.id.textViewArtist);
        tDuration = (TextView) findViewById(R.id.textViewTduration);
        cDuration = (TextView) findViewById(R.id.textView7);
        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        mediaPlayer = new MediaPlayer();

        if(checkIfAlreadyhavePermission()){
            getSongList();
            pos = random.nextInt(songList.size()-1);
            playSong();
            Toast.makeText(getApplicationContext(), "Permission is granted", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(Music.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
         switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    textToSpeech.stop();
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    task.cancel(true);
                    Intent i = new Intent(Music.this, Home.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }
                if (x1 > x2) {
                    Random random = new Random();
                    if(songList.size()>0){
                        pos = random.nextInt(songList.size()-1);
                    }
                    playSong();

                    break;
                }


                break;
        }

        return false;
    }



   static Task task;

    private void playSong() {
        try {

            mediaPlayer.reset();
            //get song
            Song playSong = songList.get(pos);
            mtitle.setText(playSong.getTitle());
            artist.setText(playSong.getArtist());

            //get id
            long currSong = playSong.getID();
            //set uri
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);

            try {
                mediaPlayer.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }

            task = new Task();
            mediaPlayer.prepare();
            task.execute();
            mediaPlayer.start();

            seekBar.setMax(mediaPlayer.getDuration());
            duration = milliSecondsToTimer(mediaPlayer.getDuration());
            tDuration.setText(duration);
            cDuration.setText(duration);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void getSongList(){
        //retrieve song info

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToPosition(3)) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Log.d("jwenkjewkn", String.valueOf(new Song(thisId, thisTitle, thisArtist)));
                songList.add(new Song(thisId, thisTitle, thisArtist));

            }
            while (musicCursor.moveToNext());
        }
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public void seekUpdation() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
    }


    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        finish();
        super.onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    class Task extends AsyncTask<Integer, Integer, Void> {
        long i = 0;

        @Override
        protected Void doInBackground(Integer... params) {

            while (mediaPlayer.isPlaying()) {
                i = mediaPlayer.getCurrentPosition();
                seekUpdation();
                publishProgress(0);
                i++;
            }
            if(!mediaPlayer.isPlaying()){
                textToSpeech.speak("swipe right for next song",TextToSpeech.QUEUE_FLUSH,null);
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tDuration.setText("" + milliSecondsToTimer(i));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongList();
                Random random = new Random();
                pos = random.nextInt(songList.size()-1);
                playSong();
                Toast.makeText(getApplicationContext(), "permission granted ... Reading messages", Toast.LENGTH_SHORT).show();
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(Music.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



}
