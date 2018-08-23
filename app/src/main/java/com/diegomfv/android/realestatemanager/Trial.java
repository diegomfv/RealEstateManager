package com.diegomfv.android.realestatemanager;

/**
 * Created by Diego Fajardo on 22/08/2018.
 */
public class Trial {

    private Rx rx;

    public Trial() {
    }

    public int deploy_rx () {

        Rx rx = new Rx() {
            @Override
            public int onNext() {
                return 0;
            }

            @Override
            public int onSuccess() {
                return 0;
            }

            @Override
            public int onError() {
                return 0;
            }
        };

        return rx.onSuccess();

    }

    private interface Rx {
        int onNext();
        int onSuccess();
        int onError();
    }

}



