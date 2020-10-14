package com.progmatik.zkhphone;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.progmatik.zkhphone.classes.myPushMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
        if ( remoteMessage.getData() != null ) {
            Map<String, String> data = remoteMessage.getData();
            if ( data != null ) {
                myPushMessage pushMessage = new myPushMessage( data.get("title"),
                                                               data.get("message"),
                                                              (data.get("sound") != null && !data.get("sound").equals("") && data.get("sound").equals("1") ),
                                                              (data.get("vibrate") != null && !data.get("vibrate").equals("") && data.get("vibrate").equals("1") ),
                                                              (data.get("is_system") != null && !data.get("is_system").equals("") && data.get("is_system").equals("1") ) );
                String is_system = data.get("is_system");
                //Пришло системное push-сообщение?
                if ( pushMessage.getIsSystem() ) {
//                    Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
//                    intent.putExtra("title",pushMessage.getTitle());
//                    intent.putExtra("message", pushMessage.getMessage());
//                    sendBroadcast(intent);
                } else {
                    sendNotification( pushMessage );
                }
            }
        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            myPushMessage pushMessage = new myPushMessage( remoteMessage.getNotification().getTitle(),
                                                           remoteMessage.getNotification().getBody(),
                                                           Boolean.TRUE,
                                                           Boolean.FALSE,
                                                           Boolean.FALSE );
            sendNotification( pushMessage );
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param pushMessage FCM message body received.
     */
    private void sendNotification(myPushMessage pushMessage ) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(pushMessage.getTitle())
                .setContentText(pushMessage.getMessage())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED ) {
            ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(800);
        }
    }
}