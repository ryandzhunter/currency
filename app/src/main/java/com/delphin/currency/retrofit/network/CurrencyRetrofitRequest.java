package com.delphin.currency.retrofit.network;

import com.delphin.currency.api.Api;
import com.delphin.currency.model.GlobalCurrencies;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class CurrencyRetrofitRequest extends RetrofitSpiceRequest<GlobalCurrencies, Api> {

    public CurrencyRetrofitRequest() {
        super(GlobalCurrencies.class, Api.class);
    }

    @Override
    public GlobalCurrencies loadDataFromNetwork() throws Exception {
        return getService().getCourses();
    }
}
