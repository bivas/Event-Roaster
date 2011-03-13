package org.eventroaster.spring;

import org.eventroaster.EventService;
import org.eventroaster.EventServiceFactory;
import org.eventroaster.EventServiceKey;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class EventServiceFactoryBean implements FactoryBean<EventService>, InitializingBean {
	
	private enum DefaultEventService implements EventServiceKey {
		DEFAULT_EVENT_SERVICE;
	}
	
	private EventService eventServiceObject;
	
	private EventServiceKey eventServiceKey;

	public void setEventServiceKey(EventServiceKey eventServiceKey) {
		this.eventServiceKey = eventServiceKey;
	}

	@Override
	public EventService getObject() throws Exception {
		return eventServiceObject;
	}

	@Override
	public Class<?> getObjectType() {
		return eventServiceObject == null? EventService.class: eventServiceObject.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (eventServiceKey == null) {
			eventServiceKey = DefaultEventService.DEFAULT_EVENT_SERVICE;
		}
		eventServiceObject = EventServiceFactory.getEventService(eventServiceKey);
	}

}
