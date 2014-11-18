package com.breaktime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.widget.ListAdapter;
import android.widget.ListView;


public class SettingsActivity extends Activity {
    private TextView studyTimeText;
    private TextView breakTimeText;
    private long studyTime;
    private long breakTime;
    private SharedPreferences settings;
    final Context context = this;
    private static final String TAG = "AppTest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.v("SettingsActivity", "Preferences retrieved: " + settings.toString());
        studyTime = settings.getLong(PrefID.STUDY_TIME, -1);
        breakTime = settings.getLong(PrefID.BREAK_TIME, -1);
        if (studyTime < 0) {
            studyTime = 30000L;
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.STUDY_TIME, studyTime);
            ed.apply();
        }
        if (breakTime < 0) {
            breakTime = 5000L;
            SharedPreferences.Editor ed = settings.edit();
            ed.putLong(PrefID.BREAK_TIME, breakTime);
            ed.apply();
        }
        studyTimeText = (TextView) findViewById(R.id.studyTimeText);
        studyTimeText.setText(String.format("%d", studyTime / 1000));
        breakTimeText = (TextView) findViewById(R.id.breakTimeText);
        breakTimeText.setText(String.format("%d", breakTime / 1000));
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = settings.edit();
        ed.putLong(PrefID.STUDY_TIME, studyTime);
        ed.putLong(PrefID.BREAK_TIME, breakTime);
        ed.apply();
    }

    //TODO: Make all time increments into minutes, instead of seconds
    public void incrementStudyTime(View v) {
        if (studyTime >= 100000L) {
            return; //TODO Feedback?
        } else {
            studyTime += 5000L;
            studyTimeText.setText(String.format("%d", studyTime / 1000));
        }
    }

    public void decrementStudyTime(View v) {
        if (studyTime <= 10000L) {
            return; //TODO Feedback?
        } else {
            studyTime -= 5000L;
            studyTimeText.setText(String.format("%d", studyTime / 1000));
        }
    }

    public void pullAppList(View v) {
        // get apps in phone
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> items = new ArrayList<String>();
        List<Integer> icons = new ArrayList<Integer>();
        int i = 0;
        // Add all apps to list
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.icon > 0 &&  pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                Log.d(TAG, "Installed package :" + packageInfo.packageName);
                Log.d(TAG, "Icon : " + packageInfo.icon);
                Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
                Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
                icons.add(packageInfo.icon);
                items.add(packageInfo.packageName);
            }
            i++;
        }
        final ListAdapter adapter = new ArrayAdapterWithIcon(context, items, icons);

        // Build alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Pick an App to add to your activities!");
        alertDialogBuilder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                SettingsActivity.this);
                        // Get Choice
                        ListView lw = ((AlertDialog)dialog).getListView();
                        Log.d(TAG, "ListView : " + lw.getMaxScrollAmount());
                        Log.d(TAG, "Item pos : " + lw.getCheckedItemPosition());
                        Log.d(TAG, "which : " + which);
                        final Object checkedItem = lw.getAdapter().getItem(which);
                        // Give message
                        builderInner.setTitle("You added Item");
                        builderInner.setMessage(checkedItem.toString());
                        builderInner.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        // Add activity to current list
                                        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        Set<String> activities = new HashSet<String>();
                                        activities = settings.getStringSet(PrefID.ACTIVITIES, null);
                                        SharedPreferences.Editor ed = settings.edit();
                                        activities.add(checkedItem.toString());
                                        ed.putStringSet("key", activities);
                                        ed.commit();
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void incrementBreakTime(View v) {
        if (breakTime >= 10000L) {
            return;
        }

        breakTime += 1000L;
        breakTimeText.setText(String.format("%d", breakTime / 1000));
    }

    public void decrementBreakTime(View v) {

        if (breakTime <= 1000L) {
            return;
        }

        breakTime -= 1000L;
        breakTimeText.setText(String.format("%d", breakTime / 1000));
    }

}
