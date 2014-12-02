package com.breaktime;

import java.util.HashMap;

import android.content.Intent;
import android.util.Log;

public class Globals{
    private static Globals instance;

    // Global variable
    private HashMap<String,Intent> intentMap = new HashMap<String, Intent>();
    private Intent currentActivity;
    private static final String TAG = "GLOBALS";

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setData(HashMap<String,Intent> d)
    {
        Log.d(TAG, "setData: " + d.entrySet());
        this.intentMap=d;
    }
    public HashMap<String,Intent> getData(){
        Log.d(TAG, "setData: " + this.intentMap.entrySet());
        return this.intentMap;
    }

    public void setActivity(Intent d){
        this.currentActivity=d;
    }

    public Intent getCurrentActivity(){
        return this.currentActivity;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}