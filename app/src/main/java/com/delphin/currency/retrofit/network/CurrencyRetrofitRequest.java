package com.delphin.currency.retrofit.network;

import com.delphin.currency.api.Api;
import com.delphin.currency.model.GlobalCurrencies;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.Map;

/**
 * Created by darkdelphin on 12.04.2015.
 */
public class CurrencyRetrofitRequest extends RetrofitSpiceRequest<GlobalCurrencies, Api> {
    protected Map<String, String> params;

    public CurrencyRetrofitRequest(Map<String, String> params) {
        super(GlobalCurrencies.class, Api.class);
        this.params = params;
    }

    @Override
    public GlobalCurrencies loadDataFromNetwork() throws Exception {
        return getService().getCourses(params);
    }
}
