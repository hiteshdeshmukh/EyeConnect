package org.tensorflow.lite.examples.detection.Reminder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class AlarmBroadcast extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String text = bundle.getString("event");
        String time = bundle.getString("date");
        String date = bundle.getString("date") + " " + bundle.getString("time");
        int rand = bundle.getInt("random");
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra("message", text);
        intentService.putExtra("time", time);
        intentService.putExtra("random", rand);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
        Intent mIntent = new Intent(context, NotificationMessage.class);
        mIntent.setAction(Intent.ACTION_MAIN);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);


    }
}
