package com.diegomfv.android.realestatemanager.utils;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Diego Fajardo on 13/08/2018.
 */
public class ToastHelper {

    private static final String TAG = ToastHelper.class.getSimpleName();

    ////////////////////////////////////

    public static void toastShort (Context context, String string) {
        Log.d(TAG, "toastShort: called!");
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();

    }

    public static void toastLong (Context context, String string){
        Log.d(TAG, "toastLong: called!");
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    ////////////////////////////////////

    public static void toastButtonClicked (Context context, View view) {
        Log.d(TAG, "toastButtonClicked: " + ((Button)view).getText().toString() + " clicked!");
        Toast.makeText(context, "Button - " + ((Button)view).getText().toString() + " - clicked", Toast.LENGTH_SHORT).show();
    }

    public static void toastMenuItemClicked (Context context, MenuItem item) {
        Log.d(TAG, "toastMenuItemClicked: " + item.getTitle() + " clicked!");
        Toast.makeText(context, "Button - " + item.getTitle().toString() + " - clicked", Toast.LENGTH_SHORT).show();

    }

}
