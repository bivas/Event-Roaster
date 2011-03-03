package org.eventroaster;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventServiceFactory {

    private static final Map<EventServiceKey, EventService> cache = new HashMap<EventServiceKey, EventService>();

    public static final EventService getEventService(final EventServiceKey eventServiceKey) {
	EventService eventService = cache.get(eventServiceKey);
	if (eventService == null) {
	    eventService = createNewService(eventServiceKey);
	    cache.put(eventServiceKey, eventService);
	}
	return eventService;
    }

    private static EventService createNewService(final EventServiceKey eventServiceKey) {
	final Map<Class<?>, List<Method>> methodsToInvoke = EventServiceScanner.getInstance().getMethodsToInvoke();
	return new EventServiceImpl(eventServiceKey, methodsToInvoke);
    }
}
