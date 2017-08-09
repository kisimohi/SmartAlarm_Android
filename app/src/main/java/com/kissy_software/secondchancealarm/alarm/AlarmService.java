package com.kissy_software.secondchancealarm.alarm;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import com.kissy_software.secondchancealarm.document.Document;
import com.kissy_software.secondchancealarm.notification.AlarmNortificationActivity;

public class AlarmService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Thread thread = new Thread(null, mTask, "MyAlarmServiceThread");
        thread.start();
    }

    Runnable mTask = new Runnable() {
        public void run() {
/*            Intent alarmBroadcast = new Intent();
            alarmBroadcast.setAction("com.kissy_software.secondchanealarm.AlarmNotification");
            sendBroadcast(alarmBroadcast);
*/
            Intent notification = new Intent(getApplicationContext(), AlarmNortificationActivity.class);
            notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(notification);

            PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "My Tag");
            wl.acquire(30000);

            KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keylock = keyguard.newKeyguardLock("disableLock");
            keylock.disableKeyguard();

            AlarmService.this.stopSelf();
        }
    };
}
