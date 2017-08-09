package com.kissy_software.secondchancealarm.document;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ParcelableInt implements Parcelable {
    private int mValue;

    public ParcelableInt(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mValue);
    }

    public static final Creator<ParcelableInt> CREATOR = new Creator<ParcelableInt>() {
        public ParcelableInt createFromParcel(Parcel in) {
            return new ParcelableInt(in);
        }

        public ParcelableInt[] newArray(int size) {
            return new ParcelableInt[size];
        }
    };

    private ParcelableInt(Parcel in) {
        mValue = in.readInt();
    }
}
