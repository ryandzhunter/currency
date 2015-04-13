package com.delphin.currency.storage;

import com.delphin.currency.CurrencyApplication;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
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

    public GlobalCourses getLastInserted(String course) {
        QueryBuilder<GlobalCourses> builder = getGlobalCoursesDao().queryBuilder();
        builder.where(new WhereCondition.PropertyCondition(GlobalCoursesDao.Properties.Course, course));
        return builder.orderDesc(GlobalCoursesDao.Properties.Date).limit(1).unique();
    }
}
