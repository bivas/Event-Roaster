package org.eventroaster.example;

import org.eventroaster.EventService;
import org.eventroaster.EventServiceFactory;

public class EchoPublisher {

    private final EventService eventService;

    public EchoPublisher() {
        eventService = EventServiceFactory.getEventService(EchoServiceKey.ECHO_SERVICE_KEY);
    }

    public void callMe() {
        eventService.fire(new EchoEvent());
    }

}
