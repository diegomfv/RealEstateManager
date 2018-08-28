package com.diegomfv.android.realestatemanager.utils;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.diegomfv.android.realestatemanager.R;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Diego Fajardo on 13/08/2018.
 */
public class ToastHelper {

    private static final String TAG = ToastHelper.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void toastShort (Context context, String string) {
        Log.d(TAG, "toastShort: called!");
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();

    }

    public static void toastLong (Context context, String string){
        Log.d(TAG, "toastLong: called!");
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void toastButtonClicked (Context context, View view) {
        Log.d(TAG, "toastButtonClicked: " + ((Button)view).getText().toString() + " clicked!");
        Toast.makeText(context, "Button - " + ((Button)view).getText().toString() + " - clicked", Toast.LENGTH_SHORT).show();
    }

    public static void toastMenuItemClicked (Context context, MenuItem item) {
        Log.d(TAG, "toastMenuItemClicked: " + item.getTitle() + " clicked!");
        Toast.makeText(context, "Button - " + item.getTitle().toString() + " - clicked", Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void toastInternalStorageAccessNotGranted (Context context) {
        Log.d(TAG, "toastInternalStorageAccessNotGranted: called!");
        Toast.makeText(context, context.getResources().getString(R.string.access_internal_storage_not_granted), Toast.LENGTH_SHORT).show();
    }

    public static void toastThereWasAnError(Context context) {
        Log.d(TAG, "toastInternalStorageAccessNotGranted: called!");
        Toast.makeText(context, context.getResources().getString(R.string.there_was_an_error), Toast.LENGTH_SHORT).show();
    }

    public static void toastSomeAccessNotGranted(Context context) {
        Log.d(TAG, "toastSomeAccessNotGranted: called!");
        Toast.makeText(context, context.getResources().getString(R.string.some_access_not_granted), Toast.LENGTH_SHORT).show();
    }

    public static void toastNotImplemented(Context context) {
        Log.d(TAG, "toastNotImplemented: called!");
        Toast.makeText(context, context.getResources().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
    }
}
