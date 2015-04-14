package com.delphin.currency.retrofit.network;

import com.delphin.currency.api.Api;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import org.androidannotations.annotations.EService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greendao.GlobalCourses;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

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

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder = super.createRestAdapterBuilder();
        builder.setConverter(new ZenRusConverter());
        return builder;
    }

    private class ZenRusConverter implements Converter {
        protected Pattern pattern = Pattern.compile(".+\\[(.+)\\,(.+)\\,(.+)\\]");

        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            GlobalCourses courses = new GlobalCourses();
            courses.setDate(new Date());

            parseData(body, courses);
            return courses;
        }

        @Override
        public TypedOutput toBody(Object object) {
            return null;
        }

        private void parseData(TypedInput body, GlobalCourses courses) {
            try {
                InputStream in = body.in();
                java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                Matcher matcher = pattern.matcher(response);
                if (matcher.matches()) {
                    String usd = matcher.group(1);
                    String eur = matcher.group(2);
                    String oil = matcher.group(3);

                    courses.setUsd(Double.parseDouble(usd));
                    courses.setEur(Double.parseDouble(eur));
                    courses.setOil(Double.parseDouble(oil));
                }
            } catch (IOException | NumberFormatException ignored) {

            }
        }
    }
}
