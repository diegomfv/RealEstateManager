package com.diegomfv.android.realestatemanager.models;

import java.util.Date;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 07/09/2018.
 */
public class Payment {

    private static final String TAG = Payment.class.getSimpleName();

    private Date paymentDate;

    private float beginningBalance;

    private float schPayment;

    private float principal;

    private float interests;

    private float totalPayment;

    private float endingBalance;

    private float cumInterests;

    public Payment(Date paymentDate, float beginningBalance, float schPayment, float principal, float interests, float totalPayment, float endingBalance, float cumInterests) {
        this.paymentDate = paymentDate;
        this.beginningBalance = beginningBalance;
        this.schPayment = schPayment;
        this.principal = principal;
        this.interests = interests;
        this.totalPayment = totalPayment;
        this.endingBalance = endingBalance;
        this.cumInterests = cumInterests;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentDate=" + paymentDate +
                ", beginningBalance=" + beginningBalance +
                ", schPayment=" + schPayment +
                ", principal=" + principal +
                ", interests=" + interests +
                ", totalPayment=" + totalPayment +
                ", endingBalance=" + endingBalance +
                ", cumInterests=" + cumInterests +
                '}';
    }
}
