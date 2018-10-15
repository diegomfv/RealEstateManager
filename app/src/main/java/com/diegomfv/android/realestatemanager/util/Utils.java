package com.diegomfv.android.realestatemanager.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppExecutors;
import com.diegomfv.android.realestatemanager.data.entities.Agent;
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

/**
 * This class is kept to show the mentor Utils modifications
 * when the project was started
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Price Conversion (Dollars to Euros):
     */
    public static float convertDollarToEuro(float dollars) {
        return dollars * 0.86f;
    }

    /**
     * Price Conversion (Euros to Dollars):
     */
    public static float convertEuroToDollar(float euros) {
        return euros * 1.16f;
    }

    /**
     * Date Conversion:
     */
    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    public static String getCurrencySymbol(int currency) {
        Log.d(TAG, "getCurrencySymbol: called!");

        switch (currency) {
            case 0: {
                return "$";
            }
            case 1: {
                return "€";
            }
            default: {
                return "$";
            }
        }
    }

    /**
     * Method that checks the currency and returns the value in dollars or
     * euros (depending on the currency value). It simply transforms the value
     * using convertDollarToEuro() method if the currency is 1 (euros). It is
     * used for DISPLAYING purposes
     */
    public static float getValueAccordingToCurrency(int currency, float price) {
        Log.d(TAG, "getValueAccordingToCurrency: called!");
        if (currency == 0) {
            return price;
        } else if (currency == 1) {
            return Utils.convertDollarToEuro(price);
        } else {
            return price;
        }
    }

    /**
     * Method that checks the currency and returns the value in dollars or
     * euros (depending on the currency value). It simply transforms the value
     * using convertEuroToDollar() method if the currency is 1 (euros). It is
     * used for TRANSFORMATION purposes
     */
    public static float convertCurrencyIfNecessary (int currency, float price) {
        Log.d(TAG, "convertIfNecessary: called!");
        if (currency == 0) {
            return price;
        } else if (currency == 1) {
            return Utils.convertEuroToDollar(price);
        } else {
            return price;
        }
    }

    /**
     * Method that checks the currency and returns the value in dollars or
     * euros (depending on the currency value) with the proper decimal format.
     * It simply transforms the value using convertDollarToEuro() method if the currency is 1 (euros)
     * and then uses formatToDecimals() method to format that value
     */
    public static String getValueFormattedAccordingToCurrency(float price, int currency) {
        Log.d(TAG, "getValueFormattedAccordingToCurrency: called!");

        if (currency == 0) {
            return Utils.formatToDecimals(price, currency);
        } else if (currency == 1) {
            return Utils.formatToDecimals(Utils.convertDollarToEuro(price), currency);
        } else {
            return Utils.formatToDecimals(price, currency);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks the currency and loads the proper icon on the menu
     */
    public static void updateCurrencyIconWhenMenuCreated(Context context, int currency, Menu menu, int itemRef) {
        Log.d(TAG, "updateCurrencyIconWhenMenuCreated: called!");
        MenuItem item = menu.findItem(itemRef);
        if (currency == 0) {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_dollar_symbol_white_24dp));

        } else {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_euro_symbol_white_24dp));
        }
    }

    /**
     * Method that loads an icon related to the currency (dollars or euros).
     */
    public static void updateCurrencyIcon(Context context, int currency, MenuItem item) {
        Log.d(TAG, "updateCurrencyIconWhenMenuCreated: called!");
        if (currency == 0) {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_dollar_symbol_white_24dp));

        } else {
            item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_euro_symbol_white_24dp));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks if internet is available
     * pinging Google servers
     */
    // Background thread!!
    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    public static boolean isInternetAvailable() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Method that checks in a background
     * thread if internet is available.
     */
    public static void checkInternetInBackgroundThread(final DisposableObserver disposableObserver) {
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

    /**
     * Method that returns a boolean depending on if internet is available (true) or not (false)
     */
    public static boolean setInternetAvailability(Object isInternetAvailable) {
        Log.d(TAG, "setInternetAvailability: called!");
        return (int) isInternetAvailable == 1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method used to connect
     * the broadcast receiver with
     * the activity
     */
    public static void connectReceiver(Context context, BroadcastReceiver receiver, IntentFilter intentFilter, Observer observer) {
        Log.d(TAG, "connectReceiver: called!");
        context.registerReceiver(receiver, intentFilter);
        ObservableObject.getInstance().addObserver(observer);
    }

    /**
     * Method used to disconnect
     * the broadcast receiver from the activity
     */
    public static void disconnectReceiver(Context context, BroadcastReceiver receiver, Observer observer) {
        Log.d(TAG, "disconnectReceiver: called!");
        context.unregisterReceiver(receiver);
        ObservableObject.getInstance().deleteObserver(observer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to create a Snackbar
     * displaying that there is no internet
     */
    public static Snackbar createSnackbar(Context context, View mainLayout, String message) {

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

    /**
     * Method to check memory situation (log use)
     */
    public static void getCurrentMemoryStatus() {
        Log.d(TAG, "getCurrentMemoryStatus: called!");

        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;

        Log.i(TAG, "onCreate: " + usedMemInMB);
        Log.i(TAG, "onCreate: " + maxHeapSizeInMB);
        Log.i(TAG, "onCreate: " + availHeapSizeInMB);

    }

    /**
     * Method to get max memory available
     */
    public static long getMaxMemory() {
        Log.d(TAG, "getMaxMemory: called!");
        return Runtime.getRuntime().maxMemory() / 1024;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks that the fundamental information for the app to work
     * regarding a place obtained using Google servers
     * does not return null
     */
    public static boolean checksPlaceFromText(PlaceFromText placeFromText) {
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

    /**
     * Method that checks that the fundamental information for the app to work
     * regarding place details obtained using Google servers
     * does not return null
     */
    public static boolean checksPlaceDetails(PlaceDetails placeDetails) {
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

    /**
     * Method that checks that the fundamental information for the app to work
     * regarding nearby places obtained using Google servers
     * does not return null
     */
    public static boolean checkPlacesByNearbyResults(PlacesByNearby placesByNearby) {
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

    /**
     * Method that checks that the fundamental information for the app to work
     * regarding the result of places nearby service obtained using Google servers
     * does not return null
     */
    public static boolean checkResultPlacesByNearby(Result result) {
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
                                            if (result.getGeometry().getLocation().getLng() != null) {
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

    /**
     * Method that
     * capitalizes a string
     */
    public static String capitalize(String str) {
        if (str.equals("")) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String replaceUnderscore(String str) {
        return str.replace("_", " ");
    }

    /**
     * Method that checks if a string
     * can be parsed to Integer
     */
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

    /**
     * Method that checks if a value is a Number
     */
    public static boolean isNumeric(String str) {
        Log.d(TAG, "isNumeric: called!");
        return str.matches("\\d+(?:\\.\\d+)?");
    }

    /**
     * Method that returns the string from a TextView
     */
    public static String getStringFromTextView(TextView textView) {
        Log.d(TAG, "getViewsText: called!");
        return textView.getText().toString().trim();
    }

    /**
     * Method that returns the Integer from a TextView
     */
    public static int getIntegerFromTextView(TextView textView) {
        Log.d(TAG, "getIntegerFromTextView: called!");

        if (isInteger(textView.getText().toString().trim())) {
            return Integer.parseInt(textView.getText().toString().trim());
        }
        return 0;
    }

    /**
     * Method that returns the Float from a TextView
     */
    public static float getFloatFromTextView(TextView textView) {
        Log.d(TAG, "getFloatFromTextView: called!");

        if (isNumeric(textView.getText().toString().trim())) {
            return Float.parseFloat(textView.getText().toString().trim());
        }
        return 0f;
    }

    /**
     * Method that returns the address in a String
     * from the address of the real estate
     */
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

    /**
     * Method to append a String to another String if that other String is not null or empty
     * (method used to avoid NullPointerExceptions)
     */
    private static void appendIfNotNullOrEmpty(StringBuilder stringBuilder, String addressField) {
        Log.d(TAG, "appendIfNotNullOrEmpty: called!");
        if (checkStringIsNotEmptyOrNull(addressField)) {
            stringBuilder.append(addressField).append(", ");
        }
    }

    /**
     * Method that checks if a String is null or Empty
     */
    private static boolean checkStringIsNotEmptyOrNull(String string) {
        Log.d(TAG, "checkStringIsNotEmptyOrNull: called!");
        if (string == null || string.equals("")) {
            return false;
        }
        return true;
    }

    /**
     * Method that checks if a TextView's String is empty or not
     */
    public static boolean textViewIsFilled(TextView textView) {
        Log.d(TAG, "textViewIsFilled: called!");
        return Utils.getStringFromTextView(textView).length() > 0;
    }

    /**
     * Method that allows to get rid of the last comma of an address element. It is only
     * used in getAddressAsString() method
     */
    private static void getRidOfLastComma(StringBuilder stringBuilder) {
        Log.d(TAG, "getRidOfLastComma: called!");
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that returns a cardView's layout's TextInputLayout view
     */
    public static TextInputLayout getTextInputLayoutFromCardview(CardView cardView) {
        Log.d(TAG, "getTextInputLayoutFromCardview: called!");
        return cardView.findViewById(R.id.text_input_layout_id);
    }

    /**
     * Method that returns a cardView's layout's TextInputAutocompleteTextView
     */
    public static TextInputAutoCompleteTextView getTextInputAutoCompleteTextViewFromCardView(CardView cardView) {
        Log.d(TAG, "getTextInputAutoCompleteTextViewFromCardView: called!");
        return getTextInputLayoutFromCardview(cardView).findViewById(R.id.text_input_autocomplete_text_view_id);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that launches an activity
     */
    public static void launchActivity(Context context, Class<? extends AppCompatActivity> activity) {
        Log.d(TAG, "launchActivity: called!");
        context.startActivity(new Intent(context, activity));

    }

    /**
     * Method that launches an activity clearing the activity stack
     */
    public static void launchActivityClearStack(Context context, Class<? extends AppCompatActivity> activity) {
        Log.d(TAG, "launchActivityClearStack: called!");
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that displays the main content
     * and hides de progress bar that occupies
     * all the screen
     */
    public static void showMainContent(View progressBarContent, View mainContent) {
        Log.d(TAG, "showMainContent: called!");

        progressBarContent.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);

    }

    /**
     * Method that hides the main content
     * and displays de progress bar that occupies
     * all the screen
     */
    public static void hideMainContent(View progressBarContent, View mainContent) {
        Log.d(TAG, "hideMainContent: called!");

        progressBarContent.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

    }

    /**
     * Method that formats the price according to the currency.
     * Dollars -> eg. 123,456.78
     * Euros -> eg. 123.456,78
     */
    private static String formatToDecimals(Number number, int currency) throws NumberFormatException {
        Log.d(TAG, "formatToDecimalsWithComma: called!");

        //CURRENCY
        //0: dollars
        //1: euros

        String formatType = ",###.##";

        switch (currency) {

            case 0: {

                DecimalFormatSymbols symbolsDollars =
                        new DecimalFormatSymbols(Locale.US);
                symbolsDollars.setDecimalSeparator('.');
                symbolsDollars.setGroupingSeparator(',');

                DecimalFormat dollarsFormatter =
                        new DecimalFormat(formatType, symbolsDollars);
                dollarsFormatter.setGroupingSize(3);
                dollarsFormatter.setMinimumFractionDigits(2);
                dollarsFormatter.setMaximumFractionDigits(2);

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
                eurosFormatter.setMinimumFractionDigits(2);
                eurosFormatter.setMaximumFractionDigits(2);

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that writes an Integer (currency) to shared preferences. This value is often used
     * by the app (used in several activities).
     */
    public static void writeCurrentCurrencyShPref(Context context, int currency) {
        Log.d(TAG, "writeCurrentCurrencyShPref: called!");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SH_PREF_CURRENCY_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.CURRENCY, currency);
        editor.apply();
    }

    /**
     * Method to read the currency value.
     */
    public static int readCurrentCurrencyShPref(Context context) {
        Log.d(TAG, "saveCurrentCurrency: called!");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SH_PREF_CURRENCY_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constants.CURRENCY, 0);
    }

    /**
     * Method that allows to write all the agent data to SharedPreferences
     */
    public static void writeAgentDataShPref(Context context, Agent agent) {
        Log.d(TAG, "writeAgentDataShPref: called!");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SH_PREF_AGENT_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SH_PREF_AGENT_FIRST_NAME, agent.getFirstName());
        editor.putString(Constants.SH_PREF_AGENT_LAST_NAME, agent.getLastName());
        editor.putString(Constants.SH_PREF_AGENT_EMAIL, agent.getEmail());
        editor.putString(Constants.SH_PREF_AGENT_PASSWORD, agent.getPassword());
        editor.putString(Constants.SH_PREF_AGENT_MEMORABLE_DATA_QUESTION, agent.getMemorableDataQuestion());
        editor.putString(Constants.SH_PREF_AGENT_MEMORABLE_DATA_ANSWER, agent.getMemorableDataAnswer());
        editor.apply();
    }

    /**
     * Method that allows to read the agent data from SharedPreferences
     */
    public static String[] readCurrentAgentData(Context context) {
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

    /**
     * Method that returns a resized Bitmap according to a width and height
     */
    public static Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    /**
     * Method that returns a resized Bitmap according to a maximum size
     */
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

    /**
     * This method uses inSampleSize to reduce the size of the image in memory
     */
    public static Bitmap decodeSampleBitmapFromInputStream(InputStream stream, int reqHeight, int reqWidth) {
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

    /**
     * This method checks the current size of the image and, if the required size is still
     * higher, it continues reducing the image
     */
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

    /**
     * Method to convert a Date object to a String
     */
    public static String dateToString(Date date) {
        Log.d(TAG, "dateToString: called!");
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    /**
     * Method to convert a String to a Date Object
     */
    public static Date stringToDate(String string) {
        Log.d(TAG, "stringToDate: called!");
        try {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to replaced an underscore character with a space
     */
    public static String replaceUnderscoreWithSpace(String string) {
        Log.d(TAG, "replaceUnderscoreWithSpace: called!");
        return string.replaceAll("_", " ");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method used by the Splash Screen to calculate distances
     */
    public static RectF calculationRectOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(), location[1] + view.getMeasuredHeight());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Changes the color of the Toolbar Overflow Button to white
     */
    public static void setOverflowButtonColor(final Toolbar toolbar, final int color) {
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), color);
            toolbar.setOverflowIcon(drawable);
        }
    }

    /**
     * Method to launch a simple dialog with positive and negative buttons and include
     * a listener for the positive button
     */
    public static void launchSimpleDialog(Context context, String mainMessage, String title,
                                          String okButtonText, String cancelButtonText,
                                          DialogInterface.OnClickListener positiveListener) {
        Log.d(TAG, "launchSimpleDialog: called!");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(mainMessage)
                .setPositiveButton(okButtonText, positiveListener)
                .setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: called!");
                        //do nothing
                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to calculate the number of columns a grid should have
     */
    public static int calculateGridsNumberOfColumns(Context context, int gridItemWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / gridItemWidth);
    }


}