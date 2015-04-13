package com.delphin.currency.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.delphin.currency.config.Courses;
import com.delphin.currency.config.ReceiverAction;
import com.delphin.currency.model.GlobalCurrencies;
import com.delphin.currency.retrofit.network.CurrencyRetrofitRequest;
import com.delphin.currency.storage.GlobalCurrencyRepository;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import greendao.GlobalCourses;

@EService
public class UpdateService extends SpiceService {
    public static final long EXECUTION_DELAY = 10 * DurationInMillis.ONE_SECOND;
    public static final long EXECUTION_PERIOD = 20 * DurationInMillis.ONE_SECOND;

    private Timer timer;
    private Handler mHandler = new CourseHandler();

    @Bean
    protected GlobalCurrencyRepository globalCurrencyRepository;

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
            updateCourses();
        }
    };

    private void updateCourses() {
        Message msg = new Message();
        msg.obj = Courses.USD_RUB;
        mHandler.dispatchMessage(msg);
        msg.obj = Courses.EUR_RUB;
        mHandler.dispatchMessage(msg);
    }

    private void getCourse(String course) {
        spiceManager.execute(new CurrencyRetrofitRequest(Collections.singletonMap("symbol", course)),
                course, EXECUTION_DELAY, new CurrencyRequestListener(course));
    }

    private class CurrencyRequestListener implements RequestListener<GlobalCurrencies> {
        protected String course;

        private CurrencyRequestListener(String course) {
            this.course = course;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            GlobalCurrencies lastCourse = getLast(course);
            if (lastCourse != null)
                onRequestSuccess(lastCourse);
            Toast.makeText(UpdateService.this, spiceException.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(GlobalCurrencies currencyCourse) {
            if (currencyCourse.isSuccess()) {
                Double value = currencyCourse.getValue();
                GlobalCurrencies lastCourse = getLast(course);

                sendBroadcast(new Intent(ReceiverAction.ON_COURSE_UPDATE_ACTION)
                        .putExtra("course", course)
                        .putExtra("value", value)
                        .putExtra("prev", lastCourse != null ? lastCourse.getValue() : value));

                save(currencyCourse, course);
            } else onRequestFailure(new SpiceException("Wrong response"));
        }
    }

    private void save(GlobalCurrencies currencyCourse, String course) {
        GlobalCourses globalCourses = new GlobalCourses();
        globalCourses.setBaseCurrency(currencyCourse.baseCurrency);
        globalCourses.setQuoteCurrency(currencyCourse.quoteCurrency);
        globalCourses.setValue(currencyCourse.getValue());
        if (currencyCourse.date != null && currencyCourse.time != null)
            globalCourses.setDate(DateUtils.addMilliseconds(currencyCourse.date, (int) currencyCourse.time.getTime()));
        globalCourses.setCourse(course);

        globalCurrencyRepository.save(globalCourses);
    }

    private GlobalCurrencies getLast(String course) {
        GlobalCourses courses = globalCurrencyRepository.getLastInserted(course);
        if (courses == null) return null;

        GlobalCurrencies globalCurrencies = new GlobalCurrencies();
        globalCurrencies.setValue(courses.getValue());
        globalCurrencies.outcome = GlobalCurrencies.SUCCESS;
        return globalCurrencies;
    }

    class CourseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String request = (String) msg.obj;
            getCourse(request);
        }

    }
}
