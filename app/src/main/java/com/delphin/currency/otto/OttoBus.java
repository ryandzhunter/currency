package com.delphin.currency.otto;

import com.squareup.otto.BasicBus;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class OttoBus extends BasicBus {
}
