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
import com.delphin.currency.config.ReceiverAction;
import com.delphin.currency.helper.ColorHelper;
import com.delphin.currency.model.PairCourse;
import com.delphin.currency.otto.OttoBus;
import com.delphin.currency.service.UpdateService_;
import com.delphin.currency.storage.Storage_;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
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

    @Bean
    protected ColorHelper colorHelper;

    @Bean
    protected OttoBus ottoBus;

    @Pref
    protected Storage_ storage;

    @SystemService
    protected NotificationManager notificationManager;

    protected Typeface roubleTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, UpdateService_.class));
        roubleTypeface = Typeface.createFromAsset(getAssets(), "rouble.otf");
        ottoBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ottoBus.unregister(this);
    }

    @AfterViews
    void afterViews() {
        usdRub.setTypeface(roubleTypeface);
        eurRub.setTypeface(roubleTypeface);

        notificationVisibility.setChecked(storage.notificationVisibility().get());
        notificationVisibility.setOnCheckedChangeListener(this);
    }

    @Subscribe
    public void onCourseUpdate(PairCourse courses) {
        GlobalCourses course = courses.current;
        GlobalCourses previous = courses.previous;

        String usdStr = String.format("%s (%s)", withRouble(String.format("%.2f", course.getUsd())),
                convertDiff(course.getUsd(), (previous != null ? previous : course).getUsd()));
        String eurStr = String.format("%s (%s)", withRouble(String.format("%.2f", course.getEur())),
                convertDiff(course.getEur(), (previous != null ? previous : course).getEur()));
        String oilStr = String.format("%s (%s)", String.format("%.2f", course.getOil()),
                convertDiff(course.getOil(), (previous != null ? previous : course).getOil()));

        setValue(usdRub, usdStr);
        setValue(eurRub, eurStr);
        setValue(oil, "$" + oilStr);

        setColor(usdRub, course.getUsd(), (previous != null ? previous : course).getUsd());
        setColor(eurRub, course.getEur(), (previous != null ? previous : course).getEur());
        setColor(oil, course.getOil(), (previous != null ? previous : course).getOil());
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

    private void setColor(TextView container, Double course, Double previous) {
        container.setTextColor(colorHelper.getColor(course, previous));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        storage.notificationVisibility().put(isChecked);
        if (!isChecked) {
            notificationManager.cancel(Config.NOTIFICATION_ID);
        } else sendBroadcast(new Intent(ReceiverAction.SNOW_NOTIFICATION_IMMEDIATELY_ACTION));
    }
}
