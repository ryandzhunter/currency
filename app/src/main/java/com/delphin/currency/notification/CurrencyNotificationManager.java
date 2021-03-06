package com.delphin.currency.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.delphin.currency.R;
import com.delphin.currency.helper.ColorHelper;
import com.delphin.currency.helper.CurrencyFormatter;
import com.delphin.currency.ui.CurrencyActivity_;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import greendao.GlobalCourses;

@EBean
public class CurrencyNotificationManager {
    private static final int NOTIFICATION_ID = 100;

    @RootContext
    protected Context context;

    @SystemService
    protected NotificationManager notificationManager;

    @Bean
    protected ColorHelper colorHelper;

    @Bean
    protected CurrencyFormatter currencyFormatter;

    public void createInfoNotification(GlobalCourses values, GlobalCourses previous) {
        NotificationCompat.Builder builder = createNotificationBuilder(values, previous);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    protected NotificationCompat.Builder createNotificationBuilder(GlobalCourses values, GlobalCourses previous) {
        RemoteViews remoteViews = getRemoteViews(values, previous);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(icon()) //иконка уведомления
                .setAutoCancel(false) //уведомление не закроется по клику на него
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(context.getString(appName())) //заголовок уведомления
                .setOngoing(true) //отображается в разделе нескрываемых уведомлений
                .setContent(remoteViews)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, CurrencyActivity_.class), 0));
    }

    private RemoteViews getRemoteViews(GlobalCourses course, GlobalCourses previous) {
        if (previous == null) {
            previous = course;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.usd_rub, currencyFormatter.format(course.getUsd()));
        remoteViews.setTextViewText(R.id.eur_rub, currencyFormatter.format(course.getEur()));
        remoteViews.setTextViewText(R.id.oil, currencyFormatter.format(course.getOil()));

        remoteViews.setTextColor(R.id.usd_rub, colorHelper.getCurrencyDiffColor(course.getUsd(), previous.getUsd()));
        remoteViews.setTextColor(R.id.eur_rub, colorHelper.getCurrencyDiffColor(course.getEur(), previous.getEur()));
        remoteViews.setTextColor(R.id.oil, colorHelper.getOilDiffColor(course.getOil(), previous.getOil()));
        return remoteViews;
    }

    public void updateNotification(GlobalCourses values, GlobalCourses previous) {
        createInfoNotification(values, previous);
    }

    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    protected int icon() {
        return R.mipmap.ic_launcher;
    }

    protected int appName() {
        return R.string.app_name;
    }
}
