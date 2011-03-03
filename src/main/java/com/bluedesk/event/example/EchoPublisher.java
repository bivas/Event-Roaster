package com.bluedesk.event.example;

import com.bluedesk.event.EventService;
import com.bluedesk.event.EventServiceFactory;

public class EchoPublisher {

    private final EventService eventService;

    public EchoPublisher() {
	eventService = EventServiceFactory.getEventService(EchoServiceKey.ECHO_SERVICE_KEY);
    }


    public void callMe() {
	eventService.fire(new EchoEvent());
    }


}
