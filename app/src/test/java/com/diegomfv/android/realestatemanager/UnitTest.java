package com.diegomfv.android.realestatemanager;

import com.diegomfv.android.realestatemanager.util.Utils;

import junit.framework.Assert;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

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
            Assert.assertTrue(Utils.isInteger(String.valueOf(random.nextInt())));
        }

        Assert.assertFalse(Utils.isInteger("a"));
        Assert.assertFalse(Utils.isInteger("aasjhhfwe"));
        Assert.assertFalse(Utils.isInteger("aasdbfyur   f"));
        Assert.assertFalse(Utils.isInteger("1237  y4726384"));

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

}