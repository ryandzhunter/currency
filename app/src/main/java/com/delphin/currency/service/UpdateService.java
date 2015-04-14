package com.delphin.currency.service;

import android.content.Intent;

import com.delphin.currency.model.PairCourse;
import com.delphin.currency.notification.CurrencyNotificationManager;
import com.delphin.currency.otto.OttoBus;
import com.delphin.currency.retrofit.network.CurrencyRetrofitRequest;
import com.delphin.currency.storage.GlobalCurrencyRepository;
import com.delphin.currency.storage.Storage_;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import greendao.GlobalCourses;

@EService
public class UpdateService extends SpiceService {
    public static final long EXECUTION_DELAY = 10 * DurationInMillis.ONE_SECOND;
    public static final long EXECUTION_PERIOD = 20 * DurationInMillis.ONE_SECOND;

    private Timer timer;

    @Bean
    protected GlobalCurrencyRepository globalCurrencyRepository;

    @Bean
    CurrencyNotificationManager currencyNotificationManager;

    @Pref
    Storage_ storage;

    @Bean
    OttoBus ottoBus;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getCourseImmediately();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(executionTask, 0L, EXECUTION_PERIOD);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }

    private TimerTask executionTask = new TimerTask() {
        @Override
        public void run() {
            getCourse(EXECUTION_DELAY);
        }
    };

    private void getCourse(long executionDelay) {
        spiceManager.execute(new CurrencyRetrofitRequest(),
                new Date().toString(), executionDelay, new CurrencyRequestListener());
    }

    private void getCourseImmediately() {
        getCourse(0L);
    }

    private class CurrencyRequestListener implements RequestListener<GlobalCourses> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            GlobalCourses lastCourse = getLast();
            if (lastCourse != null) {
                show(lastCourse);
            }
        }

        @Override
        public void onRequestSuccess(GlobalCourses currencyCourse) {
            show(currencyCourse);
            if (currencyCourse.getId() == null) {
                save(currencyCourse);
            }
        }

        private void show(GlobalCourses currencyCourse) {
            GlobalCourses lastCourse = getLast();

            ottoBus.post(new PairCourse(currencyCourse, lastCourse));

            if (storage.notificationVisibility().get()) {
                currencyNotificationManager.updateNotification(currencyCourse, lastCourse);
            }
        }
    }

    private void save(GlobalCourses currencyCourse) {
        globalCurrencyRepository.save(currencyCourse);
    }

    private GlobalCourses getLast() {
        return globalCurrencyRepository.getLastInserted();
    }
}
