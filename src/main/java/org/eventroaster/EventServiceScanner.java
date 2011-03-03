package org.eventroaster;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eventroaster.annotation.EventHandler;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;


final class EventServiceScanner {

    private static final EventServiceScanner instance = new EventServiceScanner();

    public static final EventServiceScanner getInstance() {
	return instance;
    }

    private final Map<Class<?>, List<Method>> methodsToInvoke;

    private EventServiceScanner() {
	final ConfigurationBuilder reflectionsConfiguration = new ConfigurationBuilder()
		.setUrls(ClasspathHelper.getUrlsForPackagePrefix(""))
		.setScanners(new SubTypesScanner(),
		new TypeAnnotationsScanner(),
		new MethodAnnotationsScanner());
	final Reflections reflections = new Reflections(reflectionsConfiguration);
	methodsToInvoke = collectAnnotatedMethodsToInvoke(reflections);
    }

    private Map<Class<?>, List<Method>> collectAnnotatedMethodsToInvoke(final Reflections reflections) {
	final Set<Method> eventHandlers = reflections.getMethodsAnnotatedWith(EventHandler.class);
	final Map<Class<?>, List<Method>> methods = new HashMap<Class<?>, List<Method>>();
	for (final Method method : eventHandlers) {
	    validateMethod(method);
	    collectInvokedMethod(methods, method);
	}
	return methods;
    }

    private void validateMethod(final Method method) {
	final Class<?>[] parameterTypes = method.getParameterTypes();
	Validate.isTrue(Modifier.isPublic(method.getModifiers()), method.getName() + " must be declared public");
	// The event is annotated as @Event
	final EventHandler annotation = method.getAnnotation(EventHandler.class);
	final Class<?> event = annotation.event();
	Validate.notNull(event.getAnnotation(org.eventroaster.annotation.Event.class),
		method.getName() + " must recieve a single parameter of annotated as Event");

	// check method arguments if requires the event
	Validate.isTrue(parameterTypes.length <= 1,
		method.getName() + " must recieve up to single parameter of annotated as Event");
	if (parameterTypes.length == 1) {
	    Validate.isTrue(parameterTypes[0].equals(event), "method must accept event of type ", event);
	}

    }

    private void collectInvokedMethod(final Map<Class<?>, List<Method>> methods, final Method method) {
	final EventHandler annotation = method.getAnnotation(EventHandler.class);
	final Class<?> event = annotation.event();
	List<Method> methodsPerEvent = methods.get(event);
	if (methodsPerEvent == null) {
	    methodsPerEvent = new ArrayList<Method>();
	}
	methodsPerEvent.add(method);
	methods.put(event, methodsPerEvent);
    }

    public Map<Class<?>, List<Method>> getMethodsToInvoke() {
	return Collections.unmodifiableMap(methodsToInvoke);
    }
}
