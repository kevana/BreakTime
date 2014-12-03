package com.breaktime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Patrick on 11/26/2014.
 */
public class AppListManager {
    private static AppListManager ourInstance = new AppListManager();
    private Context currentContext;
    SharedPreferences settings;
    private PackageManager currentPM;

    public static AppListManager getInstance() {
        return ourInstance;
    }

    private AppListManager() {

    }

    public void pullAppList(View v, PackageManager pm, Context context, ArrayList<String> currentApps, AppArrayAdapter appListAdapter) {
        // get apps in phone
//        final PackageManager pm = getPackageManager();
        final String TAG = "AppTest";
        final ArrayList<String> fCurrentApps = currentApps;
        final AppArrayAdapter fAppListAdapter = appListAdapter;
        this.currentContext = context;
        this.currentPM = pm;
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        List<String> appNamesAndPackage = new ArrayList<String>();

        if (settings.getStringSet(PrefID.INSTALLED_APPS_WITH_PACKAGE, null) == null) {
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            Collections.sort(packages, new ApplicationInfoComparator(pm));
            List<Integer> icons = new ArrayList<Integer>();
            final List<Intent> launches = new ArrayList<Intent>();
            int i = 0;
            // get activities currently added
            Set<String> activities = settings.getStringSet(PrefID.ACTIVITIES, null);
            // Add all apps to list
            for (ApplicationInfo packageInfo : packages) {
                if (packageInfo.icon > 0 && pm.getLaunchIntentForPackage(packageInfo.packageName) != null && !activities.contains(packageInfo.packageName)) {
                    Log.d(TAG, "Installed package :" + packageInfo.packageName);
                    Log.d(TAG, "Icon : " + packageInfo.icon);
                    Log.d(TAG, "Name : " + packageInfo.loadLabel(pm));
                    Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
                    Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
                    launches.add(pm.getLaunchIntentForPackage(packageInfo.packageName));
                    icons.add(packageInfo.icon);
                    appNamesAndPackage.add(packageInfo.loadLabel(pm).toString() + ";" + packageInfo.packageName);
                }
                i++;
            }
            HashSet<String> itemSet = new HashSet<String>();
            itemSet.addAll(appNamesAndPackage);
            //Add to SharedPref
            SharedPreferences.Editor ed = settings.edit();
            ed.putStringSet(PrefID.INSTALLED_APPS_WITH_PACKAGE, itemSet);
            ed.commit();
        } else {
            appNamesAndPackage.addAll(settings.getStringSet(PrefID.INSTALLED_APPS_WITH_PACKAGE, null));
        }
        Collections.sort(appNamesAndPackage);
        ArrayList<String> appName = new ArrayList<String>();
        final ArrayList<String> appPackage = new ArrayList<String>();
        for (String appNamePack : appNamesAndPackage) {
            String[] parts = appNamePack.split(";");
            appName.add(parts[0]);
            appPackage.add(parts[1]);
        }

        final ListAdapter adapter = new ArrayAdapterWithIcon(context, appName, new ArrayList<Integer>());

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
                                currentContext);
                        // Get Choice
                        ListView lw = ((AlertDialog) dialog).getListView();
                        final Object checkedItem = lw.getAdapter().getItem(which);
                        final Intent
                                itemIntent = currentPM.getLaunchIntentForPackage(appPackage.get(which));
                        Log.d(TAG, "Intent : " + itemIntent.toString());
                        //launches.get(which);

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
                                        settings = PreferenceManager.getDefaultSharedPreferences(currentContext);
                                        Set<String> activities = new HashSet<String>();
                                        activities = settings.getStringSet(PrefID.ACTIVITIES, null);
                                        SharedPreferences.Editor ed = settings.edit();
                                        activities.add(checkedItem.toString());
                                        ed.putStringSet(PrefID.ACTIVITIES, activities);
                                        ed.commit();

                                        // Update the settings list
                                        fCurrentApps.clear();
                                        fCurrentApps.addAll(settings.getStringSet(PrefID.ACTIVITIES, null));
                                        fAppListAdapter.notifyDataSetChanged();

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

    public void removeAppList(View v, Context context) {
        this.currentContext = context;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
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
                                currentContext);
                        // Get Choice
                        ListView lw = ((AlertDialog) dialog).getListView();
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
                                        settings = PreferenceManager.getDefaultSharedPreferences(currentContext);
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

    public class ApplicationInfoComparator implements Comparator<ApplicationInfo> {

        private PackageManager pm;

        public ApplicationInfoComparator(PackageManager packageManager) {
            pm = packageManager;

        }

        @Override
        public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
            try {
                return (lhs.loadLabel(pm).toString()).compareTo(rhs.loadLabel(pm).toString());
            } catch (Resources.NotFoundException e) {
                return 0;
            }
        }
    }


}
