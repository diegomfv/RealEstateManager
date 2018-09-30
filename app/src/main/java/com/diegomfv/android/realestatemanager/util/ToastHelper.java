package com.diegomfv.android.realestatemanager.util;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.diegomfv.android.realestatemanager.R;

/**
 * Created by Diego Fajardo on 13/08/2018.
 */

/**
 * Helper class for toasts
 */
public class ToastHelper {

    private static final String TAG = ToastHelper.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to create a short toast.
     */
    public static void toastShort(Context context, String string) {
        Log.d(TAG, "toastShort: called!");
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();

    }

    /**
     * Method to create a long toast.
     */
    public static void toastLong(Context context, String string) {
        Log.d(TAG, "toastLong: called!");
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to create a toast that notifies the user that some access has not been granted.
     */
    public static void toastSomeAccessNotGranted(Context context) {
        Log.d(TAG, "toastSomeAccessNotGranted: called!");
        Toast.makeText(context, context.getResources().getString(R.string.some_access_not_granted), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to create a toast that tells the user that a something has not been implemented yet.
     */
    public static void toastNotImplemented(Context context) {
        Log.d(TAG, "toastNotImplemented: called!");
        Toast.makeText(context, context.getResources().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
    }
}
