package com.wigzo.sdk.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.support.annotation.Keep;
import android.support.v4.app.NotificationCompat;

/**
 * Created by wigzo
 */
@Keep
public enum Configuration {

    // ----------------------------------------- MIND IT !!!!!!!!!! -----------------------------------------//
    BASE_URL("baseUrl","https://app.wigzo.com"),
    // ----------------------------------------- MIND IT !!!!!!!!!! -----------------------------------------//
    APP_KEY("appKey","APP_KEY"),
    DEFAULT_APP_VERSION("defaultAppVersion","1.0"),
    DEFAULT_SDK_VERSION("defaultSdkVersion","1.0"),
    DEVICE_ID_KEY("deviceIdKey","DEVICE_ID_KEY"),
    DEVICE_ID_TAG("deviceIdTag","DeviceInfo"),
    DEVICE_LOCATION_KEY("deviceLocationKey","DEVICE_LOCATION_KEY"),
    EMAIL_DATA_URL("emailDataUrl","/androidsdk/mapemail"),
    EMAIL_KEY("emailKey","EMAIL_KEY"),
    EMAIL_SYNC_KEY("emailSyncKey","EMAIL_SYNC_KEY"),
    EVENTS_KEY("eventKey","WIGZO_EVENTS"),
    EVENT_DATA_URL("eventDataUrl","/androidsdk/geteventdata"),
    FCM_DEVICE_MAPPED("fcmDeviceMapped", "FCM_DEVICE_MAPPED"),
    FCM_DEVICE_MAPPING_URL("fcmDeviceMappingUrl", "/androidsdk/map-fcm"),
    FCM_OPEN_KEY("fcmOpenKey", "FCM_OPEN_KEY"),
    FCM_OPEN_URL("fcmOpenUrl", "/rest/v1/push/android/track/open-multiple"),
    FCM_READ_KEY("fcmReadKey", "FCM_READ_KEY"),
    FCM_READ_URL("fcmReadUrl", "/rest/v1/push/android/track/read-multiple"),
    FCM_REGISTRATION_URL("fcmRegistrationUrl", "/rest/v1/push/android/register-subscription"),
    USER_LOCATION_URL("userLocationUrl", "/report/track/web"),
    INITIAL_DATA_URL("initialDataUrl","/androidsdk/getinitialdata"),
    ORG_TOKEN_KEY("orgToken","ORG_TOKEN_KEY"),
    PREV_TIME_SPENT_KEY("prevTimeSpent","PREV_TIME_SPENT_KEY"),
    SENT_FCM_TOKEN_TO_SERVER("sentTokenToServer", "SENT_FCM_TOKEN_TO_SERVER"),
    SESSION_DATA_URL("sessionDataUrl","/androidsdk/getsessiondata"),
    SITE_ID("siteid", "_siteid"),
    STORAGE_KEY("storageKey","WIGZO_SHARED_STORAGE"),
    TIME_DELAY("timeDelay","15"),
    TIME_SPENT_KEY("timeSpentKey","TIME_SPENT_KEY"),
    USER_PROFILE_DATA_KEY("userProfileDataKey","USER_PROFILE_DATA_KEY"),
    USER_PROFILE_PICTURE_KEY("userProfilePictureKey","USER_PROFILE_PICTURE_KEY"),
    USER_PROFILE_SYNC_KEY("userProfileSyncKey","USER_PROFILE_SYNC_KEY"),
    USER_PROFILE_URL("userProfileUrl","/androidsdk/getuserprofiledata"),
    WIGZO_FCM_LISTENER_SERVICE_TAG("wigzoFCMListenerServiceTag","WigzoFcmListenerService"),
    WIGZO_INIT_DATA_SYNC_FLAG_KEY("wigzoInitiDataSyncFlag","WIGZO_INIT_DATA_SYNC_FLAG_KEY"),
    WIGZO_REG_INTENT_SERVICE_TAG("wigzoRegIntentServiceTag","RegIntentService"),
    WIGZO_SDK_TAG("wigzoSdkTag","Wigzo"),
    APP_RUNNING_STATUS("appRunningSatus", "AppRunningStatus"),
    W_FCM_SENDER_ID("W_FCM_SENDER_ID", "768564314928"),//to be read from manifest
    W_FCM_CHANNEL_ID("W_FCM_CHANNEL_ID", NotificationChannel.DEFAULT_CHANNEL_ID),//to be read from manifest
    W_FCM_CHANNEL_NAME("W_FCM_CHANNEL_NAME", "DEFUALT"),//to be read from manifest
    W_FCM_CHANNEL_DESCRIPTION("W_FCM_CHANNEL_DESCRIPTION", ""),//to be read from manifest
    W_FCM_CHANNEL_IMPORTANCE("W_FCM_CHANNEL_IMPORTANCE", "" + NotificationManager.IMPORTANCE_DEFAULT),//to be read from manifest
    W_NOTIFICATION_PRIORITY("W_NOTIFICATION_PRIORITY", "" + NotificationCompat.PRIORITY_DEFAULT),//to be read from manifest
    W_IS_NOTIFICATION_AUTO_CANCEL("W_NOTIFICATION_AUTO_CANCEL_BEHAVIOUR", "true"),//to be read from manifest
    FCM_SCOPE("fcmScope", "FCM");

    public String key;
    public String value;

    Configuration(String key, String value)
    {
        this.key   = key;
        this.value = value;
    }


}
