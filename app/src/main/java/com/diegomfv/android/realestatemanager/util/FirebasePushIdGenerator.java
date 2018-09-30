package com.diegomfv.android.realestatemanager.util;

/**
 * Created by swftvsn (github)
 */

/** This class generates a completely unique id
 */
public class FirebasePushIdGenerator {

    // Modeled after base64 web-safe chars, but ordered by ASCII.
    private final static String PUSH_CHARS = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";

    // Timestamp of last push, used to prevent local collisions if you push twice in one ms.
    private static long LAST_PUSH_TIME = 0L;

    // We generate 72-bits of randomness which get turned into 12 characters and
    // appended to the timestamp to prevent collisions with other clients. We store the last
    // characters we generated because in the event of a collision, we'll use those same
    // characters except "incremented" by one.
    private static int[] LAST_RAND_CHARS = new int[72];

    /**
     * Method that generates a completely new id that is unique
     */
    public static synchronized String generate() {

        long now = System.currentTimeMillis();
        boolean duplicateTime = now == LAST_PUSH_TIME;
        LAST_PUSH_TIME = now;

        char[] timeStampChars = new char[8];
        for (int i = 7; i >= 0; i--) {
            timeStampChars[i] = PUSH_CHARS.charAt((int) (now % 64));
            now = (long) Math.floor(now / 64);
        }

        if (now != 0) {
            throw new AssertionError("We should have converted the entire timestamp.");
        }

        StringBuilder id = new StringBuilder(20);
        for (char c : timeStampChars) {
            id.append(c);
        }

        if (!duplicateTime) {
            for (int i = 0; i < 12; i++) {
                LAST_RAND_CHARS[i] = (int) Math.floor(Double.valueOf(Math.random() * 64).intValue());
            }
        } else {
            // If the timestamp hasn't changed since last push, use the same random number,
            //except incremented by 1.
            int i = 0;
            for (i = 11; i >= 0 && LAST_RAND_CHARS[i] == 63; i--) {
                LAST_RAND_CHARS[i] = 0;
            }
            LAST_RAND_CHARS[i]++;
        }

        for (int i = 0; i < 12; i++) {
            id.append(PUSH_CHARS.charAt(LAST_RAND_CHARS[i]));
        }

        if (id.length() != 20) {
            throw new AssertionError("Length should be 20.");
        }

        return id.toString();
    }

}