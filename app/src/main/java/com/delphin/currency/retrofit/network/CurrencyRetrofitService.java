package com.delphin.currency.retrofit.network;

import com.delphin.currency.api.Api;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import org.androidannotations.annotations.EService;

/**
 * Created by darkdelphin on 12.04.2015.
 */
@EService
public class CurrencyRetrofitService extends RetrofitGsonSpiceService {
    @Override
    protected String getServerUrl() {
        return Api.URL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(Api.class);
    }
}
