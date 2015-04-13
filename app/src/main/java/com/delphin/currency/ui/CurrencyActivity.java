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
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

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
        String course = intent.getStringExtra("course");
        double value = intent.getDoubleExtra("value", 0D);
        double previousValue = intent.getDoubleExtra("prev", value);

        String valueStr = String.format("%s (%s)", String.format("%.2f", value),
                convertDiff(value, previousValue));
        setValue(valueStr, course);
        setColor(value, previousValue, course);
    }

    private String convertDiff(Double value, Double previousValue) {
        if (previousValue == null) return "0.0";
        double diff = value - previousValue;
        return (diff < 0 ? "" : "+") + String.format("%.4f", diff);
    }

    private void setValue(String value, String course) {
        TextView container = getCourseUiContainer(course);
        if (container != null)
            container.setText(value);
    }

    private void setColor(Double value, Double previous, String course) {
        TextView container = getCourseUiContainer(course);
        if (container != null)
            container.setTextColor(getColor(value, previous));
    }

    private TextView getCourseUiContainer(String course) {
        if (Courses.USD_RUB.equalsIgnoreCase(course)) {
            return usdRub;
        } else if (Courses.EUR_RUB.equalsIgnoreCase(course)) {
            return eurRub;
        }
        return null;
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
