package com.delphin.currency.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.delphin.currency.R;
import com.delphin.currency.helper.Calculator;
import com.delphin.currency.helper.ColorHelper;
import com.delphin.currency.helper.CurrencyFormatter;
import com.delphin.currency.model.PairCourse;
import com.delphin.currency.notification.CurrencyNotificationManager;
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

    @Bean
    protected CurrencyNotificationManager notificationManager;

    @Bean
    protected Calculator calculator;

    @Bean
    protected CurrencyFormatter currencyFormatter;

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

        String usdStr = formatCurrencyWithDiff(
                currencyFormatter.formatToRouble(currencyFormatter.format(course.getUsd())),
                currencyFormatter.formatDiff(course.getUsd(), previous.getUsd()));
        String eurStr = formatCurrencyWithDiff(
                currencyFormatter.formatToRouble(currencyFormatter.format(course.getEur())),
                currencyFormatter.formatDiff(course.getEur(), previous.getEur()));
        String oilStr = formatCurrencyWithDiff(
                currencyFormatter.format(course.getOil()),
                currencyFormatter.formatDiff(course.getOil(), previous.getOil()));

        String eurToUsdStr = formatEurToUsd(
                currencyFormatter.format(
                        calculator.eurToUsd(course.getEur(), course.getUsd())));
        String oilRoubleCostStr =
                currencyFormatter.formatToRouble(
                        currencyFormatter.format(
                                calculator.oilRoubleCost(course.getUsd(), course.getOil())));

        usdRub.setText(usdStr);
        eurRub.setText(eurStr);
        oil.setText(currencyFormatter.formatToUsd(oilStr));

        setCurrencyDiffColor(usdRub, course.getUsd(), previous.getUsd());
        setCurrencyDiffColor(eurRub, course.getEur(), previous.getEur());
        setOilDiffColor(oil, course.getOil(), previous.getOil());

        eurUsd.setText(eurToUsdStr);
        oilRub.setText(oilRoubleCostStr);
    }

    private String formatEurToUsd(String s) {
        return String.format(formatEurToUsd, s);
    }

    private String formatCurrencyWithDiff(String value, String diff) {
        return String.format(currencyWithDiff, value, diff);
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
            notificationManager.cancelNotification();
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
