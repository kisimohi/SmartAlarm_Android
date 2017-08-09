package com.kissy_software.secondchancealarm.document;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmSetting implements Parcelable {
    private String mUuid;
    private int mAlarmTimeHHMM;
    private DateMask mDateMask;

    public AlarmSetting(String uuid, int alarmTimeHHMM, DateMask dateMask) {
        this.mUuid = uuid;
        this.mAlarmTimeHHMM = alarmTimeHHMM;
        this.mDateMask = dateMask;
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    public int getAlarmTimeHHMM() {
        return this.mAlarmTimeHHMM;
    }

    public void setAlarmTimeHHMM(int alarmTimeHHMM) {
        mAlarmTimeHHMM = alarmTimeHHMM;
    }

    public DateMask getDateMask() {
        return this.mDateMask;
    }

    public void setDateMask(DateMask dateMask) {
        mDateMask = dateMask;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mAlarmTimeHHMM);
        out.writeParcelable(mDateMask, flags);
    }

    public static final Parcelable.Creator<AlarmSetting> CREATOR = new Parcelable.Creator<AlarmSetting>() {
        public AlarmSetting createFromParcel(Parcel in) {
            return new AlarmSetting(in);
        }

        public AlarmSetting[] newArray(int size) {
            return new AlarmSetting[size];
        }
    };

    private AlarmSetting(Parcel in) {
        mAlarmTimeHHMM = in.readInt();
        mDateMask = in.readParcelable(DateMask.class.getClassLoader());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof AlarmSetting)) {
            return false;
        }
        AlarmSetting alarmOther = (AlarmSetting)other;
        if (mUuid == null && alarmOther.mUuid == null) {
        } else if (mUuid == null && alarmOther.mUuid != null) {
            return false;
        } else if (mUuid != null && alarmOther.mUuid == null) {
            return false;
        } else if (!mUuid.equals(alarmOther.mUuid)) {
            return false;
        }
        if (mAlarmTimeHHMM != alarmOther.mAlarmTimeHHMM) {
            return false;
        }
        if (mDateMask == null && alarmOther.mDateMask == null) {
        } else if (mDateMask == null && alarmOther.mDateMask != null) {
            return false;
        } else if (mDateMask != null && alarmOther.mDateMask == null) {
            return false;
        } else if (!mDateMask.equals(alarmOther.mDateMask)) {
            return false;
        }
        return true;
    }
}
