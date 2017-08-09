package com.kissy_software.secondchancealarm.mainpage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kissy_software.secondchancealarm.R;
import com.kissy_software.secondchancealarm.document.DateMask;
import com.kissy_software.secondchancealarm.document.AlarmSetting;

import java.util.List;

public class AlarmSettingViewAdapter extends ArrayAdapter<AlarmSetting> {

    public AlarmSettingViewAdapter(Context context, List<AlarmSetting> itemList) {
        super(context, 0, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_item_alarmsetting, parent, false);
        } else {
            view = convertView;
        }

        AlarmSetting item = getItem(position);

        TextView textViewHHMM = (TextView) view.findViewById(R.id.textViewHHMM);
        int hhmm = item.getAlarmTimeHHMM();
        String strHHMM = String.format("%d:%02d", hhmm / 100, hhmm % 100);
        textViewHHMM.setText(strHHMM);

        StringBuilder sb = new StringBuilder();
        DateMask dateMask = item.getDateMask();
        setDayStyle(view, R.id.textViewSunday, dateMask.isContain(DateMask.SUNDAY));
        setDayStyle(view, R.id.textViewMonday, dateMask.isContain(DateMask.MONDAY));
        setDayStyle(view, R.id.textViewTuesday, dateMask.isContain(DateMask.TUESDAY));
        setDayStyle(view, R.id.textViewWednesday, dateMask.isContain(DateMask.WEDNESDAY));
        setDayStyle(view, R.id.textViewThursday, dateMask.isContain(DateMask.THURSDAY));
        setDayStyle(view, R.id.textViewFriday, dateMask.isContain(DateMask.FRIDAY));
        setDayStyle(view, R.id.textViewSaturday, dateMask.isContain(DateMask.SATURDAY));

        TextView textViewHoliday = (TextView) view.findViewById(R.id.textViewHoliday);
        if (dateMask.isContain(DateMask.EXCEPT_HOLIDAY)) {
            textViewHoliday.setText(R.string.date_except_holiday);
        } else {
            textViewHoliday.setText(R.string.date_include_holiday);
        }
        textViewHoliday.setTextColor(Color.rgb(32, 128, 32));

        return view;
    }

    private void setDayStyle(View rootView, int idTextView, boolean enabled) {
        TextView textView = (TextView)rootView.findViewById(idTextView);
        if (enabled) {
            textView.setTextColor(Color.rgb(32, 32, 128));
        } else {
            textView.setTextColor(Color.rgb(160, 160, 160));
        }
    }

}
