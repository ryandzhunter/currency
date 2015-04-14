package com.delphin.currency.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.delphin.currency.R;
import com.delphin.currency.config.ReceiverAction;
import com.delphin.currency.helper.ColorHelper;
import com.delphin.currency.service.UpdateService_;
import com.delphin.currency.storage.Storage_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
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

    @Pref
    protected Storage_ storage;

    protected Typeface roubleTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, UpdateService_.class));
        roubleTypeface = Typeface.createFromAsset(getAssets(), "rouble.otf");
    }

    @AfterViews
    void afterViews() {
        usdRub.setTypeface(roubleTypeface);
        eurRub.setTypeface(roubleTypeface);

        notificationVisibility.setChecked(storage.notificationVisibility().get());
        notificationVisibility.setOnCheckedChangeListener(this);
    }

    @Receiver(actions = ReceiverAction.ON_COURSE_UPDATE_ACTION)
    void onCourseUpdate(Intent intent) {
        GlobalCourses course = (GlobalCourses) intent.getSerializableExtra("course");
        GlobalCourses previous = (GlobalCourses) intent.getSerializableExtra("prev");

        String usdStr = String.format("%s (%s)", withRouble(String.format("%.2f", course.getUsd())),
                convertDiff(course.getUsd(), previous != null ? previous.getUsd() : course.getUsd()));
        String eurStr = String.format("%s (%s)", withRouble(String.format("%.2f", course.getEur())),
                convertDiff(course.getEur(), previous != null ? previous.getEur() : course.getEur()));
        String oilStr = String.format("%s (%s)", String.format("%.2f", course.getOil()),
                convertDiff(course.getOil(), previous != null ? previous.getOil() : course.getOil()));

        setValue(usdRub, usdStr);
        setValue(eurRub, eurStr);
        setValue(oil, "$" + oilStr);

        setColor(usdRub, course.getUsd(), previous != null ? previous.getUsd() : course.getUsd());
        setColor(eurRub, course.getEur(), previous != null ? previous.getEur() : course.getEur());
        setColor(oil, course.getOil(), previous != null ? previous.getOil() : course.getOil());
    }

    private String convertDiff(Double value, Double previousValue) {
        if (previousValue == null) return "0.0";

        double diff = value - previousValue;
        return (diff < 0 ? "" : "+") + String.format("%.2f", diff);
    }

    private void setValue(TextView container, String course) {
        container.setText(course);
    }

    private String withRouble(String s) {
        String pattern = "%sa";
        return String.format(pattern, s);
    }

    private void setColor(TextView container, Double course, Double previous) {
        container.setTextColor(colorHelper.getColor(course, previous));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        storage.notificationVisibility().put(isChecked);
    }
}
