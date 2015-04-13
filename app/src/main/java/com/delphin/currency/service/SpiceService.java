package com.delphin.currency.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.delphin.currency.retrofit.network.CurrencyRetrofitService_;
import com.octo.android.robospice.SpiceManager;

public class SpiceService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected SpiceManager spiceManager = new SpiceManager(CurrencyRetrofitService_.class);

    @Override
    public void onCreate() {
        super.onCreate();
        spiceManager.start(this);
    }

    @Override
    public void onDestroy() {
        spiceManager.shouldStop();
        super.onDestroy();
    }
}
