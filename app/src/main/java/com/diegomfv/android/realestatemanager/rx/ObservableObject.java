package com.diegomfv.android.realestatemanager.rx;

import java.util.Observable;

/**
 * Created by Diego Fajardo on 13/07/2018.
 */
public class ObservableObject extends Observable {

    private static ObservableObject instance = new ObservableObject();

    public static ObservableObject getInstance () {
        return instance;
    }

    private ObservableObject() {}

    public void update (Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }

}
