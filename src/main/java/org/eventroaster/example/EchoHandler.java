package org.eventroaster.example;

import org.eventroaster.EventServiceFactory;
import org.eventroaster.annotation.EventHandler;

public class EchoHandler {

    EchoHandler() {
        EventServiceFactory.getEventService(EchoServiceKey.ECHO_SERVICE_KEY).register(this);
    }

    @EventHandler(event = EchoEvent.class)
    public void handle() {
        System.out.println("I got the event!");
    }

    @EventHandler(event = EchoEvent.class)
    public void handleEvent(final EchoEvent echoEvent) {
        System.out.println("I got event =" + echoEvent);
    }
}
