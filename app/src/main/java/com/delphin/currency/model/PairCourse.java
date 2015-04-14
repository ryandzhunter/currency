package com.delphin.currency.model;

import greendao.GlobalCourses;

public class PairCourse {
    public GlobalCourses current;
    public GlobalCourses previous;

    public PairCourse(GlobalCourses current, GlobalCourses previous) {
        this.current = current;
        this.previous = previous;
    }
}
