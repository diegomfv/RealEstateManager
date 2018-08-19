package com.diegomfv.android.realestatemanager.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppExecutors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego on 21/02/2018.
 */

/** This class is kept to show the mentor Utils modifications
 * when the project was started
 * */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Price Conversion (Dollars to Euros):
     */
    public static float convertDollarToEuro(float dollars){
        Log.d(TAG, "convertDollarToEuro: called!");
        return Math.round(dollars * 0.86);
    }

    /** Price Conversion (Euros to Dollars):
     */
    public static float convertEuroToDollar(float euros){
        return Math.round(euros * 1.16);
    }

    /** Date Conversion:
     */
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Internet Connectivity
     */
    // Background thread!!
    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    private static boolean isInternetAvailable() {
        Log.d(TAG, "isInternetAvailable: called!");
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            Log.d(TAG, "isInternetAvailable: true");
            return true;
        } catch (IOException e) {
            Log.d(TAG, "isInternetAvailable: false");
            return false; }
    }

    /** Internet Connectivity
     */
    public static void checkInternetInBackgroundThread (final DisposableObserver disposableObserver) {
        Log.d(TAG, "checkInternetInBackgroundThread: called! ");

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @SuppressLint("CheckResult")
            @Override
            public void run() {
                Log.d(TAG, "run: checking internet connection...");

                Observable.just(Utils.isInternetAvailable())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(disposableObserver);
            }

        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void checkAllPermissions(AppCompatActivity app) {
        Log.d(TAG, "checkPermissions: called!");

        if (Utils.checkPermission(app, Manifest.permission.INTERNET)
                && Utils.checkPermission(app, Manifest.permission.ACCESS_NETWORK_STATE)
                && Utils.checkPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION)
                && Utils.checkPermission(app, Manifest.permission.ACCESS_FINE_LOCATION)
                && Utils.checkPermission(app, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && Utils.checkPermission(app, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //do nothing

        } else {
            requestPermission(app, Constants.ALL_PERMISSIONS, Constants.REQUEST_CODE_ALL_PERMISSIONS);

        }
    }

    public static boolean checkPermission (Context context, String permission) {
        Log.d(TAG, "checkPermissions: called!");

        if (ContextCompat.checkSelfPermission (context.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static void requestPermission (AppCompatActivity app, String[] permissions, int requestCode) {
        Log.d(TAG, "requestPermission: called!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            app.requestPermissions(permissions, requestCode);
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Method that
     * capitalizes a string
     * */
    public static String capitalize (String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Method that launches an activity
     * */
    public static void launchActivity(Context context, Class <? extends AppCompatActivity> activity) {
        Log.d(TAG, "launchActivity: called!");

        Intent intent = new Intent(context, activity);
        context.startActivity(intent);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Add photo to temporary internal storage
     * */
    public static void addPictureToTemporaryInternalStorage (Bitmap bitmap) {
        Log.d(TAG, "addPictureToInternalStorage: called!");
    }

    /** Remove all pictures from temporary internal storage
     * */
    public static void removeAllPicturesFromTemporaryInternalStorage () {
        Log.d(TAG, "removeAllPicturesFromTemporaryInternalStorage: called!");
    }

}
