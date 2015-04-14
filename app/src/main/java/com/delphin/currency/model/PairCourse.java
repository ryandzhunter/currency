package com.delphin.currency.model;

import java.io.Serializable;

import greendao.GlobalCourses;

public class PairCourse implements Serializable {
    public GlobalCourses current;
    public GlobalCourses previous;

    public PairCourse(GlobalCourses current, GlobalCourses previous) {
        this.current = current;
        this.previous = previous;
    }
}
