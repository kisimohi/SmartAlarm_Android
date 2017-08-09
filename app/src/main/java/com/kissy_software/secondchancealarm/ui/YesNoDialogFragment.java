package com.kissy_software.secondchancealarm.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.kissy_software.secondchancealarm.R;

public class YesNoDialogFragment extends DialogFragment {
    private static final String ARG_PARAM_MESSAGE = "paramMessage";
    private static final String ARG_PARAM_DIALOG_TYPE = "paramType";
    private static final String ARG_PARAM_DIALOG_TAG = "paramTag";
    public static final String RESULT_KEY_TAG = "returnTag";

    private String mMessage;
    private DialogType mDialogType;
    private Parcelable mTag;

    public enum DialogType {
        OK,
        YES_NO,
    }

    public static YesNoDialogFragment newInstance(String message, DialogType dialogType, Parcelable tag) {
        YesNoDialogFragment dialog = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_MESSAGE, message);
        args.putInt(ARG_PARAM_DIALOG_TYPE, dialogType.ordinal());
        args.putParcelable(ARG_PARAM_DIALOG_TAG, tag);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mMessage = getArguments().getString(ARG_PARAM_MESSAGE);
            mDialogType = DialogType.values()[getArguments().getInt(ARG_PARAM_DIALOG_TYPE)];
            mTag = getArguments().getParcelable(ARG_PARAM_DIALOG_TAG);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(mMessage);
        if (mDialogType == DialogType.OK) {
            builder.setPositiveButton(R.string.button_ok, mOnYesClickListener);
        } else {
            builder.setPositiveButton(R.string.button_yes, mOnYesClickListener)
                   .setNegativeButton(R.string.button_no, null);
        };
        Dialog dialog = builder.create();
        return dialog;
    }

    private DialogInterface.OnClickListener mOnYesClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Fragment target = getTargetFragment();
            Intent data = new Intent();
            data.putExtra(RESULT_KEY_TAG, mTag);
            target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
    };
}
