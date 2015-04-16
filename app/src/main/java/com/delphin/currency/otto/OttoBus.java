package com.delphin.currency.otto;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.BasicBus;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class OttoBus extends BasicBus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void unregister(Object object) {
        try {
            super.unregister(object);
        } catch (IllegalArgumentException e) {
            Log.e("Object isn't registered", e.getMessage(), e);
        }
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    OttoBus.super.post(event);
                }
            });
        } else super.post(event);
    }
}
