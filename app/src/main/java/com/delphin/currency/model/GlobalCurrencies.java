package com.delphin.currency.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darkdelphin on 12.04.2015.
 */
public class GlobalCurrencies implements CurrencyCourse {
    @SerializedName("Mid")
    public Double mid;

    @Override
    public Double getValue() {
        return mid;
    }
}
