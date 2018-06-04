/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wigzo.sdk;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wigzo.sdk.helpers.Configuration;
import com.wigzo.sdk.helpers.ConnectionStream;
import com.wigzo.sdk.helpers.StringUtils;
import com.wigzo.sdk.helpers.WigzoSharedStorage;
import com.wigzo.sdk.helpers.WigzoUrlWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WigzoInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";
    public static String refreshedToken;
    public static boolean isSentToServer = false;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]

    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", refreshedToken);
        if (null == WigzoSDK.getInstance().getContext()) return;

        WigzoSharedStorage wigzoSharedStorage = new WigzoSharedStorage(WigzoSDK.getInstance().getContext());
        if (null == wigzoSharedStorage) return;

        SharedPreferences sharedPreferences = wigzoSharedStorage.getSharedStorage();
        if (null == sharedPreferences) return;

        // Setting SENT_FCM_TOKEN_TO_SERVER to false since we want to refresh the token.
        sharedPreferences.edit().putBoolean(Configuration.SENT_FCM_TOKEN_TO_SERVER.value, false).apply();
        sharedPreferences.edit().putBoolean(Configuration.FCM_DEVICE_MAPPED.value, false).apply();

        sendRegistrationToServer(refreshedToken);
        if (StringUtils.isNotEmpty(wigzoSharedStorage.getSharedStorage().getString(Configuration.ORG_TOKEN_KEY.value, "")) ||
                StringUtils.isNotEmpty(WigzoSDK.getInstance().getOrgToken())) {
            mapFcmToDeviceId(refreshedToken);
        }

        try {
            subscribeTopics(WigzoSDK.getInstance().getOrgToken());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // [END refresh_token]

    public static void sendRegistrationToServer(String token) {
        final Gson gson = new Gson();
        if (null == WigzoSDK.getInstance().getContext()) return;

        WigzoSharedStorage wigzoSharedStorage = new WigzoSharedStorage(WigzoSDK.getInstance().getContext());
        if (null == wigzoSharedStorage) return;

        SharedPreferences sharedPreferences = wigzoSharedStorage.getSharedStorage();
        if (null == sharedPreferences) return;

        // Send to server only when the token is not yet sent
        if (!sharedPreferences.getBoolean(Configuration.SENT_FCM_TOKEN_TO_SERVER.value, false)) {

            //hashmap to store user data to send it to FCM server
            Map<String, Object> eventData = new HashMap<>();

            //store data in eventData
            eventData.put("registrationId", token);
            eventData.put("orgtoken", WigzoSDK.getInstance().getOrgToken());

            //Convert eventData to json string
            final String eventDataStr = gson.toJson(eventData);

            //Endpoint Url
            final String url = WigzoUrlWrapper.addQueryParam(Configuration.BASE_URL.value
                    + Configuration.FCM_REGISTRATION_URL.value, Configuration.SITE_ID.value
                    , WigzoSDK.getInstance().getOrgToken());

            if (StringUtils.isNotEmpty(WigzoSDK.getInstance().getOrgToken())) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                    public Boolean call() {
                        //Post data to server
                        String response = ConnectionStream.postRequest(url, eventDataStr);

                        //Check if post request returned success if the response is not null
                        if (null != response && StringUtils.isJsonString(response)) {
                            try {
                                Map<String, Object> jsonResponse = gson.fromJson(response, new TypeToken<HashMap<String, Object>>() {
                                }.getType());
                                if ("success".equals(jsonResponse.get("status"))) {
                                    isSentToServer = true;
                                    return true;
                                }
                            } catch (Exception e) {
                                return false;
                            }
                        }
                        return false;
                    }
                });

                try {
                    //if post request was successful save the Synced data flag as true in shared preferences
                    if (future.get()) Log.i("Status ", "Token successfully send to server");
                    else Log.e("Status ", "Failed to send token to server. Please verify things are right else contact Wigzo for troubleshooting.");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void mapFcmToDeviceId(String token) {
        final Gson gson = new Gson();
        if (null == WigzoSDK.getInstance().getContext()) return;

        WigzoSharedStorage wigzoSharedStorage = new WigzoSharedStorage(WigzoSDK.getInstance().getContext());
        if (null == wigzoSharedStorage) return;

        final SharedPreferences sharedPreferences = wigzoSharedStorage.getSharedStorage();
        if (null == sharedPreferences) return;

        Boolean initDataSynced = sharedPreferences.getBoolean(Configuration.WIGZO_INIT_DATA_SYNC_FLAG_KEY.value, false);

        Boolean isFcmDeviceMapped = sharedPreferences.getBoolean(Configuration.FCM_DEVICE_MAPPED.value, false);

        if (initDataSynced && !isFcmDeviceMapped) {

            Map<String, Object> eventData = new HashMap<>();

            String deviceId = sharedPreferences.getString(Configuration.DEVICE_ID_KEY.value, "");

            eventData.put("registrationId", token);
            eventData.put("orgtoken", WigzoSDK.getInstance().getOrgToken());
            eventData.put("deviceId", deviceId);

            final String eventDataStr = gson.toJson(eventData);

            final String url = WigzoUrlWrapper.addQueryParam(Configuration.BASE_URL.value
                    + Configuration.FCM_DEVICE_MAPPING_URL.value, Configuration.SITE_ID.value
                    , WigzoSDK.getInstance().getOrgToken());

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                public Boolean call() {
                    //Post data to server
                    String response = ConnectionStream.postRequest(url, eventDataStr);
                    if (null != response) {
                        Map<String, Object> jsonResponse = gson.fromJson(response, new TypeToken<HashMap<String, Object>>() {
                        }.getType());
                        if ("success".equals(jsonResponse.get("status"))) {
                            sharedPreferences.edit().putBoolean(Configuration.FCM_DEVICE_MAPPED.value, true);
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
            });

            try {
                //if post request was successful save the Synced data flag as true in shared preferences
                if (future.get()) {
                    ((AppCompatActivity) WigzoSDK.getInstance().getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();
                            Log.d("Status: ", "Sent");
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Subscribe to any FCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token FCM token
     * @throws IOException if unable to reach the FCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        FirebaseMessaging.getInstance().subscribeToTopic("orgsubscribe-" + WigzoSDK.getInstance().getOrgToken());
    }
    // [END subscribe_topics]
}
