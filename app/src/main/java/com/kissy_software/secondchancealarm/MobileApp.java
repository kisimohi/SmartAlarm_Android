package com.kissy_software.secondchancealarm;

import android.app.Application;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kissy_software.secondchancealarm.notification.KiiAPI;

public class MobileApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Kii.initialize(getApplicationContext(), KiiAPI.APP_ID, KiiAPI.APP_KEY, Kii.Site.JP, true);
    }
}