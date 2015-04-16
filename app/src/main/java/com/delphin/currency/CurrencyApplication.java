package com.delphin.currency;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.flurry.android.FlurryAgent;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.res.StringRes;

import greendao.DaoMaster;
import greendao.DaoSession;

@EApplication
public class CurrencyApplication extends Application {
    public DaoSession daoSession;

    @StringRes(R.string.flurry_api_key)
    String flurryApiKey;

    @Override
    public void onCreate() {
        super.onCreate();
        setupDatabase();
        FlurryAgent.init(this, flurryApiKey);
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "currency-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
}
