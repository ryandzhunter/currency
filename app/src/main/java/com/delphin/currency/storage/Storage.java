package com.delphin.currency.storage;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Storage {

    boolean notificationVisibility();
}
