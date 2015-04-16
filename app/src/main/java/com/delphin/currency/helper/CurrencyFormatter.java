package com.delphin.currency.helper;

import org.androidannotations.annotations.EBean;

@EBean
public class CurrencyFormatter {
    public String format(double value) {
        return String.format("%.2f", value);
    }

    public String formatToRouble(String value) {
        return String.format("%sa", value);
    }

    public String formatToUsd(String value) {
        return "$" + value;
    }

    public String formatDiff(Double value, Double previousValue) {
        if (previousValue == null) return "0.0";

        double diff = value - previousValue;
        return (diff < 0 ? "" : "+") + format(diff);
    }
}
