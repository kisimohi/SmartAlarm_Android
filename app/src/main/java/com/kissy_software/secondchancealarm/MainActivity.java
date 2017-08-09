package com.kissy_software.secondchancealarm;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.kissy_software.secondchancealarm.alarm.AlarmController;
import com.kissy_software.secondchancealarm.document.AlarmSetting;
import com.kissy_software.secondchancealarm.document.Document;
import com.kissy_software.secondchancealarm.mainpage.EditAlarmFragment;
import com.kissy_software.secondchancealarm.mainpage.MainPageFragment;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements EditAlarmFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Document.loadDocument(getApplicationContext());

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main, MainPageFragment.newInstance());
            transaction.commit();
        }
    }

    public void showAddAlarmPage(AlarmSetting alarm) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.main, EditAlarmFragment.newInstance(alarm));
        transaction.commit();
    }

    public void onAlarmEditCompleted(AlarmSetting alarm) {
        Date nextAlarm = Document.getInstance().getAlarmList().updateAlarmSetting(alarm);
        Document.saveDocument(getApplicationContext());
        Document.setAlarm(getApplicationContext());

        getSupportFragmentManager().popBackStack();
    }
}
