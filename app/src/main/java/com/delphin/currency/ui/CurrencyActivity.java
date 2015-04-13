package com.delphin.currency.ui;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.delphin.currency.R;
import com.delphin.currency.config.Courses;
import com.delphin.currency.model.GlobalCurrencies;
import com.delphin.currency.retrofit.network.CurrencyRetrofitRequest;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.activity_currency)
public class CurrencyActivity extends SpiceActivity {
    public static final long EXECUTION_DELAY = 10 * DurationInMillis.ONE_SECOND;
    public static final long EXECUTION_PERIOD = 20 * DurationInMillis.ONE_SECOND;

    @ViewById(R.id.usd_rub)
    protected TextView usdRub;

    @ViewById(R.id.eur_rub)
    protected TextView eurRub;

    @InstanceState
    protected HashMap<String, Double> previousCurrency;

    private Timer timer;

    @AfterViews
    void afterViews() {
        timer = new Timer();
        timer.schedule(executionTask, 0L, EXECUTION_PERIOD);
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
            Toast.makeText(CurrencyActivity.this, spiceException.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(GlobalCurrencies currencyCourse) {
            if (currencyCourse.isSuccess()) {
                Double value = currencyCourse.getValue();
                Double previousValue = popPrevious(course);

                String valueStr = String.format("%s (%s)", String.format("%.2f", value),
                        convertDiff(value, previousValue));
                setValue(valueStr);
                setColor(value, previousValue);

                pushPrevious(course, value);
            } else onRequestFailure(new SpiceException("Wrong response"));
        }

        private String convertDiff(Double value, Double previousValue) {
            if (previousValue == null) return "0.0";
            double diff = value - previousValue;
            return (diff < 0 ? "" : "+") + String.format("%.4f", diff);
        }

        private void setValue(String value) {
            if (Courses.USD_RUB.equalsIgnoreCase(course)) {
                usdRub.setText(value);
            } else if (Courses.EUR_RUB.equalsIgnoreCase(course)) {
                eurRub.setText(value);
            }
        }

        private void setColor(Double value, Double previous) {
            if (Courses.USD_RUB.equalsIgnoreCase(course)) {
                usdRub.setTextColor(getColor(value, previous));
            } else if (Courses.EUR_RUB.equalsIgnoreCase(course)) {
                eurRub.setTextColor(getColor(value, previous));
            }
        }

        private int getColor(Double value, Double previous) {
            return getResources().getColor(previous != null ? previous < value ?
                    android.R.color.holo_red_light : android.R.color.holo_green_light : android.R.color.black);
        }
    }

    private Handler mHandler = new CourseHandler();

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

    private Double popPrevious(String course) {
        if (previousCurrency != null) {
            return previousCurrency.get(course);
        }
        return null;
    }

    private void pushPrevious(String course, Double value) {
        if (previousCurrency == null) {
            previousCurrency = new HashMap<>();
        }
        previousCurrency.put(course, value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }

    class CourseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String request = (String) msg.obj;
            getCourse(request);
        }
    }
}
