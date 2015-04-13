package com.delphin.currency.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.delphin.currency.R;
import com.delphin.currency.config.ReceiverAction;
import com.delphin.currency.helper.ColorHelper;
import com.delphin.currency.service.UpdateService_;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import greendao.GlobalCourses;

@EActivity(R.layout.activity_currency)
public class CurrencyActivity extends Activity {
    @ViewById(R.id.usd_rub)
    protected TextView usdRub;

    @ViewById(R.id.eur_rub)
    protected TextView eurRub;

    @Bean
    protected ColorHelper colorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, UpdateService_.class));
    }

    @Receiver(actions = ReceiverAction.ON_COURSE_UPDATE_ACTION)
    void onCourseUpdate(Intent intent) {
        GlobalCourses course = (GlobalCourses) intent.getSerializableExtra("course");
        GlobalCourses previous = (GlobalCourses) intent.getSerializableExtra("prev");

        String usdStr = String.format("%s (%s)", String.format("%.2f", course.getUsd()),
                convertDiff(course.getUsd(), previous != null ? previous.getUsd() : course.getUsd()));
        String eurStr = String.format("%s (%s)", String.format("%.2f", course.getEur()),
                convertDiff(course.getEur(), previous != null ? previous.getEur() : course.getEur()));

        setValue(usdRub, usdStr);
        setValue(eurRub, eurStr);

        setColor(usdRub, course.getUsd(), previous != null ? previous.getUsd() : course.getUsd());
        setColor(eurRub, course.getEur(), previous != null ? previous.getEur() : course.getEur());
    }

    private String convertDiff(Double value, Double previousValue) {
        if (previousValue == null) return "0.0";

        double diff = value - previousValue;
        return (diff < 0 ? "" : "+") + String.format("%.2f", diff);
    }

    private void setValue(TextView container, String course) {
        container.setText(course);
    }

    private void setColor(TextView container, Double course, Double previous) {
        container.setTextColor(colorHelper.getColor(course, previous));
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UpdateService_.class));
        super.onDestroy();
    }
}
