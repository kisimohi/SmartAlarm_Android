package com.kissy_software.secondchancealarm.document;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kissy_software.secondchancealarm.R;
import com.kissy_software.secondchancealarm.alarm.AlarmController;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Document implements Parcelable {
    private static String SHARED_PREFERENCE_NAME = "MainPreferences";
    private static String SHARED_PREFERENCE_KEY_DOCUMENT = "Document";

    private AlarmList mAlarmList = new AlarmList();

    private static Document sDocument;

    public static Document getInstance() {
        return sDocument;
    }

    public static void loadDocument(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String json = sp.getString(SHARED_PREFERENCE_KEY_DOCUMENT, null);
        if (json == null) {
            sDocument = new Document();
        } else {
            Gson gson = new Gson();
            sDocument = gson.fromJson(json, Document.class);
        }

        Date nextAlarm = Document.getInstance().getAlarmList().updateNextAlarm();
        if (nextAlarm == null) {
            AlarmController.stopAlarm(context);
        } else {
            AlarmController.setAlarm(context, nextAlarm);
        }
    }

    public static void saveDocument(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Document.getInstance());
        editor.putString(SHARED_PREFERENCE_KEY_DOCUMENT, json);
        editor.apply();
    }

    public static void setAlarm(Context context) {
        Date nextDate = sDocument.mAlarmList.getNextAlarmDate();
        if (nextDate == null) {
            AlarmController.stopAlarm(context);
        } else {
            AlarmController.setAlarm(context, nextDate);
        }

        SimpleDateFormat sf = new SimpleDateFormat(context.getString(R.string.message_next_alarm));
        String message = sf.format(nextDate);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public Document() {
    }

    public AlarmList getAlarmList() {
        return mAlarmList;
    }

    public void setAlarmList(AlarmList alarm) {
        mAlarmList = alarm;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mAlarmList, flags);
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    private Document(Parcel in) {
        mAlarmList = in.readParcelable(AlarmList.class.getClassLoader());
    }
}