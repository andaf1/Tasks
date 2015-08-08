package com.example.andreeamocean.tasks.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import com.example.andreeamocean.tasks.R;
import com.example.andreeamocean.tasks.activity.TaskDescriptionActivity;
import com.example.andreeamocean.tasks.model.Tasks;

/**
 * Created by andreeamocean on 8/2/15.
 */
public class NotificationEvent extends Service {

    Tasks task ;

    @Override
    public void onCreate() {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(this, TaskDescriptionActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)
                .setContentTitle(task.getTitle())
                .setContentText(task.getDescription())
                .setSmallIcon(R.drawable.ic_action_content_create)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .addAction(R.drawable.ic_action_content_create, "View", pIntent)
                .addAction(0, "Remind", pIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, mNotification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        task = (Tasks) intent.getSerializableExtra("task_reminder");
        return null;
    }
}
