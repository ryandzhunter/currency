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
import com.delphin.currency.model.PairCourse;
import com.delphin.currency.ui.CurrencyActivity_;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;

import greendao.GlobalCourses;

@EReceiver
public class WidgetProvider extends AppWidgetProvider {
    public static final String CURRENCY_WIDGET_UPDATE_ACTION = "com.delphin.currency.action.CURRENCY_WIDGET_UPDATE_ACTION";

    @Bean
    protected ColorHelper colorHelper;

    @ReceiverAction(CURRENCY_WIDGET_UPDATE_ACTION)
    public void onCourseUpdate(@ReceiverAction.Extra PairCourse courses, Context context) {
        ComponentName thatWidget = new ComponentName(context.getApplicationContext(), getClass());
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(thatWidget);
        for (int id : appWidgetIds) {
            update(context, courses, id);
        }
    }

    private void update(Context context, PairCourse courses, int id) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        GlobalCourses course = courses.current;
        GlobalCourses previous = courses.previous;

        remoteViews.setTextViewText(R.id.usd_rub, String.valueOf(course.getUsd()));
        remoteViews.setTextViewText(R.id.eur_rub, String.valueOf(course.getEur()));
        remoteViews.setTextViewText(R.id.oil, String.valueOf(course.getOil()));

        remoteViews.setTextColor(R.id.usd_rub, colorHelper.getColor(course.getUsd(),
                (previous != null ? previous : course).getUsd()));
        remoteViews.setTextColor(R.id.eur_rub, colorHelper.getColor(course.getEur(),
                (previous != null ? previous : course).getEur()));
        remoteViews.setTextColor(R.id.oil, colorHelper.getColor(course.getOil(),
                (previous != null ? previous : course).getOil()));

        Intent intent = new Intent(context, CurrencyActivity_.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.content, pendingIntent);
        // Instruct the widget manager to update the widget
        AppWidgetManager.getInstance(context).updateAppWidget(id, remoteViews);
    }
}
