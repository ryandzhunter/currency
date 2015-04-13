package com.delphin.currency.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class GlobalCurrencies implements CurrencyCourse {
    public static final String SUCCESS = "Success";

    /*
        {
            "Outcome": "Success",
            "Message": null,
            "Identity": "Request",
            "Delay": 0.0073843,
            "BaseCurrency": "USD",
            "QuoteCurrency": "RUB",
            "Symbol": "USDRUB",
            "Date": "04/13/2015",
            "Time": "6:38:10 AM",
            "QuoteType": "Calculated",
            "Bid": 53.169,
            "Mid": 53.3755,
            "Ask": 53.582,
            "Spread": 0.413,
            "Text": "1 United States dollar = 53.3755 Russian ruble",
            "Source": "SIX Financial Information"
        }
     */
    @SerializedName("Outcome")
    public String outcome;
    @SerializedName("BaseCurrency")
    public String baseCurrency;
    @SerializedName("QuoteCurrency")
    public String quoteCurrency;
    @SerializedName("Mid")
    public Double mid;

    @SerializedName("Date")
    public Date date;
    @SerializedName("Time")
    public Date time;

    @Override
    public Double getValue() {
        return mid;
    }

    @Override
    public void setValue(Double value) {
        this.mid = value;
    }

    @Override
    public boolean isSuccess() {
        return SUCCESS.equalsIgnoreCase(outcome);
    }
}
