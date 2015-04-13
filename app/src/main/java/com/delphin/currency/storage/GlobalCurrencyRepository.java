package com.delphin.currency.storage;

import com.delphin.currency.CurrencyApplication;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.GlobalCourses;
import greendao.GlobalCoursesDao;

@EBean
public class GlobalCurrencyRepository {
    @App
    CurrencyApplication application;

    public void save(GlobalCourses globalCourses) {
        getGlobalCoursesDao().insert(globalCourses);
    }

    public GlobalCoursesDao getGlobalCoursesDao() {
        return application.daoSession.getGlobalCoursesDao();
    }

    public GlobalCourses getLastInserted() {
        QueryBuilder<GlobalCourses> builder = getGlobalCoursesDao().queryBuilder();
        return builder.orderDesc(GlobalCoursesDao.Properties.Date).limit(1).unique();
    }
}
