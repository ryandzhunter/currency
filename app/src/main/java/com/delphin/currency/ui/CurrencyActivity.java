package com.delphin.currency.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.delphin.currency.R;
import com.delphin.currency.config.Config;
import com.delphin.currency.helper.Calculator;
import com.delphin.currency.helper.ColorHelper;
import com.delphin.currency.model.PairCourse;
import com.delphin.currency.otto.OttoBus;
import com.delphin.currency.otto.events.CheckServiceStatusEvent;
import com.delphin.currency.otto.events.ServiceIsRunningEvent;
import com.delphin.currency.otto.events.ShowNotificationImmediatelyEvent;
import com.delphin.currency.service.UpdateService_;
import com.delphin.currency.storage.Storage_;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import greendao.GlobalCourses;

@EActivity(R.layout.activity_currency)
public class CurrencyActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    @ViewById(R.id.usd_rub)
    protected TextView usdRub;

    @ViewById(R.id.eur_rub)
    protected TextView eurRub;

    @ViewById(R.id.oil)
    protected TextView oil;

    @ViewById(R.id.notification_visibility)
    protected CheckBox notificationVisibility;

    @ViewById(R.id.eur_usd)
    protected TextView eurUsd;

    @ViewById(R.id.oil_rub)
    protected TextView oilRub;

    @Bean
    protected ColorHelper colorHelper;

    @Bean
    protected OttoBus ottoBus;

    @Pref
    protected Storage_ storage;

    @SystemService
    protected NotificationManager notificationManager;

    @Bean
    protected Calculator calculator;

    @StringRes(R.string.currency_with_diff)
    protected String currencyWithDiff;

    @StringRes(R.string.format_eur_to_usd)
    protected String formatEurToUsd;

    protected Typeface roubleTypeface;
    protected boolean serviceIsRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roubleTypeface = Typeface.createFromAsset(getAssets(), "rouble.otf");
        ottoBus.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkServiceStatus();
    }

    private void checkServiceStatus() {
        ottoBus.post(new CheckServiceStatusEvent());
        startServiceIfShould();
    }

    @Subscribe
    public void onServiceRunning(ServiceIsRunningEvent event) {
        serviceIsRunning = true;
    }

    @Background
    protected void startServiceIfShould() {
        try {
            Thread.sleep(1000);
            if (!serviceIsRunning) {
                startService(new Intent(this, UpdateService_.class));
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ottoBus.unregister(this);
    }

    @AfterViews
    void afterViews() {
        setRoubleTypeface(usdRub, eurRub, oilRub);

        notificationVisibility.setChecked(storage.notificationVisibility().get());
        notificationVisibility.setOnCheckedChangeListener(this);
    }

    @Subscribe
    public void onCourseUpdate(PairCourse courses) {
        GlobalCourses course = courses.current;
        GlobalCourses previous = courses.previous != null ? courses.previous : course;

        String usdStr = formatCurrencyWithDiff(withRouble(cut(course.getUsd())), convertDiff(course.getUsd(), previous.getUsd()));
        String eurStr = formatCurrencyWithDiff(withRouble(cut(course.getEur())), convertDiff(course.getEur(), previous.getEur()));
        String oilStr = formatCurrencyWithDiff(cut(course.getOil()), convertDiff(course.getOil(), previous.getOil()));

        setValue(usdRub, usdStr);
        setValue(eurRub, eurStr);
        setValue(oil, "$" + oilStr);

        setCurrencyDiffColor(usdRub, course.getUsd(), previous.getUsd());
        setCurrencyDiffColor(eurRub, course.getEur(), previous.getEur());
        setOilDiffColor(oil, course.getOil(), previous.getOil());

        setValue(eurUsd, formatEurToUsd(cut(calculator.eurToUsd(course.getEur(), course.getUsd()))));
        setValue(oilRub, withRouble(cut(calculator.oilRoubleCost(course.getUsd(), course.getOil()))));
    }

    private String formatEurToUsd(String s) {
        return String.format(formatEurToUsd, s);
    }

    private String cut(double a) {
        return String.format("%.2f", a);
    }

    private String formatCurrencyWithDiff(String value, String diff) {
        return String.format(currencyWithDiff, value, diff);
    }

    private String convertDiff(Double value, Double previousValue) {
        if (previousValue == null) return "0.0";

        double diff = value - previousValue;
        return (diff < 0 ? "" : "+") + String.format("%.2f", diff);
    }

    private String withRouble(String s) {
        String pattern = "%sa";
        return String.format(pattern, s);
    }

    private void setValue(TextView container, String course) {
        container.setText(course);
    }

    private void setCurrencyDiffColor(TextView container, Double course, Double previous) {
        container.setTextColor(colorHelper.getCurrencyDiffColor(course, previous));
    }

    private void setOilDiffColor(TextView container, Double course, Double previous) {
        container.setTextColor(colorHelper.getOilDiffColor(course, previous));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        storage.notificationVisibility().put(isChecked);
        if (!isChecked) {
            notificationManager.cancel(Config.NOTIFICATION_ID);
        } else ottoBus.post(new ShowNotificationImmediatelyEvent());
    }

    private void setRoubleTypeface(TextView... views) {
        if (views != null && views.length > 0) {
            for (TextView textView : views) {
                textView.setTypeface(roubleTypeface);
            }
        }
    }
}
