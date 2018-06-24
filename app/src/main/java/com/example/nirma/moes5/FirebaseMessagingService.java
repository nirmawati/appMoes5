package com.example.nirma.moes5;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mvryan on 20/06/18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();

        String actionClick = remoteMessage.getNotification().getClickAction();

        String fromSenderId = remoteMessage.getData().get("from_sender_id").toString();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody);

        Intent resultIntent = new Intent(actionClick);
        resultIntent.putExtra("visit_user_id",fromSenderId);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        int mNotificationId = (int) System.currentTimeMillis();//notification id
        //get instance from notif manager service
        NotificationManager mNotifMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //build the notif
        mNotifMgr.notify(mNotificationId, mBuilder.build());
    }
}
