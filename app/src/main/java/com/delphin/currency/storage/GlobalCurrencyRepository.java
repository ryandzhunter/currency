package com.delphin.currency.storage;

import com.delphin.currency.CurrencyApplication;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import java.util.Calendar;

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

    public GlobalCourses getFirstTodayCourse() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        QueryBuilder<GlobalCourses> builder = getGlobalCoursesDao().queryBuilder();
        return builder.where(GlobalCoursesDao.Properties.Date.ge(calendar.getTime())).orderAsc(GlobalCoursesDao.Properties.Date).limit(1).unique();
    }

    public GlobalCourses getPrevious(GlobalCourses last) {
        if (last == null) return null;

        QueryBuilder<GlobalCourses> builder = getGlobalCoursesDao().queryBuilder();
        return builder.where(GlobalCoursesDao.Properties.Date.le(last.getDate())).orderDesc(GlobalCoursesDao.Properties.Date).limit(1).unique();
    }
}
