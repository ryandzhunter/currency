package com.delphin.currency.retrofit.network;

import android.text.TextUtils;

import com.delphin.currency.api.Api;
import com.delphin.currency.model.GlobalCurrencies;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class CurrencyRetrofitRequest extends RetrofitSpiceRequest<GlobalCurrencies, Api> {
    protected Map<String, String> params;
    protected RestAdapter restAdapter;

    public CurrencyRetrofitRequest(Map<String, String> params) {
        super(GlobalCurrencies.class, Api.class);
        this.params = params;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String s = json.getAsString();
                        if (!TextUtils.isEmpty(s)) {
                            try {
                                return new SimpleDateFormat("MM/dd/yyyy").parse(s);
                            } catch (ParseException e) {
                                try {
                                    return SimpleDateFormat.getTimeInstance().parse(s);
                                } catch (ParseException e1) {
                                    return null;
                                }
                            }
                        }
                        return null;
                    }
                })
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String s = json.getAsString();
                        if (!TextUtils.isEmpty(s)) {
                            return new Date();
                        }
                        return null;
                    }
                })
                .create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(Api.URL)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    @Override
    public GlobalCurrencies loadDataFromNetwork() throws Exception {
        return getService().getCourses(params);
    }

    @Override
    public Api getService() {
        return restAdapter.create(Api.class);
    }
}
