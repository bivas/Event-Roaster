package com.bluedesk.event.example;

import com.bluedesk.event.EventServiceFactory;
import com.bluedesk.event.annotation.EventHandler;

public class EchoHandler {

    EchoHandler() {
	EventServiceFactory.getEventService(EchoServiceKey.ECHO_SERVICE_KEY).register(this);
    }


    @EventHandler(event = EchoEvent.class)
    public void handle(final EchoEvent event) {
	System.out.println("I got the event!");
    }
}
