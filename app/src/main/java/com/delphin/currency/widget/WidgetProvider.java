package com.delphin.currency.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.delphin.currency.R;
import com.delphin.currency.helper.ColorHelper;
import com.delphin.currency.helper.CurrencyFormatter;
import com.delphin.currency.model.PairCourse;
import com.delphin.currency.otto.OttoBus;
import com.delphin.currency.otto.events.ImmediatelyUpdateActionEvent;
import com.delphin.currency.ui.CurrencyActivity_;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EReceiver;

import greendao.GlobalCourses;

@EReceiver
public class WidgetProvider extends AppWidgetProvider {
    Context context;

    @Bean
    protected ColorHelper colorHelper;

    @Bean
    protected OttoBus ottoBus;

    @Bean
    protected CurrencyFormatter currencyFormatter;

    @Bean
    protected OttoSubscriber subscriber;

    @Override
    public void onEnabled(Context context) {
        this.context = context;
        subscriber.setProvider(this);
        ottoBus.register(subscriber);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        ottoBus.post(new ImmediatelyUpdateActionEvent());
    }

    @Override
    public void onDisabled(Context context) {
        ottoBus.unregister(subscriber);
        super.onDisabled(context);
    }

    public void onCourseUpdate(PairCourse courses) {
        ComponentName thatWidget = new ComponentName(context.getApplicationContext(), getClass());
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(thatWidget);
        for (int id : appWidgetIds) {
            update(context, courses, id);
        }
    }

    private void update(Context context, PairCourse courses, int id) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        GlobalCourses course = courses.current;
        GlobalCourses previous = courses.previous != null ? courses.previous : course;

        remoteViews.setTextViewText(R.id.usd_rub, currencyFormatter.format(course.getUsd()));
        remoteViews.setTextViewText(R.id.eur_rub, currencyFormatter.format(course.getEur()));
        remoteViews.setTextViewText(R.id.oil, currencyFormatter.format(course.getOil()));

        remoteViews.setTextColor(R.id.usd_rub, colorHelper.getCurrencyDiffColor(course.getUsd(), previous.getUsd()));
        remoteViews.setTextColor(R.id.eur_rub, colorHelper.getCurrencyDiffColor(course.getEur(), previous.getEur()));
        remoteViews.setTextColor(R.id.oil, colorHelper.getCurrencyDiffColor(course.getOil(), previous.getOil()));

        Intent intent = new Intent(context, CurrencyActivity_.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.content, pendingIntent);
        // Instruct the widget manager to update the widget
        AppWidgetManager.getInstance(context).updateAppWidget(id, remoteViews);
    }

    @EBean(scope = EBean.Scope.Singleton)
    static class OttoSubscriber {

        protected WidgetProvider provider;

        @Subscribe
        public void onCourseUpdate(PairCourse courses) {
            if (provider != null)
                provider.onCourseUpdate(courses);
        }

        public void setProvider(WidgetProvider provider) {
            this.provider = provider;
        }
    }
}
