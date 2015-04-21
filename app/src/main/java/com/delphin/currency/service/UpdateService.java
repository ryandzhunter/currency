package com.delphin.currency.service;

import android.content.Intent;

import com.delphin.currency.model.PairCourse;
import com.delphin.currency.notification.CurrencyNotificationManager;
import com.delphin.currency.otto.OttoBus;
import com.delphin.currency.otto.events.ImmediatelyUpdateActionEvent;
import com.delphin.currency.otto.events.RefreshActionEvent;
import com.delphin.currency.otto.events.ShowNotificationImmediatelyEvent;
import com.delphin.currency.retrofit.network.CurrencyRetrofitRequest;
import com.delphin.currency.storage.GlobalCurrencyRepository;
import com.delphin.currency.storage.Storage_;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import greendao.GlobalCourses;

@EService
public class UpdateService extends SpiceService {
    public static final long EXECUTION_PERIOD = 20 * DurationInMillis.ONE_SECOND;

    @Bean
    protected GlobalCurrencyRepository globalCurrencyRepository;

    @Bean
    CurrencyNotificationManager currencyNotificationManager;

    @Pref
    Storage_ storage;

    @Bean
    OttoBus ottoBus;

    protected Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getCourseImmediately();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ottoBus.register(this);
        timer = new Timer();
        timer.schedule(executionTask, 0L, EXECUTION_PERIOD);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ottoBus.unregister(this);
        if (timer != null)
            timer.cancel();
    }

    private TimerTask executionTask = new TimerTask() {
        @Override
        public void run() {
            getCourse();
        }
    };

    private void getCourse() {
        spiceManager.execute(new CurrencyRetrofitRequest(),
                new Date().toString(), DurationInMillis.ALWAYS_EXPIRED, new CurrencyRequestListener());
    }

    private void getCourseImmediately() {
        PairCourse pairCourse = lastPair();
        if (pairCourse.current != null) {
            sendData(pairCourse);
        } else getCourse();
    }

    private PairCourse lastPair() {
        GlobalCourses last = getLast();
        return new PairCourse(last, getPrevious(last));
    }

    private GlobalCourses getPrevious(GlobalCourses last) {
        return globalCurrencyRepository.getPrevious(last);
    }

    private void sendData(PairCourse pairCourse) {
        ottoBus.post(pairCourse);
        showNotificationIfShould(pairCourse);
    }

    private void showNotificationIfShould(PairCourse pairCourse) {
        if (storage.notificationVisibility().get()) {
            currencyNotificationManager.updateNotification(pairCourse.current, pairCourse.previous);
        }
    }

    private void save(GlobalCourses currencyCourse) {
        globalCurrencyRepository.save(currencyCourse);
    }

    private GlobalCourses getLast() {
        Boolean dailyDifferenceActive = storage.dailyDifferenceActive().get();
        if (dailyDifferenceActive) {
            return globalCurrencyRepository.getFirstTodayCourse();
        } else return globalCurrencyRepository.getLastInserted();
    }

    @Subscribe
    public void onImmediatelyUpdateCalling(ImmediatelyUpdateActionEvent event) {
        getCourseImmediately();
    }

    @Subscribe
    public void onRefreshCalling(RefreshActionEvent event) {
        getCourse();
    }

    @Subscribe
    public void onImmediatelyNotificationShowing(ShowNotificationImmediatelyEvent event) {
        showNotificationIfShould(lastPair());
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
            sendData(new PairCourse(currencyCourse, getPrevious(currencyCourse)));
        }
    }
}
