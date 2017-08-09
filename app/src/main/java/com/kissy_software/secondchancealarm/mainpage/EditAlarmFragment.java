package com.kissy_software.secondchancealarm.mainpage;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kissy_software.secondchancealarm.MainActivity;
import com.kissy_software.secondchancealarm.R;
import com.kissy_software.secondchancealarm.document.AlarmSetting;
import com.kissy_software.secondchancealarm.document.DateMask;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class EditAlarmFragment extends Fragment {
    private static final String ARG_PARAM_ALARM = "alarm1";

    private AlarmSetting mAlarm;

    private OnFragmentInteractionListener mListener;
    private Unbinder mButterknifeUnbunder;

    @BindView(R.id.timePicker)
    TimePicker mTimePicker;
    @BindView(R.id.toggleButtonSunday)
    ToggleButton mToggleButtonSunday;
    @BindView(R.id.toggleButtonMonday) ToggleButton mToggleButtonMonday;
    @BindView(R.id.toggleButtonTuesday) ToggleButton mToggleButtonTuesday;
    @BindView(R.id.toggleButtonWednesday) ToggleButton mToggleButtonWednesday;
    @BindView(R.id.toggleButtonThursday) ToggleButton mToggleButtonThursday;
    @BindView(R.id.toggleButtonFriday) ToggleButton mToggleButtonFriday;
    @BindView(R.id.toggleButtonSaturday) ToggleButton mToggleButtonSaturday;
    @BindView(R.id.checkBoxExceptHoliday)
    CheckBox mCheckBoxExceptHoliday;

    public EditAlarmFragment() {
    }

    public static EditAlarmFragment newInstance(AlarmSetting alarm) {
        EditAlarmFragment fragment = new EditAlarmFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_ALARM,alarm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlarm = getArguments().getParcelable(ARG_PARAM_ALARM);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_alarm, container, false);
        mButterknifeUnbunder = ButterKnife.bind(this, view);

        mTimePicker.setCurrentHour(mAlarm.getAlarmTimeHHMM() / 100);
        mTimePicker.setCurrentMinute(mAlarm.getAlarmTimeHHMM() % 100);
        mTimePicker.setIs24HourView(true);
        mToggleButtonSunday.setChecked(mAlarm.getDateMask().isContain(DateMask.SUNDAY));
        mToggleButtonMonday.setChecked(mAlarm.getDateMask().isContain(DateMask.MONDAY));
        mToggleButtonTuesday.setChecked(mAlarm.getDateMask().isContain(DateMask.TUESDAY));
        mToggleButtonWednesday.setChecked(mAlarm.getDateMask().isContain(DateMask.WEDNESDAY));
        mToggleButtonThursday.setChecked(mAlarm.getDateMask().isContain(DateMask.THURSDAY));
        mToggleButtonFriday.setChecked(mAlarm.getDateMask().isContain(DateMask.FRIDAY));
        mToggleButtonSaturday.setChecked(mAlarm.getDateMask().isContain(DateMask.SATURDAY));
        mCheckBoxExceptHoliday.setChecked(mAlarm.getDateMask().isContain(DateMask.EXCEPT_HOLIDAY));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mButterknifeUnbunder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.buttonOk)
    void onClickOk(View button) {
        String uuid = mAlarm.getUuid();
        int hhmm = mTimePicker.getCurrentHour() * 100 + mTimePicker.getCurrentMinute();

        DateMask mask = new DateMask();
        if (mToggleButtonSunday.isChecked()) { mask.plus(DateMask.SUNDAY); }
        if (mToggleButtonMonday.isChecked()) { mask.plus(DateMask.MONDAY); }
        if (mToggleButtonTuesday.isChecked()) { mask.plus(DateMask.TUESDAY); }
        if (mToggleButtonWednesday.isChecked()) { mask.plus(DateMask.WEDNESDAY); }
        if (mToggleButtonThursday.isChecked()) { mask.plus(DateMask.THURSDAY); }
        if (mToggleButtonFriday.isChecked()) { mask.plus(DateMask.FRIDAY); }
        if (mToggleButtonSaturday.isChecked()) { mask.plus(DateMask.SATURDAY); }
        if (mCheckBoxExceptHoliday.isChecked()) { mask.plus(DateMask.EXCEPT_HOLIDAY); }

        if (!mask.isAvailable()) {
            Toast.makeText(getContext(), R.string.error_no_day_of_week, Toast.LENGTH_LONG).show();
            return;
        }
        AlarmSetting alarm = new AlarmSetting(uuid, hhmm, mask);
        ((MainActivity)getActivity()).onAlarmEditCompleted(alarm);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAlarmEditCompleted(mAlarm);
        }
    }

    public interface OnFragmentInteractionListener {
        void onAlarmEditCompleted(AlarmSetting alarm);
    }
}
