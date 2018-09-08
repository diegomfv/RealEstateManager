package com.diegomfv.android.realestatemanager.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
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

    public static void updateCurrencyIconWhenMenuCreated(Context context, int currency, Menu menu, int itemRef) {
        Log.d(TAG, "updateCurrencyIconWhenMenuCreated: called!");
        MenuItem item = menu.findItem(itemRef);
        if (currency == 0) {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_dollar_symbol_white_24dp));

        } else {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_euro_symbol_white_24dp));
        }
    }

    public static void updateCurrencyIcon(Context context, int currency, MenuItem item) {
        Log.d(TAG, "updateCurrencyIconWhenMenuCreated: called!");
        if (currency == 0) {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_dollar_symbol_white_24dp));

        } else {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_euro_symbol_white_24dp));
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

    public static void getCurrentMemoryStatus () {
        Log.d(TAG, "getCurrentMemoryStatus: called!");

        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;

        Log.i(TAG, "onCreate: " + usedMemInMB);
        Log.i(TAG, "onCreate: " + maxHeapSizeInMB);
        Log.i(TAG, "onCreate: " + availHeapSizeInMB);

    }

    public static long getMaxMemory () {
        Log.d(TAG, "getMaxMemory: called!");
        return Runtime.getRuntime().maxMemory() / 1024;
    }

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

    public static boolean isNumeric (String str) {
        Log.d(TAG, "isNumeric: called!");
        return str.matches("\\d+(?:\\.\\d+)?");
    }

    public static String getStringFromTextView(TextView textView) {
        Log.d(TAG, "getViewsText: called!");
        return textView.getText().toString().trim();
    }

    public static int getIntegerFromTextView(TextView textView) {
        Log.d(TAG, "getIntegerFromTextView: called!");

        if (isInteger(textView.getText().toString().trim())) {
            return Integer.parseInt(textView.getText().toString().trim());
        }
        return 0;
    }

    public static float getFloatFromTextView(TextView textView) {
        Log.d(TAG, "getFloatFromTextView: called!");

        if (isNumeric(textView.getText().toString().trim())) {
            return Float.parseFloat(textView.getText().toString().trim());
        }
        return 0f;
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

    public static TextInputLayout getTextInputLayoutFromCardview(CardView cardView) {
        Log.d(TAG, "getTextInputLayoutFromCardview: called!");
        return cardView.findViewById(R.id.text_input_layout_id);
    }

    public static TextInputAutoCompleteTextView getTextInputAutoCompleteTextViewFromCardView(CardView cardView) {
        Log.d(TAG, "getTextInputAutoCompleteTextViewFromCardView: called!");
        return getTextInputLayoutFromCardview(cardView).findViewById(R.id.text_input_autocomplete_text_view_id);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Method that launches an activity
     * */
    public static void launchActivity(Context context, Class <? extends AppCompatActivity> activity) {
        Log.d(TAG, "launchActivity: called!");

        Intent intent = new Intent(context, activity);
        context.startActivity(intent);

    }

    public static void launchActivityWithIntent (Context context, Intent intent) {
        Log.d(TAG, "launchActivityWithIntent: called!");
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

        //CURRENCY
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

    public static String floatToString (Number amount) {
        Log.d(TAG, "floatTwoDecimals: called!");
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(amount);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void writeCurrentCurrencyShPref (Context context, int currency) {
        Log.d(TAG, "writeCurrentCurrencyShPref: called!");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SH_PREF_CURRENCY_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.CURRENCY, currency);
        editor.apply();
    }

    public static int readCurrentCurrencyShPref (Context context) {
        Log.d(TAG, "saveCurrentCurrency: called!");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SH_PREF_CURRENCY_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constants.CURRENCY, 0);
    }

    public static void writeAgentDataShPref (Context context, String firstName, String lastName, String email, String password, String memDataQ, String memDataA) {
        Log.d(TAG, "writeAgentDataShPref: called!");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SH_PREF_AGENT_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SH_PREF_AGENT_FIRST_NAME, firstName);
        editor.putString(Constants.SH_PREF_AGENT_LAST_NAME, lastName);
        editor.putString(Constants.SH_PREF_AGENT_EMAIL, email);
        editor.putString(Constants.SH_PREF_AGENT_PASSWORD, password);
        editor.putString(Constants.SH_PREF_AGENT_MEMORABLE_DATA_QUESTION, memDataQ);
        editor.putString(Constants.SH_PREF_AGENT_MEMORABLE_DATA_ANSWER, memDataA);
        editor.apply();
    }

    public static String[] readCurrentAgentData (Context context) {
        Log.d(TAG, "saveCurrentCurrency: called!");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SH_PREF_AGENT_SETTINGS, Context.MODE_PRIVATE);
        return new String[]{
                sharedPreferences.getString(Constants.SH_PREF_AGENT_FIRST_NAME, ""),
                sharedPreferences.getString(Constants.SH_PREF_AGENT_LAST_NAME, ""),
                sharedPreferences.getString(Constants.SH_PREF_AGENT_EMAIL, ""),
                sharedPreferences.getString(Constants.SH_PREF_AGENT_PASSWORD, ""),
                sharedPreferences.getString(Constants.SH_PREF_AGENT_MEMORABLE_DATA_QUESTION, ""),
                sharedPreferences.getString(Constants.SH_PREF_AGENT_MEMORABLE_DATA_ANSWER, ""),
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /** This method uses inSampleSize to reduce the size of the image in memory
     * */
    public static Bitmap decodeSampleBitmapFromInputStream (InputStream stream, int reqHeight, int reqWidth) {
        Log.d(TAG, "decodeSampleBitmapFromFile: called!");

        //Height and Width in pixels
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);

        Log.i(TAG, "readBitmapDimensionsAndType: height = " + options.outHeight);
        Log.i(TAG, "readBitmapDimensionsAndType: width = " + options.outWidth);
        Log.i(TAG, "readBitmapDimensionsAndType: memeType= " + options.outMimeType);
        Log.i(TAG, "readBitmapDimensionsAndType: sizeInMemory = " + options.outHeight * options.outWidth * 4);

        options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);

        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        Log.i(TAG, "readBitmapDimensionsAndType: height = " + options.outHeight);
        Log.i(TAG, "readBitmapDimensionsAndType: width = " + options.outWidth);
        Log.i(TAG, "readBitmapDimensionsAndType: memeType= " + options.outMimeType);
        Log.i(TAG, "readBitmapDimensionsAndType: sizeInMemory = " + options.outHeight * options.outWidth * 4);
        Log.i(TAG, "readBitmapDimensionsAndType: bitmap size() = " + bitmap.getByteCount());

        return bitmap;

    }

    //Can be static
    /** This method checks the current size of the image and, if the required size is still
     * higher, it continues reducing the image
     * */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
        Log.d(TAG, "calculateInSampleSize: called!");

        //Height and Width in pixels
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        Log.d(TAG, "calculateInSampleSize: height = " + height);
        Log.d(TAG, "calculateInSampleSize: reqHeight = " + reqHeight);
        Log.d(TAG, "calculateInSampleSize: width = " + width);
        Log.d(TAG, "calculateInSampleSize: reqWidth = " + reqWidth);

        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {

                Log.i(TAG, "calculateInSampleSize: in while loop...");

                // 2 --> check inSampleSize docs
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static String dateToString (Date date) {
        Log.d(TAG, "dateToString: called!");
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static Date stringToDate (String string) {
        Log.d(TAG, "stringToDate: called!");
        try {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static RectF calculationRectOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(), location[1] + view.getMeasuredHeight());
    }
}