package com.wigzo.sdk;

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
import android.support.annotation.Keep;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.wigzo.sdk.helpers.StringUtils;
import com.wigzo.sdk.model.FcmRead;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ankit on 16/5/16.
 */
@Keep
public class WigzoNotification {

    private static int mNotificationId = 0;
    private static int campaignId = 0;
    private static int organizationId = 0;

    public static void notification(final Context applicationContext, Class<? extends Activity> targetActivity, NotificationCompat.Builder notificationBuilder, String intentData, final String uuid, Integer notificationId, String linkType, String link, Integer secondSound, Integer id, Integer orgId) {
        // if notification_id is provided use it.
        mNotificationId = null != notificationId ? notificationId : new Random().nextInt();
        campaignId = null != id ? id : 0;
        organizationId = orgId;

        int icon = applicationContext.getApplicationInfo().icon;


        Intent proxyIntent = new Intent(applicationContext, ProxyActivity.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.

        proxyIntent.putExtra("targetActivity", targetActivity);
        proxyIntent.putExtra("uuid", uuid);
        proxyIntent.putExtra("intentData", intentData);
        proxyIntent.putExtra("linkType", linkType);
        proxyIntent.putExtra("link", link);
        proxyIntent.putExtra("campaignId", campaignId);
        proxyIntent.putExtra("organizationId", organizationId);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        applicationContext,
                        0,
                        proxyIntent,
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
        notificationBuilder.setVibrate(new long[]{0, 330, 300, 300});
        notificationBuilder.setContentIntent(resultPendingIntent);


        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) applicationContext.getSystemService(applicationContext.NOTIFICATION_SERVICE);

        Log.e("NotificatioId", "" + mNotificationId);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notificationBuilder.build());

        // Play sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone ringtone = RingtoneManager.getRingtone(applicationContext, defaultSoundUri);
        final ScheduledExecutorService soundWorker = Executors.newSingleThreadScheduledExecutor();
        Runnable playSound = new Runnable() {
            public void run() {
                System.out.println("here");
                ringtone.stop();
                ringtone.play();
            }
        };

        soundWorker.schedule(playSound, 0, TimeUnit.SECONDS);
        // Play second sound
        if (null != secondSound && secondSound > 0) {
            if (secondSound > 10) {
                secondSound = 10;
            }
            soundWorker.schedule(playSound, secondSound, TimeUnit.SECONDS);
        }

        // increase counter
        if (StringUtils.isNotEmpty(uuid)) {
            final ScheduledExecutorService fcmReadWorker = Executors.newSingleThreadScheduledExecutor();
            fcmReadWorker.schedule(new Runnable() {
                @Override
                public void run() {
                    FcmRead fcmRead = new FcmRead(uuid, campaignId, organizationId);
                    FcmRead.Operation operation = FcmRead.Operation.saveOne(fcmRead);
                    FcmRead.editOperation(applicationContext, operation);
                }
            }, 0, TimeUnit.SECONDS);
        }
    }

    public static void simpleNotification(Context applicationContext, Class<? extends Activity> targetActivity, String title, String body, String intentData, String uuid, Integer notificationId, String linkType, String link, Integer secondSound, Integer campaignId, Integer organizationId) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(applicationContext)
                .setContentTitle(title)
                .setContentText(body);
        notification(applicationContext, targetActivity, notificationBuilder, intentData, uuid, notificationId, linkType, link, secondSound, campaignId, organizationId);
    }

    public static void imageNotification(Context applicationContext, Class<? extends Activity> targetActivity, String title, String body, String imageUrl, String intentData, String uuid, Integer notificationId, String linkType, String link, Integer secondSound, Integer campaignId, Integer organizationId) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(applicationContext)
                .setContentTitle(title)
                .setSmallIcon(applicationContext.getApplicationInfo().icon)
                .setContentText(body);

        if (StringUtils.isNotEmpty(imageUrl)) {
            Bitmap remote_picture = null;
            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
            try {
                remote_picture = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            notiStyle.bigPicture(remote_picture);

            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            notificationBuilder.setStyle(notiStyle);
        }
        notification(applicationContext, targetActivity, notificationBuilder, intentData, uuid, notificationId, linkType, link, secondSound, campaignId, organizationId);
    }
}
