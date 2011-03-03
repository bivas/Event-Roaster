package com.bluedesk.event;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

final class EventHandlingCallback implements Callable<Boolean> {

    private final Object event;
    private final Method method;
    private final Object listener;

    public EventHandlingCallback(final Object event, final Object listener, final Method method) {
	this.event = event;
	this.method = method;
	this.listener = listener;
    }

    @Override
    public Boolean call() throws Exception {
	try {
	    final Class<?>[] types = method.getParameterTypes();
	    if (types.length == 0) {
		method.invoke(listener);
	    } else {
		method.invoke(listener, event);
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return true;
    }
}
