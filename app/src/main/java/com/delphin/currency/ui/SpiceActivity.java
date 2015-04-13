package com.delphin.currency.ui;

import android.app.Activity;

import com.delphin.currency.retrofit.network.CurrencyRetrofitService_;
import com.octo.android.robospice.SpiceManager;

public abstract class SpiceActivity extends Activity {
    protected SpiceManager spiceManager = new SpiceManager(CurrencyRetrofitService_.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
}
