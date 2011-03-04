package org.eventroaster;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.Validate;
import org.eventroaster.annotation.Event;

final class EventServiceImpl implements EventService {

    private final Map<Class<?>, List<Method>> methodsToInvoke;
    private final List<WeakReference<Object>> listeners = new CopyOnWriteArrayList<WeakReference<Object>>();
    private final BlockingQueue<Object> events = new LinkedBlockingQueue<Object>();

    EventServiceImpl(final EventServiceKey eventServiceKey,
                     final Map<Class<?>, List<Method>> methodsToInvoke) {
        this.methodsToInvoke = methodsToInvoke;
        startEventServiceHandling(eventServiceKey);
    }

    private void startEventServiceHandling(final EventServiceKey eventServiceKey) {
        new EventHandling(eventServiceKey, methodsToInvoke, events, listeners).start();
    }

    @Override
    public void fire(final Object event) {
        Validate.isTrue(event.getClass().isAnnotationPresent(Event.class),
                        "event is not annotated as @Event");
        events.add(event);
    }

    @Override
    public void register(final Object listener) {
        Validate.notNull(listener, "listner is required to register");
        final WeakReference<Object> weakListener = new WeakReference<Object>(listener);
        validateListener(listener);
        listeners.add(weakListener);
    }

    @Override
    public void unregister(final Object listener) {
        Validate.notNull(listener, "listner is required to unregister");
        for (final WeakReference<Object> weakListner : listeners) {
            final Object registeredListener = weakListner.get();
            if ((registeredListener != null) && registeredListener.equals(listener)) {
                listeners.remove(weakListner);
                break;
            }
        }
    }

    private void validateListener(final Object listener) {
        for (final WeakReference<Object> weakListener : listeners) {
            Validate.isTrue(!weakListener.get().equals(listener));
        }
        final Class<? extends Object> listenerClass = listener.getClass();
        for (final List<Method> methods : methodsToInvoke.values()) {
            for (final Method method : methods) {
                final Class<?> declaringClass = method.getDeclaringClass();
                if (declaringClass.equals(listenerClass)) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException("No @EventHandler declare for listener " + listener);
    }
}
