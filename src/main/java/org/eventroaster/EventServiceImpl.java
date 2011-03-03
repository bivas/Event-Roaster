package org.eventroaster;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang.Validate;
import org.eventroaster.annotation.Event;


final class EventServiceImpl implements EventService {

    private final EventServiceKey eventServiceKey;

    private final Map<Class<?>, List<Method>> methodsToInvoke;
    private final List<Object> listeners = new CopyOnWriteArrayList<Object>();
    private final ExecutorService executorService;
    private final BlockingQueue<Object> events = new LinkedBlockingQueue<Object>();

    EventServiceImpl(final EventServiceKey eventServiceKey, final Map<Class<?>, List<Method>> methodsToInvoke) {
	this.eventServiceKey = eventServiceKey;
	this.methodsToInvoke = methodsToInvoke;
	executorService = createExecutorService();
	startEventServiceHandling(eventServiceKey);
    }

    private ExecutorService createExecutorService() {
	return Executors.newCachedThreadPool(new ThreadFactory() {
	    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

	    @Override
	    public Thread newThread(final Runnable r) {
		final Thread thread = defaultThreadFactory.newThread(r);
		thread.setDaemon(true);
		return thread;
	    }
	});
    }

    private void startEventServiceHandling(final EventServiceKey eventServiceKey) {
	executorService.execute(new EventHandling());
    }

    @Override
    public void fire(final Object event) {
	Validate.notNull(event.getClass().getAnnotation(Event.class), "event is not annotated as @Event");
	events.add(event);
    }

    @Override
    public void register(final Object listener) {
	Validate.notNull(listener, "listner is required to register");
	validateListener(listener);
	listeners.add(listener);
    }

    @Override
    public void unregister(final Object listener) {
	Validate.notNull(listener, "listner is required to unregister");
	listeners.remove(listener);
    }

    private void validateListener(final Object listener) {
	Validate.isTrue(!listeners.contains(listener), "listener already registered", listener);
	for (final List<Method> methods : methodsToInvoke.values()) {
	    for (final Method method : methods) {
		final Class<?> declaringClass = method.getDeclaringClass();
		if (declaringClass.equals(listener.getClass())){
		    return;
		}
	    }
	}
	throw new IllegalArgumentException("No @EventHandler declare for listener " + listener);
    }

    public EventServiceKey getEventServiceKey() {
        return eventServiceKey;
    }

    private final class EventHandling implements Runnable {
	@Override
	public void run() {
	    while (true) {
		try {
		    fireEvent(events.take());
		} catch (final InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	    }
	}

	private void fireEvent(final Object event) {
	    final List<Callable<Boolean>> fireEvents = new ArrayList<Callable<Boolean>>();
	    for (final Method method : methodsToInvoke.get(event.getClass())) {
		final Object listener = findListener(method.getDeclaringClass());
		if (listener == null) {
		    continue;
		}
		fireEvents.add(new EventHandlingCallback(event, listener, method));
	    }
	    try {
		executorService.invokeAll(fireEvents);
	    } catch (final InterruptedException e) {
		Thread.currentThread().interrupt();
	    }
	}

	private Object findListener(final Class<?> declaringClass) {
	    for (final Object listener : listeners) {
		if ( declaringClass.equals(listener.getClass())) {
		    return listener;
		}
	    }
	    return null;
	}
    }

}
