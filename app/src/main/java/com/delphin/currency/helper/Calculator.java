package com.delphin.currency.helper;

import org.androidannotations.annotations.EBean;

@EBean
public class Calculator {
    public double eurToUsd(double eur, double usd) {
        return eur / usd;
    }

    public double oilRoubleCost(double usd, double oil) {
        return usd * oil;
    }
}
