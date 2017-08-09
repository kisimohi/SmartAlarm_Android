package com.kissy_software.secondchancealarm.document;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmList implements Parcelable {
    private List<AlarmSetting> mItemList = new ArrayList<AlarmSetting>();

    private Date mNextAlarmDate;

    public AlarmList() {
    }

    @NonNull
    public List<AlarmSetting> getItemList() {
        return mItemList;
    }

    public void getItemList(List<AlarmSetting> itemList) {
        mItemList = itemList;
    }

    @Nullable
    public Date getNextAlarmDate() {
        return mNextAlarmDate;
    }

    public void setDate(Date date) {
        mNextAlarmDate = date;
    }

    public void addItem(AlarmSetting alarm) {
        mItemList.add(alarm);
    }

    @Nullable
    public Date updateAlarmSetting(AlarmSetting alarm) {
        for (int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i).getUuid().equals(alarm.getUuid())) {
                mItemList.set(i, alarm);
                updateNextAlarm();
                return mNextAlarmDate;
            }
        }
        mItemList.add(alarm);
        updateNextAlarm();
        return mNextAlarmDate;
    }

    public Date updateNextAlarm() {
        Date current = new Date();
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, mItemList);
        if (result != null) {
            mNextAlarmDate = result.targetDate;
        } else {
            mNextAlarmDate = null;
        }
        return mNextAlarmDate;
    }

    public void removeItem(AlarmSetting alarm) {
        mItemList.remove(alarm);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mItemList.size());
        for (AlarmSetting setting : mItemList) {
            out.writeParcelable(setting, flags);
        }
    }

    public static final Parcelable.Creator<AlarmList> CREATOR = new Parcelable.Creator<AlarmList>() {
        public AlarmList createFromParcel(Parcel in) {
            return new AlarmList(in);
        }

        public AlarmList[] newArray(int size) {
            return new AlarmList[size];
        }
    };

    private AlarmList(Parcel in) {
        int size = in.readInt();
        mItemList.clear();
        for (int i = 0; i < size; i++) {
            AlarmSetting setting = in.readParcelable(AlarmSetting.class.getClassLoader());
            mItemList.add(setting);
        }
    }
}