package com.kissy_software.secondchancealarm.document;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AlarmListUtilsTest {
    @Test
    public void simpleTest1() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 600, new DateMask().plus(DateMask.MONDAY).plus(DateMask.TUESDAY)));
        Date current = newDate("2016/12/1 5:50");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/5 6:00"));
        assertEquals(result.origin.getUuid(), "alarm1");
    }

    @Test
    public void simpleTest2() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 810, new DateMask().plus(DateMask.MONDAY).plus(DateMask.WEDNESDAY)));
        Date current = newDate("2016/12/6 5:50");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/7 8:10"));
        assertEquals(result.origin.getUuid(), "alarm1");
    }

    @Test
    public void simpleTest3() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 810, new DateMask().plus(DateMask.MONDAY).plus(DateMask.WEDNESDAY)));
        Date current = newDate("2016/12/5 8:11");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/7 8:10"));
        assertEquals(result.origin.getUuid(), "alarm1");
    }

    @Test
    public void multiTest1() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 700, new DateMask().plus(DateMask.WEDNESDAY).plus(DateMask.FRIDAY)));
        list.add(new AlarmSetting("alarm2", 810, new DateMask().plus(DateMask.WEDNESDAY)));
        Date current = newDate("2016/12/7 7:10");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/7 8:10"));
        assertEquals(result.origin.getUuid(), "alarm2");
    }

    @Test
    public void multiTest2() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 700, new DateMask().plus(DateMask.THURSDAY).plus(DateMask.SATURDAY)));
        list.add(new AlarmSetting("alarm2", 810, new DateMask().plus(DateMask.THURSDAY)));
        Date current = newDate("2016/12/1 8:20");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/3 7:00"));
        assertEquals(result.origin.getUuid(), "alarm1");
    }

    @Test
    public void molidayTest1() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 700, new DateMask().plus(DateMask.MONDAY).plus(DateMask.TUESDAY).plus(DateMask.WEDNESDAY).plus(DateMask.THURSDAY).plus(DateMask.FRIDAY).plus(DateMask.EXCEPT_HOLIDAY)));
        Date current = newDate("2016/12/22 8:20");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/26 7:00"));
        assertEquals(result.origin.getUuid(), "alarm1");
    }

    @Test
    public void molidayTest2() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 700, new DateMask().plus(DateMask.MONDAY).plus(DateMask.TUESDAY).plus(DateMask.WEDNESDAY).plus(DateMask.THURSDAY).plus(DateMask.FRIDAY)));
        Date current = newDate("2016/12/22 8:20");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/23 7:00"));
        assertEquals(result.origin.getUuid(), "alarm1");
    }

    @Test
    public void molidayTest3() throws Exception {
        List<AlarmSetting> list = new ArrayList<>();
        list.add(new AlarmSetting("alarm1", 700, new DateMask().plus(DateMask.MONDAY).plus(DateMask.TUESDAY).plus(DateMask.WEDNESDAY).plus(DateMask.THURSDAY).plus(DateMask.FRIDAY).plus(DateMask.EXCEPT_HOLIDAY)));
        list.add(new AlarmSetting("alarm2", 615, new DateMask().plus(DateMask.MONDAY).plus(DateMask.FRIDAY).plus(DateMask.EXCEPT_HOLIDAY)));
        Date current = newDate("2016/12/22 8:20");
        AlarmListUtils.AlarmActualTime result = AlarmListUtils.getEarliestAlarm(current, list);

        assertEquals(result.targetDate, newDate("2016/12/26 6:15"));
        assertEquals(result.origin.getUuid(), "alarm2");
    }

    private Date newDate(String data) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = format.parse(data);
        return date;
    }
}
