package com.kissy_software.secondchancealarm.document;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmListUtils {
    @Nullable
    public static AlarmActualTime getEarliestAlarm(Date current, List<AlarmSetting> list) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.MINUTE, 1);
        Date base = calendar.getTime();

        AlarmActualTime earliest = null;
        for (AlarmSetting alarm : list) {
            AlarmActualTime actualTime = getEarliestAlarm(base, alarm);
            if (earliest == null) {
                earliest = actualTime;
            } else if (actualTime.targetDate.getTime() < earliest.targetDate.getTime()) {
                earliest = actualTime;
            }
        }
        return earliest;
    }

    private static AlarmActualTime getEarliestAlarm(Date current, AlarmSetting alarm) {
        Calendar target = Calendar.getInstance();
        target.setTime(current);
        target.set(Calendar.HOUR_OF_DAY, alarm.getAlarmTimeHHMM() / 100);
        target.set(Calendar.MINUTE, alarm.getAlarmTimeHHMM() % 100);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        while (true) {
            boolean today = false;
            switch (target.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    today = alarm.getDateMask().isContain(DateMask.SUNDAY);
                    break;
                case Calendar.MONDAY:
                    today = alarm.getDateMask().isContain(DateMask.MONDAY);
                    break;
                case Calendar.TUESDAY:
                    today = alarm.getDateMask().isContain(DateMask.TUESDAY);
                    break;
                case Calendar.WEDNESDAY:
                    today = alarm.getDateMask().isContain(DateMask.WEDNESDAY);
                    break;
                case Calendar.THURSDAY:
                    today = alarm.getDateMask().isContain(DateMask.THURSDAY);
                    break;
                case Calendar.FRIDAY:
                    today = alarm.getDateMask().isContain(DateMask.FRIDAY);
                    break;
                case Calendar.SATURDAY:
                    today = alarm.getDateMask().isContain(DateMask.SATURDAY);
                    break;
            }
            if (alarm.getDateMask().isContain(DateMask.EXCEPT_HOLIDAY) && isNationalHoliday(target.getTime())) {
                today = false;
            }
            if (target.getTimeInMillis() < current.getTime()) {
                today = false;
            }
            if (today) {
                return new AlarmActualTime(target.getTime(), alarm);
            }
            target.add(Calendar.DATE, 1);
        }
    }

    private static boolean isNationalHoliday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int yyyymmdd = cal.get(Calendar.YEAR) * 10000 + cal.get(Calendar.MONTH) * 100 + cal.get(Calendar.DATE);
        boolean result = HOLIDAY_TABLE.contains(yyyymmdd);
        return result;
    }

    public static class AlarmActualTime {
        Date targetDate;
        AlarmSetting origin;

        public AlarmActualTime(Date targetDate, AlarmSetting origin) {
            this.targetDate = targetDate;
            this.origin = origin;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (!(other instanceof  AlarmActualTime)) {
                return false;
            }
            AlarmActualTime alarmOther = (AlarmActualTime)other;
            if (targetDate == null && alarmOther.targetDate == null) {
            } else if (targetDate == null && alarmOther.targetDate != null) {
                return false;
            } else if (targetDate != null && alarmOther.targetDate == null) {
                return false;
            } else if (targetDate.getTime() != alarmOther.targetDate.getTime()) {
                return false;
            }
            if (origin == null || alarmOther.origin == null) {
            } else if (origin == null && alarmOther.origin != null) {
                return false;
            } else if (origin != null && alarmOther.origin == null) {
                return false;
            } else if (!origin.equals(alarmOther.origin)) {
                return false;
            }
            return true;
        }
    }

    private static final Set<Integer> HOLIDAY_TABLE = new HashSet<Integer>();
    static {
        // 2013年
        HOLIDAY_TABLE.add(20130101);        // 元日
        HOLIDAY_TABLE.add(20130114);        // 成人の日
        HOLIDAY_TABLE.add(20130211);        // 建国記念の日
        HOLIDAY_TABLE.add(20130320);        // 春分の日
        HOLIDAY_TABLE.add(20130429);        // 昭和の日
        HOLIDAY_TABLE.add(20130503);        // 憲法記念日
        HOLIDAY_TABLE.add(20130504);        // みどりの日
        HOLIDAY_TABLE.add(20130505);        // こどもの日
        HOLIDAY_TABLE.add(20130506);        // 振替休日
        HOLIDAY_TABLE.add(20130715);        // 海の日
        HOLIDAY_TABLE.add(20130916);        // 敬老の日
        HOLIDAY_TABLE.add(20130923);        // 秋分の日
        HOLIDAY_TABLE.add(20131014);        // 体育の日
        HOLIDAY_TABLE.add(20131103);        // 文化の日
        HOLIDAY_TABLE.add(20131104);        // 振替休日
        HOLIDAY_TABLE.add(20131123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20131223);        // 天皇誕生日
    
        // 2014年
        HOLIDAY_TABLE.add(20140101);        // 元日
        HOLIDAY_TABLE.add(20140113);        // 成人の日
        HOLIDAY_TABLE.add(20140211);        // 建国記念の日
        HOLIDAY_TABLE.add(20140321);        // 春分の日
        HOLIDAY_TABLE.add(20140429);        // 昭和の日
        HOLIDAY_TABLE.add(20140503);        // 憲法記念日
        HOLIDAY_TABLE.add(20140504);        // みどりの日
        HOLIDAY_TABLE.add(20140505);        // こどもの日
        HOLIDAY_TABLE.add(20140506);        // 振替休日
        HOLIDAY_TABLE.add(20140721);        // 海の日
        HOLIDAY_TABLE.add(20140915);        // 敬老の日
        HOLIDAY_TABLE.add(20140923);        // 秋分の日
        HOLIDAY_TABLE.add(20141013);        // 体育の日
        HOLIDAY_TABLE.add(20141103);        // 文化の日
        HOLIDAY_TABLE.add(20141123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20141124);        // 振替休日
        HOLIDAY_TABLE.add(20141223);        // 天皇誕生日

        // 2015年
        HOLIDAY_TABLE.add(20150101);        // 元日
        HOLIDAY_TABLE.add(20150112);        // 成人の日
        HOLIDAY_TABLE.add(20150211);        // 建国記念の日
        HOLIDAY_TABLE.add(20150321);        // 春分の日
        HOLIDAY_TABLE.add(20150429);        // 昭和の日
        HOLIDAY_TABLE.add(20150503);        // 憲法記念日
        HOLIDAY_TABLE.add(20150504);        // 振替休日
        HOLIDAY_TABLE.add(20150504);        // みどりの日
        HOLIDAY_TABLE.add(20150505);        // こどもの日
        HOLIDAY_TABLE.add(20150720);        // 海の日
        HOLIDAY_TABLE.add(20150921);        // 敬老の日
        HOLIDAY_TABLE.add(20150923);        // 秋分の日
        HOLIDAY_TABLE.add(20151012);        // 体育の日
        HOLIDAY_TABLE.add(20151103);        // 文化の日
        HOLIDAY_TABLE.add(20151123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20151223);        // 天皇誕生日

        // 2016年
        HOLIDAY_TABLE.add(20160101);        // 元日
        HOLIDAY_TABLE.add(20160111);        // 成人の日
        HOLIDAY_TABLE.add(20160211);        // 建国記念の日
        HOLIDAY_TABLE.add(20160320);        // 春分の日
        HOLIDAY_TABLE.add(20160321);        // 振替休日
        HOLIDAY_TABLE.add(20160429);        // 昭和の日
        HOLIDAY_TABLE.add(20160503);        // 憲法記念日
        HOLIDAY_TABLE.add(20160504);        // みどりの日
        HOLIDAY_TABLE.add(20160505);        // こどもの日
        HOLIDAY_TABLE.add(20160718);        // 海の日
        HOLIDAY_TABLE.add(20160919);        // 敬老の日
        HOLIDAY_TABLE.add(20160922);        // 秋分の日
        HOLIDAY_TABLE.add(20161010);        // 体育の日
        HOLIDAY_TABLE.add(20161103);        // 文化の日
        HOLIDAY_TABLE.add(20161123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20161223);        // 天皇誕生日

        // 2017年
        HOLIDAY_TABLE.add(20170101);        // 元日
        HOLIDAY_TABLE.add(20170102);        // 振替休日
        HOLIDAY_TABLE.add(20170109);        // 成人の日
        HOLIDAY_TABLE.add(20170211);        // 建国記念の日
        HOLIDAY_TABLE.add(20170320);        // 春分の日
        HOLIDAY_TABLE.add(20170429);        // 昭和の日
        HOLIDAY_TABLE.add(20170503);        // 憲法記念日
        HOLIDAY_TABLE.add(20170504);        // みどりの日
        HOLIDAY_TABLE.add(20170505);        // こどもの日
        HOLIDAY_TABLE.add(20170717);        // 海の日
        HOLIDAY_TABLE.add(20170811);        // 山の日
        HOLIDAY_TABLE.add(20170918);        // 敬老の日
        HOLIDAY_TABLE.add(20170923);        // 秋分の日
        HOLIDAY_TABLE.add(20171009);        // 体育の日
        HOLIDAY_TABLE.add(20171103);        // 文化の日
        HOLIDAY_TABLE.add(20171123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20171223);        // 天皇誕生日

        // 2018年
        HOLIDAY_TABLE.add(20180101);        // 元日
        HOLIDAY_TABLE.add(20180108);        // 成人の日
        HOLIDAY_TABLE.add(20180211);        // 建国記念の日
        HOLIDAY_TABLE.add(20180212);        // 振替休日
        HOLIDAY_TABLE.add(20180321);        // 春分の日
        HOLIDAY_TABLE.add(20180429);        // 昭和の日
        HOLIDAY_TABLE.add(20180430);        // 振替休日
        HOLIDAY_TABLE.add(20180503);        // 憲法記念日
        HOLIDAY_TABLE.add(20180504);        // みどりの日
        HOLIDAY_TABLE.add(20180505);        // こどもの日
        HOLIDAY_TABLE.add(20180716);        // 海の日
        HOLIDAY_TABLE.add(20180811);        // 山の日
        HOLIDAY_TABLE.add(20180917);        // 敬老の日
        HOLIDAY_TABLE.add(20180923);        // 秋分の日
        HOLIDAY_TABLE.add(20180924);        // 振替休日
        HOLIDAY_TABLE.add(20181008);        // 体育の日
        HOLIDAY_TABLE.add(20181103);        // 文化の日
        HOLIDAY_TABLE.add(20181123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20181223);        // 天皇誕生日
        HOLIDAY_TABLE.add(20181224);        // 振替休日

        // 2019年
        HOLIDAY_TABLE.add(20190101);        // 元日
        HOLIDAY_TABLE.add(20190114);        // 成人の日
        HOLIDAY_TABLE.add(20190211);        // 建国記念の日
        HOLIDAY_TABLE.add(20190321);        // 春分の日
        HOLIDAY_TABLE.add(20190429);        // 昭和の日
        HOLIDAY_TABLE.add(20190503);        // 憲法記念日
        HOLIDAY_TABLE.add(20190504);        // みどりの日
        HOLIDAY_TABLE.add(20190505);        // こどもの日
        HOLIDAY_TABLE.add(20190506);        // 振替休日
        HOLIDAY_TABLE.add(20190715);        // 海の日
        HOLIDAY_TABLE.add(20190811);        // 山の日
        HOLIDAY_TABLE.add(20190812);        // 振替休日
        HOLIDAY_TABLE.add(20190916);        // 敬老の日
        HOLIDAY_TABLE.add(20190923);        // 秋分の日
        HOLIDAY_TABLE.add(20191014);        // 体育の日
        HOLIDAY_TABLE.add(20191103);        // 文化の日
        HOLIDAY_TABLE.add(20191104);        // 振替休日
        HOLIDAY_TABLE.add(20191123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20191223);        // 天皇誕生日

        // 2020年
        HOLIDAY_TABLE.add(20200101);        // 元日
        HOLIDAY_TABLE.add(20200113);        // 成人の日
        HOLIDAY_TABLE.add(20200211);        // 建国記念の日
        HOLIDAY_TABLE.add(20200320);        // 春分の日
        HOLIDAY_TABLE.add(20200429);        // 昭和の日
        HOLIDAY_TABLE.add(20200503);        // 憲法記念日
        HOLIDAY_TABLE.add(20200504);        // みどりの日
        HOLIDAY_TABLE.add(20200505);        // こどもの日
        HOLIDAY_TABLE.add(20200506);        // 振替休日
        HOLIDAY_TABLE.add(20200720);        // 海の日
        HOLIDAY_TABLE.add(20200811);        // 山の日
        HOLIDAY_TABLE.add(20200921);        // 敬老の日
        HOLIDAY_TABLE.add(20200922);        // 秋分の日
        HOLIDAY_TABLE.add(20201012);        // 体育の日
        HOLIDAY_TABLE.add(20201103);        // 文化の日
        HOLIDAY_TABLE.add(20201123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20201223);        // 天皇誕生日

        // 2021年
        HOLIDAY_TABLE.add(20210101);        // 元日
        HOLIDAY_TABLE.add(20210111);        // 成人の日
        HOLIDAY_TABLE.add(20210211);        // 建国記念の日
        HOLIDAY_TABLE.add(20210320);        // 春分の日
        HOLIDAY_TABLE.add(20210429);        // 昭和の日
        HOLIDAY_TABLE.add(20210503);        // 憲法記念日
        HOLIDAY_TABLE.add(20210504);        // みどりの日
        HOLIDAY_TABLE.add(20210505);        // こどもの日
        HOLIDAY_TABLE.add(20210719);        // 海の日
        HOLIDAY_TABLE.add(20210811);        // 山の日
        HOLIDAY_TABLE.add(20210920);        // 敬老の日
        HOLIDAY_TABLE.add(20210923);        // 秋分の日
        HOLIDAY_TABLE.add(20211011);        // 体育の日
        HOLIDAY_TABLE.add(20211103);        // 文化の日
        HOLIDAY_TABLE.add(20211123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20211223);        // 天皇誕生日

        // 2022年
        HOLIDAY_TABLE.add(20220101);        // 元日
        HOLIDAY_TABLE.add(20220110);        // 成人の日
        HOLIDAY_TABLE.add(20220211);        // 建国記念の日
        HOLIDAY_TABLE.add(20220321);        // 春分の日
        HOLIDAY_TABLE.add(20220429);        // 昭和の日
        HOLIDAY_TABLE.add(20220503);        // 憲法記念日
        HOLIDAY_TABLE.add(20220504);        // みどりの日
        HOLIDAY_TABLE.add(20220505);        // こどもの日
        HOLIDAY_TABLE.add(20220718);        // 海の日
        HOLIDAY_TABLE.add(20220811);        // 山の日
        HOLIDAY_TABLE.add(20220919);        // 敬老の日
        HOLIDAY_TABLE.add(20220923);        // 秋分の日
        HOLIDAY_TABLE.add(20221010);        // 体育の日
        HOLIDAY_TABLE.add(20221103);        // 文化の日
        HOLIDAY_TABLE.add(20221123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20221223);        // 天皇誕生日

        // 2023年
        HOLIDAY_TABLE.add(20230101);        // 元日
        HOLIDAY_TABLE.add(20230102);        // 振替休日
        HOLIDAY_TABLE.add(20230109);        // 成人の日
        HOLIDAY_TABLE.add(20230211);        // 建国記念の日
        HOLIDAY_TABLE.add(20230321);        // 春分の日
        HOLIDAY_TABLE.add(20230429);        // 昭和の日
        HOLIDAY_TABLE.add(20230503);        // 憲法記念日
        HOLIDAY_TABLE.add(20230504);        // みどりの日
        HOLIDAY_TABLE.add(20230505);        // こどもの日
        HOLIDAY_TABLE.add(20230717);        // 海の日
        HOLIDAY_TABLE.add(20230811);        // 山の日
        HOLIDAY_TABLE.add(20230918);        // 敬老の日
        HOLIDAY_TABLE.add(20230923);        // 秋分の日
        HOLIDAY_TABLE.add(20231009);        // 体育の日
        HOLIDAY_TABLE.add(20231103);        // 文化の日
        HOLIDAY_TABLE.add(20231123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20231223);        // 天皇誕生日

        // 2024年
        HOLIDAY_TABLE.add(20240101);        // 元日
        HOLIDAY_TABLE.add(20240108);        // 成人の日
        HOLIDAY_TABLE.add(20240211);        // 建国記念の日
        HOLIDAY_TABLE.add(20240212);        // 振替休日
        HOLIDAY_TABLE.add(20240320);        // 春分の日
        HOLIDAY_TABLE.add(20240429);        // 昭和の日
        HOLIDAY_TABLE.add(20240503);        // 憲法記念日
        HOLIDAY_TABLE.add(20240504);        // みどりの日
        HOLIDAY_TABLE.add(20240505);        // こどもの日
        HOLIDAY_TABLE.add(20240506);        // 振替休日
        HOLIDAY_TABLE.add(20240715);        // 海の日
        HOLIDAY_TABLE.add(20240811);        // 山の日
        HOLIDAY_TABLE.add(20240812);        // 振替休日
        HOLIDAY_TABLE.add(20240916);        // 敬老の日
        HOLIDAY_TABLE.add(20240922);        // 秋分の日
        HOLIDAY_TABLE.add(20240923);        // 振替休日
        HOLIDAY_TABLE.add(20241014);        // 体育の日
        HOLIDAY_TABLE.add(20241103);        // 文化の日
        HOLIDAY_TABLE.add(20241104);        // 振替休日
        HOLIDAY_TABLE.add(20241123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20241223);        // 天皇誕生日

        // 2025年
        HOLIDAY_TABLE.add(20250101);        // 元日
        HOLIDAY_TABLE.add(20250113);        // 成人の日
        HOLIDAY_TABLE.add(20250211);        // 建国記念の日
        HOLIDAY_TABLE.add(20250320);        // 春分の日
        HOLIDAY_TABLE.add(20250429);        // 昭和の日
        HOLIDAY_TABLE.add(20250503);        // 憲法記念日
        HOLIDAY_TABLE.add(20250504);        // みどりの日
        HOLIDAY_TABLE.add(20250505);        // こどもの日
        HOLIDAY_TABLE.add(20250506);        // 振替休日
        HOLIDAY_TABLE.add(20250721);        // 海の日
        HOLIDAY_TABLE.add(20250811);        // 山の日
        HOLIDAY_TABLE.add(20250915);        // 敬老の日
        HOLIDAY_TABLE.add(20250923);        // 秋分の日
        HOLIDAY_TABLE.add(20251013);        // 体育の日
        HOLIDAY_TABLE.add(20251103);        // 文化の日
        HOLIDAY_TABLE.add(20251123);        // 勤労感謝の日
        HOLIDAY_TABLE.add(20251124);        // 振替休日
        HOLIDAY_TABLE.add(20251223);        // 天皇誕生日
    };
}
