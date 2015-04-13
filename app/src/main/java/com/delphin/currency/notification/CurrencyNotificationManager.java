package com.delphin.currency.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.delphin.currency.R;
import com.delphin.currency.config.Config;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import greendao.GlobalCourses;

@EBean
public class CurrencyNotificationManager {

    @RootContext
    protected Context context;

    @SystemService
    protected PowerManager powerManager;

    @SystemService
    protected NotificationManager notificationManager;

    public CurrencyNotificationManager() {
    }

    public void createInfoNotification(GlobalCourses values) {
        NotificationCompat.Builder builder = createNotificationBuilder(values);
        notificationManager.notify(Config.NOTIFICATION_ID, builder.build());
    }

    protected NotificationCompat.Builder createNotificationBuilder(GlobalCourses values) {
        RemoteViews remoteViews = getRemoteViews(values);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(icon()) //иконка уведомления
                .setAutoCancel(false) //уведомление не закроется по клику на него
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(context.getString(appName())) //заголовок уведомления
                .setContent(remoteViews);
    }

    private RemoteViews getRemoteViews(GlobalCourses values) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.usd_rub, values.getUsd());
        remoteViews.setTextViewText(R.id.eur_rub, values.getEur());
        return remoteViews;
    }

    public void updateNotification(GlobalCourses values) {
        createInfoNotification(values);
    }

    protected int icon() {
        return R.mipmap.ic_launcher;
    }

    protected int appName() {
        return R.string.app_name;
    }
}
