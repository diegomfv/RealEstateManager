package com.diegomfv.android.realestatemanager;

import com.diegomfv.android.realestatemanager.util.Utils;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class UnitTest {

    @Test
    public void isInteger () {

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
    public void decimalFormat () {

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
    public void getRidOfLastComma () {

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
    public void stringToDateAndComparison () {

        String string1 = "05/09/2018";
        String string2 = "06/09/2018";
        String string3 = "07/09/2018";
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        try {
            Date date1 = format.parse(string1);
            Date date2 = format.parse(string2);
            Date date3 = format.parse(string3);

            assertTrue (date3.after(date2));
            assertTrue (date2.after(date1));
            assertTrue (date1.before(date2));
            assertFalse (date1.after(date3));

        } catch (ParseException e) {
            System.out.println("Parse exception");
        }
    }

    @Test
    public void calculatePayment () {

        double principal = 100000;

        double i = 0.05f;

        int n = 20;

        double payment2 = principal * i / (1 - Math.pow(1+i, -n));

        System.out.println(payment2 / 12);

    }

    @Test
    public void calculatePrincipalAndInterests () {

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