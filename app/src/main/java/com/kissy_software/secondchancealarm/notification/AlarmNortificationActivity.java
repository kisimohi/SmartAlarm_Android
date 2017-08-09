package com.kissy_software.secondchancealarm.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kissy_software.secondchancealarm.MainActivity;
import com.kissy_software.secondchancealarm.R;
import com.kissy_software.secondchancealarm.document.Configuration;
import com.kissy_software.secondchancealarm.document.Document;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.android.AndroidDeferredManager;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AlarmNortificationActivity extends AppCompatActivity {
    enum KII_API_STATE {
        NEXT_ONBOARD,
        DONE_ONBOARD,
        NEXT_SEND_COMMAND,
        DONE_SEND_COMMAND,
        RETRY_SEND_COMMAND,
        NEXT_RECEIVE_RESULT,
        DONE_RECEIVE_RESULT,
        COMPLETED,
    }

    enum NOTIFY_STATE {
        CONFIRMING,
        CONFIRMING_IN_DARK,
        NOTIFYING,
    }

    private final int WATCH_TIMER_INTERVAL = 1000;
    private final int RETRY_WAIT_TIMER_COUNT = 3;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Command mLastCommand = null;
    private int mRemainTime;
    private int mRetryWaitCount = 0;
    private KII_API_STATE mApiState = KII_API_STATE.NEXT_ONBOARD;
    private NOTIFY_STATE mNotifyState = NOTIFY_STATE.CONFIRMING;
    private boolean mBrightnessHigh = false;

    private MediaPlayer mPlayer;
    private Vibrator mVibrator;
    private Unbinder mButterknifeUnbunder;
    private AndroidDeferredManager mAdm = new AndroidDeferredManager();
    private ThingIFAPI mThingIFAPI = null;
    private Handler mHandlerWatchTimer = new Handler();

    private Runnable mHandlerProcess = new Runnable() {
        @Override
        public void run() {
            onTimer();
        }
    };

    @BindView(R.id.textViewTitle)
    TextView mTextViewTitle;

    @BindView(R.id.textViewTime)
    TextView mTextViewTime;

    @BindView(R.id.textViewStatus)
    TextView mTextViewStatus;

    @BindView(R.id.textViewDetail)
    TextView mTextViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_nortification);
        mButterknifeUnbunder = ButterKnife.bind(this);

        Document.loadDocument(getApplicationContext());

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onTimer();
            }
        };

        mRemainTime = Configuration.getInstance().getAlarmRemainTime();

        mTextViewTitle.setText(R.string.app_name);
        mTextViewTime.setText("");
        mTextViewStatus.setText("");
        mTextViewDetail.setText("");

        mHandlerWatchTimer.postDelayed(mHandlerProcess, WATCH_TIMER_INTERVAL);
        displayState();
        blinkScreen();
    }

    @Override
    public void onDestroy() {
        mButterknifeUnbunder.unbind();
        stopAlarm();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(SAFirebaseMessagingService.INTENT_MESSAGE_ARRIVED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void onTimer() {
        blinkScreen();
        if (mNotifyState == NOTIFY_STATE.NOTIFYING) {
        } else if (mRetryWaitCount > 0) {
            mRetryWaitCount = Math.max(0, mRetryWaitCount - 1);
        } else {
            switch (mApiState) {
                case NEXT_ONBOARD:
                    onboard();
                    break;
                case NEXT_SEND_COMMAND:
                case RETRY_SEND_COMMAND:
                    sendCommand();
                    break;
                case NEXT_RECEIVE_RESULT:
                    receiveCommandResult();
                    break;
                case COMPLETED:
                    closeNotificationPage(R.string.notify_bar_close_success);
                    break;
            }
        }

        mRemainTime--;
        if (mRemainTime <= 0) {
            if (mNotifyState != NOTIFY_STATE.NOTIFYING) {
                startSound();
                mNotifyState = NOTIFY_STATE.NOTIFYING;
            } else if (mRemainTime < -Configuration.getInstance().getAlarmRingTime()) {
                closeNotificationPage(R.string.notify_bar_alarm_timeout);
            }
        }

        displayState();
        if (mApiState != KII_API_STATE.COMPLETED) {
            mHandlerWatchTimer.removeMessages(0);
            mHandlerWatchTimer.postDelayed(mHandlerProcess, WATCH_TIMER_INTERVAL);
        }
    }

    private void onboard() {
        KiiAPI api = new KiiAPI(mAdm, null);
        Configuration config = Configuration.getInstance();
        mAdm.when(api.initializeThingIFAPI(getApplicationContext(), config.getUserName(), config.getPassword(), config.getThingVendorID(), config.getThingPassword())
        ).then(new DoneCallback<ThingIFAPI>() {
            @Override
            public void onDone(ThingIFAPI api) {
                mApiState = KII_API_STATE.NEXT_SEND_COMMAND;
                mThingIFAPI = api;
                sendCommand();
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(final Throwable tr) {
                mRetryWaitCount = RETRY_WAIT_TIMER_COUNT;
                mApiState = KII_API_STATE.NEXT_ONBOARD;
                setDetailMessage(R.string.notify_detail_kii_initialize, tr.getLocalizedMessage());
            }
        });
        mApiState = KII_API_STATE.DONE_ONBOARD;
    }

    private void sendCommand() {
        KiiAPI api = new KiiAPI(mAdm, mThingIFAPI);
        List<Action> actions = new ArrayList<>();
        actions.add(new CheckSensorAction());
        mAdm.when(api.postNewCommand(actions)
        ).then(new DoneCallback<Command>() {
            @Override
            public void onDone(Command command) {
                mApiState = KII_API_STATE.NEXT_RECEIVE_RESULT;
                mLastCommand = command;
                receiveCommandResult();
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(final Throwable tr) {
                mRetryWaitCount = RETRY_WAIT_TIMER_COUNT;
                mApiState = KII_API_STATE.NEXT_SEND_COMMAND;
                setDetailMessage(R.string.notify_detail_send_command, tr.getLocalizedMessage());
            }
        });
        mApiState = KII_API_STATE.DONE_SEND_COMMAND;
    }

    private void receiveCommandResult() {
        if (mLastCommand == null) {
            setDetailMessage(R.string.notify_detail_internal_error, mApiState.toString());
            return;
        }
        KiiAPI api = new KiiAPI(mAdm, mThingIFAPI);
        mAdm.when(api.getCommand(mLastCommand.getCommandID())
        ).then(new DoneCallback<Command>() {
            @Override
            public void onDone(Command command) {
                CheckSensorActionResult result = (CheckSensorActionResult) command.getActionResult(new CheckSensorAction());
                if (result != null) {
                    if (result.succeeded) {
                        mApiState = KII_API_STATE.COMPLETED;
                    } else {
                        mApiState = KII_API_STATE.RETRY_SEND_COMMAND;
                        mNotifyState = NOTIFY_STATE.CONFIRMING_IN_DARK;
                        mRetryWaitCount = RETRY_WAIT_TIMER_COUNT;
                        mLastCommand = null;
                    }
                } else {
                    mApiState = KII_API_STATE.NEXT_RECEIVE_RESULT;
                    mRetryWaitCount = RETRY_WAIT_TIMER_COUNT;
                }
            }
        }).fail(new FailCallback<Throwable>() {
            @Override
            public void onFail(final Throwable tr) {
                setDetailMessage(R.string.notify_detail_command_result, tr.getLocalizedMessage());
                mApiState = KII_API_STATE.NEXT_SEND_COMMAND;
                sendCommand();
            }
        });
        mApiState = KII_API_STATE.DONE_RECEIVE_RESULT;
    }

    private void displayState() {
        if (mTextViewStatus == null) {
            return;
        }
        switch (mNotifyState) {
            case CONFIRMING:
                mTextViewStatus.setText(R.string.notify_title_loading);
                break;
            case CONFIRMING_IN_DARK:
                mTextViewStatus.setText(R.string.notify_title_dark);
                break;
            case NOTIFYING:
                mTextViewStatus.setText(R.string.notify_title_notifying);
                break;
        }

        if (mRemainTime > 0) {
            String remain = getString(R.string.notify_remain_time);
            remain = MessageFormat.format(remain, mRemainTime);
            mTextViewTime.setText(remain);
        } else {
            mTextViewTime.setText("");
        }

        String detail = "";
        if (mNotifyState == NOTIFY_STATE.NOTIFYING || mRetryWaitCount > 0) {
        } else {
            switch (mApiState) {
                case NEXT_ONBOARD:
                case DONE_ONBOARD:
                    detail = getString(R.string.status_detail_onboard);
                    break;
                case NEXT_SEND_COMMAND:
                case DONE_SEND_COMMAND:
                    detail = getString(R.string.status_detail_send_command);
                    break;
                case NEXT_RECEIVE_RESULT:
                case DONE_RECEIVE_RESULT:
                    detail = getString(R.string.status_detail_result);
                    break;
                case RETRY_SEND_COMMAND:
                    detail = getString(R.string.status_detail_retry);
                    break;
               case COMPLETED:
                    detail = getString(R.string.status_detail_completed);
                    break;
            }
        }
        mTextViewDetail.setText(detail);
    }

    private void setDetailMessage(int idMessage, String message) {
        String format = getString(idMessage, message);
        mTextViewDetail.setText(format);
    }

    private void closeNotificationPage(int message) {
        stopAlarm();

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(intent);
        builder.setTicker(getString(R.string.app_name));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(message));
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        Notification notify = builder.build();
        notify.flags |= NotificationCompat.FLAG_AUTO_CANCEL;
        manager.notify(0, notify);

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        mHandlerWatchTimer.removeCallbacksAndMessages(null);

        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();

        finish();
    }

    private void startSound() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "SecondChanceAlarm");
        wakeLock.acquire(mRemainTime * 1000);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().setAttributes(lp);

        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {10, 500, 500};
        mVibrator.vibrate(pattern, 0);

        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(getApplicationContext(), uri);
            mPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAlarm() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }
        if (mPlayer != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }

    @OnClick(R.id.buttonStop)
    public void onClickStop(View view) {
        closeNotificationPage(R.string.notify_bar_stop);
    }

    private void blinkScreen() {
        if (mTextViewTitle == null) {
            return;
        }
        View view = this.findViewById(R.id.activity_alarm_nortification);
        int back, text;
        if (mBrightnessHigh) {
            back = Color.WHITE;
            text = Color.BLACK;
        } else {
            back = Color.BLACK;
            text = Color.WHITE;
        }
        if (mNotifyState == NOTIFY_STATE.NOTIFYING) {
            mBrightnessHigh = !mBrightnessHigh;
        }
        view.setBackgroundColor(back);
        mTextViewTitle.setBackgroundColor(back);
        mTextViewTitle.setTextColor(text);
        mTextViewTime.setBackgroundColor(back);
        mTextViewTime.setTextColor(text);
        mTextViewStatus.setBackgroundColor(back);
        mTextViewStatus.setTextColor(text);
        mTextViewDetail.setBackgroundColor(back);
        mTextViewDetail.setTextColor(text);
    }
}
