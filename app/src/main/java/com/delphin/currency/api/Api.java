package com.delphin.currency.api;

import greendao.GlobalCourses;
import retrofit.http.GET;

public interface Api {
    String URL = "http://zenrus.ru";

    @GET(value = "/js/build/currents.js")
    //var current = [52.42,55.30,58.46]
    GlobalCourses getCourses();
}
