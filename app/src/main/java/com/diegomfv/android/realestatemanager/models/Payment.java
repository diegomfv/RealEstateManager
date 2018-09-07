package com.diegomfv.android.realestatemanager.models;

import java.util.Date;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 07/09/2018.
 */
public class Payment {

    private static final String TAG = Payment.class.getSimpleName();

    private int payN;

    private Date paymentDate;

    private float beginningBalance;

    private float schPayment;

    private float principal;

    private float interests;

    private float endingBalance;

    private float cumInterests;

    public Payment(int payN, Date paymentDate, float beginningBalance, float schPayment, float principal, float interests, float endingBalance, float cumInterests) {
        this.payN = payN;
        this.paymentDate = paymentDate;
        this.beginningBalance = beginningBalance;
        this.schPayment = schPayment;
        this.principal = principal;
        this.interests = interests;
        this.endingBalance = endingBalance;
        this.cumInterests = cumInterests;
    }

    private Payment(final Builder builder) {
        this.payN = builder.payN;
        this.paymentDate = builder.paymentDate;
        this.paymentDate = builder.paymentDate;
        this.beginningBalance = builder.beginningBalance;
        this.schPayment = builder.schPayment;
        this.principal = builder.principal;
        this.interests = builder.interests;
        this.endingBalance = builder.endingBalance;
        this.cumInterests = builder.cumInterests;

    }

    public int getPayN() {
        return payN;
    }

    public void setPayN(int payN) {
        this.payN = payN;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public float getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(float beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    public float getSchPayment() {
        return schPayment;
    }

    public void setSchPayment(float schPayment) {
        this.schPayment = schPayment;
    }

    public float getPrincipal() {
        return principal;
    }

    public void setPrincipal(float principal) {
        this.principal = principal;
    }

    public float getInterests() {
        return interests;
    }

    public void setInterests(float interests) {
        this.interests = interests;
    }

    public float getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(float endingBalance) {
        this.endingBalance = endingBalance;
    }

    public float getCumInterests() {
        return cumInterests;
    }

    public void setCumInterests(float cumInterests) {
        this.cumInterests = cumInterests;
    }

    public static class Builder {

        private int payN;
        private Date paymentDate;
        private float beginningBalance;
        private float schPayment;
        private float principal;
        private float interests;
        private float endingBalance;
        private float cumInterests;

        public Builder setPaynN (int payN) {
            this.payN = payN;
            return this;
        }

        public Builder setPaymentDate(Date date) {
            this.paymentDate = date;
            return this;
        }

        public Builder setBeginningBalance(float begginingBalance) {
            this.beginningBalance = begginingBalance;
            return this;
        }

        public Builder setSchPayment(float schPayment) {
            this.schPayment = schPayment;
            return this;
        }

        public Builder setPrincipal(float principal) {
            this.principal = principal;
            return this;
        }

        public Builder setInterests(float interests) {
            this.interests = interests;
            return this;
        }

        public Builder setEndingBalance(float endingBalance) {
            this.endingBalance = endingBalance;
            return this;
        }

        public Builder setCumInterests(float cumInterests) {
            this.cumInterests = cumInterests;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
