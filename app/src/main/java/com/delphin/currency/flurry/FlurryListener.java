package com.delphin.currency.flurry;

import android.content.Context;

import com.delphin.currency.R;

public abstract class FlurryListener {
    protected String getApiKey(Context context) {
        return context.getResources().getString(R.string.flurry_api_key);
    }
}
