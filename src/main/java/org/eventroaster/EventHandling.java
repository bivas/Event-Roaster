package org.eventroaster;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class EventHandling implements Callable<Boolean> {

    private final BlockingQueue<Object> events;
    private final List<WeakReference<Object>> listeners;
    private final ExecutorService executorService;
    private final Map<Class<?>, List<Method>> methodsToInvoke;

    EventHandling(final EventServiceKey eventServiceKey,
                  final Map<Class<?>, List<Method>> methodsToInvoke,
                  final BlockingQueue<Object> events,
                  final List<WeakReference<Object>> listeners) {
        this.methodsToInvoke = methodsToInvoke;
        this.events = events;
        this.listeners = listeners;
        executorService = createExecutorService();
        Thread.currentThread().setName(eventServiceKey.toString());
    }

    private ExecutorService createExecutorService() {
        return Executors.newCachedThreadPool();
    }

    @Override
    public Boolean call() {
        while (true) {
            try {
                fireEvent(events.take());
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    private void fireEvent(final Object event) throws InterruptedException {
        final List<Callable<Boolean>> fireEvents = new ArrayList<Callable<Boolean>>();
        for (final Method method : methodsToInvoke.get(event.getClass())) {
            final Object listener = findListener(method.getDeclaringClass());
            if (listener == null) {
                continue;
            }
            fireEvents.add(new EventHandlingCallback(event, listener, method));
        }
        executorService.invokeAll(fireEvents);
    }

    private Object findListener(final Class<?> declaringClass) {
        for (final WeakReference<Object> weakListener : listeners) {
            final Object listener = weakListener.get();
            if (listener == null) {
                listeners.remove(weakListener);
            } else if (declaringClass.equals(listener.getClass())) {
                return listener;
            }
        }
        return null;
    }

    public void start() {
        executorService.submit(this);
    }
}
