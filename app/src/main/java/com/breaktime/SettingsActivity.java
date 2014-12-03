package com.breaktime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SettingsActivity extends Activity {
    private TextView studyTimeText;
    private TextView breakTimeText;
    private long studyTime;
    private long breakTime;
    private SharedPreferences settings;
    final Context context = this;
    private static final String TAG = "AppTest";
    private ArrayList<String> currentApps;
    private AppArrayAdapter appListAdapter;

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
        //Create app list:
        currentApps = new ArrayList<String>();
        currentApps.addAll(settings.getStringSet(PrefID.ACTIVITIES, null));

        appListAdapter = new AppArrayAdapter(this, currentApps);

        ListView appList = (ListView) findViewById(R.id.breakAppsList);
        appList.setAdapter(appListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = settings.edit();
        ed.putLong(PrefID.STUDY_TIME, studyTime);
        ed.putLong(PrefID.BREAK_TIME, breakTime);
        ed.putLong(PrefID.STUDY_TIME_REMAINING, -1);

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

    public void pullAppList_old(View v) {
        // get apps in phone
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        final List<String> items = new ArrayList<String>();
        List<Integer> icons = new ArrayList<Integer>();
        final List<Intent> launches = new ArrayList<Intent>();
        int i = 0;
        // get activities currently added
        Set<String> activities = settings.getStringSet(PrefID.ACTIVITIES, null);
        // Add all apps to list
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.icon > 0 &&  pm.getLaunchIntentForPackage(packageInfo.packageName) != null && !activities.contains(packageInfo.packageName)) {
                Log.d(TAG, "Installed package :" + packageInfo.packageName);
                Log.d(TAG, "Icon : " + packageInfo.icon);
                Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
                Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
                launches.add(pm.getLaunchIntentForPackage(packageInfo.packageName));
                icons.add(packageInfo.icon);
                items.add(packageInfo.name);
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
                        final Object checkedItem = lw.getAdapter().getItem(which);
                        int intentIndex = items.indexOf(which);
                        final Intent itemIntent  = launches.get(intentIndex);
                        Log.d(TAG, "Intent : " + itemIntent.toString());
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
                                        ed.putStringSet(PrefID.ACTIVITIES, activities);
                                        ed.commit();
                                        // Add intent to global list
                                        Globals g = Globals.getInstance();
                                        HashMap<String, Intent> intents = g.getData();
                                        intents.put(checkedItem.toString(), itemIntent);
                                        Log.d(TAG, "intents : " + itemIntent.toString());
                                        g.setData(intents);

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

    public void removeAppList_old(View v) {
        Set<String> activities = settings.getStringSet(PrefID.ACTIVITIES, null);
        String[] items = activities.toArray(new String[activities.size()]);
        final ListAdapter adapter = new ArrayAdapterWithIcon(context, items, new Integer[items.length]);

        // Build alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Pick an App to remove from your activities");
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
                        final Object checkedItem = lw.getAdapter().getItem(which);
                        // Give message
                        builderInner.setTitle("You have removed the activity");
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
                                        activities.remove(checkedItem.toString());
                                        ed.putStringSet(PrefID.ACTIVITIES, activities);
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

    public void pullAppList(View v){
        AppListManager.getInstance().pullAppList(v, getPackageManager(), this, currentApps, appListAdapter);
        currentApps.clear();
        currentApps.addAll(settings.getStringSet(PrefID.ACTIVITIES, null));
        appListAdapter.notifyDataSetChanged();
    }

    public void removeAppList(View v){
        AppListManager.getInstance().removeAppList(v, this, currentApps, appListAdapter);
        currentApps.clear();
        currentApps.addAll(settings.getStringSet(PrefID.ACTIVITIES, null));
        appListAdapter.notifyDataSetChanged();
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
