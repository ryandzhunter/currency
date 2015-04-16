package com.delphin.currency.helper;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class ColorHelper {
    @RootContext
    Context context;

    public int getCurrencyDiffColor(Double value, Double previous) {
        return context.getResources().getColor(previous != null ? previous < value ?
                android.R.color.holo_red_light : android.R.color.holo_green_light : android.R.color.black);
    }

    public int getOilDiffColor(Double value, Double previous) {
        return context.getResources().getColor(previous != null ? previous > value ?
                android.R.color.holo_red_light : android.R.color.holo_green_light : android.R.color.black);
    }
}
