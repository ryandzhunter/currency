package com.delphin.currency.retrofit.network;

import com.delphin.currency.api.Api;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import greendao.GlobalCourses;

public class CurrencyRetrofitRequest extends RetrofitSpiceRequest<GlobalCourses, Api> {

    public CurrencyRetrofitRequest() {
        super(GlobalCourses.class, Api.class);
    }

    @Override
    public GlobalCourses loadDataFromNetwork() throws Exception {
        return getService().getCourses();
    }
}
