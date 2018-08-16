package com.diegomfv.android.realestatemanager.data;

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.entities.Image;
import com.diegomfv.android.realestatemanager.data.entities.Place;
import com.diegomfv.android.realestatemanager.data.entities.RealState;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class FakeDataGenerator {

    private static final String TAG = FakeDataGenerator.class.getSimpleName();

    private String[] fake_types = {"flat", "apartment", "house"};

    private float[] fake_surface_area = {100f, 200f, 300f, 400f, 500f, 600f};

    private int[] fake_number_rooms = {1,2,3,4,5,6,7,8};

    private String[] fake_description = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Integer et nulla pharetra ligula fringilla viverra. Maecenas erat massa, " +
                    "vulputate vel iaculis quis, molestie sed orci. Integer nibh nulla, efficitur vitae ipsum in, " +
                    "cursus tempus risus. Mauris pellentesque congue facilisis. Sed eget elit ut neque facilisis lobortis. " +
                    "Morbi condimentum ligula ut neque posuere, eu vestibulum ligula ultricies. Suspendisse placerat ex hendrerit " +
                    "laoreet pharetra. Suspendisse potenti. Cras non dui nec ligula pretium sodales. " +
                    "Nulla ac leo et sem sollicitudin consequat eu non mi.",

            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque ex ex, " +
                    "hendrerit sit amet enim eu, mollis varius turpis. " +
                    "Class aptent taciti sociosqu ad litora torquent per conubia nostra, " +
                    "per inceptos himenaeos. Sed rhoncus justo ac ante pharetra, " +
                    "et scelerisque nulla mattis. Etiam mollis maximus ex sit amet faucibus. " +
                    "Integer tempor arcu purus. Suspendisse dictum auctor accumsan. " +
                    "Fusce hendrerit, magna",

            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi fermentum faucibus tempus. " +
                    "Aliquam consectetur facilisis augue, vitae porttitor mauris elementum ac. " +
                    "Vivamus in varius dolor. Suspendisse id egestas dui. Vivamus mattis suscipit velit, " +
                    "ut accumsan augue blandit id. Maecenas semper elit ante, id porta lacus commodo ac. " +
                    "Praesent lorem nisi, accumsan nec enim et, pretium tincidunt arcu. Vivamus hendrerit " +
                    "tortor libero, a vestibulum nisl faucibus vitae. Pellentesque volutpat suscipit ligula, " +
                    "a gravida lectus rhoncus ac. Vivamus placerat, quam vitae ultrices vulputate, odio lorem " +
                    "lacinia sem, imperdiet hendrerit est dolor in sem."

    };

    private String[] fake_address = {"B1A AAB", "C2A XFB", "KA1 254", "LF5 2J1"};

    private String[] fake_agent = {"agent1@gmail.com", "agent2@gmail.com", "agent3@gmail.com"};

    /////////////////////////////////////////////////////

    public RealState generateFakeData () {
        Log.d(TAG, "generateFakeData: called!");

        RealState.Builder builder = new RealState.Builder();
        builder.setType(fake_types[randBetween(0,2)]);
        builder.setSurfaceArea(fake_surface_area[randBetween(0,5)]);
        builder.setNumberOfRooms(fake_number_rooms[randBetween(0,7)]);
        builder.setDescription(fake_description[randBetween(0,2)]);
        builder.setAddress(fake_address[randBetween(0,4)]);
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
