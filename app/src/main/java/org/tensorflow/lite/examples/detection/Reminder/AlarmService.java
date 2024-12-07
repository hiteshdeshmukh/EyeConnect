package org.tensorflow.lite.examples.detection.Reminder;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.tensorflow.lite.examples.detection.R;

public class AlarmService extends Service {
    private Vibrator vibrator;
     static String message,time;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent intent1 = new Intent(this, NotificationMessage.class);
        message = intent.getStringExtra("message");
        time = intent.getStringExtra("time");
        Bundle bundle = intent.getExtras();

         PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent1,0);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
         NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "notify_001");
        //here we set all the properties for the notification
        RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.notification_layout);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
        contentView.setTextViewText(R.id.message, intent.getStringExtra("message"));
        contentView.setTextViewText(R.id.date, "date");
        mBuilder.setSmallIcon(R.drawable.alarm);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mBuilder.setContent(contentView);
        mBuilder.setContentIntent(pendingIntent);

        //we have to create notification channel after api level 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);

            Notification notification = mBuilder.build();
            notificationManager.cancelAll();
            long[] pattern = {0, 100, 1000, 200, 2000};
            vibrator.vibrate(pattern, -1);
            startForeground(1, notification);

        }
        return START_STICKY;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();

         vibrator.cancel();

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }



 }

