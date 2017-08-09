package com.kissy_software.secondchancealarm.document;

import android.os.Parcel;
import android.os.Parcelable;

public class DateMask implements Parcelable {
    private static final int DATE_MASK_SUNDAY = 0x1;
    private static final int DATE_MASK_MONDAY = 0x2;
    private static final int DATE_MASK_TUESDAY = 0x4;
    private static final int DATE_MASK_WEDNESDAY = 0x8;
    private static final int DATE_MASK_THURSDAY = 0x10;
    private static final int DATE_MASK_FRIDAY = 0x20;
    private static final int DATE_MASK_SATURDAY = 0x40;
    private static final int DATE_MASK_EXCEPT_HOLIDAY = 0x80;
    public static DateMask SUNDAY = new DateMask(DATE_MASK_SUNDAY);
    public static DateMask MONDAY = new DateMask(DATE_MASK_MONDAY);
    public static DateMask TUESDAY = new DateMask(DATE_MASK_TUESDAY);
    public static DateMask WEDNESDAY = new DateMask(DATE_MASK_WEDNESDAY);
    public static DateMask THURSDAY = new DateMask(DATE_MASK_THURSDAY);
    public static DateMask FRIDAY = new DateMask(DATE_MASK_FRIDAY);
    public static DateMask SATURDAY = new DateMask(DATE_MASK_SATURDAY);
    public static DateMask EXCEPT_HOLIDAY = new DateMask(DATE_MASK_EXCEPT_HOLIDAY);

    private int mFlag;

    private DateMask(int flag) {
        mFlag = flag;
    }

    public DateMask() {
        mFlag = 0;
    }

    public static DateMask getDefault() {
        DateMask date = new DateMask();
        date.plus(MONDAY).plus(TUESDAY).plus(WEDNESDAY).plus(THURSDAY).plus(FRIDAY).plus(EXCEPT_HOLIDAY);
        return date;
    }

    public DateMask plus(DateMask other) {
        mFlag |= other.mFlag;
        return this;
    }

    public DateMask minus(DateMask other) {
        mFlag = (mFlag & (0xffff ^ other.mFlag));
        return this;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }

    public boolean isContain(DateMask test) {
        return ((mFlag & test.mFlag) != 0);
    }

    public boolean isAvailable() {
        if ((mFlag & DATE_MASK_SUNDAY) != 0 ||
                (mFlag & DATE_MASK_MONDAY) != 0 ||
                (mFlag & DATE_MASK_TUESDAY) != 0 ||
                (mFlag & DATE_MASK_WEDNESDAY) != 0 ||
                (mFlag & DATE_MASK_THURSDAY) != 0 ||
                (mFlag & DATE_MASK_FRIDAY) != 0 ||
                (mFlag & DATE_MASK_SATURDAY) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mFlag);
    }

    public static final Parcelable.Creator<DateMask> CREATOR = new Parcelable.Creator<DateMask>() {
        public DateMask createFromParcel(Parcel in) {
            return new DateMask(in);
        }

        public DateMask[] newArray(int size) {
            return new DateMask[size];
        }
    };

    private DateMask(Parcel in) {
        mFlag = in.readInt();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof DateMask)) {
            return false;
        }
        DateMask dateOther = (DateMask)other;
        if (mFlag != dateOther.mFlag) {
            return false;
        }
        return true;

    }
}
