package com.delaroystudios.taskmakerapp;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.TextView;
import android.widget.Toast;

public class AlertReciever  extends WakefulBroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent) {
        Toast.makeText(context, "start task", Toast.LENGTH_LONG).show();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
    }
}

