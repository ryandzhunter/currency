package com.delphin.currency.storage;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Storage {

    @DefaultBoolean(false)
    boolean notificationVisibility();

    @DefaultBoolean(false)
    boolean dailyDifferenceActive();
}
