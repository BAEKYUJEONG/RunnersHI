package com.A306.runnershi.Services;

import android.util.Log;

import com.A306.runnershi.Activity.AlarmActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        Log.d("FCM Log","Refreshed token > " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            Log.d("FCM Log", "알림 메시지 > " + remoteMessage.getData().get("Title"));
            String messageBody = remoteMessage.getData().get("Body");
            String messageTitle = remoteMessage.getData().get("Title");
            Intent intent = new Intent(this, AlarmActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_ONE_SHOT);
            String channelId = "friend";
            Uri Default = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            //.setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(messageTitle)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(Default)
                            .setContentIntent(pendingIntent);
            NotificationManager notimanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = "friend";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notimanager.createNotificationChannel(channel);
            }
            notimanager.notify(0,notificationBuilder.build());
        }
    }

}
