package com.delphin.currency;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.EApplication;

import greendao.DaoMaster;
import greendao.DaoSession;

@EApplication
public class CurrencyApplication extends Application {
    public DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        setupDatabase();
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "currency-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
}
