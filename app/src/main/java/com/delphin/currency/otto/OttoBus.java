package com.delphin.currency.otto;

import android.util.Log;

import com.squareup.otto.BasicBus;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class OttoBus extends BasicBus {
    @Override
    public void unregister(Object object) {
        try {
            super.unregister(object);
        } catch (IllegalArgumentException e) {
            Log.e("Object isn't registered", e.getMessage(), e);
        }
    }
}
