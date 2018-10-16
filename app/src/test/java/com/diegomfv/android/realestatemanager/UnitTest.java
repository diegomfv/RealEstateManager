package com.diegomfv.android.realestatemanager;

import com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext.PlaceFromText;
import com.diegomfv.android.realestatemanager.network.remote.GoogleService;
import com.diegomfv.android.realestatemanager.util.Utils;

import org.junit.Test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class UnitTest {

    @Test
    public void convertDollarToEuroTest() {
        float priceInDollars = new Random().nextFloat();
        float conversionRate = 0.86f;
        assertEquals(priceInDollars * conversionRate, Utils.convertDollarToEuro(priceInDollars));
    }

    @Test
    public void convertEurosToDollars() {
        float priceInEuros = new Random().nextFloat();
        float conversionRate = 1.16f;
        assertEquals(priceInEuros * conversionRate, Utils.convertEuroToDollar(priceInEuros));
    }

    @Test
    public void isInternetAvailableTest() {

        if (Utils.isInternetAvailable()) {

            assertTrue(true);
            System.out.println("Internet available");

            Observer<PlaceFromText> observer = new Observer<PlaceFromText>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(PlaceFromText placeFromText) {
                    assertNotNull(placeFromText);
                    System.out.println("Response from API Not null");

                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            };

            final String GOOGLE_API_FIND_PLACE_FROM_TEXT = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
            Retrofit retrofitPlaceFromText = new Retrofit.Builder()
                    .baseUrl(GOOGLE_API_FIND_PLACE_FROM_TEXT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            GoogleService googleService = retrofitPlaceFromText.create(GoogleService.class);

            googleService.fetchPlaceFromText("Corn Street" + ","
                            + "Bristol" + ","
                            + "Bristol" + ","
                            + "BS8",
                    "textquery",
                    "AIzaSyDDaW_rQKqJtwEdqnib_-WQLCeSodUnb5g")
                    .subscribeWith(observer);

        } else {
            System.out.println("No internet");
            fail();
        }
    }

    @Test
    public void isInteger() {

        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            assertTrue(Utils.isInteger(String.valueOf(random.nextInt())));
        }

        assertFalse(Utils.isInteger("a"));
        assertFalse(Utils.isInteger("aasjhhfwe"));
        assertFalse(Utils.isInteger("aasdbfyur   f"));
        assertFalse(Utils.isInteger("1237  y4726384"));

    }

    @Test
    public void decimalFormat() {

        Random random = new Random();

        DecimalFormatSymbols unusualSymbols =
                new DecimalFormatSymbols(Locale.US);
        unusualSymbols.setDecimalSeparator(',');
        unusualSymbols.setGroupingSeparator('.');

        String strange = ",###.00";
        DecimalFormat weirdFormatter =
                new DecimalFormat(strange, unusualSymbols);
        weirdFormatter.setGroupingSize(3);

        String bizarre = weirdFormatter.format(random.nextInt() + random.nextFloat());
        System.out.println(bizarre + " $");

        ////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Test
    public void getRidOfLastComma() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Some Text");
        stringBuilder.append(", ");

        stringBuilder.setLength(stringBuilder.length() - 2);

        System.out.println(stringBuilder);

    }

    @Test
    public void dateToString() {

        Date date = new Date();
        String dateAsString = new SimpleDateFormat("dd/MM/YYYY").format(date);

        System.out.println(dateAsString);

    }

    @Test
    public void stringToDateAndComparison() {

        String string1 = "05/09/2018";
        String string2 = "06/09/2018";
        String string3 = "07/09/2018";
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        try {
            Date date1 = format.parse(string1);
            Date date2 = format.parse(string2);
            Date date3 = format.parse(string3);

            assertTrue(date3.after(date2));
            assertTrue(date2.after(date1));
            assertTrue(date1.before(date2));
            assertFalse(date1.after(date3));

        } catch (ParseException e) {
            System.out.println("Parse exception");
        }
    }

    @Test
    public void calculatePayment() {

        double principal = 100000;

        double i = 0.05f;

        int n = 20;

        double payment2 = principal * i / (1 - Math.pow(1 + i, -n));

        System.out.println(payment2 / 12);

    }

    @Test
    public void calculatePrincipalAndInterests() {

        double remainingCapital = 90000;

        double i = 0.05;

        int n = 12;

        double payment = 670.05;

        double interests = i * remainingCapital / n;

        double principal = payment - interests;

        System.out.println(payment);
        System.out.println(principal);
        System.out.println(interests);

    }
}