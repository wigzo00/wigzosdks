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

package wigzo.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import wigzo.sdk.helpers.Configuration;
import wigzo.sdk.helpers.ConnectionStream;
import wigzo.sdk.helpers.WigzoSharedStorage;

public class WigzoRegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public WigzoRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(WigzoSDK.getInstance().getSenderId(),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            mapGcmToDeviceId(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Gson gson = new Gson();
        WigzoSharedStorage wigzoSharedStorage = new WigzoSharedStorage(WigzoSDK.getInstance().getContext());
        SharedPreferences sharedPreferences = wigzoSharedStorage.getSharedStorage();

        // Send to server only when the token is not yet sent
        if (!sharedPreferences.getBoolean(Configuration.SENT_TOKEN_TO_SERVER.value, false)) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("registrationId", token);
            eventData.put("orgtoken", WigzoSDK.getInstance().getOrgToken());

            final String eventDataStr = gson.toJson(eventData);
            final String url = Configuration.BASE_URL.value + Configuration.GCM_REGISTRATION_URL.value;

            String response = ConnectionStream.postRequest(url, eventDataStr);
            if (null != response) {
                Map<String, Object> jsonResponse = gson.fromJson(response, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                if ("success".equals(jsonResponse.get("status"))) {
                    sharedPreferences.edit().putBoolean(Configuration.SENT_TOKEN_TO_SERVER.value, true).apply();
                }
            }
        }

    }

    private void mapGcmToDeviceId(String token) {
        Gson gson = new Gson();
        WigzoSharedStorage wigzoSharedStorage = new WigzoSharedStorage(WigzoSDK.getInstance().getContext());
        SharedPreferences sharedPreferences = wigzoSharedStorage.getSharedStorage();
        Boolean initDataSynced = sharedPreferences.getBoolean(Configuration.WIGZO_INIT_DATA_SYNC_FLAG_KEY.value, false);
        Boolean isGcmDeviceMapped = sharedPreferences.getBoolean(Configuration.GCM_DEVICE_MAPPED.value, false);
        if (initDataSynced && !isGcmDeviceMapped) {
            Map<String, Object> eventData = new HashMap<>();
            String deviceId = sharedPreferences.getString(Configuration.DEVICE_ID_KEY.value, "");

            eventData.put("registrationId", token);
            eventData.put("orgtoken", WigzoSDK.getInstance().getOrgToken());
            eventData.put("deviceId", deviceId);

            final String eventDataStr = gson.toJson(eventData);
            final String url = Configuration.BASE_URL.value + Configuration.GCM_DEVICE_MAPPING_URL.value;

            String response = ConnectionStream.postRequest(url, eventDataStr);
            if (null != response) {
                Map<String, Object> jsonResponse = gson.fromJson(response, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                if ("success".equals(jsonResponse.get("status"))) {
                    sharedPreferences.edit().putBoolean(Configuration.GCM_DEVICE_MAPPED.value, true);
                }
            }
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}