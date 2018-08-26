package com.diegomfv.android.realestatemanager.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppExecutors;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.network.models.placebynearby.PlacesByNearby;
import com.diegomfv.android.realestatemanager.network.models.placebynearby.Result;
import com.diegomfv.android.realestatemanager.network.models.placedetails.PlaceDetails;
import com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext.PlaceFromText;
import com.diegomfv.android.realestatemanager.rx.ObservableObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observer;

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

    public static String getCurrencySymbol (int currency) {
        Log.d(TAG, "getCurrencySymbol: called!");

        switch (currency) {
            case 0: { return " $"; }
            case 1: { return " €"; }
            default: { return " $"; }
        }
    }

    public static float getPriceAccordingToCurrency (int currency, float price) {
        Log.d(TAG, "getPriceAccordingToCurrency: called!");

        if (currency == 0) {
            return price;
        } else if (currency == 1) {
            return Utils.convertDollarToEuro(price);
        } else {
             return price;
        }
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

    public static boolean setInternetAvailability(Object isInternetAvailable) {
        Log.d(TAG, "setInternetAvailability: called!");
        return (int) isInternetAvailable == 1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Method used to connect
     * the broadcast receiver with
     * the activity
     * */
    public static void connectReceiver (Context context, BroadcastReceiver receiver, IntentFilter intentFilter, Observer observer){
        Log.d(TAG, "connectReceiver: called!");

        context.registerReceiver(receiver, intentFilter);
        ObservableObject.getInstance().addObserver(observer);

    }

    /** Method used to disconnect
     * the broadcast receiver from the activity
     * */
    public static void disconnectReceiver (Context context, BroadcastReceiver receiver, Observer observer) {
        Log.d(TAG, "disconnectReceiver: called!");

        context.unregisterReceiver(receiver);
        ObservableObject.getInstance().deleteObserver(observer);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Method to create a Snackbar
     * displaying that there is no internet
     * */
    public static Snackbar createSnackbar (Context context, View mainLayout, String message) {

        final Snackbar snackbar = Snackbar.make(
                mainLayout,
                message,
                Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction(
                context.getResources().getString(R.string.snackbarNoInternetButton),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: snackbar clicked!");
                        snackbar.dismiss();
                    }
                });

        View snackbarView = snackbar.getView();
        //snackbarView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(context.getResources().getColor(android.R.color.white));
        Button snackbarButton = (Button) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        snackbarButton.setTextColor(context.getResources().getColor(android.R.color.white));
        snackbar.show();

        return snackbar;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean checksPlaceFromText (PlaceFromText placeFromText) {
        Log.d(TAG, "checkPlaceFromText: called!");

        if (placeFromText != null) {
            if (placeFromText.getStatus().equals(Constants.REQUEST_STATUS_PLACE_FROM_TEXT_IS_OK)) {
                if (placeFromText.getCandidates() != null) {
                    if (placeFromText.getCandidates().size() > 0) {
                        if (placeFromText.getCandidates().get(0) != null) {
                            if (placeFromText.getCandidates().get(0).getPlaceId() != null) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean checksPlaceDetails (PlaceDetails placeDetails) {
        Log.d(TAG, "checksPlaceDetails: called!");

        if (placeDetails != null) {
            if (placeDetails.getResult() != null) {
                if (placeDetails.getResult().getGeometry() != null) {
                    if (placeDetails.getResult().getGeometry().getLocation() != null) {
                        if (placeDetails.getResult().getGeometry().getLocation().getLat() != null) {
                            if (placeDetails.getResult().getGeometry().getLocation().getLng() != null) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkPlacesByNearbyResults (PlacesByNearby placesByNearby) {
        Log.d(TAG, "checkPlacesByNearby: called!");

        if (placesByNearby != null) {
            if (placesByNearby.getResults() != null) {
                if (placesByNearby.getResults().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkResultPlacesByNearby (Result result) {
        Log.d(TAG, "checkResultPlacesByNearby: called!");

        if (result != null) {
            if (result.getPlaceId() != null) {
                if (result.getName() != null) {
                    if (result.getVicinity() != null) {
                        if (result.getTypes() != null) {
                            if (result.getTypes().size() > 0) {
                                if (result.getGeometry() != null) {
                                    if (result.getGeometry().getLocation() != null) {
                                        if (result.getGeometry().getLocation().getLat() != null) {
                                            if (result.getGeometry().getLocation().getLng() != null){
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
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
        if (str.equals("")) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String replaceUnderscore(String str) {
        return str.replace("_", " ");
    }

    /** Method that checks if a string
     * can be parsed to Integer
     * */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        if (str.isEmpty()) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (str.length() == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static String getTextViewString(TextView textView) {
        Log.d(TAG, "getViewsText: called!");
        return textView.getText().toString().trim();
    }

    public static int getTextViewInteger(TextView textView) {
        Log.d(TAG, "getTextViewInteger: called!");

        if (isInteger(textView.getText().toString().trim())) {
            return Integer.parseInt(textView.getText().toString().trim());
        }
        return 0;
    }

    public static String getAddressAsString(RealEstate realEstate) {
        Log.d(TAG, "setAddressFromRealEstateCache: called!");
        if (realEstate.getAddress() != null) {
            StringBuilder str = new StringBuilder();
            appendIfNotNullOrEmpty(str, realEstate.getAddress().getStreet());
            appendIfNotNullOrEmpty(str, realEstate.getAddress().getLocality());
            appendIfNotNullOrEmpty(str, realEstate.getAddress().getCity());
            appendIfNotNullOrEmpty(str, realEstate.getAddress().getPostcode());
            getRidOfLastComma(str);
            return str.toString();
        }
        return "";
    }

    private static void appendIfNotNullOrEmpty (StringBuilder stringBuilder, String addressField) {
        Log.d(TAG, "appendIfNotNullOrEmpty: called!");
        if (checkStringIsNotEmptyOrNull(addressField)) {
            stringBuilder.append(addressField).append(", ");
        }
    }

    private static boolean checkStringIsNotEmptyOrNull (String string) {
        Log.d(TAG, "checkStringIsNotEmptyOrNull: called!");

        if (string == null || string.equals("")) {
            return false;
        }
        return true;
    }

    private static void getRidOfLastComma (StringBuilder stringBuilder) {
        Log.d(TAG, "getRidOfLastComma: called!");
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
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

    /** Method that displays the main content
     * and hides de progress bar that occupies
     * all the screen
     * */
    public static void showMainContent (View progressBarContent, View mainContent) {
        Log.d(TAG, "showMainContent: called!");

        progressBarContent.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);

    }

    /** Method that hides the main content
     * and displays de progress bar that occupies
     * all the screen
     * */
    public static void hideMainContent (View progressBarContent, View mainContent) {
        Log.d(TAG, "hideMainContent: called!");

        progressBarContent.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

    }

    public static String formatToDecimals(int number, int currency) {
        Log.d(TAG, "formatToDecimalsWithComma: called!");

        //currency
        //0: dollars
        //1: euros

        String formatType = ",###.00";

        switch (currency) {

            case 0: {

                DecimalFormatSymbols symbolsDollars =
                        new DecimalFormatSymbols(Locale.US);
                symbolsDollars.setDecimalSeparator('.');
                symbolsDollars.setGroupingSeparator(',');

                DecimalFormat dollarsFormatter =
                        new DecimalFormat(formatType, symbolsDollars);
                dollarsFormatter.setGroupingSize(3);

                return dollarsFormatter.format(number);
            }

            case 1: {

                DecimalFormatSymbols symbolsEuros =
                        new DecimalFormatSymbols(Locale.GERMAN);
                symbolsEuros.setDecimalSeparator(',');
                symbolsEuros.setGroupingSeparator('.');

                DecimalFormat eurosFormatter =
                        new DecimalFormat(formatType, symbolsEuros);
                eurosFormatter.setGroupingSize(3);

                return eurosFormatter.format(number);
            }

            default: {

                DecimalFormatSymbols symbolsDollars =
                        new DecimalFormatSymbols(Locale.US);
                symbolsDollars.setDecimalSeparator('.');
                symbolsDollars.setGroupingSeparator(',');

                DecimalFormat dollarsFormatter =
                        new DecimalFormat(formatType, symbolsDollars);
                dollarsFormatter.setGroupingSize(3);

                return dollarsFormatter.format(number);
            }

        }

    }


}