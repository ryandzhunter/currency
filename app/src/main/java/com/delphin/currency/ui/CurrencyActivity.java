package com.delphin.currency.ui;

import android.widget.TextView;

import com.delphin.currency.R;
import com.delphin.currency.config.Courses;
import com.delphin.currency.model.GlobalCurrencies;
import com.delphin.currency.retrofit.network.CurrencyRetrofitRequest;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;

/**
 * Created by darkdelphin on 12.04.2015.
 */
@EActivity(R.layout.activity_currency)
public class CurrencyActivity extends SpiceActivity {
    public static final long EXECUTION_DELAY = 10 * DurationInMillis.ONE_SECOND;

    @ViewById(R.id.usd_rub)
    protected TextView usdRub;

    @ViewById(R.id.eur_rub)
    protected TextView eurRub;

    @AfterViews
    void afterViews() {
        getCourse(Courses.USD_RUB);
        getCourse(Courses.EUR_RUB);
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

        }

        @Override
        public void onRequestSuccess(GlobalCurrencies currencyCourse) {
            if (Courses.USD_RUB.equalsIgnoreCase(course)) {
                usdRub.setText(String.valueOf(currencyCourse.getValue()));
            } else if (Courses.EUR_RUB.equalsIgnoreCase(course)) {
                eurRub.setText(String.valueOf(currencyCourse.getValue()));
            }
        }
    }
}
