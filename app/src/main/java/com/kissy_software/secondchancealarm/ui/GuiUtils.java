package com.kissy_software.secondchancealarm.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

public class GuiUtils {
    public static void setAppIcon(Context context, ImageView imageView, String appPackage) {
        PackageManager pm = context.getPackageManager();
        try {
            Drawable icon = pm.getApplicationIcon(appPackage);
            imageView.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    public static void showDialog(Fragment baseFragment, int requestCode, DialogFragment dialog) {
        FragmentManager manager = baseFragment.getFragmentManager();
        dialog.setTargetFragment(baseFragment, requestCode);
        dialog.show(manager, dialog.getClass().getName());
    }
}
