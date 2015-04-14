package com.delphin.currency.service;

import android.content.Intent;
import android.widget.Toast;

import com.delphin.currency.config.ReceiverAction;
import com.delphin.currency.notification.CurrencyNotificationManager;
import com.delphin.currency.retrofit.network.CurrencyRetrofitRequest;
import com.delphin.currency.storage.GlobalCurrencyRepository;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
            getCourse();
        }
    };

    private void getCourse() {
        spiceManager.execute(new CurrencyRetrofitRequest(),
                new Date().toString(), EXECUTION_DELAY, new CurrencyRequestListener());
    }

    private class CurrencyRequestListener implements RequestListener<GlobalCourses> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            GlobalCourses lastCourse = getLast();
            if (lastCourse != null)
                show(lastCourse);
            Toast.makeText(UpdateService.this, spiceException.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(GlobalCourses currencyCourse) {
            show(currencyCourse);
            save(currencyCourse);
        }

        private void show(GlobalCourses currencyCourse) {
            GlobalCourses lastCourse = getLast();

            sendBroadcast(new Intent(ReceiverAction.ON_COURSE_UPDATE_ACTION)
                    .putExtra("course", currencyCourse)
                    .putExtra("prev", lastCourse));
            currencyNotificationManager.updateNotification(currencyCourse, lastCourse);
        }
    }

    private void save(GlobalCourses currencyCourse) {
        globalCurrencyRepository.save(currencyCourse);
    }

    private GlobalCourses getLast() {
        return globalCurrencyRepository.getLastInserted();
    }
}
