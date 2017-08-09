package com.kissy_software.secondchancealarm.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.PushMessageBundleHelper;
import com.kii.cloud.storage.ReceivedMessage;

import java.util.Map;

public class SAFirebaseMessagingService extends FirebaseMessagingService {
    public static final String INTENT_MESSAGE_ARRIVED = "com.kissy_software.secondchancealarm.RECEIVED";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> payload = remoteMessage.getData();
        Bundle bundle = new Bundle();
        for (String key : payload.keySet()) {
            bundle.putString(key, payload.get(key));
        }

        ReceivedMessage message = PushMessageBundleHelper.parse(bundle);
        KiiUser sender = message.getSender();
        PushMessageBundleHelper.MessageType type = message.pushMessageType();
        switch (type) {
            case DIRECT_PUSH:
                Intent registrationComplete = new Intent(INTENT_MESSAGE_ARRIVED);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                break;
        }
    }
}