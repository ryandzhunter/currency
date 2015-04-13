package com.delphin.currency.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.delphin.currency.R;
import com.delphin.currency.config.Courses;
import com.delphin.currency.config.ReceiverAction;
import com.delphin.currency.service.UpdateService_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

@EActivity(R.layout.activity_currency)
public class CurrencyActivity extends Activity {
    @ViewById(R.id.usd_rub)
    protected TextView usdRub;

    @ViewById(R.id.eur_rub)
    protected TextView eurRub;

    @InstanceState
    protected HashMap<String, Double> previousCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, UpdateService_.class));
    }

    @Receiver(actions = ReceiverAction.ON_COURSE_UPDATE_ACTION)
    void onCourseUpdate(Intent intent) {
        String course = intent.getStringExtra("course");
        double value = intent.getDoubleExtra("value", 0D);

        Double previousValue = popPrevious(course);

        String valueStr = String.format("%s (%s)", String.format("%.2f", value),
                convertDiff(value, previousValue));
        setValue(valueStr, course);
        setColor(value, previousValue, course);

        pushPrevious(course, value);
    }

    private String convertDiff(Double value, Double previousValue) {
        if (previousValue == null) return "0.0";
        double diff = value - previousValue;
        return (diff < 0 ? "" : "+") + String.format("%.4f", diff);
    }

    private void setValue(String value, String course) {
        if (Courses.USD_RUB.equalsIgnoreCase(course)) {
            usdRub.setText(value);
        } else if (Courses.EUR_RUB.equalsIgnoreCase(course)) {
            eurRub.setText(value);
        }
    }

    private void setColor(Double value, Double previous, String course) {
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
}
