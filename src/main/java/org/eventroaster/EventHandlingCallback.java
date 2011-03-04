package org.eventroaster;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.apache.commons.logging.LogFactory;

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
    public Boolean call() {
        try {
            final Class<?>[] types = method.getParameterTypes();
            if (types.length == 0) {
                method.invoke(listener);
            } else {
                method.invoke(listener, event);
            }
        } catch (final Exception e) {
            LogFactory.getLog(EventHandlingCallback.class).warn("Got exception while invoking "
                                                                        + method,
                                                                e);
            return false;
        }
        return true;
    }
}
