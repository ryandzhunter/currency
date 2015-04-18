package com.delphin.currency.flurry;

import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.google.inject.Inject;

import roboguice.activity.event.OnStopEvent;
import roboguice.context.event.OnStartEvent;
import roboguice.event.Observes;

public class FlurrySessionListener extends FlurryListener {
    @Inject
    Context context;

    public void onStartSession(@Observes OnStartEvent event) {
        FlurryAgent.onStartSession(context, getApiKey(context));
    }

    public void onEndSession(@Observes OnStopEvent event) {
        FlurryAgent.onEndSession(context);
    }
}
