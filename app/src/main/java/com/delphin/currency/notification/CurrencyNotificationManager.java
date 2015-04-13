package com.delphin.currency.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.delphin.currency.R;
import com.delphin.currency.config.Config;
import com.delphin.currency.config.Courses;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.util.HashMap;
import java.util.Map;

@EBean
public class CurrencyNotificationManager {

    @RootContext
    protected Context context;

    @SystemService
    protected PowerManager powerManager;

    @SystemService
    protected NotificationManager notificationManager;

    public Map<String, String> values = new HashMap<>();

    public CurrencyNotificationManager() {
    }

    public void createInfoNotification(Map<String, String> values) {
        this.values.putAll(values);
        NotificationCompat.Builder builder = createNotificationBuilder(this.values);
        notificationManager.notify(Config.NOTIFICATION_ID, builder.build());
    }

    protected NotificationCompat.Builder createNotificationBuilder(Map<String, String> values) {
        RemoteViews remoteViews = getRemoteViews(values);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(icon()) //иконка уведомления
                .setAutoCancel(false) //уведомление не закроется по клику на него
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(context.getString(appName())) //заголовок уведомления
                .setContent(remoteViews);
    }

    private RemoteViews getRemoteViews(Map<String, String> values) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            remoteViews.setTextViewText(getCourseContainer(entry.getKey()), entry.getValue());
        }
        return remoteViews;
    }

    private int getCourseContainer(String course) {
        if (Courses.USD_RUB.equalsIgnoreCase(course)) {
            return R.id.usd_rub;
        } else if (Courses.EUR_RUB.equalsIgnoreCase(course)) {
            return R.id.eur_rub;
        }
        return 0;
    }

    public void updateNotification(Map<String, String> values) {
        createInfoNotification(values);
    }

    protected int icon() {
        return R.mipmap.ic_launcher;
    }

    protected int appName() {
        return R.string.app_name;
    }
}
