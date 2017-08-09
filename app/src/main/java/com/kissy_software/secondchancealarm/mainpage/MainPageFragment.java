package com.kissy_software.secondchancealarm.mainpage;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.kissy_software.secondchancealarm.MainActivity;
import com.kissy_software.secondchancealarm.R;
import com.kissy_software.secondchancealarm.document.AlarmList;
import com.kissy_software.secondchancealarm.document.AlarmSetting;
import com.kissy_software.secondchancealarm.document.DateMask;
import com.kissy_software.secondchancealarm.document.Document;
import com.kissy_software.secondchancealarm.document.ParcelableInt;
import com.kissy_software.secondchancealarm.ui.GuiUtils;
import com.kissy_software.secondchancealarm.ui.YesNoDialogFragment;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.Unbinder;

public class MainPageFragment extends Fragment {
    /** 削除確認ダイアログのリクエストコード */
    private static final int REQUEST_CODE_CONFIRM_DELETE = 100;

    public static final String SAVE_LIST_POSITION = "saveListPosition";

    private Unbinder mButterknifeUnbunder;
    private AlarmSettingViewAdapter mAdapter;
    private int mListPosition;

    @BindView(R.id.textViewNextAlarm)
    TextView mTextViewNextAlarm;

    @BindView(R.id.list_alarm)
    AbsListView mListView;

    public MainPageFragment() {
    }

    public static MainPageFragment newInstance() {
        MainPageFragment fragment = new MainPageFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        mButterknifeUnbunder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            mListPosition = savedInstanceState.getInt(SAVE_LIST_POSITION);
        } else {
            mListPosition = 0;
        }

        List<AlarmSetting> itemList = Document.getInstance().getAlarmList().getItemList();
        mAdapter = new AlarmSettingViewAdapter(getActivity(), itemList);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mListPosition);

        Date nextAlarm = Document.getInstance().getAlarmList().getNextAlarmDate();
        mTextViewNextAlarm.setText(getTextNextAlarm(nextAlarm));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Date nextAlarm = Document.getInstance().getAlarmList().getNextAlarmDate();
        mTextViewNextAlarm.setText(getTextNextAlarm(nextAlarm));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListView != null) {
            mListPosition = mListView.getFirstVisiblePosition();
            outState.putInt(SAVE_LIST_POSITION, mListPosition);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mButterknifeUnbunder.unbind();
    }

    @OnClick(R.id.buttonAddAlarm)
    void onClickAddAlarm(View button) {
        String uuid = UUID.randomUUID().toString();
        int hhmm = 630;
        DateMask mask = DateMask.getDefault();
        AlarmSetting alarm = new AlarmSetting(uuid, hhmm, mask);
        ((MainActivity)getActivity()).showAddAlarmPage(alarm);
    }

    @OnItemClick(R.id.list_alarm)
    void onItemClick(int position) {
        AlarmSetting alarm = mAdapter.getItem(position);
        ((MainActivity)getActivity()).showAddAlarmPage(alarm);
    }

    @OnItemLongClick(R.id.list_alarm)
    public boolean onItemLongClick(int position) {
        AlarmSetting alarm = mAdapter.getItem(position);
        String message = getString(R.string.main_alarm_delete);
        GuiUtils.showDialog(this, REQUEST_CODE_CONFIRM_DELETE, YesNoDialogFragment.newInstance(message, YesNoDialogFragment.DialogType.YES_NO, new ParcelableInt(position)));

        return true;
    }

    private String getTextNextAlarm(Date next) {
        String strDate;
        if (next == null) {
            strDate = getText(R.string.main_page_next_none).toString();
        } else {
            SimpleDateFormat format = new SimpleDateFormat(getText(R.string.main_page_next_alarm).toString());
            strDate = format.format(next);
        }
        return strDate;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CONFIRM_DELETE: {
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                ParcelableInt tag = data.getParcelableExtra(YesNoDialogFragment.RESULT_KEY_TAG);
                onConfirmDelete(tag.getValue());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onConfirmDelete(int position) {
        AlarmSetting alarm = mAdapter.getItem(position);
        Document.getInstance().getAlarmList().removeItem(alarm);
        Document.getInstance().getAlarmList().updateNextAlarm();
        Document.saveDocument(getContext().getApplicationContext());
        Document.setAlarm(getContext().getApplicationContext());
        mAdapter.remove(alarm);
    }
}

