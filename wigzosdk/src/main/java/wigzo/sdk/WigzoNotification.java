package wigzo.sdk;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ankit on 16/5/16.
 */
public class WigzoNotification {
    public static void notification(Context applicationContext, Class<? extends Activity> targetActivity, NotificationCompat.Builder notificationBuilder, Map<String, Object> intentDataMap, Integer notificationId, Integer secondSound) {
        // if notification_id is provided use it.
        final int mNotificationId = null != notificationId ? notificationId : new Random().nextInt();
        int icon = applicationContext.getApplicationInfo().icon;


        Intent resultIntent = new Intent(applicationContext, targetActivity);

        if (null != intentDataMap) {
            for (Map.Entry<String, Object> entry : intentDataMap.entrySet())
            {
                if (entry.getValue() instanceof CharSequence) {
                    resultIntent.putExtra(entry.getKey(), (CharSequence) entry.getValue());
                }
                else if (entry.getValue() instanceof Number) {
                    resultIntent.putExtra(entry.getKey(), (Number) entry.getValue());
                }
                else if (entry.getValue() instanceof Boolean) {
                    resultIntent.putExtra(entry.getKey(), (Boolean) entry.getValue());
                }
            }
        }

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        applicationContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Resources resources = applicationContext.getResources(),
                systemResources = Resources.getSystem();
        notificationBuilder.setLights(
                ContextCompat.getColor(applicationContext, systemResources
                        .getIdentifier("config_defaultNotificationColor", "color", "android")),
                resources.getInteger(systemResources
                        .getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                resources.getInteger(systemResources
                        .getIdentifier("config_defaultNotificationLedOff", "integer", "android")));


        notificationBuilder.setSmallIcon(icon);
//            .setSound(defaultSoundUri)
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setVibrate(new long[] { 0, 330, 300, 300 });
        notificationBuilder.setContentIntent(resultPendingIntent);




        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) applicationContext.getSystemService(applicationContext.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notificationBuilder.build());

        // Play sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone ringtone = RingtoneManager.getRingtone(applicationContext, defaultSoundUri);
        final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
        Runnable playSound = new Runnable() {
            public void run() {
                System.out.println("here");
                ringtone.stop();
                ringtone.play();
            }
        };

        worker.schedule(playSound, 0, TimeUnit.SECONDS);
        // Play second sound
        if (null != secondSound && secondSound > 0) {
            if (secondSound > 10) {
                secondSound = 10;
            }
            worker.schedule(playSound, secondSound, TimeUnit.SECONDS);
        }
    }

    public static void simpleNotification(Context applicationContext, Class<? extends Activity> targetActivity, String title, String body, Map<String, Object> intentDataMap, Integer notificationId, Integer secondSound) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(applicationContext)
            .setContentTitle(title)
            .setContentText(body);
        notification(applicationContext, targetActivity, notificationBuilder,  intentDataMap, notificationId, secondSound);
    }

    public static void imageNotification(Context applicationContext, Class<? extends Activity> targetActivity, String title, String body, String imageUrl, Map<String, Object> intentDataMap, Integer notificationId, Integer secondSound) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(applicationContext)
                .setContentTitle(title)
                .setContentText(body);

        if(StringUtils.isNotEmpty(imageUrl)){
            Bitmap remote_picture = null;
            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
            try {
                remote_picture = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            notiStyle.bigPicture(remote_picture);
//            notificationBuilder.setLargeIcon(remote_picture);
             notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            notificationBuilder.setStyle(notiStyle);
        }
        notification(applicationContext, targetActivity, notificationBuilder,  intentDataMap, notificationId, secondSound);
    }


}
