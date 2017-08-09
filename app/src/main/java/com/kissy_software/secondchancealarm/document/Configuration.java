package com.kissy_software.secondchancealarm.document;

/**
 * Created by hideaki.kishimoto on 2016/12/22.
 */

public class Configuration {
    static Configuration sConfig = new Configuration();

    private String mUserName;
    private String mPassword;
    private String mThingVendorID;
    private String mThingPassword;
    private int mAlarmRemainTime;
    private int mAlarmRingTime;

    public static Configuration getInstance() {
        return sConfig;
    }

    private Configuration() {
        mUserName = "aaaa";
        mPassword = "bbbb";
        mThingVendorID = "1111";
        mThingPassword = "2222";
        mAlarmRemainTime = 30;
        mAlarmRingTime = 30;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getThingVendorID() {
        return mThingVendorID;
    }

    public String getThingPassword() {
        return mThingPassword;
    }

    public int getAlarmRemainTime() {
        return mAlarmRemainTime;
    }

    public int getAlarmRingTime() {
        return mAlarmRingTime;
    }
}
