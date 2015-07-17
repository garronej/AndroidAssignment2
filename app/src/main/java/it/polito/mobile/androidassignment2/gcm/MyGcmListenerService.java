/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package it.polito.mobile.androidassignment2.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.ScheduleChangedActivity;
import it.polito.mobile.androidassignment2.StudentFlow.StudentProfileActivity;
import it.polito.mobile.androidassignment2.StudentFlow.chat.ConversationsActivity;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.NoticeBoard;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public static final String MESSAGE_RECEIVED = "it.polito.mobile.androidassignment2.gcm.NEW_MESSAGE_RECEIVED";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String message = data.getString("message");
        String type = data.getString("type");
        String messageJson = data.getString("message_json");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(type, title, message, messageJson);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private static int scheduleChangeCounter = 0;

    private void sendNotification(String type, String title, String message, String messageJson) {
        Intent intent = null;
        int notificationId;
        int counter;
        String notificationTitle;
        String notificationMessage;

        if (type.contains("schedule_change")) {
            scheduleChangeCounter++;
            if (scheduleChangeCounter == 10) {
                scheduleChangeCounter = 0;
            }
            notificationId = scheduleChangeCounter;
            counter = 0;
            intent = new Intent(this, ScheduleChangedActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("message", message);
            notificationTitle = title;
            notificationMessage = message;
        } else if (type.contains("message")) {
            notificationId = 0;
            if(getSharedPreferences("notifications", MODE_PRIVATE).contains("message_counter")) {
                counter = getSharedPreferences("notifications", MODE_PRIVATE).getInt("message_counter", -1);
                counter++;
            } else {
                counter = 1;
            }
            SharedPreferences.Editor editor = getSharedPreferences("notifications", MODE_PRIVATE).edit();
            editor.putInt("message_counter", counter);
            editor.commit();

            intent = new Intent(this, ConversationsActivity.class);
            notificationTitle = getApplicationContext().getString(R.string.new_message);
            notificationMessage = getApplicationContext().getString(R.string.you_have_received_new_messages);

            Intent i = new Intent(MESSAGE_RECEIVED);
            i.putExtra("messageJson", messageJson);
            sendBroadcast(i);
        } else {
            throw new RuntimeException("type of notification not supported");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setNumber(counter)
                .setTicker(notificationTitle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
