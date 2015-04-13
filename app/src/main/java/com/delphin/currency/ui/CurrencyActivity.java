package com.delphin.currency.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.delphin.currency.R;
import com.delphin.currency.config.ReceiverAction;
import com.delphin.currency.service.UpdateService_;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, UpdateService_.class));
    }

    @Receiver(actions = ReceiverAction.ON_COURSE_UPDATE_ACTION)
    void onCourseUpdate(Intent intent) {
        GlobalCourses course = (GlobalCourses) intent.getSerializableExtra("course");
        GlobalCourses previous = (GlobalCourses) intent.getSerializableExtra("prev");

        String usdStr = String.format("%s (%s)", course.getUsd(), convertDiff(course.getUsd(), previous.getUsd()));
        String eurStr = String.format("%s (%s)", course.getEur(), convertDiff(course.getEur(), previous.getEur()));

        setValue(usdRub, usdStr);
        setValue(eurRub, eurStr);

        setColor(usdRub, course.getUsd(), previous.getUsd());
        setColor(eurRub, course.getEur(), previous.getEur());
    }

    private String convertDiff(String value, String previousValue) {
        if (previousValue == null) return "0.0";

        double diff = Double.parseDouble(value) - Double.parseDouble(previousValue);
        return (diff < 0 ? "" : "+") + String.format("%.2f", diff);
    }

    private void setValue(TextView container, String course) {
        container.setText(course);
    }

    private void setColor(TextView container, String course, String previous) {
        container.setTextColor(getColor(Double.parseDouble(course), Double.parseDouble(previous)));
    }

    private int getColor(Double value, Double previous) {
        return getResources().getColor(previous != null ? previous < value ?
                android.R.color.holo_red_light : android.R.color.holo_green_light : android.R.color.black);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UpdateService_.class));
        super.onDestroy();
    }
}
