package com.delphin.currency.helper;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by darkdelphin on 14.04.2015.
 */
@EBean
public class ColorHelper {
    @RootContext
    Context context;

    public int getColor(Double value, Double previous) {
        return context.getResources().getColor(previous != null ? previous < value ?
                android.R.color.holo_red_light : android.R.color.holo_green_light : android.R.color.black);
    }
}