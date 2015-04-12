package com.delphin.currency.api;

import com.delphin.currency.model.GlobalCurrencies;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by darkdelphin on 12.04.2015.
 */
public interface Api {
    public static final String URL = "http://globalcurrencies.xignite.com";

    @GET(value = "/xGlobalCurrencies.json/GetRealTimeRate?_token=D0533439180E4203924E873B0FEF3A35")
    GlobalCurrencies getCourses(@QueryMap Map<String, String> params);
}
