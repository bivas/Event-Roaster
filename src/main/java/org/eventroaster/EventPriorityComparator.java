package org.eventroaster;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.eventroaster.annotation.EventHandler;

final class EventPriorityComparator implements Comparator<Method> {
    @Override
    public int compare(final Method m1, final Method m2) {
	final EventHandler eventHandler1 = m1.getAnnotation(EventHandler.class);
	final EventHandler eventHandler2 = m2.getAnnotation(EventHandler.class);
	return eventHandler1.priority() - eventHandler2.priority();
    }
}
