package com.diegomfv.android.realestatemanager.data;

import android.annotation.SuppressLint;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
// TODO: 26/08/2018 Add a textWatcher (RxJava) to the price and surface textViews
// TODO: 26/08/2018 To format the value (add dots)
public class FakeDataGenerator {

    private static final String TAG = FakeDataGenerator.class.getSimpleName();

    private String[] fake_types = {"Flat", "Apartment", "House"};

    private String[] fake_description = {
            "This spacious -96 sqm/1033 sqft - modern and minimal apartment is the perfect choice for " +
                    "friends or family with up to four persons who want both, to be in the heart of " +
                    "the city and at the same time have privacy.",

            "The flat is just over 100 square metres (about 1,100 sq. ft.) and is comprised of a spacious lounge, " +
                    "a separate dining room, with open kitchen, and a central hall off which are three bedrooms and two bathrooms. " +
                    "There is also a private roof terrace which is also just over 100 square metres.\t",

            "The lounge is large, and features French doors looking out at the campanile (bell-tower) " +
                    "of the San Francisco church and the surrounding rooftops. These bells, by the way, " +
                    "only ring once a day, at noon, except on Sundays when they ring two or three times, " +
                    "but not early. The lounge has a ceiling fan, as well as a heat pump (for heat or air conditioning) " +
                    "and is illuminated by beautiful modern gallery-style lighting. It is furnished with 3 sofas, " +
                    "a coffee table and bookcases. A sound system with CD, cassette and radio is provided, " +
                    "and for 2006 we will provide a television and DVD player."
    };


    private String[] fake_agent = {"agent1@gmail.com", "agent2@gmail.com", "agent3@gmail.com"};

    /////////////////////////////////////////////////////

    public RealEstate generateFakeData () {
        Log.d(TAG, "generateFakeData: called!");

        RealEstate.Builder builder = new RealEstate.Builder();
        builder.setType(fake_types[randBetween(0,2)]);
        builder.setSurfaceArea(randBetween(50,600));
        builder.setPrice(randBetween(100000, 1000000));
        builder.setBedrooms(randBetween(1,3));
        builder.setBathrooms(randBetween(1,3));
        builder.setOtherRooms(randBetween(1,3));
        builder.setDescription(fake_description[randBetween(0,2)]);
        builder.setAgent(fake_agent[randBetween(0,2)]);
        builder.setDatePut(getRandomFakeDate());

        return builder.build();

    }

    private String getRandomFakeDate() {
        Log.d(TAG, "getRandomFakeDate: called!");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, - randBetween(1,200));
        return dateToString(getDateFromCalendar(calendar));

    }

    private Date getDateFromCalendar (Calendar calendar) {
        Log.d(TAG, "getDateFromCalendar: called!");
        return calendar.getTime();
    }

    private String dateToString (Date date) {
        Log.d(TAG, "dateToString: called!");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    private int randBetween(int start, int end) {
        Log.d(TAG, "randBetween: called!");
        return start + (int)Math.round(Math.random() * (end - start));
    }

}
