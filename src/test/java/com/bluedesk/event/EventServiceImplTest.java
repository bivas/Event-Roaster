package com.bluedesk.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.bluedesk.event.annotation.Event;
import com.bluedesk.event.annotation.EventHandler;

public class EventServiceImplTest {

    private boolean called = false;
    private EventService eventService;

    @Before
    public void setUp() {
	eventService = EventServiceFactory.getEventService(TestServiceKey.TEST_KEY);
	called = false;
    }

    @Test
    public void fireEventWithoutHandler() throws Exception {
	eventService.fire(new TestEvent());
	waitForEvent();
	assertFalse(called);
    }

    @Test
    public void fireEventWithHandler() throws Exception {
	register();
	eventService.fire(new TestEvent());
	waitForEvent();
	assertTrue(called);
    }


    @Test
    public void registerTwiceShouldFail() throws Exception {
	register();
	try {
	    register();
	    fail("trying to register twice - should fail");
	} catch (final IllegalArgumentException e) {
	    //good
	}
    }

    @Test
    public void registerListnerWithoutEventHandler() throws Exception {
	try {
	    eventService.register(new Object());
	    fail("no event handler method - should fail");
	} catch (final IllegalArgumentException e) {
	    //good
	}
    }

    @Test
    public void unregisterNonExistingListenerShouldNotFail() throws Exception {
	eventService.unregister(new Object());
    }

    @Test
    public void failOnObjectNotAnnotatedAsEvent() throws Exception {
	try {
	    eventService.fire(new Object());
	    fail("this is not an object with event annotation");
	} catch (final IllegalArgumentException e) {
	    // good
	}
    }

    private void register() {
	eventService.register(this);
    }

    private void waitForEvent() throws InterruptedException {
	Thread.sleep(2L);
    }

    @EventHandler(event = TestEvent.class)
    public void handlerTest(final TestEvent event) {
	called = true;
    }

    private enum TestServiceKey implements EventServiceKey {
	TEST_KEY;
    }

    @Event
    private static final class TestEvent {}

}
