package com.diegomfv.android.realestatemanager;

import com.diegomfv.android.realestatemanager.utils.Utils;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Random;

import static org.junit.Assert.*;

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



}